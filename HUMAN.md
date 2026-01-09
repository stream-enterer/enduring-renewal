# Human Operator Guide

## Quick Reference

```
# Start/resume session
Read PROGRESS and continue from where we left off.

# Verification responses
Confirmed. [details]
Actually, [what really happens]
Can't test - [reason]

# Control
Stop here for now.
Skip this method, reason: [X]. Move to next.
Mark as blocked, reason: [X]. Move to next.

# When stuck
Stop. Read PROGRESS. What phase are we in? Continue from there.
Show me the current state of PROGRESS.
```

---

## Prerequisites

**You need to know Slice & Dice well enough to:**
- Set up specific scenarios (choose heroes, face specific enemies)
- Observe combat mechanics in action
- Recognize when behavior matches or differs from a hypothesis

If the game has a debug/sandbox mode, use it. Otherwise, you'll need to play until you can test the scenario.

---

## First Session Ever

The first time you run this project:

```
Read PROGRESS and continue from where we left off.
```

PROGRESS starts at `TestStrangeScenarios` with `method: null`. Claude will:
1. Read the first test file
2. List the @Test methods
3. Start with the first method

---

## Starting a Session

1. Open terminal in `/home/ar/Documents/tann/decompiled`
2. Run `claude` to start Claude Code
3. Prompt:

```
Read PROGRESS and continue from where we left off.
```

To check state manually: `cat combat/PROGRESS`

---

## The Loop

### Phase 1: ANALYZE (Claude works)

Claude reads the Java test and presents a hypothesis like:

> "This test verifies that attacking an enemy with 1 damage reduces their HP by 1."

**Your job:** Read the hypothesis. If it's obviously wrong based on what you know, say so. Otherwise, proceed to verification.

**If hypothesis looks wrong:**
```
That doesn't sound right. [What you think it actually tests]
```

---

### Phase 2: VERIFY (You work)

Claude asks you to verify in the real game.

**What to do:**

1. Open Slice & Dice
2. Set up the scenario (or as close as possible)
3. Observe the behavior
4. Report back

**Response templates:**

