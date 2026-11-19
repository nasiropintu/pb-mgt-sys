# Personal Business & Life OS
## Version-Based Development Roadmap — v1.0

**Document status:** Development execution roadmap  
**Roadmap version:** 1.0  
**Based on:** Personal Business & Life OS — Complete Project Details v1.0  
**Date:** 18 November 2026  
**Primary platforms:** Android (Kotlin/Jetpack Compose), Windows 11 (optional local AI worker)

---

## 1. Purpose

This roadmap converts the PBLOS master specification into an ordered, version-based development plan. It defines what each version should deliver, how versions depend on one another, and what must be verified before moving forward.

The roadmap uses two levels:

1. **Development milestones (v0.x):** internal prototypes used to build and validate the system incrementally.
2. **Product releases (v1.x):** meaningful releases intended for regular daily use.

A version is not complete because its code has been written. It is complete only after its acceptance criteria, tests, device checks, documentation, and handover are complete.

## 2. Product principles that govern every version

1. Capture without friction.
2. Never lose important information.
3. Always show the next useful action.
4. Save locally before AI processing or synchronization.
5. Core operation must not depend on the PC, internet, or AI.
6. AI suggestions must be explainable, validated, and reversible where practical.
7. Money-related records and consequential actions require explicit approval.
8. Synchronization is not backup.
9. Never claim a feature or test is complete unless it has actually been verified.

## 3. Version map

| Version | Name | Main outcome | Product status |
|---|---|---|---|
| v0.1 | Project Foundation | Repository, architecture, schemas, build pipelines | Internal |
| v0.2 | Local Capture Core | Voice/text capture and durable local Inbox | Internal prototype |
| v0.3 | Daily Organizer | Today, Inbox, Spaces, basic tasks and search | Internal prototype |
| v0.4 | Data Safety | Export, encrypted backup design, restore verification | Internal prototype |
| **v1.0** | **Daily-use Foundation** | Dependable offline organizer for daily use | **First usable release** |
| v1.1 | Local AI Assistant | Windows transcription and structured suggestions | Feature release |
| v1.2 | Two-phone Sync | Secure local peer synchronization and conflict handling | Feature release |
| v1.3 | Daily Management Plus | Waiting For, recurring tasks, reviews and quick capture shortcuts | Feature release |
| v1.4 | Shop & Personal Finance | Basic business records and confirmed financial tracking | Feature release |
| v1.5 | Connected Workspace | Nuro Photo Studio and other app integrations | Feature release |
| v1.6 | Knowledge & Insights | Source-grounded search, summaries and insights | Feature release |
| v1.7 | Desktop Companion | Expanded Windows workflows if proven useful | Optional feature release |
| v2.0 | Mature Personal OS | Hardened, measured and extensible ecosystem | Major release |

**Important:** v1.0 is deliberately narrower than the complete long-term vision. The aim is to establish a dependable daily-use foundation before adding AI, synchronization, finance, and integrations.

---

# Part A — Internal development milestones

## 4. v0.1 — Project Foundation

**Maps to:** Engineering Phase 0  
**Goal:** Establish a stable, buildable, documented project foundation.  
**Do not:** Build the complete AI engine or full synchronization implementation in this milestone.

### Deliverables

- Confirm product name, application identifiers, package names, and repository ownership.
- Create the monorepo and license.
- Add the master specification and product requirements.
- Add architecture documentation and architecture decision records.
- Create the Android Kotlin/Jetpack Compose project.
- Create the Windows Electron + React + TypeScript worker shell.
- Establish shared contracts, UUID rules, timestamps, and timezone policy.
- Define initial inbox, attachment, task, project, and Space schemas.
- Create initial SQLite migration structure.
- Draft device identity and synchronization protocol.
- Define AI job and structured extraction schemas.
- Document privacy, security, backup, and retention requirements.
- Add automated test frameworks, formatting, static checks, and build scripts.
- Create README, CHANGELOG, and HANDOVER.
- Verify Android builds/install on OPPO A3x and Vivo Y20.
- Verify the Windows shell builds on the Ryzen 5 5600G PC.

### Suggested repository baseline

