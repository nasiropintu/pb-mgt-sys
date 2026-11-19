# Personal Business & Life OS
## Complete Project Details and Development Specification — Version 1.0

**Document status:** Consolidated master baseline  
**Version:** 1.0  
**Date:** 18 November 2026  
**Purpose:** Single authoritative product, UX, architecture, security, and development reference for the Personal Business & Life OS (PBLOS).

---

## Document authority and how to use this specification

This document consolidates the original technical proposal with the Product and UX Refinement v2.0 and the subsequent agreed recommendations. It is intended to replace the two separate documents as the working project baseline.

- The product vision, device constraints, technical architecture, data model, AI pipeline, integrations, security, testing, risks, and governance retain the substance of the original technical proposal.
- The mobile information architecture, screen behavior, capture experience, AI review behavior, onboarding, and release priorities are updated to the refined UX direction.
- Where earlier recommendations conflict, this v1.0 master specification takes precedence.
- This is a project specification, not evidence that any feature has already been implemented or tested. Acceptance criteria must be verified on the actual devices.

### Core product promise

> **Say it, save it, find it, act on it.**

### Three product principles

1. **Capture without friction.**
2. **Never lose important information.**
3. **Always show the next useful action.**

---

# 1. Executive Summary

Personal Business & Life OS (PBLOS) is a zero-mandatory-cost,
Android-first personal organization system designed to help one person
manage daily life, shop operations, finances, learning, software
development projects and future applications from one connected
information system.

The system is designed around the user's actual devices:

  -----------------------------------------------------------------------
  Device                  Hardware                Primary role
  ----------------------- ----------------------- -----------------------
  OPPO A3x                4 GB RAM                Primary daily capture
                                                  and organizer

  Vivo Y20                4 GB RAM                Secondary capture,
                                                  access and synchronized
                                                  copy

  Windows 11 PC           AMD Ryzen 5 5600G, 16   Optional AI processing,
                          GB RAM, integrated      desktop management,
                          graphics, 500 GB NVMe + development and backup
                          500 GB SATA SSD         
  -----------------------------------------------------------------------

The PC is not always available. Therefore, the product must remain
useful when the PC is switched off and when internet access is
unavailable.

The core operating principle is:

> Capture immediately. Save locally. Organize what can be organized.
> Queue heavier AI work. Synchronize when devices meet. Never lose the
> original.

The Android phones handle voice recording, camera capture, sharing from
other applications, local tasks, reminders and basic rules-based
organization. When the Windows PC is available, it processes queued
recordings using local speech recognition and a compact language model.
The phones and PC exchange data directly over a local network; a paid
cloud service or permanent server is not required.

The product will also provide a shared integration interface so
applications such as Nuro Photo Studio and future software projects can
connect to common projects, tasks, notes, decisions and development
records.

## 1.1 Project objectives

1.  Capture voice, photos, documents, links and text quickly from either
    Android phone.
2.  Preserve captured information immediately, without waiting for AI or
    internet access.
3.  Support Bengali, English and mixed Bengali-English speech.
4.  Convert captured information into proposed tasks, reminders, ideas,
    notes and financial records.
5.  Connect extracted information to the correct shop, personal area or
    software project.
6.  Manage tasks, projects, shop activity and basic finances in one
    place.
7.  Keep both phones synchronized without a paid cloud service.
8.  Use the PC for heavier AI processing only when it is available.
9.  Connect existing and future applications through a documented
    integration API.
10. Provide backup, export, recovery, privacy and data ownership
    controls.

## 1.2 Non-goals for the first release

The first release will not attempt to provide:

-   Full accounting, tax filing, payroll or statutory reporting.
-   Automatic bank access or money transfers.
-   A multi-user business ERP or full CRM.
-   Unrestricted autonomous agents.
-   Always-on cloud infrastructure.
-   Guaranteed real-time background AI processing on Android.
-   Large language models running continuously on either 4 GB phone.
-   A full replacement for specialist photo-editing or accounting
    applications.

------------------------------------------------------------------------

# 2. Product Vision and Principles

## 2.1 Vision

Create one dependable personal information system that remembers what
the user says, sees and shares; understands it when resources are
available; organizes it into useful records; and helps the user act at
the right time.

## 2.2 Product principles

### Capture first

Every capture is written to durable local storage before transcription,
classification, upload or synchronization begins.

### Offline-first

Core capture, viewing, task management and local reminders work without
internet.

### PC-optional

The system does not depend on the Windows PC being switched on. PC-only
AI work waits in a persistent queue.

### AI-assisted, not AI-dependent

Rules and ordinary application logic handle reliable operations. AI
interprets ambiguous content and proposes structured records.

### Human control

Financial records, ambiguous deadlines, deletion and consequential
external actions require appropriate confirmation.

### Preserve provenance

Every generated record can be traced back to the original recording,
image, document or text capture.

### One source of truth

Shared records use stable IDs and a common schema across devices and
connected applications.

### No silent data loss

Interrupted work, synchronization conflicts and invalid AI output must
be recoverable and visible.

### No mandatory subscriptions

The core product must not require paid APIs, hosting, storage or
automation subscriptions.

### Simple user experience

The user should be able to speak naturally without first choosing a
category, project or form.

------------------------------------------------------------------------

# 3. Users, Devices and Operating Environment

## 3.1 Primary user

The initial product is designed for one owner/operator who manages:

-   A retail/service shop.
-   Personal responsibilities and routines.
-   Business income, purchases and expenses.
-   Software development projects.
-   Learning and technology goals.
-   Family and personal commitments.

The system should support a busy day with frequent context changes and
limited time for manual data entry.

## 3.2 Device roles

### OPPO A3x --- primary daily device

Responsibilities:

-   Voice capture.
-   Camera and document capture.
-   Android share-intent reception.
-   Local inbox and primary daily dashboard.
-   Tasks, notes, projects and reminders.
-   Local notifications.
-   Basic rules-based classification.
-   Local database and pending synchronization queue.

The app must be conservative with RAM, CPU, battery and storage. It
should not require a large local language model.

### Vivo Y20 --- secondary device

Responsibilities:

-   Capture voice, photos, text and shared files.
-   View and update tasks and notes.
-   Receive reminders.
-   Maintain a synchronized copy of records and selected attachments.
-   Work offline and synchronize later.

The Vivo must not be treated as a passive screen. It should be a
functional peer with its own local database.

### Windows 11 PC --- optional AI workstation

Known hardware:

-   AMD Ryzen 5 5600G CPU.
-   16 GB RAM.
-   Integrated graphics.
-   500 GB M.2 NVMe SSD.
-   500 GB SATA SSD.

Responsibilities:

-   Local speech transcription.
-   Compact local language model inference.
-   Batch classification and extraction.
-   Desktop management interface.
-   Development and project administration.
-   Synchronization with both phones.
-   Backups and exports.
-   Integration services for applications running on the PC.

The PC is not a permanent server. Its services should start when the
user logs in, process pending jobs, and stop safely when Windows shuts
down.

## 3.3 Hardware constraints

-   Both phones have 4 GB RAM. Avoid large on-device AI models.
-   Phone background processing is subject to Android battery and
    process-management policies.
-   The Ryzen 5 5600G has no dedicated GPU in the supplied
    configuration. Prefer CPU-friendly quantized models.
-   16 GB RAM is sufficient to evaluate small models, but multiple
    models should not be run concurrently.
-   Model speed and Bengali quality must be measured on real recordings
    before final selection.
-   No hardware purchase is required for the initial development phases.

------------------------------------------------------------------------

# 4. Functional Scope

## 4.1 Universal capture

The capture system is the entry point for the entire product.

Supported capture types:

1.  Voice recordings.
2.  Camera photos.
3.  Receipt and document images.
4.  Typed text.
5.  URLs and shared text.
6.  Shared images and files.
7.  Audio files received from other applications.
8.  Windows file imports and drag-and-drop.
9.  Optional desktop quick-capture shortcut.

