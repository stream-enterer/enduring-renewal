#!/usr/bin/env python3
"""Oracle test triage state management tool.

Tracks triage progress across sessions using triage_state.json.
"""

import argparse
import json
import subprocess
import sys
from datetime import datetime
from pathlib import Path

STATE_FILE = Path(__file__).parent.parent / "triage_state.json"
ORACLE_TESTS = Path(__file__).parent.parent / "oracle_tests" / "generated_tests.json"

VERDICTS = {"ORACLE_WRONG", "IMPL_WRONG", "HARNESS_GAP"}
STATUSES = {"untriaged", "triaged", "fixed", "verified"}
PRIORITIES = {"P0", "P1", "P2", "P3"}


def load_state() -> dict:
    """Load state from file or return empty state."""
    if STATE_FILE.exists():
        with open(STATE_FILE) as f:
            return json.load(f)
    return {
        "version": 1,
        "last_scan": None,
        "stats": {},
        "failures": {},
    }


def save_state(state: dict) -> None:
    """Save state to file."""
    with open(STATE_FILE, "w") as f:
        json.dump(state, f, indent=2)
    print(f"State saved to {STATE_FILE}")


def run_oracle_tests() -> tuple[list[str], list[str], dict]:
    """Run oracle tests and parse output."""
    result = subprocess.run(
        ["uv", "run", "python", "tools/verify_oracle.py"],
        capture_output=True,
        text=True,
        cwd=Path(__file__).parent.parent,
    )

    failures = []
    errors = []
    stats = {"total": 0, "passed": 0, "failed": 0, "errors": 0, "skipped": 0}

    for line in result.stdout.split("\n") + result.stderr.split("\n"):
        if line.startswith("Total:"):
            stats["total"] = int(line.split()[-1])
        elif line.startswith("Passed:"):
            # Handle ANSI codes
            stats["passed"] = int(line.split()[-1].replace("\x1b[92m", "").replace("\x1b[0m", ""))
        elif line.startswith("Failed:"):
            stats["failed"] = int(line.split()[-1].replace("\x1b[91m", "").replace("\x1b[0m", ""))
        elif line.startswith("Skipped:"):
            stats["skipped"] = int(line.split()[-1].replace("\x1b[93m", "").replace("\x1b[0m", ""))
        elif line.startswith("Errors:"):
            stats["errors"] = int(line.split()[-1].replace("\x1b[91m", "").replace("\x1b[0m", ""))
        elif "✗" in line and ":" in line:
            # Extract test ID from failure line
            parts = line.split(":")
            if parts:
                test_id = parts[0].strip().replace("✗", "").strip()
                # Remove ANSI codes
                test_id = test_id.replace("\x1b[91m", "").replace("\x1b[0m", "").strip()
                if test_id:
                    failures.append(test_id)
        elif "!" in line and "ERROR" in line:
            parts = line.split(":")
            if parts:
                test_id = parts[0].strip().replace("!", "").strip()
                test_id = test_id.replace("\x1b[91m", "").replace("\x1b[0m", "").strip()
                if test_id:
                    errors.append(test_id)

    return failures, errors, stats


def cmd_scan(args):
    """Scan oracle tests and update state."""
    print("Running oracle tests...")
    failures, errors, stats = run_oracle_tests()

    state = load_state()
    state["last_scan"] = datetime.now().isoformat()
    state["stats"] = stats

    # Add new failures
    all_issues = failures + errors
    added = 0
    for test_id in all_issues:
        if test_id not in state["failures"]:
            is_error = test_id in errors
            state["failures"][test_id] = {
                "status": "untriaged",
                "verdict": None,
                "notes": None,
                "citation": None,
                "priority": "P0" if is_error else None,
                "is_error": is_error,
            }
            added += 1

    # Mark resolved tests
    resolved = 0
    for test_id in list(state["failures"].keys()):
        if test_id not in all_issues and state["failures"][test_id]["status"] != "verified":
            state["failures"][test_id]["status"] = "verified"
            resolved += 1

    save_state(state)
    print(f"\nStats: {stats}")
    print(f"Added {added} new failures, resolved {resolved}")