```text
personal-business-life-os/
├── apps/
│   ├── android/
│   └── desktop/
├── packages/
│   ├── shared-contracts/
│   ├── shared-types/
│   ├── database-schema/
│   ├── sync-protocol/
│   └── integration-sdk/
├── engines/
│   ├── transcription/
│   ├── automation/
│   └── search/
├── ai/
│   ├── prompts/
│   ├── schemas/
│   └── evaluation/
├── database/
│   ├── migrations/
│   └── fixtures/
├── integrations/
├── docs/
├── scripts/
├── tests/
├── README.md
├── HANDOVER.md
└── CHANGELOG.md
```

### Acceptance criteria

- Clean checkout can be built using documented instructions.
- Android app installs and opens on both target phones.
- Windows worker shell builds and opens on the target PC.
- Initial database migration runs successfully.
- Core data contracts are documented and versioned.
- Automated test and build commands run.
- No paid API, hosting, or cloud service is required for the foundation.
- HANDOVER identifies the first v0.2 task.

### Exit gate

Do not begin feature development until the repository, build workflow, data contracts, and device installation checks are stable.

---

## 5. v0.2 — Local Capture Core

**Maps to:** First part of Engineering Phase 1  
**Goal:** Make capturing and preserving information dependable before adding organization features.

### Deliverables

#### Voice capture
- User-initiated recording screen.
- Clear recording indicator and elapsed time.
- Pause, finish, and cancel controls where supported.
- Save original audio to application-private local storage.
- Create an Inbox record only after the audio is safely committed.
- Record metadata such as UUID, creation time, duration, MIME type, file size, and source device.
- Provide playback of saved recordings.

#### Text capture
- Fast text entry.
- Save directly to the local Inbox.
- Preserve original text and timestamps.

#### Inbox foundation
- Chronological list of captures.
- Initial states: Saved, Organizing, Ready, Needs your choice, Couldn't process.
- Capture detail screen with source playback/text.
- Basic retry/error placeholder for future processing.
- No category selection required before saving.

#### Reliability
- Room/SQLite persistence.
- Persistent local job records.
- Safe handling of app closure and process interruption.
- Low-storage and file-write error handling.
- Basic attachment metadata and checksum support.

### Acceptance criteria

- A voice note can be recorded and saved with the PC off and internet disabled.
- A text note can be saved offline.
- Completed captures remain after force-closing and reopening the app.
- Original audio is not modified by later processing.
- A failed save is clearly reported and does not create a misleading “Saved” state.
- Duplicate taps/retries do not create duplicate capture records.
- Core capture flow is usable on OPPO A3x and Vivo Y20.

### Exit gate

Capture durability must be demonstrated through repeated recording, app restart, phone restart, offline, and low-storage tests before moving to v0.3.

---

## 6. v0.3 — Daily Organizer

**Maps to:** Daily-use UI and core task portion of Engineering Phase 5  
**Goal:** Turn the saved Inbox into a useful daily organizer without AI dependency.

### Deliverables

#### Mobile navigation
- Today.
- Inbox.
- Spaces.
- Persistent Capture action.
- Secondary Settings/More area.

#### Today
- Date and local status.
- Up to three user-selected focus priorities.
- Next time-specific commitment placeholder/data view.
- Continue card for an in-progress task or project.
- Due tasks.
- Waiting section placeholder until v1.3.
- Recent capture shortcut.

#### Tasks
- Create, edit, complete, reopen, archive.
- Title, notes, priority, optional due date, Space/project link.
- Task source link to originating capture.
- Basic task filters.

#### Spaces and projects
- Initial Spaces: Shop, Personal, App Development.
- Create, rename, reorder, archive Spaces.
- Basic project goal, status, next action, and linked tasks.
- Avoid duplicated records across views.

#### Search
- Local search across capture text, task titles, project names, and notes.
- Basic filters by type, date, Space, and status.
- Search results open the canonical record and its source.

#### UX foundation
- Light and dark themes.
- Bengali and English typography checks.
- System font scaling.
- Accessible touch targets.
- Empty states and error states.

### Acceptance criteria

- User can navigate the three main destinations without a dense menu.
- User can create and complete a task without AI.
- User can link a task to a Space/project and source capture.
- Today displays selected focus and useful next actions.
- Search finds recently saved content.
- The app remains useful with PC and internet unavailable.

### Exit gate

Run usability checks on both phones using real scenarios: shop reminder, personal note, and Nuro development idea. Fix confusing navigation before adding backup and release hardening.

---

## 7. v0.4 — Data Safety and Recovery

**Maps to:** Security, backup, and resilience foundation  
**Goal:** Protect the user's data before treating synchronization as a safety mechanism.

### Deliverables