Every capture should create an inbox item with:

-   Unique ID.
-   Capture timestamp.
-   Source device.
-   Source type.
-   Original file or content reference.
-   MIME type and checksum where applicable.
-   Processing status.
-   Related records.
-   Sync status.
-   Retention and privacy metadata.

The capture flow must not require a category selection.

## 4.2 Voice capture

Requirements:

-   Start and stop recording explicitly.
-   Display recording duration and state.
-   Save the audio file locally as soon as recording ends.
-   Allow recording cancellation with clear confirmation.
-   Recover or mark incomplete recordings after interruption.
-   Support Bengali, English and mixed speech.
-   Allow transcript editing.
-   Keep the original recording linked to its transcript and extracted
    records.
-   Allow reprocessing without creating duplicate records.
-   Provide a visible processing queue.

The product must not claim that it can continuously listen in the
background. Voice capture is user-initiated and clearly indicated.

## 4.3 Camera and document capture

Requirements:

-   Capture images using Android CameraX.
-   Import images from the gallery or share sheet.
-   Store original images.
-   Support multiple pages or images for one document.
-   Provide image rotation and basic cropping where practical.
-   Run OCR only when a suitable offline engine and language model are
    available.
-   Preserve the original image and OCR result separately.
-   Allow manual correction of extracted text.

Bengali OCR quality must be evaluated independently. Do not assume that
an OCR engine supports Bengali merely because it supports other
languages.

## 4.4 Android sharing

The Android application should register for appropriate share actions,
including:

-   Text.
-   URLs.
-   Single images.
-   Multiple images.
-   PDFs and supported documents.
-   Audio files where the source application permits sharing.

The app must copy received content from temporary content URIs into its
own managed storage before relying on it. It must validate file type,
size and checksum, and handle revoked or inaccessible URIs safely.

## 4.5 Inbox and automatic organization

Inbox states:

-   New.
-   Saved.
-   Waiting for processing.
-   Processing.
-   Needs review.
-   Organized.
-   Failed.
-   Archived.

A single capture may generate multiple proposed records. For example,
one voice note may produce an expense, a shop task and a
software-development idea.

The user can:

-   Approve.
-   Edit.
-   Reject.
-   Merge.
-   Reprocess.
-   Open the original source.

## 4.6 Tasks and reminders

Task fields include:

-   Title.
-   Description.
-   Status.
-   Priority.
-   Due date and time.
-   Estimated duration.
-   Project or area.
-   Source inbox item.
-   Created and updated timestamps.
-   Device and sync metadata.

Suggested lifecycle:

`Captured → Proposed → Approved → Scheduled → In Progress → Waiting/Blocked → Completed → Archived`

Additional terminal states include Cancelled and Dismissed.

Reminder requirements:

-   Local notifications.
-   One-time and recurring reminders.
-   Snooze and reschedule.
-   Missed-reminder handling.
-   Timezone-aware dates.
-   Explicit notification permission status.
-   Clear explanation if Android battery settings may delay work.

The system must not invent exact dates from ambiguous speech. Relative
expressions should be resolved using the capture timestamp and
Asia/Dhaka timezone. If the date remains ambiguous, ask the user.

## 4.7 Projects and development management

A project should contain:

-   Name and objective.
-   Description.
-   Category.
-   Status.
-   Milestones.
-   Tasks and issues.
-   Decisions.
-   Technical documents.
-   AI coding prompts.
-   Acceptance criteria.
-   Test results.
-   Budget and time records where needed.
-   Next action.
-   History and source links.

Project statuses:

-   Planning.
-   Active.
-   Paused.
-   Blocked.
-   Completed.
-   Archived.

Each active project should have a visible next action.

The initial project integration target is Nuro Photo Studio. The
organizer should be able to store and retrieve its decisions, tasks,
bugs, milestones, technical notes and next actions.

## 4.8 Business management

Initial shop features:

-   Daily sales and service revenue.
-   Purchases and expenses.
-   Customer and order notes.
-   Pending deliveries.
-   Outstanding payments.
-   Supplies and stock reminders.
-   Daily and weekly summaries.

Do not attempt full inventory accounting in the first release.

## 4.9 Personal finance

Initial finance features:

-   Accounts or cash locations.
-   Income.
-   Expenses.
-   Transfers.
-   Categories.
-   Budgets.
-   Daily, weekly and monthly summaries.
-   Pending confirmation status.
-   CSV export.

Transfers between the user's own accounts must not count as income or
expense.

Use integer minor units or exact decimal arithmetic for money. Do not
use binary floating-point values for financial amounts. Default currency
is BDT.

AI-extracted financial entries remain pending until confirmed. The
system must distinguish actual recorded transactions from estimates and
suggestions.

## 4.10 Knowledge library

Store:

-   Notes.
-   Ideas.
-   Decisions.
-   Research.
-   Technical documentation.
-   Tutorials.
-   Customer procedures.
-   Business plans.
-   Project references.

The assistant should answer questions using retrieved records and
provide links to the source records. It must not invent a remembered
decision when no matching source exists.

## 4.11 Dashboard

The home screen should answer:

> What should I do now?

Show:

-   Three recommended priorities.
-   Today's tasks.
-   Upcoming reminders.
-   Waiting and blocked items.
-   Recent captures.
-   Quick capture controls.
-   Shop snapshot.
-   Active project next actions.

Recommendations are advisory. The user can override them.

------------------------------------------------------------------------

## 4.12 Additional daily-management capabilities

The following capabilities are part of the product direction, but their implementation should follow the release priorities in Section 13.

### Waiting For
Track items where the next action belongs to another person, organization, or system. Include description, responsible party/system, optional follow-up date, related Space/project, status, and source link. Do not invent dates.

### Recurring tasks and routines
Support daily, weekly, monthly, and configurable recurrence. Separate recurrence definitions from individual occurrences. Preserve completed history and prevent duplicate occurrences during retries or synchronization.

### Universal search
Search captures, transcripts, tasks, projects, Spaces, notes, decisions, and permitted business records. Start with local full-text and metadata search. Add semantic search only after the basic index is reliable.

### Daily and weekly reviews
Provide optional morning orientation, evening closing review, and weekly review. Reviews should summarize existing records and offer actions; they must not silently reschedule deadlines or rewrite priorities.

### Quick capture entry points
After the main capture flow is stable, provide a home-screen widget, Quick Settings tile, app shortcuts, and share-sheet entry points. All entry points must use the same canonical capture pipeline and local persistence rules.


# 5. Technical Architecture

## 5.1 Architecture overview

Use a local-first peer architecture with three clients:

1.  Android application on OPPO.
2.  Android application on Vivo.
3.  Windows desktop application.

Each device has a local database. Devices exchange changes directly when
reachable. The PC runs optional AI workers and a local integration API
while it is on.

There is no mandatory central cloud database.

## 5.2 High-level data flow

``` text
OPPO A3x                         Vivo Y20
  |                                  |
  | Capture                          | Capture
  v                                  v
Local inbox + SQLite             Local inbox + SQLite
  |                                  |
  +---------- Local Wi-Fi ------------+
                  |
                  v
         Device synchronization
                  |
                  v
         Windows PC (when on)
         Local database replica
                  |
          +-------+-------+
          |               |
          v               v
      Whisper.cpp       Ollama
      Transcription     Classification
          |               |
          +-------+-------+
                  |
                  v
         Validated suggestions
                  |
                  v
         Synchronize to phones
```

## 5.3 Recommended architecture style

Use a modular monolith with well-defined modules, not microservices.

Reasons:

-   One primary user.
-   Limited hardware.
-   No permanent server.
-   Easier local deployment and debugging.
-   Less maintenance.
-   Shared data contracts across clients.

The internal design should still separate capture, transcription,
classification, tasks, finance, projects, sync and integrations.

