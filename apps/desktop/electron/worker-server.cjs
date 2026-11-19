/**
 * PBLOS Windows Worker — HTTP API Server
 * Runs inside the Electron main process (Node.js context).
 * Receives audio files from Android, queues transcription + extraction jobs,
 * and returns results to the phone on demand.
 *
 * Crafted with ❤️ by NasirPintu
 */

'use strict';

const express = require('express');
const multer = require('multer');
const path = require('node:path');
const fs = require('node:fs');
const { v4: uuidv4 } = require('uuid');
const Database = require('better-sqlite3');

// ─── Constants ────────────────────────────────────────────────────────────────

const SERVER_VERSION = '1.1.0';
const SCHEMA_VERSION = '1';
const DEFAULT_PORT = 7474;

// ─── State ────────────────────────────────────────────────────────────────────

let _db = null;
let _app = null;
let _server = null;
let _startedAt = Date.now();
let _statusCallback = null; // called whenever queue/status changes

// ─── Database Setup ───────────────────────────────────────────────────────────

function initDatabase(dataDir) {
  const dbPath = path.join(dataDir, 'worker.db');
  const db = new Database(dbPath);
  db.pragma('journal_mode = WAL');
  db.pragma('foreign_keys = ON');

  db.exec(`
    CREATE TABLE IF NOT EXISTS ai_jobs (
      id               TEXT PRIMARY KEY,
      inbox_item_id    TEXT NOT NULL,
      device_id        TEXT NOT NULL,
      audio_file_path  TEXT,
      status           TEXT NOT NULL DEFAULT 'queued',
      attempt_count    INTEGER NOT NULL DEFAULT 0,
      requested_at     TEXT NOT NULL,
      started_at       TEXT,
      completed_at     TEXT,
      error_code       TEXT,
      error_message    TEXT,
      transcript       TEXT,
      extraction_json  TEXT,
      model_version    TEXT,
      prompt_version   TEXT,
      schema_version   TEXT DEFAULT '${SCHEMA_VERSION}'
    );

    CREATE INDEX IF NOT EXISTS idx_jobs_status      ON ai_jobs(status);
    CREATE INDEX IF NOT EXISTS idx_jobs_inbox_item  ON ai_jobs(inbox_item_id);
    CREATE INDEX IF NOT EXISTS idx_jobs_requested   ON ai_jobs(requested_at DESC);
  `);

  return db;
}

// ─── Job Queue ────────────────────────────────────────────────────────────────

function insertJob(db, { id, inboxItemId, deviceId, audioFilePath, requestedAt }) {
  db.prepare(`
    INSERT INTO ai_jobs (id, inbox_item_id, device_id, audio_file_path, status, attempt_count, requested_at)
    VALUES (?, ?, ?, ?, 'queued', 0, ?)
  `).run(id, inboxItemId, deviceId, audioFilePath, requestedAt);
}

function getJob(db, jobId) {
  return db.prepare('SELECT * FROM ai_jobs WHERE id = ?').get(jobId);
}

function getJobByInboxItemId(db, inboxItemId) {
  return db.prepare('SELECT * FROM ai_jobs WHERE inbox_item_id = ? ORDER BY requested_at DESC LIMIT 1').get(inboxItemId);
}

function listRecentJobs(db, limit = 50) {
  return db.prepare('SELECT * FROM ai_jobs ORDER BY requested_at DESC LIMIT ?').all(limit);
}

function markJobRunning(db, jobId, now) {
  db.prepare(`UPDATE ai_jobs SET status='running', started_at=?, attempt_count=attempt_count+1 WHERE id=?`).run(now, jobId);
}

function markJobCompleted(db, jobId, { transcript, extractionJson, modelVersion, promptVersion, now }) {
  db.prepare(`
    UPDATE ai_jobs SET status='completed', completed_at=?, transcript=?, extraction_json=?, model_version=?, prompt_version=?
    WHERE id=?
  `).run(now, transcript, extractionJson, modelVersion, promptVersion, jobId);
}