- Define backup format and versioning.
- Export database and attachment files consistently.
- Add encrypted backup support after selecting and testing the encryption approach.
- Allow backup to a user-selected local destination, such as a PC folder or removable drive.
- Support multiple backup generations.
- Show last successful backup time and backup result.
- Implement restore workflow.
- Validate backup integrity before restore.
- Handle interrupted backup and restore safely.
- Document retention and deletion behavior.
- Test recovery from a damaged or missing local database.

### Acceptance criteria

- A backup contains the database and referenced attachments needed to restore.
- Backup integrity can be checked.
- Restore recreates records and attachment links.
- Incomplete backup does not replace a known-good backup.
- The user can understand when the last successful backup occurred.
- Restore is tested on a clean app installation or test device.
- Backup and restore do not require a paid cloud service.

### Exit gate

A verified backup must be restorable before v1.0 is declared ready.

---

# Part B — Product releases

## 8. v1.0 — Daily-use Foundation

<badge>First usable release</badge>

**Includes:** v0.1 + v0.2 + v0.3 + v0.4  
**Goal:** Deliver a dependable offline organizer that the user can use every day.

### Release scope

- Android voice and text capture.
- Local Inbox and original audio preservation.
- Today, Inbox, and Spaces.
- Basic task creation, editing, completion, and source links.
- Local search.
- Clear local processing/sync status language, even before remote sync is available.
- Basic reminders and notification handling.
- Export, backup, and verified restore.
- Bengali and English UI/content support.
- Compatibility checks on OPPO A3x and Vivo Y20.
- PC is not required for normal operation.

### Explicitly out of scope for v1.0

- AI transcription and extraction.
- Two-phone synchronization.
- Full accounting and inventory.
- Advanced calendar planning.
- Semantic search.
- Full Windows organizer.
- Broad third-party integration ecosystem.
- Always-on voice listening.

### Release acceptance checklist

- [ ] Voice capture works offline.
- [ ] Text capture works offline.
- [ ] Completed captures survive app and phone restart.
- [ ] Original audio remains available and unchanged.
- [ ] User can create, edit, and complete tasks.
- [ ] Today, Inbox, and Spaces are understandable.
- [ ] Search returns relevant local records.
- [ ] Backup can be created and restored.
- [ ] Permissions denied and storage low have clear recovery paths.
- [ ] Bengali, English, and mixed-script text remains readable.
- [ ] Core workflows are tested on both target phones.
- [ ] Release notes, known limitations, and recovery instructions are documented.

### Release decision

Release v1.0 only after all critical checklist items pass. Do not postpone data-loss or restore defects to a later release.

---

## 9. v1.1 — Local AI Assistant

**Maps to:** Engineering Phases 2 and 3  
**Goal:** Add optional local transcription and safe structured suggestions through the Windows PC.

### Deliverables

#### Windows worker
- Worker lifecycle and status UI.
- Pairing/authentication foundation with the phone.
- Persistent job queue.
- Job retry, cancellation, and error reporting.
- Clean startup and shutdown.
- Sequential processing to control RAM usage.

#### Transcription
- Integrate whisper.cpp.
- Benchmark multilingual Whisper Base and Small on approved Bengali, English, and mixed-language recordings.
- Evaluate shop noise and regional accents.
- Preserve transcript timestamps/segments where available.
- Link transcript to original audio.

#### Language understanding
- Integrate Ollama behind a provider interface.
- Benchmark a compact quantized instruction model on the target PC.
- Extract only supported structured types initially:
  - Note or idea.
  - Task suggestion.
  - Space/project suggestion.
  - Optional date expression, preserving original wording.
- Validate model output against versioned JSON schemas.
- Reject invalid or unsupported fields.
- Store model/prompt/schema version for traceability.

#### Review experience
- Return suggestions to the phone.
- Display Filed automatically, Suggested, Needs your choice.
- Show the evidence that matters.
- Support approve, edit, reject, retry, and keep in Inbox.
- Preserve source audio and transcript links.

#### AI evaluation
- Create a versioned Bengali/English test set using recordings the user approves for testing.
- Measure transcription WER/CER and extraction precision/recall.
- Track human correction rate and duplicate prevention.

### Acceptance criteria

- PC off does not affect capture or task use.
- Audio is processed only after local save and successful transfer.
- Failed AI jobs remain retryable.
- Reprocessing is idempotent.
- Invalid model output cannot write directly to core records.
- Ambiguous tasks remain suggestions or Inbox items.
- Every accepted AI-created record links to its source.
- Actual memory and processing performance is measured on Ryzen 5 5600G / 16 GB RAM.

