/**
 * Personal Business & Life OS (PBLOS)
 * Core Domain Types & Contracts
 * 
 * Crafted with ❤️ by NasirPintu
 */

export type UUID = string;

/**
 * Standard ISO-8601 UTC timestamp string, e.g. "2026-11-18T12:00:00.000Z".
 * System operations internally use UTC; local display defaults to Asia/Dhaka.
 */
export type ISOTimestamp = string;

/**
 * Device identification & roles in the PBLOS mesh
 */
export type DeviceRole = 'primary_mobile' | 'secondary_mobile' | 'desktop_worker';

export interface DeviceInfo {
  id: UUID;
  name: string;
  role: DeviceRole;
  hardwareDescription: string;
  pairedAt: ISOTimestamp;
  lastSeenAt: ISOTimestamp;
  isActive: boolean;
  publicKeyHex?: string;
}

/**
 * Capture source types supported by PBLOS
 */
export type CaptureType =
  | 'voice'
  | 'text'
  | 'photo'
  | 'document'
  | 'url'
  | 'shared_file'
  | 'audio_import'
  | 'desktop_import';

/**
 * Universal Inbox lifecycle states
 */
export type InboxItemStatus =
  | 'new'
  | 'saved'
  | 'waiting_for_processing'
  | 'processing'
  | 'needs_review'
  | 'organized'
  | 'failed'
  | 'archived';

/**
 * Universal Inbox entity
 */
export interface InboxItem {
  id: UUID;
  captureType: CaptureType;
  sourceDeviceId: UUID;
  capturedAt: ISOTimestamp;
  rawContent?: string;
  attachmentId?: UUID;
  status: InboxItemStatus;
  retryCount: number;
  lastError?: string;
  processingMetadataJson?: string;
  createdAt: ISOTimestamp;
  updatedAt: ISOTimestamp;
  isDeleted: boolean;
  version: number;
}

/**
 * Attachment entity representing durable media (audio, photos, documents)
 */
export interface Attachment {
  id: UUID;
  inboxItemId: UUID;
  fileName: string;
  filePath: string;
  mimeType: string;
  byteSize: number;
  sha256Hash: string;
  durationMs?: number; // for audio
  createdAt: ISOTimestamp;
  updatedAt: ISOTimestamp;
  isDeleted: boolean;
  version: number;
}

/**
 * Space entity (organizational contexts: Shop, Personal, App Development, etc.)
 */
export interface Space {
  id: UUID;
  name: string;
  slug: string;
  description?: string;
  colorHex?: string;
  sortOrder: number;
  isArchived: boolean;
  createdAt: ISOTimestamp;
  updatedAt: ISOTimestamp;
  version: number;
}

/**
 * Project entity (software, personal, shop initiatives)
 */
export type ProjectStatus =
  | 'planning'
  | 'active'
  | 'paused'
  | 'blocked'
  | 'completed'
  | 'archived';

export interface Project {
  id: UUID;
  spaceId: UUID;
  name: string;
  objective?: string;
  status: ProjectStatus;
  nextAction?: string;
  sourceInboxItemId?: UUID;
  createdAt: ISOTimestamp;
  updatedAt: ISOTimestamp;
  isDeleted: boolean;
  version: number;
}

/**
 * Task priority levels
 */
export type TaskPriority = 'low' | 'normal' | 'high' | 'urgent';

/**
 * Task lifecycle states
 */
export type TaskStatus =
  | 'captured'
  | 'proposed'
  | 'approved'
  | 'scheduled'
  | 'in_progress'
  | 'waiting_blocked'
  | 'completed'
  | 'cancelled'
  | 'archived';

/**
 * Task entity
 */
export interface Task {
  id: UUID;
  spaceId?: UUID;
  projectId?: UUID;
  sourceInboxItemId?: UUID;
  title: string;
  description?: string;
  status: TaskStatus;
  priority: TaskPriority;
  dueAt?: ISOTimestamp;
  dateExpression?: string; // original spoken phrase (e.g. "kal sokale")
  estimatedMinutes?: number;
  completedAt?: ISOTimestamp;
  createdAt: ISOTimestamp;
  updatedAt: ISOTimestamp;
  isDeleted: boolean;
  version: number;
}

/**
 * Financial amounts must be represented using exact integer minor units (BDT Paisa: 1 BDT = 100 Paisa).
 * Floating-point arithmetic is strictly forbidden for money.
 */
export type MinorUnits = number; // integer (e.g. 50000 = 500.00 BDT)

export type TransactionType = 'income' | 'expense' | 'transfer';

export interface Transaction {
  id: UUID;
  type: TransactionType;
  sourceAccountId: UUID;
  destinationAccountId?: UUID; // required for transfer
  amountMinor: MinorUnits;
  currency: 'BDT';
  category: string;
  notes?: string;
  transactionDate: string; // YYYY-MM-DD
  isConfirmed: boolean; // AI-suggested transactions require explicit human confirmation
  sourceInboxItemId?: UUID;
  createdAt: ISOTimestamp;
  updatedAt: ISOTimestamp;
  isDeleted: boolean;
  version: number;
}

/**
 * Synchronization Protocol Entities
 */
export interface SyncChange {
  id: UUID;
  deviceId: UUID;
  entityType: string;
  entityId: UUID;
  operation: 'INSERT' | 'UPDATE' | 'DELETE';
  changePayloadJson: string;
  sequenceNumber: number;
  createdAt: ISOTimestamp;
}