function markJobFailed(db, jobId, { errorCode, errorMessage, now }) {
  db.prepare(`
    UPDATE ai_jobs SET status='failed', completed_at=?, error_code=?, error_message=?
    WHERE id=?
  `).run(now, errorCode, errorMessage, jobId);
}

function cancelJob(db, jobId) {
  db.prepare(`UPDATE ai_jobs SET status='cancelled', completed_at=? WHERE id=? AND status IN ('queued')`).run(isoNow(), jobId);
}

function getQueueLength(db) {
  return db.prepare(`SELECT COUNT(*) as n FROM ai_jobs WHERE status='queued'`).get().n;
}

function getProcessedToday(db) {
  const today = new Date().toISOString().slice(0, 10);
  return db.prepare(`SELECT COUNT(*) as n FROM ai_jobs WHERE status='completed' AND completed_at >= ?`).get(today + 'T00:00:00.000Z').n;
}

function getNextQueuedJob(db) {
  return db.prepare(`SELECT * FROM ai_jobs WHERE status='queued' ORDER BY requested_at ASC LIMIT 1`).get();
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

function isoNow() {
  return new Date().toISOString();
}

function jobToResponse(job) {
  if (!job) return null;
  let extraction = undefined;
  try { if (job.extraction_json) extraction = JSON.parse(job.extraction_json); } catch (_) {}
  return {
    jobId: job.id,
    inboxItemId: job.inbox_item_id,
    deviceId: job.device_id,
    status: job.status,
    attemptCount: job.attempt_count,
    requestedAt: job.requested_at,
    startedAt: job.started_at,
    completedAt: job.completed_at,
    transcript: job.transcript,
    extraction,
    errorCode: job.error_code,
    errorMessage: job.error_message,
    modelVersion: job.model_version,
  };
}

// ─── Whisper Check ────────────────────────────────────────────────────────────

let _whisperAvailable = false;
let _whisperPath = null;

function detectWhisper() {
  // Check common whisper.cpp binary locations
  const candidates = [
    path.join(process.env.USERPROFILE || '', 'whisper.cpp', 'build', 'bin', 'whisper-cli.exe'),
    path.join(process.env.USERPROFILE || '', 'whisper.cpp', 'build', 'bin', 'Release', 'whisper-cli.exe'),
    path.join(process.env.USERPROFILE || '', 'whisper.cpp', 'build', 'bin', 'main.exe'),
    'C:\\whisper\\whisper-cli.exe',
    'C:\\tools\\whisper\\whisper-cli.exe',
  ];
  for (const p of candidates) {
    if (fs.existsSync(p)) { _whisperPath = p; _whisperAvailable = true; return true; }
  }
  return false;
}

function getWhisperModelPath() {
  if (!_whisperPath) return null;
  const dir = path.dirname(_whisperPath);
  const modelNames = ['ggml-small.bin', 'ggml-base.bin', 'ggml-tiny.bin'];
  const modelDirs = [
    path.join(path.dirname(path.dirname(path.dirname(dir))), 'models'),
    path.join(path.dirname(path.dirname(dir)), 'models'),
    path.join(dir, 'models'),
    path.join(process.env.USERPROFILE || '', 'whisper.cpp', 'models'),
  ];
  for (const mdir of modelDirs) {
    for (const name of modelNames) {
      const mp = path.join(mdir, name);
      if (fs.existsSync(mp)) return { path: mp, name: name.replace('ggml-', '').replace('.bin', '') };
    }
  }
  return null;
}

// ─── Ollama Check ─────────────────────────────────────────────────────────────

let _ollamaAvailable = false;
let _ollamaModel = null;
const OLLAMA_BASE = 'http://127.0.0.1:11434';
const PREFERRED_MODELS = ['gemma3:1b', 'qwen2.5:0.5b', 'qwen2.5:1.5b', 'llama3.2:1b', 'phi3:mini', 'mistral:7b-instruct'];

async function detectOllama() {
  try {
    const res = await fetch(`${OLLAMA_BASE}/api/tags`, { signal: AbortSignal.timeout(3000) });
    if (!res.ok) return false;
    const data = await res.json();
    const names = (data.models || []).map(m => m.name);
    for (const preferred of PREFERRED_MODELS) {
      const found = names.find(n => n === preferred || n.startsWith(preferred.split(':')[0] + ':'));
      if (found) { _ollamaModel = found; _ollamaAvailable = true; return true; }
    }
    // Fall back to first available model
    if (names.length > 0) { _ollamaModel = names[0]; _ollamaAvailable = true; return true; }
  } catch (_) {}
  return false;
}

// ─── Transcription ────────────────────────────────────────────────────────────

const { execFile } = require('node:child_process');
const { promisify } = require('node:util');
const execFileAsync = promisify(execFile);

async function runTranscription(audioFilePath) {
  if (!_whisperAvailable) throw new Error('whisper.cpp not found');
  const model = getWhisperModelPath();
  if (!model) throw new Error('No whisper model found — download a model to ~/whisper.cpp/models/');

  const t0 = Date.now();
  const { stdout } = await execFileAsync(_whisperPath, [
    '--model', model.path,
    '--file', audioFilePath,
    '--language', 'auto',
    '--output-json',
    '--no-timestamps',
    '--threads', '4',
  ], { timeout: 120_000, maxBuffer: 10 * 1024 * 1024 });

  // whisper-cli --output-json writes to audioFilePath + '.json'
  const jsonPath = audioFilePath + '.json';
  let parsed = null;
  if (fs.existsSync(jsonPath)) {
    try { parsed = JSON.parse(fs.readFileSync(jsonPath, 'utf8')); } catch (_) {}
    fs.unlink(jsonPath, () => {});
  }

  const transcript = parsed?.transcription?.map(s => s.text).join(' ').trim()
    || stdout.trim();

  return {
    schemaVersion: '1',
    transcript,
    language: parsed?.result?.language,
    segments: parsed?.transcription?.map(s => ({
      startMs: Math.round((s.timestamps?.from_ms ?? 0)),
      endMs: Math.round((s.timestamps?.to_ms ?? 0)),
      text: s.text,
    })),
    whisperModel: model.name,
    processingMs: Date.now() - t0,
  };
}

// ─── Extraction ───────────────────────────────────────────────────────────────

const PROMPT_VERSION = 'v1.0';

function buildExtractionPrompt(transcript) {
  return `You are a personal assistant for a Bangladeshi user who speaks Bengali and English.
Analyze the following voice memo transcript and extract any clearly stated items.

TRANSCRIPT:
"${transcript}"

Return a JSON object with this exact structure (no extra text, no markdown fences):
{
  "items": [
    {
      "kind": "note|task|idea|space_suggestion",
      "title": "concise title",
      "body": "optional detail or null",
      "spaceHint": "Shop|Personal|App Development or null",
      "dateExpression": "original words for a date/time if mentioned, or null",
      "confidence": "high|medium|low"
    }
  ]
}

Rules:
- Only extract what is clearly stated. Never invent tasks or dates.
- Do NOT extract financial transactions or amounts.
- If the transcript is unclear, return { "items": [] }.
- Use the user's original words for dateExpression, e.g. "kal sokale", "Friday", "next week".
- kind must be exactly one of: note, task, idea, space_suggestion.`;
}

async function runExtraction(transcript) {
  if (!_ollamaAvailable || !_ollamaModel) throw new Error('Ollama not available');
  const t0 = Date.now();
  const prompt = buildExtractionPrompt(transcript);

  const res = await fetch(`${OLLAMA_BASE}/api/generate`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ model: _ollamaModel, prompt, stream: false, format: 'json' }),
    signal: AbortSignal.timeout(90_000),
  });
  if (!res.ok) throw new Error(`Ollama returned ${res.status}`);
  const data = await res.json();
  const rawOutput = data.response || '';

  let items = [];
  try {
    const parsed = JSON.parse(rawOutput);
    items = Array.isArray(parsed.items) ? parsed.items : [];
  } catch (_) {
    // Extraction failed to parse — return empty set safely
    items = [];
  }

  // Validate and sanitize each item
  const validKinds = ['note', 'task', 'idea', 'space_suggestion'];
  const validConfidence = ['high', 'medium', 'low'];
  items = items
    .filter(item => item && typeof item.title === 'string' && item.title.trim())
    .map(item => ({
      kind: validKinds.includes(item.kind) ? item.kind : 'note',
      title: String(item.title).trim().slice(0, 300),
      body: item.body ? String(item.body).trim().slice(0, 2000) : undefined,
      spaceHint: item.spaceHint || undefined,
      dateExpression: item.dateExpression || undefined,
      confidence: validConfidence.includes(item.confidence) ? item.confidence : 'low',
    }));

  return {
    schemaVersion: '1',
    items,
    ollamaModel: _ollamaModel,
    promptVersion: PROMPT_VERSION,
    processingMs: Date.now() - t0,
    rawModelOutput: rawOutput.slice(0, 4000), // truncated for traceability
  };
}