### Deferred within v1.1

Do not automatically create financial transactions. Add financial extraction only after the confirmation/review design is separately tested and approved.

---

## 10. v1.2 — Two-phone Synchronization and Resilience

**Maps to:** Engineering Phase 4  
**Goal:** Make OPPO and Vivo securely exchange changes without requiring the PC or cloud.

### Deliverables

- QR-based device pairing.
- Device identity and revocation.
- Authenticated encrypted local connection.
- Local peer discovery and connection workflow.
- Change log and incremental synchronization.
- Record UUIDs and version metadata.
- Idempotency keys and acknowledgements.
- Retry and resume after interruption.
- Attachment transfer with checksums.
- Tombstones and deletion propagation rules.
- Conflict detection and resolution UI.
- Special handling for financial and sensitive records.
- PC as optional third peer.
- Sync diagnostics and understandable status messages.

### Acceptance criteria

- Two phones synchronize when reachable on the same local network.
- New and updated records transfer without full-database copying.
- Independent changes merge safely.
- Same-field conflicts are preserved for user resolution.
- Interrupted transfers resume or retry safely.
- Duplicate sync does not duplicate records or attachments.
- User can revoke a paired device.
- Each phone remains useful when the other phone and PC are unavailable.
- Queued changes are never described as synced.

### Important limitation

Direct peer synchronization does not provide remote sync when phones are separated and cannot reach one another. Remote availability would require an optional relay or always-available service, which is excluded from the no-cost serverless baseline.

### Exit gate

Test real-world sync with both phones on the same network, different networks, offline periods, interrupted transfers, simultaneous edits, device revocation, and restore from backup.

---

## 11. v1.3 — Daily Management Plus

**Goal:** Improve follow-through and recurring life/shop routines after the core organizer is dependable.

### Deliverables

#### Waiting For
- Track items awaiting a customer, supplier, person, or system.
- Optional expected follow-up date.
- Related Space, project, customer, or task.
- Status and source capture.
- Show relevant items on Today.

#### Recurring tasks
- Daily, weekly, monthly, and configurable recurrence.
- Separate recurrence definition from generated occurrences.
- Preserve completed history.
- Pause, resume, edit future occurrences, and end recurrence.
- Prevent duplicate occurrence generation.

#### Daily and weekly review
- Morning orientation.
- Optional evening closing review.
- Weekly review of Inbox, overdue, waiting, and project next actions.
- Snooze/skip without losing data.
- No silent deadline or priority changes.

#### Quick capture shortcuts
- Android home-screen widget.
- Quick Settings tile.
- App shortcuts.
- Share-sheet entry point.
- Optional lock-screen shortcut where supported.

### Acceptance criteria

- Waiting For records are distinguishable from tasks.
- Follow-up dates are optional and never invented.
- Recurring tasks preserve occurrence history.
- Repeated job execution does not create duplicate occurrences.
- Reviews summarize existing data and require approval for changes.
- Quick capture shortcuts use the same durable local capture pipeline.
- Vendor battery and permission limitations are clearly explained.

---

## 12. v1.4 — Shop and Personal Finance

**Maps to:** Engineering Phase 6  
**Goal:** Add practical, lightweight financial and shop records without turning PBLOS into complex accounting software.

### Deliverables

#### Finance
- Accounts and opening balances.
- Income, expense, and transfer records.
- BDT as default currency.
- Exact money representation; never use floating-point arithmetic.
- Pending confirmation state for extracted financial suggestions.
- Date, amount, account, category, and source.
- Simple daily/monthly summaries.
- CSV export.

#### Shop records
- Basic customers.
- Basic orders and order status.
- Payment status and outstanding balance.
- Links to relevant captures, tasks, and customers.
- Simple shop snapshot on Today or inside Shop Space.

#### Financial safety
- Transfers between own accounts are not income or expense.
- AI-extracted money records remain pending until confirmed.
- Edits and deletions are auditable.
- Financial sync conflicts require explicit handling.
- No automatic movement of money or external payment actions.

### Acceptance criteria

- User can manually record income, expense, and transfer.
- Account balances reconcile against recorded transactions.
- Transfers do not inflate income/expense totals.
- Pending AI suggestions do not affect confirmed totals.
- User can export and restore financial records.
- Reports show their date range and currency.
- No advanced accounting claims are made unless the corresponding accounting controls exist.