## 5.4 Android stack

Recommended:

-   Kotlin.
-   Jetpack Compose.
-   Room over SQLite.
-   WorkManager.
-   CameraX.
-   Android notification APIs.
-   Android Keystore.
-   MediaRecorder or AudioRecord.
-   Android share intents.
-   DataStore for non-sensitive preferences.

Android should own immediate capture and local user interactions. It
should not be responsible for large-model inference in the first
release.

## 5.5 Windows stack

Recommended:

-   Electron.
-   React.
-   TypeScript.
-   SQLite.
-   Node.js background worker.
-   Whisper.cpp.
-   Ollama.
-   Local HTTP API bound to localhost or a trusted local-network
    interface only when required.

The Windows worker should:

1.  Start at user login if enabled.
2.  Check available storage and model availability.
3.  Discover or accept synchronized pending jobs.
4.  Process jobs sequentially.
5.  Validate model output.
6.  Store results.
7.  Synchronize results to phones.
8.  Log failures and retry safely.
9.  Stop cleanly on shutdown.

Do not expose the local API to the public internet.

## 5.6 Database strategy

Use SQLite on every device. Use the same logical schema and migrations,
with platform-specific storage paths.

Each device has:

-   A device ID.
-   Its own local database.
-   A change log.
-   An outgoing synchronization queue.
-   An incoming synchronization cursor.
-   A local attachment store.

A record may be created or edited offline. Synchronization transfers
changes rather than copying the entire database each time.

## 5.7 Synchronization strategy

Use:

-   UUIDs for record IDs.
-   Device IDs for origin.
-   Monotonic local versions or change sequence numbers.
-   Idempotency keys.
-   Change log.
-   Tombstones for deletions.
-   Attachment checksums.
-   Acknowledgements.
-   Conflict records.
-   Retry with backoff.

Do not use blind last-write-wins for all data.

Conflict rules:

-   Different fields changed independently: merge when safe.
-   Same field changed on two devices: preserve both versions and ask
    for resolution.
-   Financial records: do not silently merge amounts or dates.
-   Deleted records: use tombstones so they do not reappear
    unexpectedly.
-   AI processing: use idempotency keys to prevent duplicate derived
    records.

### Network discovery

Initial implementation can use QR-code pairing and manual local-network
connection. Automatic discovery can be added using local network
discovery mechanisms, with a manual fallback.

Devices should authenticate one another using paired device credentials.
Local Wi-Fi does not automatically mean trusted.

### No remote synchronization while devices are separated

Without a cloud relay or an always-on device, two phones cannot exchange
new changes while they are in different locations and neither can reach
the other. They synchronize when they share a reachable network. This is
an intentional cost and availability trade-off.

## 5.8 AI provider abstraction

Define interfaces so that providers can be replaced.

Example conceptual interface:

``` ts
interface TranscriptionProvider {
  transcribe(input: AudioInput): Promise<TranscriptResult>;
}

interface LanguageModelProvider {
  classify(input: ClassificationInput): Promise<ClassificationResult>;
  extract(input: ExtractionInput): Promise<ExtractionResult>;
  summarize(input: SummaryInput): Promise<SummaryResult>;
}
```

Initial providers:

-   Whisper.cpp for transcription on PC.
-   Ollama for local language-model processing on PC.
-   Deterministic rules on Android and Windows for simple extraction.

Cloud providers are not part of the required baseline. If introduced
later, they must be optional, disabled by default and separately
approved.

------------------------------------------------------------------------

# 6. Voice Processing and AI Design

## 6.1 Processing pipeline

1.  Capture voice on Android.
2.  Save the original recording locally.
3.  Create an inbox record and persistent job.
4.  Synchronize the audio to the PC when available.
5.  Validate audio format, duration and file integrity.
6.  Transcribe with Whisper.cpp.
7.  Save transcript separately from original audio.
8.  Detect language and mixed-language segments where possible.
9.  Split the transcript into topics.
10. Extract entities, amounts, dates, tasks, ideas and project
    references.
11. Apply deterministic validation and duplicate checks.
12. Match records to existing projects or areas.
13. Assign confidence and risk labels.
14. Create proposed records.
15. Synchronize proposals to the phones.
16. Notify the user when review is needed.

## 6.2 Model constraints

Start with Whisper.cpp multilingual Base and Small model benchmarking.
Do not assume the larger model is automatically best for the user's
environment.

Start with a quantized 3B-class instruct model in Ollama for
classification. Test a larger model only if the smaller one fails
meaningful evaluation cases and memory permits.

Run transcription and language-model processing sequentially to avoid
unnecessary memory pressure.

Do not load AI models on both phones in the first release.

## 6.3 Classification categories

Initial record types:

-   Task.
-   Reminder.
-   Expense.
-   Income.
-   Transfer.
-   Idea.
-   Project update.
-   Bug or issue.
-   Note.
-   Customer or order note.
-   Knowledge resource.
-   Event or appointment.
-   Unknown / needs review.

Each extracted item should include:

-   Type.
-   Title or summary.
-   Structured fields.
-   Source segment or timestamp if available.
-   Suggested project or area.
-   Confidence.
-   Risk level.
-   Validation warnings.
-   Proposed action.

## 6.4 Structured output

AI must return data matching a versioned JSON schema. Model output is
untrusted until validated.

Example:

``` json
{
  "schema_version": "1.0",
  "source_id": "uuid",
  "items": [
    {
      "type": "expense",
      "title": "Photo paper purchase",
      "amount_minor": 50000,
      "currency": "BDT",
      "category": "shop_supplies",
      "confirmation_required": true,
      "confidence": 0.94
    },
    {
      "type": "task",
      "title": "Buy printer ink",
      "due_date": null,
      "date_expression": "tomorrow",
      "confidence": 0.91
    }
  ]
}
```

This example is illustrative. The actual schema must define nullability,
allowed categories, exact date representation, currency rules,
confidence range, source spans and validation errors.

## 6.5 Confidence and review policy

Initial policy, to be calibrated using real recordings:

-   High confidence: allow low-risk organization automatically.
-   Medium confidence: create a suggestion with review indicator.
-   Low confidence: leave in review inbox.
-   Financial records: require confirmation regardless of confidence.
-   Ambiguous dates: ask rather than invent.
-   Destructive actions and external communications: require explicit
    approval.

Confidence scores are not calibrated probabilities until tested. They
must not be treated as proof of correctness.

## 6.6 Bengali and mixed-language evaluation

Build a private evaluation dataset with consent and no unnecessary
personal details. Include:

-   Standard Bengali.
-   Regional Bangladeshi accents.
-   Bengali-English code-switching.
-   Shop vocabulary.
-   Product names.
-   Software-development terms.
-   Currency amounts.
-   Dates and relative dates.
-   Background shop noise.
-   Short and long recordings.
-   Different speakers and speaking speeds.

Measure:

-   Word error rate and character error rate.
-   Amount extraction accuracy.
-   Date extraction accuracy.
-   Topic segmentation accuracy.
-   Task extraction precision and recall.
-   Project matching accuracy.
-   Duplicate detection.
-   Human correction rate.

Do not promise perfect transcription. Provide transcript editing and
reprocessing.

------------------------------------------------------------------------

# 7. Data Model

## 7.1 Core entities

  Entity             Purpose
  ------------------ -----------------------------------------
  users              Local owner profile and preferences
  devices            Paired device identity and status
  inbox_items        Universal capture record
  attachments        Audio, image and document files
  transcripts        Transcription results and versions
  processing_jobs    Persistent work queue
  tasks              Actions and deadlines
  reminders          Notification schedules
  projects           Personal and development projects
  milestones         Project checkpoints
  notes              General notes and knowledge
  ideas              Uncommitted proposals
  accounts           Cash and financial accounts
  transactions       Income, expenses and transfers
  budgets            Spending targets
  customers          Basic shop customer records
  orders             Basic order and delivery records
  calendar_events    Appointments and scheduled events
  time_entries       Optional time tracking
  ai_suggestions     Proposed extracted actions
  feedback           Corrections and classification feedback
  sync_changes       Change log for replication
  sync_conflicts     Unresolved concurrent changes
  audit_logs         Sensitive action history
  app_integrations   Registered connected applications

