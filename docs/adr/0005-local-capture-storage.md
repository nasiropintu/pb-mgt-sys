# ADR 0005: Local capture storage

- Status: Accepted for v0.2 implementation
- Date: 2026-11-18

## Decision

Store structured captures in Room/SQLite and voice files in the app's persistent internal `filesDir/recordings` directory. Create an Inbox row and attachment reference before starting the microphone. A successful stop finalizes duration, byte size and SHA-256 metadata. Text notes are committed directly to the local Inbox.

If a voice row is still marked `processing` on the next launch, retain the referenced file, mark the capture incomplete, and show it to the user. Never silently discard an interrupted recording or use cache storage for original captures.

Voice recording is explicit and foreground-only. The app finalizes the recording when it leaves the foreground; if the process is terminated before finalization, startup recovery preserves the attempt and any available partial file.

## Consequences

- Room entities map to the existing `devices`, `inbox_items`, and `attachments` schema.
- Microphone access is requested only when the user starts a recording.
- Private app storage avoids broad storage permissions; uninstalling the app removes its private files, so export/backup remains a later milestone.
- Real-device verification must confirm microphone behavior, playback, interruption recovery, storage exhaustion handling, and Bengali text entry on both phones.