### Deferred

Complex inventory, tax filing, payroll, invoicing compliance, and full double-entry accounting remain out of scope unless a separately approved requirement justifies them.

---

## 13. v1.5 — Connected Workspace and App Integrations

**Maps to:** Engineering Phase 7  
**Goal:** Allow Nuro Photo Studio and future apps to exchange approved information with PBLOS.

### Deliverables

- Versioned Personal OS Integration API.
- App identity and scoped permissions.
- Authenticated local access.
- Schema validation.
- Idempotency.
- Audit logs.
- Revocation.
- API version compatibility policy.
- Integration SDK and sample app.
- Nuro Photo Studio integration.

### Initial Nuro use cases

- Create a task from a Nuro development issue or improvement.
- Retrieve authorized project tasks and milestones.
- Record decisions and next actions.
- Send project status events to PBLOS.
- Link every imported item to its originating app and source.

### Acceptance criteria

- External apps cannot directly edit PBLOS database files.
- Every integration has an identity and explicit scopes.
- Invalid or unauthorized requests are rejected.
- Duplicate event delivery does not create duplicate records.
- Access can be revoked.
- Integration activity is auditable.
- API documentation and versioning tests are available.

---

## 14. v1.6 — Knowledge Assistant and Insights

**Maps to:** Engineering Phase 8  
**Goal:** Make the user's stored information easier to retrieve and understand.

### Deliverables

- Better local full-text search.
- Bengali-aware indexing and search evaluation.
- Search across captures, transcripts, tasks, projects, Spaces, notes, decisions, and authorized finance/business data.
- Optional semantic search if local performance permits.
- Source-grounded question answering.
- Summaries with links to supporting records.
- Project and weekly summaries.
- User feedback for incorrect answers.

### Safety requirements

- Answers must distinguish stored facts from generated interpretation.
- Important claims must link to source records.
- Missing information must be stated rather than guessed.
- Finance answers must use confirmed records unless clearly labeled otherwise.
- Search must respect privacy settings and permissions.

### Acceptance criteria

- Search finds records using realistic Bengali, English, and mixed-language queries.
- AI answers link to relevant source records.
- The system does not invent transactions, deadlines, or project decisions.
- User can inspect the underlying source material.

---

## 15. v1.7 — Expanded Windows Companion (Optional)

**Goal:** Add desktop workflows only if real usage demonstrates a need beyond the local worker.

### Possible scope

- Desktop inbox and search.
- Task/project views.
- Drag-and-drop capture.
- Local file import.
- Sync and device management.
- AI job review.
- Backup management.

### Gate before development

Collect real usage evidence first. If the Windows worker and status utility meet the user's needs, do not build a full desktop organizer merely for feature parity.

### Acceptance criteria

- Desktop functionality does not become required for Android operation.
- Data remains consistent with the same logical record model.
- Desktop UI does not bypass authorization, validation, or audit rules.
- Packaging and update process are documented.

---

## 16. v2.0 — Mature Personal Business & Life OS

**Goal:** Consolidate the proven product into a hardened, maintainable ecosystem.

### Candidate scope

- Security and privacy audit.
- Performance optimization on both phones and PC.
- Database migration and upgrade hardening.
- Backup/restore recovery drills.
- Sync reliability and conflict-resolution improvements.
- AI model/prompt evaluation and regression suite.
- Accessibility and Bengali-language quality review.
- Integration compatibility tests.
- Stable release packaging and update strategy.
- Complete user documentation and recovery guide.

### v2.0 acceptance criteria

- Critical data-loss defects are resolved.
- Backup restore has been tested repeatedly.
- Synchronization passes the defined failure matrix.
- AI output is measured against a maintained evaluation set.
- Security and permission boundaries are reviewed.
- Upgrade and rollback/recovery behavior is documented.
- The app remains useful when AI, PC, internet, or the second phone is unavailable.

---

# Part C — Cross-version execution controls

## 17. Engineering phase to version mapping

