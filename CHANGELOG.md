# Changelog

## v1.0 — Daily-use Foundation

- **Task Reminders & Local Notifications** — AlarmManager-powered exact alarms trigger system notifications at the task's due time, even after device reboot (`BOOT_COMPLETED` reschedules all pending reminders via `TaskReminderReceiver`).
- **Due Date Picker** — New inline `DueDatePickerRow` in task creation and editing dialogs with quick-select chips: None, Today 6 PM, Tomorrow 9 AM, or Custom (date + time pickers). Reminders are automatically scheduled, rescheduled, or cancelled on save/delete/completion.
- **Overdue task badge** — Task cards show a ⏰ date badge; badge turns red when a task is past its due date and not yet completed.
- **Notification permission** — Android 13+ POST_NOTIFICATIONS permission requested at startup; SCHEDULE_EXACT_ALARM permission declared for precise alarm delivery.
- **Task cancel-on-complete** — Completing or deleting a task automatically cancels its pending reminder alarm.
- Bump version to `1.0.0` (versionCode 3).

## v0.4 — Data Safety & Recovery

- Added persistent Settings tab (⚙️) with client-side Encrypted Backup & Restore interface.
- Implemented `BackupManager` with AES-256-GCM encryption and PBKDF2-SHA-256 key derivation (100,000 iterations, 16-byte random salt, 12-byte IV).
- Added cryptographic SHA-256 payload integrity check to verify backup authenticity before database restoration.
- Integrated Android Storage Access Framework (SAF) document pickers for exporting `.bin` / `.pblos` backup files and restoring from device storage.
- Added full database serialization and transactional restore across devices, spaces, tasks, inbox notes, and attachments.
- Added interactive task detail & editing dialog (`EditTaskDialog`): tap any task across Today, Tasks, or Search to modify title, description, space assignment, priority (Normal/High/Urgent), Top 3 Focus promotion, or delete.
- Added `updateTaskDetails` and soft `deleteTask` methods to `CaptureRepository` and `CaptureDao`.
- Resolved Jetpack Compose `AlertDialog` imports, updated Gradle packaging configuration, and streamlined Room database singleton management.

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
