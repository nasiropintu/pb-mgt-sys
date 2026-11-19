# ADR 0003: Local-first capture and processing

- Status: Accepted for v0.1
- Date: 2026-11-18

## Decision

Persist each capture and its original attachment locally before queuing AI work or future sync. The local device remains useful without AI, internet, or the PC. Keep suggestions separate from confirmed user data.

## Consequences

Capture durability, restart recovery, backup, and restore are release gates. Sync is not a backup strategy. The first implementation milestone prioritizes local storage over automation.