def cmd_status(args):
    """Show triage progress."""
    state = load_state()

    if not state["failures"]:
        print("No failures tracked. Run 'triage.py scan' first.")
        return

    # Count by status
    by_status = {s: 0 for s in STATUSES}
    by_verdict = {v: 0 for v in VERDICTS}
    by_priority = {p: 0 for p in PRIORITIES}

    for test_id, data in state["failures"].items():
        by_status[data["status"]] += 1
        if data["verdict"]:
            by_verdict[data["verdict"]] += 1
        if data["priority"]:
            by_priority[data["priority"]] += 1

    print("=== Triage Progress ===")
    print(f"Last scan: {state.get('last_scan', 'never')}")
    print(f"Stats: {state.get('stats', {})}")
    print()
    print("By Status:")
    for status, count in by_status.items():
        if count > 0:
            print(f"  {status}: {count}")
    print()
    print("By Verdict:")
    for verdict, count in by_verdict.items():
        if count > 0:
            print(f"  {verdict}: {count}")
    print()
    print("By Priority:")
    for priority, count in sorted(by_priority.items()):
        if count > 0:
            print(f"  {priority}: {count}")


def cmd_next(args):
    """Show next untriaged failure."""
    state = load_state()

    # Priority order
    priority_order = ["P0", "P1", "P2", "P3", None]

    for priority in priority_order:
        if args.priority and priority != args.priority:
            continue

        for test_id, data in state["failures"].items():
            if data["status"] == "untriaged":
                if priority is None or data.get("priority") == priority:
                    print(f"Next: {test_id}")
                    print(f"  Priority: {data.get('priority', 'unset')}")
                    print(f"  Error: {data.get('is_error', False)}")
                    print()
                    print("Commands:")
                    print(f"  uv run python tools/verify_oracle.py --filter {test_id} -v")
                    print(f"  uv run python tools/triage.py set {test_id} <VERDICT> \"<notes>\"")
                    return

    print("All failures triaged!")


def cmd_set(args):
    """Set verdict for a failure."""
    state = load_state()

    if args.test_id not in state["failures"]:
        print(f"Unknown test: {args.test_id}")
        print("Run 'triage.py scan' to refresh failures.")
        return 1

    if args.verdict not in VERDICTS:
        print(f"Invalid verdict: {args.verdict}")
        print(f"Valid: {VERDICTS}")
        return 1

    # Auto-assign priority based on verdict
    priority = {
        "HARNESS_GAP": "P1",
        "ORACLE_WRONG": "P2",
        "IMPL_WRONG": "P3",
    }.get(args.verdict)

    # Keep P0 for errors
    if state["failures"][args.test_id].get("is_error"):
        priority = "P0"

    state["failures"][args.test_id].update({
        "status": "triaged",
        "verdict": args.verdict,
        "notes": args.notes,
        "citation": args.citation,
        "priority": priority,
    })

    save_state(state)
    print(f"Set {args.test_id}: {args.verdict} ({priority})")


def cmd_show(args):
    """Show details of a failure."""
    state = load_state()

    if args.test_id not in state["failures"]:
        print(f"Unknown test: {args.test_id}")
        return 1

    data = state["failures"][args.test_id]
    print(f"Test: {args.test_id}")
    for key, value in data.items():
        print(f"  {key}: {value}")