| Engineering phase from master specification | Main work | Target version |
|---|---|---|
| Phase 0 | Requirements, repository, schemas, migrations, shells, build/test foundation | v0.1 |
| Phase 1 | Android capture hub, local Inbox, tasks, local persistence | v0.2–v1.0 |
| Phase 2 | Windows AI worker, transcription, model benchmark | v1.1 |
| Phase 3 | AI organization, project matching, review, provenance | v1.1 |
| Phase 4 | Pairing, phone sync, conflict handling, attachments | v1.2 |
| Phase 5 | Tasks, Spaces, planning and daily management | v0.3, v1.0, v1.3 |
| Phase 6 | Business and finance | v1.4 |
| Phase 7 | Shared integration API and Nuro integration | v1.5 |
| Phase 8 | Knowledge assistant and insights | v1.6 |
| Phase 9 | Hardening, security, recovery, performance, packaging | Every release; consolidated in v2.0 |

## 18. Standard task workflow

Every implementation task should follow this sequence:

1. Read the master specification and current `HANDOVER.md`.
2. Inspect the current repository and existing implementation.
3. Identify the exact version and acceptance criteria.
4. Write a small implementation plan.
5. Implement one bounded task at a time.
6. Add or update automated tests.
7. Run formatting, static checks, unit tests, integration tests, and build.
8. Test on actual target devices when relevant.
9. Record files changed, commands run, and verified behavior.
10. Document limitations and known defects.
11. Update `CHANGELOG.md` and `HANDOVER.md`.
12. Identify the next task without silently expanding scope.

AI coding tools must not invent successful test results. Mark a test as passed only if it was actually run.

## 19. Definition of done for every version

A version can be marked complete only when:

- [ ] All version deliverables are implemented or explicitly deferred.
- [ ] Every acceptance criterion has a verification result.
- [ ] Relevant unit tests pass.
- [ ] Relevant integration and end-to-end tests pass.
- [ ] Build and static checks pass.
- [ ] Database migrations are tested from the previous supported version.
- [ ] Failure and recovery cases are exercised.
- [ ] Actual-device checks are completed where applicable.
- [ ] No known critical data-loss or security defect remains.
- [ ] Documentation and release notes are updated.
- [ ] `HANDOVER.md` records current state and next action.

## 20. Testing matrix across versions

| Test area | Required from | Repeated in |
|---|---|---|
| Offline capture and app restart | v0.2 | Every release |
| Original attachment preservation | v0.2 | Every release |
| Task lifecycle and source links | v0.3 | Every release |
| Bengali/English typography | v0.3 | Every UI release |
| Backup and restore | v0.4 | Every database/release migration |
| AI transcription quality | v1.1 | Every model change |
| AI schema validation and idempotency | v1.1 | Every AI change |
| Two-phone sync and conflict tests | v1.2 | Every sync change |
| Recurrence duplicate prevention | v1.3 | Every recurrence change |
| Financial arithmetic and transfer rules | v1.4 | Every finance change |
| Integration authentication and revocation | v1.5 | Every integration change |
| Source-grounded answers | v1.6 | Every retrieval/model change |
| Upgrade and recovery | v1.0 | Every release |

## 21. Version management and compatibility

- Use semantic versioning for released builds: `MAJOR.MINOR.PATCH`.
- Internal milestone builds may use `0.x.y`.
- Increment MAJOR for incompatible data/protocol changes requiring explicit migration planning.
- Increment MINOR for backward-compatible features.
- Increment PATCH for backward-compatible fixes.
- Version database schemas, sync protocol, AI output schemas, and integration API independently.
- Do not assume app version and database schema version are the same.
- Maintain migration tests from every supported prior schema.
- Never silently discard fields or records during migration.
- Preserve original captures and source provenance across upgrades.

## 22. Scope and priority rules

When time or development capacity is limited, prioritize in this order:

1. Data durability and recovery.
2. Capture reliability.
3. Daily usability.
4. Correctness of tasks and records.
5. Security and privacy.
6. AI quality and helpfulness.
7. Synchronization reliability.
8. Business/finance capabilities.
9. Integrations and advanced insights.
10. Visual polish beyond the core usability requirements.

Do not pull later-version features into an earlier milestone unless the change is necessary to meet an existing acceptance criterion or is explicitly approved and documented.

## 23. Recommended immediate next step

Start with **v0.1 — Project Foundation / Phase 0**.

The first implementation package should produce:
- Repository and folder structure.
- Product/architecture/database/security documentation.
- Android shell.
- Windows worker shell.
- Initial shared schemas and migrations.
- Build/test commands.
- Device installation verification.
- `HANDOVER.md` with the first v0.2 implementation task.

Do not start with AI transcription, synchronization, finance, or a large dashboard. Establish the foundation and prove the build pipeline first.

---

**End of Version-Based Development Roadmap v1.0**