## 7.2 Provenance

Every derived record should reference its source inbox item. Where
possible, it should also store the transcript segment or image region
that supports the extraction.

This makes it possible to answer:

-   Which recording created this task?
-   Why was this expense categorized as shop supplies?
-   Which source suggested this project idea?
-   Was this information entered manually or generated by AI?

## 7.3 Financial data rules

-   Default currency: BDT.
-   Store money using integer minor units or exact decimal
    representation.
-   Never use floating-point arithmetic for accounting values.
-   Keep transaction date separate from creation timestamp.
-   Transfers are not income or expenses.
-   AI-extracted transactions remain pending until confirmed.
-   Keep source and confirmation status.
-   Export transactions to CSV.

## 7.4 Time and locale

-   Default timezone: `Asia/Dhaka`.
-   Store timestamps in UTC internally where appropriate.
-   Preserve local date semantics for reminders and financial dates.
-   Store the original spoken date expression when one exists.
-   Do not silently convert an uncertain date into a definite deadline.
-   Bengali and English text must be stored as Unicode without lossy
    conversion.

------------------------------------------------------------------------

# 8. Automation Engine

## 8.1 Design

Use an internal event-driven workflow engine. It should be part of the
application, not dependent on n8n or an external automation
subscription.

Example events:

-   `CAPTURE_CREATED`
-   `AUDIO_SAVED`
-   `TRANSCRIPTION_REQUESTED`
-   `TRANSCRIPTION_COMPLETED`
-   `CLASSIFICATION_REQUESTED`
-   `CLASSIFICATION_COMPLETED`
-   `TASK_CREATED`
-   `TASK_DUE_SOON`
-   `TRANSACTION_PENDING`
-   `DEVICE_PAIRED`
-   `SYNC_STARTED`
-   `SYNC_COMPLETED`
-   `SYNC_CONFLICT_DETECTED`

## 8.2 Persistent jobs

Each job should have:

-   Job ID.
-   Job type.
-   Source ID.
-   Status.
-   Attempt count.
-   Next eligible run time.
-   Created and updated timestamps.
-   Last error.
-   Idempotency key.
-   Device or worker assignment.

Job states:

`Pending → Running → Completed`

Alternative states:

`Retrying`, `WaitingForPC`, `NeedsReview`, `Failed`, `Cancelled`.

Jobs must be safe to retry. A retry must not create duplicate expenses,
tasks or attachments.

## 8.3 Scheduling

Android WorkManager handles eligible background tasks such as sync and
local maintenance. Exact execution time is not guaranteed by Android.

Use notification scheduling for reminders, and handle notification
permission requirements.

The PC worker handles heavy jobs only while the PC is on and the worker
is running.

## 8.4 Automation approval matrix

  -----------------------------------------------------------------------
  Action                              Policy
  ----------------------------------- -----------------------------------
  Save capture                        Automatic

  Transcribe audio                    Automatic when resources available

  Categorize low-risk note            Automatic or suggestion based on
                                      confidence

  Create ordinary task                Automatic only under configured
                                      policy

  Create expense                      Pending confirmation

  Record income                       Pending confirmation

  Change important deadline           Confirmation

  Delete important records            Confirmation

  Send messages or emails             Explicit approval

  Transfer money                      Never autonomous

  Publish private data                Explicit approval
  -----------------------------------------------------------------------

------------------------------------------------------------------------

# 9. Application Integration Strategy

## 9.1 Shared integration API

Create a versioned API and common data contract for connected
applications.

Initial local API examples:

``` text
POST   /api/v1/inbox
GET    /api/v1/inbox
POST   /api/v1/tasks
GET    /api/v1/tasks
PATCH  /api/v1/tasks/{id}
GET    /api/v1/projects
POST   /api/v1/projects
GET    /api/v1/projects/{id}
POST   /api/v1/integrations/events
GET    /api/v1/search
GET    /api/v1/sync/changes
POST   /api/v1/sync/ack
```

The exact API should be finalized in Phase 0.

## 9.2 Integration requirements

Each integration must:

-   Have an application ID.
-   Request only required permissions.
-   Authenticate requests.
-   Validate payloads.
-   Use idempotency keys.
-   Log important actions.
-   Avoid direct database access.
-   Support API versioning.
-   Be revocable.

## 9.3 Nuro Photo Studio integration

Initial useful events:

-   Project created or updated.
-   Development task created.
-   Bug reported.
-   Milestone completed.
-   Technical decision recorded.
-   Next action changed.

The organizer should be able to retrieve project status, open tasks,
blockers and latest decisions.

The first integration can use a local API and structured JSON events. It
does not require a cloud account.

------------------------------------------------------------------------

# 10. Security, Privacy and Data Ownership

## 10.1 Local data protection

-   Store data in application-private directories.
-   Evaluate SQLCipher or another supported encryption solution for
    sensitive SQLite data.
-   Use Android Keystore for device secrets.
-   Use Windows-protected credential storage for desktop secrets.
-   Do not store API tokens in source code.
-   Encrypt exported backups.
-   Protect paired-device credentials.
-   Use authenticated local connections.

## 10.2 Network protection

-   No public internet exposure of the local PC API.
-   Pair devices explicitly.
-   Authenticate every paired device.
-   Validate all incoming payloads.
-   Use encryption for device-to-device communication.
-   Reject unpaired devices.
-   Provide a device-revocation mechanism.

## 10.3 AI privacy

Default policy:

-   Local processing only.
-   No automatic cloud upload.
-   No audio upload to third parties.
-   No finance upload.
-   No external model calls without explicit opt-in.

## 10.4 User control

The product must support:

-   Export all data.
-   Export selected records.
-   Backup.
-   Restore.
-   Archive.
-   Delete with confirmation.
-   Device unlinking.
-   AI processing controls.
-   Attachment retention settings.
-   Inspection of AI-generated records.

## 10.5 Backup policy

Recommended:

-   Automatic local backup when a device is available.
-   PC backup when the PC is switched on.
-   Optional encrypted backup to removable storage.
-   Retain multiple backup generations.
-   Verify backup integrity.
-   Test restoration regularly.

A second phone is not a complete backup by itself. Deletion or
corruption may synchronize. Keep independent backup generations.

------------------------------------------------------------------------


# 11. User Experience and Product Interaction Specification

## 11.1 Product direction

The product must feel like a calm personal command center, not a miniature ERP or a collection of forms. The primary product risk is asking one person to learn and maintain too many sections before the system has earned a place in their daily routine.

The product should allow the user to capture a thought without deciding its category, trust that it has been saved, and later see either the organized result or a clear next step.

### Product rules

1. Capture is the front door. Every other function should be reachable from a captured item or a clear daily need.
2. Keep one primary action per screen and avoid competing controls.
3. AI must never be a prerequisite for saving or acting.
4. Organize quietly; interrupt only when a human decision materially matters.
5. Show the original source and why an AI suggestion was made.
6. Show system state in plain language, such as “Saved on this phone; waiting to sync.”
7. Keep advanced finance, integration, and AI controls out of the everyday path until needed.
8. Use Undo for reversible automatic filing and task completion where practical.
9. Do not use streaks, guilt language, or urgency styling to pressure routine productivity.

## 11.2 Mobile information architecture

Replace the earlier ten-destination mobile navigation with three primary destinations and one persistent capture action:

- **Today** — the day's focus, next appointment, and a short list of due or chosen tasks.
- **Inbox** — recent captures, processing state, and the small number of items requiring a decision.
- **Spaces** — related work and life contexts, such as Shop, Personal, and App Development.
- **Capture** — a persistent, visually distinct action for voice, text, photo, and import.