def cmd_verify(args):
    """Verify a fix by running the test."""
    state = load_state()

    if args.all:
        # Verify all fixed
        to_verify = [
            tid for tid, data in state["failures"].items()
            if data["status"] == "fixed"
        ]
    else:
        if args.test_id not in state["failures"]:
            print(f"Unknown test: {args.test_id}")
            return 1
        to_verify = [args.test_id]

    for test_id in to_verify:
        print(f"Verifying {test_id}...")
        result = subprocess.run(
            ["uv", "run", "python", "tools/verify_oracle.py", "--filter", test_id],
            capture_output=True,
            text=True,
            cwd=Path(__file__).parent.parent,
        )

        passed = "✓" in result.stdout or result.returncode == 0
        if passed and "✗" not in result.stdout and "ERROR" not in result.stderr:
            state["failures"][test_id]["status"] = "verified"
            print(f"  PASSED - marked as verified")
        else:
            state["failures"][test_id]["status"] = "triaged"
            print(f"  FAILED - reverted to triaged")
            print(result.stdout[-500:] if len(result.stdout) > 500 else result.stdout)

    save_state(state)


def cmd_list(args):
    """List failures by filter."""
    state = load_state()

    for test_id, data in state["failures"].items():
        if args.status and data["status"] != args.status:
            continue
        if args.verdict and data["verdict"] != args.verdict:
            continue
        if args.priority and data.get("priority") != args.priority:
            continue

        print(f"{test_id}: {data['status']} ({data.get('verdict', '-')}) [{data.get('priority', '-')}]")


def cmd_mark_fixed(args):
    """Mark a triaged failure as fixed (ready for verification)."""
    state = load_state()

    if args.test_id not in state["failures"]:
        print(f"Unknown test: {args.test_id}")
        return 1

    if state["failures"][args.test_id]["status"] != "triaged":
        print(f"Test {args.test_id} is not triaged (status: {state['failures'][args.test_id]['status']})")
        return 1

    state["failures"][args.test_id]["status"] = "fixed"
    save_state(state)
    print(f"Marked {args.test_id} as fixed. Run 'triage.py verify {args.test_id}' to verify.")


def main():
    parser = argparse.ArgumentParser(description="Oracle test triage tool")
    subparsers = parser.add_subparsers(dest="command", required=True)

    # scan
    subparsers.add_parser("scan", help="Scan oracle tests and update state")

    # status
    subparsers.add_parser("status", help="Show triage progress")

    # next
    next_parser = subparsers.add_parser("next", help="Show next untriaged failure")
    next_parser.add_argument("--priority", "-p", choices=list(PRIORITIES), help="Filter by priority")

    # set
    set_parser = subparsers.add_parser("set", help="Set verdict for a failure")
    set_parser.add_argument("test_id", help="Test ID")
    set_parser.add_argument("verdict", choices=list(VERDICTS), help="Verdict")
    set_parser.add_argument("notes", help="Notes about the decision")
    set_parser.add_argument("--citation", "-c", help="Java source citation")

    # show
    show_parser = subparsers.add_parser("show", help="Show failure details")
    show_parser.add_argument("test_id", help="Test ID")

    # verify
    verify_parser = subparsers.add_parser("verify", help="Verify a fix")
    verify_parser.add_argument("test_id", nargs="?", help="Test ID")
    verify_parser.add_argument("--all", action="store_true", help="Verify all fixed")

    # list
    list_parser = subparsers.add_parser("list", help="List failures")
    list_parser.add_argument("--status", "-s", choices=list(STATUSES), help="Filter by status")
    list_parser.add_argument("--verdict", "-v", choices=list(VERDICTS), help="Filter by verdict")
    list_parser.add_argument("--priority", "-p", choices=list(PRIORITIES), help="Filter by priority")

    # fixed
    fixed_parser = subparsers.add_parser("fixed", help="Mark as fixed")
    fixed_parser.add_argument("test_id", help="Test ID")

    args = parser.parse_args()

    commands = {
        "scan": cmd_scan,
        "status": cmd_status,
        "next": cmd_next,
        "set": cmd_set,
        "show": cmd_show,
        "verify": cmd_verify,
        "list": cmd_list,
        "fixed": cmd_mark_fixed,
    }

    return commands[args.command](args)


if __name__ == "__main__":
    sys.exit(main() or 0)