// ─── Job Processor ────────────────────────────────────────────────────────────

let _processingJob = false;

async function processNextJob(db) {
  if (_processingJob) return;
  const job = getNextQueuedJob(db);
  if (!job) return;

  _processingJob = true;
  const now = isoNow();
  markJobRunning(db, job.id, now);
  _statusCallback?.();

  let transcript = null;
  let extraction = null;
  let modelVersion = null;

  try {
    // Step 1: Transcription
    if (job.audio_file_path && fs.existsSync(job.audio_file_path)) {
      const txResult = await runTranscription(job.audio_file_path);
      transcript = txResult.transcript;
      modelVersion = `whisper-${txResult.whisperModel}`;
    } else if (!job.audio_file_path) {
      throw new Error('No audio file path recorded for this job');
    } else {
      throw new Error(`Audio file not found: ${job.audio_file_path}`);
    }

    // Step 2: Extraction (only if we have a transcript and Ollama is available)
    if (transcript && _ollamaAvailable) {
      try {
        extraction = await runExtraction(transcript);
        if (modelVersion) modelVersion += `+ollama-${_ollamaModel}`;
      } catch (err) {
        // Extraction failure is non-fatal — we still save the transcript
        console.error('[PBLOS Worker] Extraction failed (non-fatal):', err.message);
        extraction = { schemaVersion: '1', items: [], ollamaModel: _ollamaModel || 'unknown', promptVersion: PROMPT_VERSION, processingMs: 0, error: err.message };
      }
    }

    markJobCompleted(db, job.id, {
      transcript,
      extractionJson: extraction ? JSON.stringify(extraction) : null,
      modelVersion,
      promptVersion: PROMPT_VERSION,
      now: isoNow(),
    });

  } catch (err) {
    console.error('[PBLOS Worker] Job failed:', job.id, err.message);
    markJobFailed(db, job.id, { errorCode: 'PROCESSING_ERROR', errorMessage: err.message, now: isoNow() });
  } finally {
    _processingJob = false;
    _statusCallback?.();
    // Check for more queued jobs
    setTimeout(() => processNextJob(db), 500);
  }
}