Search must be available from the main screens and search across captures, tasks, projects, notes, and other permitted records.

Finance belongs inside the relevant Space (for example, Shop or Personal) and may be protected by a privacy setting. It should not be a permanent top-level mobile destination in the first release.

Settings, reports, archive, device management, integrations, and other secondary tools belong behind a profile or “More” area.

On Windows, begin with a compact processing and synchronization status utility. Do not build a second full-featured desktop organizer until real desktop workflows justify it.

## 11.3 Today screen

The Today screen must answer:

> **What is the best next use of my attention?**

Recommended order:

1. Compact greeting, date, and sync status.
2. **Focus for today:** up to three user-selected priorities. If none were selected, suggest up to three and explain why.
3. The next time-specific commitment, if one exists.
4. A concise Continue card for an in-progress task or project.
5. A short Due / Waiting section.
6. A discreet route to recent captures and a compact Shop snapshot.

Do not fill the first screen with charts, every active project, every reminder, or an AI-generated daily essay. The user should understand the day at a glance. Sections can expand for more detail.

The AI must not imply that its suggested priorities are authoritative. User-selected focus always takes precedence.

## 11.4 Inbox and review states

Use one chronological feed with clear, human-readable states:

- **Saved**
- **Organizing**
- **Ready**
- **Needs your choice**
- **Couldn’t process**

Keep the original capture visible and make derived tasks, notes, or records visually distinct from it.

Most items should not demand review. When a decision is needed, explain it in one sentence and offer a direct action. Example: “This sounds like a task for Nuro Photo Studio. Add it there?” Actions may include **Add**, **Choose another**, and **Keep in Inbox**.

Do not require a long form to correct one field. Use inline editing for simple corrections.

Provide Undo after automatic filing. Corrections may improve future suggestions where practical, but the system must never silently change explicit user preferences.

A processing failure must never make the original capture disappear. Keep it available with a retry option and a manual path to create a task or note.

## 11.5 Spaces and projects

A Space is a view of related records, not a separate data silo. Initial editable Spaces may include:

- Shop
- Personal
- App Development

Users may add, rename, archive, or reorder Spaces.

A project page should show:
- Goal and current status.
- Next action.
- Open and waiting tasks.
- Milestones.
- Recent decisions.
- Source-linked notes and captures.

Avoid asking users to maintain the same information in multiple places. Records should have a single canonical identity and may appear in multiple relevant views through relationships or tags.

## 11.6 Premium capture experience

Capture is the most important product workflow and deserves more polish than analytics or reporting screens.

### Voice capture

1. One tap opens a focused recorder.
2. Recording state is unmistakable.
3. Show a large waveform or simple level animation, elapsed time, and explicit Pause, Finish, and Cancel controls.
4. On Finish, save the original audio locally before transcription or network activity.
5. Confirm with a clear message such as **“Saved on this phone.”**
6. Return to the previous screen or show a compact capture receipt. Never make the user wait for transcription.
7. If the PC is unavailable, say **“Saved. AI sorting will continue when your PC is available.”**
8. Support playback, transcript correction, retry, and deletion according to retention settings.

### Other capture methods

The same entry point should support:
- Typed text.
- Camera capture and document/receipt capture.
- Android share intents for text, URLs, images, multiple images, PDFs, documents, and supported audio.
- File import.
- Windows drag-and-drop/import in a later desktop workflow.

### Capture receipt

After a capture, show a small, dismissible receipt containing:
- What was saved and when.
- Whether it is local, synced, or waiting.
- What processing may happen next.
- A link to the original.

The receipt should reassure, not become another review form.

### Quick capture entry points

After the primary in-app capture flow is reliable, add:
- Android home-screen widget with quick voice capture.
- Android Quick Settings tile.
- App shortcuts for common capture types.
- Optional lock-screen shortcut where supported.
- Share-to-Inbox from other apps.

These are convenience entry points into the same capture pipeline, not separate storage systems. Platform restrictions and permissions must be explained honestly.

### Permission timing

Request microphone, camera, and notification permissions only when the user first invokes the related feature. Explain why permission is needed in plain language.

Recording must be user-initiated and visibly indicated. Do not implement always-on listening.

## 11.7 Smart filing and AI behavior

Treat classification as **smart filing**, not autonomous management.

Default behavior:

- Notes, ideas, references, and project updates may be filed automatically when the match is clear. Preserve the original and provide Undo.
- Create ordinary tasks automatically only when the spoken intent is clear. Otherwise, offer a one-tap suggestion.
- Match projects and Spaces using known names and recent context. If uncertain, leave the item in Inbox rather than blocking capture.
- Use dates only when the spoken expression is clear in the configured timezone. Preserve the original wording and never invent a deadline.
- Extracted income, expenses, and transfers remain pending until the user confirms them.
- Never send messages, publish information, delete important records, or move money without explicit approval.

Do not display raw numerical confidence scores as though they were calibrated probabilities. Prefer labels such as **Filed automatically**, **Suggested**, or **Needs your choice**. Show relevant evidence, such as the recognized project name or date phrase.

## 11.8 Waiting For

Add a first-class **Waiting For** record type. Not every open item is an action the user can personally complete.

Examples:
- Waiting for a customer to provide a photograph.
- Waiting for a supplier delivery.
- Waiting for a phone call or response.
- Waiting for another person's decision.
- Waiting for an AI job or device synchronization.

Each Waiting For record should support:
- Description of what is awaited.
- Person, organization, or system being waited on, if known.
- Expected follow-up date, if known.
- Related Space, project, customer, or task.
- Status (open, received/completed, cancelled, archived).
- Source capture and audit history.

Show relevant waiting items on Today. Do not invent follow-up dates.

## 11.9 Recurring tasks and routines

Support recurring tasks for daily, weekly, monthly, and configurable recurrence patterns.

Examples:
- Daily shop opening checklist.
- Daily cash balance check.
- Weekly expense review.
- Monthly account review.
- Weekly project review.

A recurring task definition must be separate from its generated occurrences. Completing one occurrence must not rewrite historical occurrences. Store completion time and occurrence identity so retries or synchronization cannot create duplicate occurrences.

Recurring tasks should support pause, resume, edit-future-occurrences, and end recurrence. Changes to a recurrence should not silently rewrite completed history.

## 11.10 Universal search and command entry

Provide a universal search entry point across captures, transcripts, tasks, projects, Spaces, notes, decisions, and authorized business/finance records.

Initial search should use local full-text indexing, metadata, filters, and date ranges. Semantic or natural-language AI search can be added later.

Example queries:
- “That idea I recorded about Nuro last week.”
- “Unpaid customer orders.”
- “Expenses from yesterday.”
- “Tasks related to the shop.”

Search must respect privacy settings and record permissions. Results should link to the original source and canonical record.

A command palette or quick-action interface may later support commands such as “New task”, “Open Shop”, and “Start voice capture”. Do not make an untested AI command interpreter a dependency for core navigation.

## 11.11 Morning and evening rhythm

### Morning orientation

The user should be able to understand the day in less than a minute:
- Review up to three focus priorities.
- See the next time-specific commitment.
- See urgent due and waiting items.
- Start the next useful action.

### Evening closing review

Offer an optional short closing routine:
1. What was completed?
2. What remains unfinished?
3. What am I waiting for?
4. What should move to tomorrow?

Allow the user to skip or snooze. Do not automatically reschedule deadlines or alter priorities without approval.

### Weekly review

Provide one guided review of:
- Inbox items needing attention.
- Overdue tasks.
- Waiting items.
- Project next actions.
- Optional Shop/business snapshot.

Allow the user to snooze or skip without losing data.

## 11.12 Notifications and reminders

Notifications should be quiet by default. Notify for:
- A user-requested reminder.
- An important processing failure.
- A small daily or weekly digest if enabled.

