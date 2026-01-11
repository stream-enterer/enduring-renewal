# Workflow Template Verification Guide

This document helps verify that adaptations of `CLAUDE_TEMPLATE.md` preserve what makes it work. The template is battle-tested. Changes should be minimal and verified.

---

## Invariants (Must Remain True)

### Invariant 1: Immutable Contract
```
The `all` array must:
- Contain every unit from the authoritative source
- Never be modified after initial creation
- Match the source count exactly
```

**Verification:** Count items in `all`. Count items in authoritative source. Numbers must match.

### Invariant 2: Safety Equation
```
all = implemented ∪ blocked ∪ remaining
```

Every unit is always in exactly one state. Nothing can be lost.

**Verification:** `len(all) == len(implemented) + len(blocked) + len(remaining)`

### Invariant 3: Ground Truth Hierarchy
```
1. Authoritative source (code/spec)
2. Executable tests
3. NOT documentation
```

If tests pass but behavior is wrong, the test is wrong—not the implementation.

**Verification:** Can you point to the authoritative source? Is it external to CLAUDE.md?

### Invariant 4: State in Filesystem
```
Progress persists in JSON, not conversation.
A fresh context can resume from JSON alone.
```

**Verification:** Delete conversation history mentally. Can agent continue from JSON?

---

## Required Sections Checklist

The adapted CLAUDE.md must have these sections. Missing sections indicate incomplete adaptation.

- [ ] **Methodology** - States what authoritative source is
- [ ] **State Tracking** - Documents JSON structure and safety invariant
- [ ] **On "continue"** - Has workflow steps agent follows
- [ ] **Implementation Patterns** - Has code locations with signatures and variables
- [ ] **Test Patterns** - Has working examples with imports
- [ ] **Error Recovery** - Has guidance for failures
- [ ] **Blocked Handling** - Has process for units that can't be implemented

---

## Pattern Verification

### Implementation Patterns Must Include:

For each implementation location:
- [ ] File path (e.g., `src/fight.py`)
- [ ] Line number or method name (e.g., `line ~1267`)
- [ ] Full method signature with parameters
- [ ] List of available variables and their types
- [ ] Code example showing the pattern

**Failure mode:** Agent reads full files to discover what's missing.

### Test Patterns Must Include:

- [ ] Import statements
- [ ] Helper function definitions (or reference to where they exist)
- [ ] Complete test method (setup, act, assert)
- [ ] Both positive and negative test cases

**Failure mode:** Agent writes tests that don't compile/run.

### Patterns Must Be Verified:

- [ ] Implementation pattern code was copy-pasted from actual working code
- [ ] Test pattern was actually executed and passed

**Failure mode:** Patterns look right but don't work.

---

## Adaptation Boundary Check

### What Should Change:

- Domain-specific content (file paths, method names, unit names)
- The `all` array contents
- Implementation locations and patterns
- Test patterns and assertions
- Infrastructure dependencies list
- Funnel sieve test selection

### What Should NOT Change:

- Spec-as-test methodology principle
- JSON state structure (`implemented`, `blocked`, `all`)
- Safety invariant concept
- Error recovery rules
- Two-tier verification concept
- Token efficiency guidance pattern

**Verification:** Diff the adapted file against template. Changes should only be in "should change" areas.

---

## Red Flags (Indicates Bad Adaptation)

### In State JSON:
- [ ] Metadata beyond simple arrays (categories, descriptions, difficulty)
- [ ] Nested objects in `all` array
- [ ] Missing `blocked` object
- [ ] `all` array count doesn't match source

### In CLAUDE.md:
- [ ] No authoritative source identified
- [ ] Implementation patterns without method signatures
- [ ] Implementation patterns without available variables
- [ ] Test patterns without imports
- [ ] No error recovery section
- [ ] No blocked handling process
- [ ] Token efficiency section removed entirely

### In Behavior:
- [ ] Agent reads full implementation files on every "continue"
- [ ] Agent doesn't know where to add code without reading files
- [ ] Tests fail when using documented patterns
- [ ] Units get "lost" (not in implemented, blocked, or remaining)

---

## Verification Questions

Before using an adapted template, the adapting agent should answer:

1. **Source:** "The authoritative source is _____ and it contains exactly _____ units."

2. **Contract:** "The `all` array has _____ items, matching the source."

3. **Locations:** "Implementation goes in _____ at line _____. The method signature is _____. Available variables are _____."

4. **Tests:** "I ran the test pattern and it [passed/failed]."

5. **Blocked:** "These units are blocked because _____: [list]"

Vague answers ("I think...", "probably...", "somewhere in...") indicate incomplete adaptation.

---

## Minimal Adaptation Principle

The template works. Adaptations should be minimal.

```
Ideal adaptation:
- Replace domain-specific nouns (keyword → item, fight.py → inventory.py)
- Update `all` array from new source
- Update code locations from new codebase
- Verify patterns work

NOT ideal:
- Restructuring sections
- Adding new concepts
- Removing "unnecessary" sections
- Changing the methodology
```

If you find yourself making structural changes, stop and verify why. The structure exists because removing parts caused failures.

---

## Post-Adaptation Smoke Test

After adaptation, simulate "continue" mentally:

1. Agent reads state JSON
2. Agent computes remaining = all - implemented - blocked
3. Agent picks next unit (does soft priority make sense?)
4. Agent looks up implementation location (is it documented with signature?)
5. Agent writes test (does pattern have imports?)
6. Agent implements (does pattern show available variables?)
7. Agent runs tests (will pattern work?)
8. Agent updates JSON (is the structure correct?)

If any step requires reading undocumented files, the adaptation is incomplete.