// ─── Express App ──────────────────────────────────────────────────────────────

function buildApp(db, uploadsDir) {
  const app = express();
  app.use(express.json());

  const storage = multer.diskStorage({
    destination: (req, file, cb) => cb(null, uploadsDir),
    filename: (req, file, cb) => {
      const ext = path.extname(file.originalname) || '.m4a';
      cb(null, `${uuidv4()}${ext}`);
    },
  });
  const upload = multer({ storage, limits: { fileSize: 100 * 1024 * 1024 } });

  // ── Health check
  app.get('/api/status', async (req, res) => {
    await detectOllama();
    res.json({
      version: SERVER_VERSION,
      ready: true,
      whisperAvailable: _whisperAvailable,
      whisperPath: _whisperPath,
      whisperModel: getWhisperModelPath()?.name ?? null,
      ollamaAvailable: _ollamaAvailable,
      ollamaModel: _ollamaModel,
      queueLength: getQueueLength(db),
      processedToday: getProcessedToday(db),
      uptime: Math.floor((Date.now() - _startedAt) / 1000),
    });
  });

  // ── Submit job
  app.post('/api/jobs', upload.single('audio'), (req, res) => {
    const { inboxItemId, deviceId } = req.body;
    if (!inboxItemId || !deviceId) {
      return res.status(400).json({ error: 'inboxItemId and deviceId are required' });
    }

    // Check for duplicate (idempotency)
    const existing = getJobByInboxItemId(db, inboxItemId);
    if (existing && ['queued', 'running', 'completed'].includes(existing.status)) {
      return res.json({ jobId: existing.id, status: existing.status, message: 'Job already exists' });
    }

    const jobId = uuidv4();
    const audioFilePath = req.file?.path ?? null;
    insertJob(db, { id: jobId, inboxItemId, deviceId, audioFilePath, requestedAt: isoNow() });

    // Kick off processing
    setTimeout(() => processNextJob(db), 100);
    _statusCallback?.();

    res.json({ jobId, status: 'queued', message: 'Job queued successfully' });
  });

  // ── Get job status
  app.get('/api/jobs/:jobId', (req, res) => {
    const job = getJob(db, req.params.jobId);
    if (!job) return res.status(404).json({ error: 'Job not found' });
    res.json(jobToResponse(job));
  });

  // ── List recent jobs
  app.get('/api/jobs', (req, res) => {
    const jobs = listRecentJobs(db, 50).map(jobToResponse);
    res.json({ jobs });
  });

  // ── Cancel a queued job
  app.delete('/api/jobs/:jobId', (req, res) => {
    cancelJob(db, req.params.jobId);
    const job = getJob(db, req.params.jobId);
    res.json(jobToResponse(job));
  });

  // ── Retry a failed job
  app.post('/api/jobs/:jobId/retry', (req, res) => {
    const job = getJob(db, req.params.jobId);
    if (!job) return res.status(404).json({ error: 'Job not found' });
    if (job.status !== 'failed' && job.status !== 'cancelled') {
      return res.status(400).json({ error: `Job status is '${job.status}', only failed/cancelled jobs can be retried` });
    }
    db.prepare(`UPDATE ai_jobs SET status='queued', error_code=NULL, error_message=NULL, started_at=NULL, completed_at=NULL WHERE id=?`).run(job.id);
    setTimeout(() => processNextJob(db), 100);
    res.json({ jobId: job.id, status: 'queued', message: 'Job re-queued' });
  });

  return app;
}