Do not notify for every successfully filed note. Let the user configure quiet hours and notification categories.

Android background execution is not guaranteed to happen at an exact time. Use persistent jobs and notifications where appropriate, and explain battery optimization or permission limitations.

## 11.13 Onboarding

Onboarding should take only a few minutes and can be completed later.

1. Choose interface language and confirm timezone.
2. Confirm or rename initial Spaces.
3. Create or select the first active project.
4. Make a test voice capture and show where it appears.
5. Explain that pairing the second phone and PC can happen later, and describe when direct synchronization works.

Do not require the user to define their complete workflow, enter every project, or configure AI policies before the first capture.

## 11.14 Visual design system and accessibility

Aim for **quiet confidence**: warm, focused, tactile, modern, and restrained.

- Warm neutral canvas, high-contrast text, and one recognizable deep-green or teal action color.
- Amber for attention and red for destructive or urgent states.
- Semantic tokens for success, waiting, warning, and error. Never rely on color alone.
- Consistent 8-point spacing scale, generous card padding, and clear grouping.
- Avoid nested cards inside cards and decorative dashboard clutter.
- Legible type scale and comfortable line height.
- Reliable Bengali and English rendering, including mixed-script alignment, wrapping, and font scaling.
- Touch targets of at least 48 dp where practical.
- Short transitions for recording, saving, filing, and completing tasks.
- Respect reduced-motion preferences.
- Support light and dark themes, system font scaling, screen readers, and high-contrast viewing.
- Buttons should describe outcomes: **Save task**, **File under Shop**, **Keep in Inbox**, rather than vague labels such as **OK**.
- Provide useful empty states that teach one next action.

## 11.15 Windows experience

The first Windows release should be a small local worker/status utility, not a duplicate of the mobile organizer. Its responsibilities are:
- Run and manage local transcription and language-model jobs.
- Show queue, model, processing, and error status.
- Pair securely with phones and synchronize when reachable.
- Offer controlled model/download settings and logs.
- Start and stop cleanly without being required for phone operation.

A richer desktop organizer can be considered only after actual desktop workflows justify it.

## 11.16 UX acceptance criteria

A release must pass usability checks on the actual OPPO A3x and Vivo Y20, not merely build successfully.

### Capture and trust
- Start voice capture from the main screen in one tap.
- Save completed capture locally before transcription or network activity.
- Communicate saved, processing, and waiting state within one second where technically measurable.
- Preserve completed captures across app close and restart.
- Never require category selection before saving.

### Organization and retrieval
- Automatically file a clear, low-risk note and allow Undo.
- Leave ambiguous items safely in Inbox.
- Require confirmation for money and uncertain deadlines.
- Find recent captures and tasks through search or Spaces without remembering an exact category.
- Open the source capture from every AI-created item.

### Daily usability
- Today shows the next useful action without requiring navigation through a dense dashboard.
- Core functions remain usable with large text on the smaller 4 GB phones.
- Permissions denied, PC unavailable, storage low, offline, AI failure, and sync conflict each have a clear explanation and recovery action.
- Bengali, English, and mixed-script content remains readable.
- Waiting For, recurring tasks, and evening review do not create unnecessary maintenance overhead.

Before adding more modules, test real situations: a quick shop reminder, an app-development idea, an expense mention, an offline note, a waiting-for-customer item, a recurring shop routine, and an ambiguous voice note. Measure completion time, correction effort, missed captures, and user trust.

---

# 12. Repository and Project Structure

Recommended monorepo:

``` text
personal-business-life-os/
├── apps/
│   ├── android/
│   │   ├── app/
│   │   └── docs/
│   └── desktop/
│       ├── electron/
│       ├── src/
│       └── tests/
├── packages/
│   ├── shared-contracts/
│   ├── shared-types/
│   ├── database-schema/
│   ├── sync-protocol/
│   ├── api-client/
│   └── integration-sdk/
├── server/
│   ├── local-api/
│   └── workers/
├── engines/
│   ├── transcription/
│   ├── classification/
│   ├── automation/
│   └── search/
├── ai/
│   ├── prompts/
│   ├── schemas/
│   ├── evaluation/
│   └── model-config/
├── database/
│   ├── migrations/
│   ├── seeds/
│   └── fixtures/
├── integrations/
│   ├── nuro-photo-studio/
│   └── examples/
├── docs/
│   ├── product/
│   ├── architecture/
│   ├── database/
│   ├── api/
│   ├── privacy/
│   ├── testing/
│   ├── decisions/
│   └── handover/
├── scripts/
├── tests/
│   ├── unit/
│   ├── integration/
│   ├── synchronization/
│   ├── ai-evaluation/
│   └── end-to-end/
├── README.md
├── HANDOVER.md
├── CHANGELOG.md
└── LICENSE
```

Use Gradle for Android and pnpm for the TypeScript desktop/shared
packages. Avoid forcing Kotlin and TypeScript into one build system.

------------------------------------------------------------------------

# 13. Consolidated Development Roadmap and Release Strategy

## 13.1 Roadmap principles

The original ten-phase technical roadmap remains useful as an engineering work breakdown, but the first public/useful release must be smaller. Prioritize a complete daily-use loop over breadth.

The recommended release sequence is:

1. Daily-use foundation.
2. Useful local AI loop.
3. Two-phone synchronization and resilience.
4. Business, finance, integrations, and knowledge capabilities as validated later releases.

Backup and restore must be implemented before synchronization is treated as production-ready. Synchronization is not a backup.

## 13.2 Release 1 — Daily-use foundation

### Scope

- Android local-first voice and text capture.
- Local Inbox and original-audio preservation.
- Basic task creation, editing, and completion.
- Today, Inbox, and Spaces navigation.
- Source links between captures and derived records.
- Clear processing and synchronization states.
- Local search across core records.
- Initial backup and restore, including a verified export path.
- Basic reminders and notification handling.
- A small, usability-tested interface on OPPO A3x, with compatibility checks on Vivo Y20.

### Explicitly defer

- Full finance/accounting.
- Complex inventory and order management.
- Full desktop organizer.
- Advanced semantic search.
- Autonomous financial entry creation.
- Broad integration ecosystem.
- Always-on recording or background listening.

### Acceptance criteria

- Capture works offline.
- Original audio/text is committed locally before AI or network activity.
- App restart does not lose completed captures.
- User can create and manage a basic task.
- User can find a recent capture.
- User can restore a verified backup.
- Core flow works with the PC turned off.

## 13.3 Release 1.1 — Useful AI loop

### Scope

- Windows local worker.
- Whisper.cpp transcription.
- Compact local language model through Ollama or another tested provider.
- Bengali, English, and mixed-language benchmark.
- Transcript, task, idea, and project/Space suggestions.
- Schema validation, provenance, retries, and idempotency.
- Human review for ambiguous results.
- AI job queue visible in plain language.

Financial extraction should be added only after the review and correction experience is dependable.

### Acceptance criteria

- AI unavailability never blocks capture.
- A failed job remains retryable.
- Reprocessing does not create duplicate records.
- Generated records link to the original capture and transcript segment.
- Suggestions are validated before database writes.
- Actual performance is measured on the specified Ryzen 5 5600G / 16 GB PC.

## 13.4 Release 1.2 — Two-phone synchronization and resilience

### Scope

- QR-based pairing.
- Authenticated encrypted local synchronization.
- Change logs, acknowledgements, retries, and idempotency.
- Attachment transfer and checksum verification.
- Conflict detection and conflict resolution workflow.
- PC as optional third peer.
- Device revocation and recovery testing.

### Acceptance criteria

- Phones synchronize directly when they can reach each other on the same local network.
- Independent changes merge safely.
- Same-field conflicts are preserved for resolution rather than silently overwritten.
- Financial records receive special conflict handling.
- Offline changes remain queued and survive app/device restart.
- The UI never labels queued changes as synced.
- A phone remains useful when the other phone and PC are unavailable.

