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

## Unreleased — v0.3 daily organizer

- Added 4-tab mobile navigation: Today, Inbox, Tasks, Spaces.
- Added Today view featuring date display, local offline status badge, user-curated Top 3 Focus Priorities, recent capture continue card, and pending tasks.
- Added Task management engine with Room entities, DAOs, task creation dialog, completion toggling, and Space assignment.
- Added default seed Spaces: Shop, Personal, App Development with isolated task filtering.
- Added instant offline search across task titles, task descriptions, and inbox notes.
- Bumped Room schema to version 2 with destructive migration fallback for dev installations.

## v0.2 — Local Capture Core

- Added Room-backed local Inbox storage for text and voice captures.
- Added explicit microphone permission and foreground voice recording to app-private persistent files.
- Added audio playback and startup recovery for interrupted recordings.
- Added the v0.2 local capture storage decision record (ADR 0005).
- Configured Gradle 8.14.3 wrapper with JDK 21 compatibility and root `npm run build:android` task.
- Resolved Jetpack Compose, Lifecycle, and Room toolchain dependencies; verified clean build producing `app-debug.apk`.
- Realigned project and application branding to **P&B MGT Sys** (Personal & Business Life Management System) per Brand Guide v1.0.
- Elevated Voice Capture to primary hero card on Android mobile capture screen.
- Verified offline capture, voice playback, and recovery on physical OPPO A3x device.
