# Product Requirements Baseline

Status: derived from the approved PBLOS project details v1.0 and roadmap v1.0. This document summarizes Phase 0 constraints; the master specification is authoritative if details diverge.

## Product promise

Say it, save it, find it, act on it. The system must remain useful without AI, internet, or the PC.

## v0.1 scope

- Define a Kotlin/Jetpack Compose Android app for the OPPO A3x and Vivo Y20.
- Define an Electron/React/TypeScript Windows worker shell for the Ryzen 5 5600G PC.
- Establish shared contracts, SQLite migrations, repository documentation, and build foundations.
- No full AI engine or production synchronization implementation in v0.1.

## Product constraints

- Capture persists locally before transcription, AI processing, or synchronization.
- Keep original captures and their provenance.
- Core mobile workflows cannot depend on the PC, internet, paid APIs, or cloud services.
- Bengali, English, and mixed-language content must be representable and editable.
- AI output is a proposal; ambiguous or consequential actions require human review. Financial entries require explicit confirmation.
- Synchronization is not backup; backup and restore validation precede production reliance on sync.
- No continuous/background listening claim. Voice capture is explicit and visible.

## Release sequence

| Milestone | Scope |
|---|---|
| v0.1 | Foundation, app shells, contracts, migrations, build verification |
| v0.2 | Offline voice/text capture, durable Room storage, audio playback, restart recovery |
| v0.3 | Today (three priorities), Inbox, Spaces, tasks, local full-text search |
| v0.4 | Export, encrypted backups, verified restore drills |
| v1.0 | Dependable offline daily organizer on both phones |
| v1.1 | Optional Windows Whisper.cpp/Ollama worker and mobile review |
| v1.2 | Direct two-phone sync and conflict resolution |
| v1.3+ | Management features and integrations as specified in the roadmap |

## Acceptance principle

A milestone is complete only after its documented acceptance criteria, tests, device checks, and handover are verified. Never report a planned capability as implemented or tested.