Remote synchronization between separated phones requires an optional relay or another reachable always-available service. That is outside the zero-cost, serverless baseline.

## 13.5 Later releases

Add capabilities only when actual usage demonstrates the need:

- Waiting For and follow-up workflows.
- Recurring tasks and routines.
- Android share-sheet support and advanced import.
- Camera, receipt, document, and OCR capture.
- Shop records, customers, orders, and finance.
- Calendar views and richer planning.
- Nuro Photo Studio integration API and other app integrations.
- Source-grounded knowledge assistant and insights.
- Rich Windows dashboard if desktop workflows justify it.

## 13.6 Engineering phase mapping

The following mapping preserves the original technical phase structure while aligning it to the smaller release strategy.

| Engineering phase | Work | Release alignment |
|---|---|---|
| Phase 0 | Requirements, repository, schemas, migrations, shells, build/test foundation | Foundation |
| Phase 1 | Android capture hub, local inbox, basic tasks, local persistence, backup foundation | Release 1 |
| Phase 2 | Windows AI processing engine, transcription, local model benchmark | Release 1.1 |
| Phase 3 | AI organization, project matching, review, provenance, feedback | Release 1.1 |
| Phase 4 | Pairing, phone-to-phone sync, conflict handling, attachment transfer | Release 1.2 |
| Phase 5 | Tasks, Spaces, planning, Waiting For, recurring routines, daily/weekly review | Release 1 and later refinement |
| Phase 6 | Business and finance | Later release |
| Phase 7 | Shared app integrations and Nuro integration | Later release |
| Phase 8 | Knowledge assistant and insights | Later release |
| Phase 9 | Hardening, security, recovery, performance, packaging, release | Every release gate; final hardening |

## 13.7 Priority order for implementation

1. Make capture instant and dependable.
2. Make Today and Inbox calm and obvious.
3. Make basic tasks and Spaces useful.
4. Implement backup and restore before relying on multiple copies.
5. Make smart filing reversible and source-linked.
6. Prove bilingual AI on the actual PC and phones.
7. Implement honest two-phone synchronization.
8. Add Waiting For and recurring routines.
9. Add business, finance, and integrations one validated slice at a time.

## 13.8 First end-to-end vertical slice

The first complete prototype should demonstrate:

1. Record a Bengali or English voice note on OPPO.
2. Save the original audio and Inbox item locally.
3. Close and reopen the app; verify the capture remains.
4. Synchronize the audio to the PC over the local network when reachable.
5. Transcribe with Whisper.cpp.
6. Use the local language model to propose a task, idea, or note.
7. Validate the structured output.
8. Return the suggestion to the phone.
9. Review and approve it.
10. Open the created record and verify its source link.
11. Turn off the PC and verify the phone remains fully useful.

Do not proceed to broad feature development until this vertical slice is dependable.

---

# 14. Testing Strategy

## 14.1 Unit tests

Test:

-   Schema validation.
-   Money arithmetic.
-   Date parsing.
-   Timezone conversion.
-   Duplicate detection.
-   Task state transitions.
-   Sync version comparison.
-   Idempotency.
-   AI output validation.
-   Permission checks.

## 14.2 Integration tests

Test:

-   Android database and file storage.
-   Voice capture and job creation.
-   PC transcription worker.
-   Ollama structured extraction.
-   Phone-to-phone synchronization.
-   PC-to-phone result synchronization.
-   Attachment transfer.
-   Backup and restore.
-   Application integration API.

## 14.3 Failure and recovery tests

-   Internet disconnects during capture.
-   App closes during recording.
-   Phone restarts with pending jobs.
-   PC is shut down during transcription.
-   AI model is unavailable.
-   Audio file is corrupt.
-   Disk space becomes low.
-   Device disconnects during sync.
-   Same record changes on two devices.
-   Duplicate file is shared twice.
-   Backup is incomplete.
-   Notification permission is denied.

## 14.4 Performance targets

These are initial engineering targets to validate, not guarantees:

-   Saving a completed voice recording should not wait for
    transcription.
-   Normal text capture should feel immediate.
-   The app should remain responsive on 4 GB phones.
-   Sync should transfer only changed data and required attachments.
-   The PC should process queued jobs sequentially without exhausting
    memory.
-   The app should expose queue status instead of appearing frozen.

## 14.5 AI evaluation

Maintain a versioned test set and report:

-   Transcription WER/CER.
-   Amount extraction precision and recall.
-   Date extraction precision and recall.
-   Topic segmentation quality.
-   Task extraction precision and recall.
-   Project matching accuracy.
-   Duplicate prevention rate.
-   Human correction rate.

Never change models or prompts in production without running the
evaluation suite.

------------------------------------------------------------------------

# 15. Privacy, Cost and Licensing Policy

## 15.1 Cost target

**Mandatory software and service cost: ৳0.**

The architecture must not require:

-   Paid AI API.
-   Paid cloud database.
-   Paid cloud hosting.
-   Paid automation service.
-   Paid synchronization relay.
-   Paid subscription for core functionality.

Potential real-world costs such as internet data, electricity, optional
removable storage and hardware maintenance are outside the software-cost
target.

## 15.2 Free software does not mean costless operation

Open-source tools may still require:

-   Storage.
-   CPU and battery.
-   Electricity.
-   Maintenance.
-   Model downloads.
-   Internet access for initial setup.
-   License compliance.

Review licenses for every dependency and model before distribution.
Model weights may have different licenses from their inference software.

## 15.3 Optional future cloud services

A future cloud relay may improve remote synchronization, but it is not
part of the baseline. Any future cloud feature must:

-   Be optional.
-   Have a clear cost estimate.
-   Have a local-only fallback.
-   Explain what data leaves the device.
-   Require explicit opt-in.
-   Never be necessary for basic capture or task management.

------------------------------------------------------------------------

# 16. Risks and Mitigations

  -----------------------------------------------------------------------
  Risk                    Impact                  Mitigation
  ----------------------- ----------------------- -----------------------
  Android background      Delayed processing or   WorkManager, persistent
  restrictions            reminders               queues, notification
                                                  permissions, battery
                                                  guidance

  PC is switched off      AI jobs wait            Save locally and queue
                                                  work

  Bengali transcription   Incorrect records       Real speech evaluation,
  errors                                          editable transcripts,
                                                  source retention

  Small PC RAM            Slow AI inference       Compact quantized
                                                  models, sequential
                                                  workers

  Phone storage fills     Capture failure         Storage warnings,
                                                  retention settings, PC
                                                  transfer

  Sync conflict           Lost or inconsistent    Versioning, conflict
                          changes                 records, explicit
                                                  resolution

  Duplicate AI extraction Duplicate tasks or      Idempotency keys and
                          expenses                source-based
                                                  deduplication

  Device lost             Data loss or exposure   Encryption, independent
                                                  backups, device
                                                  revocation

  Integration bugs        Incorrect external      Versioned API,
                          changes                 permissions, validation
                                                  and audit

  Model licensing changes Distribution            License review and
                          restrictions            model replacement
                                                  capability

  User overtrusts AI      Incorrect decisions     Review policy,
                                                  provenance and explicit
                                                  confirmation
  -----------------------------------------------------------------------

------------------------------------------------------------------------

# 17. Project Governance and AI-Assisted Development

## 17.1 Source of truth

Maintain these files in the repository:

-   `README.md` --- setup and project overview.
-   `HANDOVER.md` --- current state and next action.
-   `CHANGELOG.md` --- notable changes.
-   `docs/product/PRD.md` --- product requirements.
-   `docs/architecture/ARCHITECTURE.md` --- system design.
-   `docs/database/DATA_MODEL.md` --- data model.
-   `docs/api/API_CONTRACT.md` --- integration API.
-   `docs/privacy/PRIVACY.md` --- data and security policy.
-   `docs/testing/TEST_STRATEGY.md` --- test plan.
-   `docs/decisions/` --- architecture decision records.