| Situation | Response |
|-----------|----------|
| Confirmed exactly | `Confirmed. [optional details]` |
| Confirmed with nuance | `Confirmed. Note: [additional observation]` |
| Different behavior | `Actually, [what really happens]` |
| Can't set up scenario | `Can't test - [reason, e.g., "can't force this enemy to spawn"]` |
| RNG-dependent | `Tested 5 times: [observed pattern]` |
| Unclear | `Not sure - [what you observed]` |

**Examples:**
- `Confirmed. 3 damage against 10 HP enemy left it at 7 HP.`
- `Actually, shield blocks damage one-for-one, not all at once.`
- `Can't test - this requires a specific hero I haven't unlocked.`
- `Tested 5 times: healing always capped at max HP, never overhealed.`

**If verification will take a while:**
```
Pausing to test this. I'll report back.
```

Then come back and give your response.

---

### Phase 3: IMPLEMENT (Claude works)

Claude writes the Python test and implementation, runs pytest until green.

**Your job:** Wait and watch. Usually no intervention needed.

**If Claude gets stuck:**
```
What's the error?
```
```
Show me: 1) the test, 2) the implementation, 3) the error
```
```
Try a different approach.
```

**If you disagree with the approach:**
```
I'd prefer [different approach]. Try that instead.
```

---

### Phase 4: COMMIT (Claude works)

Claude updates PROGRESS, commits to git, advances to next method.

**Your job:** Nothing. Claude handles this automatically.

**To stop the loop:**
```
Stop here for now.
```

**To continue:**
Claude should auto-continue. If not:
```
Continue to the next method.
```

---

## New Test File

When Claude encounters a file with `method: null`:

Claude will list @Test methods and ask about relevance.

**If clearly non-combat (e.g., TestMusic, TestFiles):**
```
Skip this file - [reason, e.g., "audio system, not combat"]
```

**If relevant or unclear:**
```
Proceed.
```

---

## Problem Handling

### Claude: "I don't understand this Java code"
```
What specifically is unclear? Show me the confusing part.
```
Then help interpret, or:
```
Mark as blocked, reason: unclear Java pattern. Move to next.
```

### Claude: "I can't figure out how to test this in-game"
```
Skip this method, reason: can't verify in-game. Move to next.
```

### Claude: "Tests are failing and I'm stuck"
```
Show me: 1) the test, 2) the implementation, 3) the error
```
Then either help debug or:
```
Mark as blocked, reason: implementation issue. Move to next.
```

### Claude: "This needs a system we haven't built"
```
What system is missing? Can we stub it for now?
```
If truly blocking:
```
Mark as blocked, reason: needs [X] system. Move to next.
```

### Claude: "I think our earlier implementation is wrong"
```
What's wrong with it? Which test revealed the problem?
```
Then:
```
Fix it and make sure all existing tests still pass.
```

### Claude goes off-track or seems confused
```
Stop. Read PROGRESS. What phase are we in? Continue from there.
```

### Claude asks too many questions
```
Make a decision and proceed. I'll correct if needed.
```

### You notice a problem Claude doesn't mention
```
Wait. [Describe the problem you see]. Address this first.
```

### Context seems degraded (Claude forgetting things)
```
Let's refresh. Read CLAUDE.md, then read PROGRESS, then continue.
```

---

## Context Management

Claude Code shows context **remaining** as a percentage. It warns around 12%.

### When to compact

| Context remaining | Action |
|-------------------|--------|
| > 30% | No action needed |
| 15-30% | Consider compacting after current method completes |
| 10-15% | Compact at next natural break (after COMMIT phase) |
| < 10% | Interrupt and compact immediately |

### How to compact

At a natural break (after COMMIT):
```
/compact
```

If you need to interrupt mid-phase:
```
Stop. Save state to PROGRESS.
```
Then run `/compact`.

### The summary argument

`/compact [summary]` takes an optional summary. Use it to preserve key context:

```
/compact Currently on TestBasicEff.attackEnemy, implementing. Shield system uses one-for-one blocking.
```

Include:
- Current file and method
- Current phase
- Any important implementation decisions made this session

### After compaction

Context is cleared but Claude loses conversation memory. Always follow with:
```
Read CLAUDE.md, then read PROGRESS, then continue from where we left off.
```

### Signs context is degraded (even if % looks ok)

- Claude forgets what phase we're in
- Claude re-asks questions you already answered
- Claude suggests approaches you already rejected
- Claude doesn't remember earlier implementation decisions

If this happens, compact with summary and resume.

### Proactive compaction

After completing a test file (all methods done), good time to compact:
```
/compact Finished TestBasicEff. All methods complete. Starting TestItem next.
```

Then resume fresh for the next file.

---

## Session Management

### Pausing mid-phase
If you need to stop during VERIFY or IMPLEMENT:
```
I need to pause. Save current state - I'll resume later.
```

Claude should ensure PROGRESS reflects current position.

### Ending a session
```
Stop here. Make sure PROGRESS is saved.
```
Verify: `cat combat/PROGRESS` shows correct state.

### Resuming after a break
```
Read PROGRESS and continue from where we left off.
```

### Checking progress
```
Show me the current state of PROGRESS.
```
```
How many methods have we completed? How many remain in this file?
```
```
Summarize what we've built so far.
```

---

## Emergency Reset

If state gets corrupted or out of sync:

1. Check git: `git status`, `git log --oneline -10`
2. See what changed: `git diff combat/PROGRESS`
3. Reset if needed: `git checkout combat/PROGRESS`
4. Or manually edit PROGRESS to correct state
5. Tell Claude:
```
PROGRESS was wrong. We are at file [X], method [Y], status [Z]. Continue from there.
```

---

## After All Tests Complete

When all 27 test files are done:

1. Run full test suite: `cd combat && uv run pytest -v`
2. Review what was skipped: check `skipped` array in PROGRESS
3. Decide whether to revisit blocked/skipped items
4. The combat system is now ready for use in your fangame

---

## Files Reference

| File | Purpose |
|------|---------|
| `CLAUDE.md` | Agent instructions (Claude reads this) |
| `HUMAN.md` | This file (you read this) |
| `combat/PROGRESS` | Current state (both read/write) |
| `combat/src/` | Implementation code |
| `combat/tests/` | Python test files |