// ─── Public API ───────────────────────────────────────────────────────────────

function start({ dataDir, port = DEFAULT_PORT, onStatusChange }) {
  if (_server) return { port, url: `http://127.0.0.1:${port}` };

  const uploadsDir = path.join(dataDir, 'uploads');
  fs.mkdirSync(uploadsDir, { recursive: true });

  _db = initDatabase(dataDir);
  _statusCallback = onStatusChange;
  _startedAt = Date.now();

  detectWhisper();
  detectOllama().then(() => onStatusChange?.());

  _app = buildApp(_db, uploadsDir);
  _server = _app.listen(port, '127.0.0.1', () => {
    console.log(`[PBLOS Worker] HTTP server listening on http://127.0.0.1:${port}`);
    // Resume any jobs that were 'running' when the app last closed
    if (_db) {
      _db.prepare(`UPDATE ai_jobs SET status='queued', started_at=NULL WHERE status='running'`).run();
      processNextJob(_db);
    }
  });

  return { port, url: `http://127.0.0.1:${port}` };
}

function stop() {
  _server?.close();
  _db?.close();
  _server = null;
  _db = null;
}

function getStatus() {
  if (!_db) return null;
  return {
    whisperAvailable: _whisperAvailable,
    whisperModel: getWhisperModelPath()?.name ?? null,
    ollamaAvailable: _ollamaAvailable,
    ollamaModel: _ollamaModel,
    queueLength: getQueueLength(_db),
    processedToday: getProcessedToday(_db),
    uptime: Math.floor((Date.now() - _startedAt) / 1000),
    processingJob: _processingJob,
  };
}

function refreshAiStatus() {
  detectWhisper();
  return detectOllama();
}

module.exports = { start, stop, getStatus, refreshAiStatus, DEFAULT_PORT };