## 17.2 AI coding workflow

For each implementation task:

1.  Read the relevant specification and current handover.
2.  Inspect the existing code before editing.
3.  Produce a small implementation plan.
4.  Implement one task at a time.
5.  Add or update tests.
6.  Run formatting, static checks, unit tests and build.
7.  Test on actual target devices when applicable.
8.  Report files changed and behavior verified.
9.  Document known limitations.
10. Update `HANDOVER.md` with the next task.

AI coding tools must not invent successful test results. A test is only
marked passed if it was actually executed.

## 17.3 Definition of done

A task is complete only when:

-   Acceptance criteria are met.
-   Relevant tests pass.
-   No unexplained build failure remains.
-   Data migration behavior is checked.
-   Errors and edge cases are handled.
-   Documentation is updated.
-   The handover identifies the next task.

------------------------------------------------------------------------

# 18. Phase 0 Implementation Checklist

The first development package should contain:

-   [ ] Confirm final product name and package identifiers.
-   [ ] Create repository and license.
-   [ ] Add product requirements document.
-   [ ] Add architecture document.
-   [ ] Add architecture decision records.
-   [ ] Create Android Kotlin project.
-   [ ] Create Windows Electron + React + TypeScript project.
-   [ ] Define common UUID and timestamp rules.
-   [ ] Define inbox, attachment, task and project schemas.
-   [ ] Create initial SQLite migrations.
-   [ ] Define device identity and sync protocol draft.
-   [ ] Define AI job and extraction schemas.
-   [ ] Add privacy and security requirements.
-   [ ] Add automated test framework.
-   [ ] Add build scripts and developer documentation.
-   [ ] Verify Android installation on OPPO A3x.
-   [ ] Verify Android installation on Vivo Y20.
-   [ ] Verify Windows build on Ryzen 5 5600G PC.
-   [ ] Create `HANDOVER.md` with Phase 1 next action.

Phase 0 must not prematurely implement the entire AI engine or
synchronization protocol. It should establish a stable foundation and
test the build pipeline.

------------------------------------------------------------------------

# 19. Recommended First End-to-End Prototype

After Phase 0, build this vertical slice before expanding the product:

1.  Open the OPPO app.
2.  Record a short Bengali/English voice note.
3.  Save the audio and inbox item locally.
4.  Confirm the item remains after closing and reopening the app.
5.  Connect the phone and PC to the same local network.
6.  Synchronize the audio to the PC.
7.  Transcribe with Whisper.cpp.
8.  Extract two or three proposed records with Ollama.
9.  Validate the result against the schema.
10. Review the proposals on the phone.
11. Approve one task and one idea.
12. Confirm both records retain their source link.
13. Switch off the PC and confirm the phone remains useful.

This prototype proves the most important architectural assumption before
substantial investment in other modules.

------------------------------------------------------------------------

# 20. Final Recommendation

Build PBLOS as a native Android-first, local-first modular monolith with
a Windows AI workstation and peer-to-peer synchronization.

The recommended priorities are:

**P0 --- Essential** - Reliable capture. - Local storage. - Tasks and
reminders. - Persistent processing queue. - PC transcription and AI
extraction. - Source provenance.

**P1 --- Connected system** - Two-phone synchronization. - Project and
development management. - Basic business and finance. - PC desktop
dashboard.

**P2 --- Expansion** - Nuro Photo Studio integration. - Future
application integrations. - Knowledge assistant. - Reports and planning
intelligence.

**P3 --- Optional advanced features** - Better local OCR. - Voice
responses. - Advanced analytics. - Optional encrypted remote relay if
the user later approves the trade-off and cost.

The project succeeds if the user can capture something on either phone,
trust that it is saved, and later find it organized without needing to
remember where it belongs.

The PC should make the system smarter, not keep it alive.

------------------------------------------------------------------------

# Appendix A --- Initial Technology References

Official documentation and project repositories to review during
implementation:

-   Android Jetpack Compose:
    https://developer.android.com/jetpack/compose
-   Android Room:
    https://developer.android.com/training/data-storage/room
-   Android WorkManager:
    https://developer.android.com/topic/libraries/architecture/workmanager
-   Android CameraX: https://developer.android.com/training/camerax
-   Android receiving shared data:
    https://developer.android.com/develop/ui/views/receive
-   Android Keystore:
    https://developer.android.com/privacy-and-security/keystore
-   SQLite: https://www.sqlite.org/docs.html
-   Electron: https://www.electronjs.org/docs/latest/
-   React: https://react.dev/
-   TypeScript: https://www.typescriptlang.org/docs/
-   Whisper.cpp: https://github.com/ggml-org/whisper.cpp
-   Ollama: https://ollama.com/
-   Ollama documentation: https://docs.ollama.com/
-   JSON Schema: https://json-schema.org/
-   Gradle: https://docs.gradle.org/
-   pnpm: https://pnpm.io/

Check current documentation, project activity, supported platforms and
licenses before selecting exact dependency versions or model weights.

# Appendix B --- Open Decisions for Phase 0

The following details should be confirmed or tested before
implementation choices become irreversible:

1.  Exact OPPO A3x Android version and available storage.
2.  Exact Vivo Y20 Android version and available storage.
3.  Available free storage on each phone.
4.  Whether both phones usually connect to the same Wi-Fi network.
5.  Whether the PC and phones share a local network without client
    isolation.
6.  Preferred backup destination and retention period.
7.  Exact transcription speed and quality on the Ryzen PC.
8.  Best compact Ollama model for the user's Bengali/English recordings.
9.  Bengali OCR engine and tested accuracy.
10. Whether the initial app interface should be Bengali-first,
    English-first or bilingual.

These are implementation validation items, not blockers to starting
Phase 0.


---

# 21. Version 1.0 Decisions and Items to Validate

## 21.1 Decisions established by this baseline

- Android is the primary daily-use platform; OPPO A3x is the primary capture device.
- Vivo Y20 is a fully functional secondary peer, not merely a remote display.
- The Windows 11 PC is an optional AI worker and synchronization peer, not a required server.
- The first mobile navigation has Today, Inbox, and Spaces, plus persistent Capture.
- The core system works without AI, internet, or PC availability.
- Local persistence precedes transcription, synchronization, and AI processing.
- Original captures and provenance are retained according to user-configurable retention rules.
- AI suggestions are validated; ambiguous or consequential actions require human review.
- Money-related records require confirmation.
- Backup and restore precede production reliance on synchronization.
- Direct phone-to-phone synchronization requires network reachability; remote relay is excluded from the zero-cost baseline.
- A compact Windows worker precedes any full desktop organizer.
- The original ten-phase engineering breakdown is retained but implemented through the smaller release sequence in Section 13.

## 21.2 Items to validate during Phase 0 and prototypes

These are implementation choices or test questions, not assumptions that should be silently treated as solved:

- Exact Bengali-capable speech model and model size on the Ryzen 5 5600G.
- Exact compact instruction model and quantization suitable for Bengali/mixed-language extraction.
- Whether SQLCipher or another encryption approach meets performance and maintenance requirements.
- Reliable local network discovery and secure pairing behavior across the two Android devices.
- Android vendor battery-management behavior and notification reliability.
- Backup destination, encryption, retention count, and restore UX.
- Search indexing strategy and Bengali tokenization quality.
- Accessibility and Bengali/English typography on both phones.
- Which optional quick-capture shortcuts are supported by the Android versions and launcher.
- Whether Windows should remain a worker-only utility or later gain a richer dashboard.

## 21.3 Change control

Any change that affects core data ownership, offline behavior, synchronization guarantees, privacy, financial approval, or capture durability must be documented and reviewed before implementation. Feature additions should be justified by a real workflow and should not make the daily capture loop slower.

---

**End of Personal Business & Life OS — Complete Project Details v1.0**