export interface SyncConflict {
  id: UUID;
  entityType: string;
  entityId: UUID;
  localPayloadJson: string;
  remotePayloadJson: string;
  remoteDeviceId: UUID;
  detectedAt: ISOTimestamp;
  resolvedAt?: ISOTimestamp;
  resolution?: 'local_wins' | 'remote_wins' | 'manual_merged';
}

// ─── AI Job Queue ────────────────────────────────────────────────────────────

/** Processing requests are durable queue records; they do not imply that AI is available. */
export type AIJobStatus = 'queued' | 'running' | 'completed' | 'failed' | 'cancelled';
export type AIJobKind = 'transcription' | 'extraction' | 'classification' | 'ocr';

export interface AIJob {
  id: UUID;
  kind: AIJobKind;
  sourceInboxItemId: UUID;
  attachmentRelativePath?: string; // path to audio file, relative to jobs dir
  deviceId: UUID;
  status: AIJobStatus;
  attemptCount: number;
  requestedAt: ISOTimestamp;
  startedAt?: ISOTimestamp;
  completedAt?: ISOTimestamp;
  errorCode?: string;
  errorMessage?: string;
  resultJson?: string;
  modelVersion?: string;   // whisper model name or ollama model tag used
  promptVersion?: string;  // extraction prompt version for traceability
  schemaVersion?: string;  // output schema version
}

// ─── Extraction Output Schemas ───────────────────────────────────────────────

/** Whisper transcription result */
export interface TranscriptionResult {
  schemaVersion: '1';
  transcript: string;
  language?: string;          // detected language code e.g. 'bn', 'en'
  segments?: TranscriptSegment[];
  whisperModel: string;
  processingMs: number;
}

export interface TranscriptSegment {
  startMs: number;
  endMs: number;
  text: string;
}

/** Ollama extraction result — only supported types, no financial extraction in v1.1 */
export type ExtractionItemKind = 'note' | 'task' | 'space_suggestion' | 'idea';

export interface ExtractionItem {
  kind: ExtractionItemKind;
  title: string;
  body?: string;
  spaceHint?: string;         // suggested space name (never auto-created)
  dateExpression?: string;    // original spoken phrase e.g. "kal sokale"
  confidence: 'high' | 'medium' | 'low';
}

export interface ExtractionResult {
  schemaVersion: '1';
  items: ExtractionItem[];
  ollamaModel: string;
  promptVersion: string;
  processingMs: number;
  rawModelOutput?: string;    // kept for traceability/debugging
}

// ─── Worker API Contracts (HTTP) ─────────────────────────────────────────────

/** POST /api/jobs — submit a new audio job */
export interface SubmitJobRequest {
  inboxItemId: UUID;
  deviceId: UUID;
  audioFileName: string;    // filename only; audio bytes sent as multipart
}

export interface SubmitJobResponse {
  jobId: UUID;
  status: 'queued';
  message: string;
}

/** GET /api/jobs/:jobId — poll job status */
export interface JobStatusResponse {
  jobId: UUID;
  inboxItemId: UUID;
  status: AIJobStatus;
  attemptCount: number;
  requestedAt: ISOTimestamp;
  startedAt?: ISOTimestamp;
  completedAt?: ISOTimestamp;
  transcript?: string;
  extraction?: ExtractionResult;
  errorCode?: string;
  errorMessage?: string;
}

/** GET /api/jobs — list recent jobs */
export interface JobListResponse {
  jobs: JobStatusResponse[];
}

/** GET /api/status — worker health */
export interface WorkerStatusResponse {
  version: string;
  ready: boolean;
  whisperAvailable: boolean;
  ollamaAvailable: boolean;
  ollamaModel?: string;
  queueLength: number;
  processedToday: number;
  uptime: number; // seconds
}

// ─── Proposal Review ─────────────────────────────────────────────────────────

/** Machine suggestions remain unapproved until the user explicitly accepts them. */
export interface ExtractionProposal<T> {
  id: UUID;
  sourceInboxItemId: UUID;
  jobId: UUID;
  kind: ExtractionItemKind | 'transaction';
  confidence?: number;
  payload: T;
  status: 'pending_review' | 'approved' | 'edited' | 'rejected';
  createdAt: ISOTimestamp;
  reviewedAt?: ISOTimestamp;
}

// ─── Sync & Envelope ─────────────────────────────────────────────────────────

export interface SyncChange {
  id: UUID;
  deviceId: UUID;
  entityType: string;
  entityId: UUID;
  operation: 'INSERT' | 'UPDATE' | 'DELETE';
  changePayloadJson: string;
  sequenceNumber: number;
  createdAt: ISOTimestamp;
}

export interface SyncConflict {
  id: UUID;
  entityType: string;
  entityId: UUID;
  localPayloadJson: string;
  remotePayloadJson: string;
  remoteDeviceId: UUID;
  detectedAt: ISOTimestamp;
  resolvedAt?: ISOTimestamp;
  resolution?: 'local_wins' | 'remote_wins' | 'manual_merged';
}

/** A portable capture envelope for future phone/worker interchange. */
export interface CaptureEnvelope {
  schemaVersion: 1;
  item: InboxItem;
  attachment?: Attachment;
}

/** Timestamp helper contract: generate ISO-8601 UTC strings, never local wall-clock strings. */
export function toISOTimestamp(date: Date = new Date()): ISOTimestamp {
  return date.toISOString();
}
