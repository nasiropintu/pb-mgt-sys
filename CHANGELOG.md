# Changelog

## Unreleased — v0.1 foundation

- Added Phase 0 requirements, architecture, and decision records.
- Added initial SQLite schema and shared contracts.
- Added Android and Windows shell scaffolds.
- Added creator attribution and collaboration credits.
- Renamed the application display name to P&B Management Sys and applied a professional navy/blue theme.
- Matched the About & Credits panel to the supplied reference layout and added the supplied creator portrait.
- Packaged the Windows desktop shell as P&B Management Sys.
- Android build and target-device installation remain pending.

## v0.2 — Local Capture Core

- Added Room-backed local Inbox storage for text and voice captures.
- Added explicit microphone permission and foreground voice recording to app-private persistent files.
- Added audio playback and startup recovery for interrupted recordings.
- Added the v0.2 local capture storage decision record (ADR 0005).
- Configured Gradle 8.14.3 wrapper with JDK 21 compatibility and root `npm run build:android` task.
- Resolved Jetpack Compose, Lifecycle, and Room toolchain dependencies; verified clean build producing `app-debug.apk`.
- Real-device field testing on OPPO A3x and Vivo Y20 verified as ready for physical installation.
