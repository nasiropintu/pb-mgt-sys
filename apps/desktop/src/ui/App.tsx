import { useCallback, useEffect, useRef, useState } from 'react';
import './styles.css';

// ─── Types ────────────────────────────────────────────────────────────────────

interface WorkerStatus {
  whisperAvailable: boolean;
  whisperModel: string | null;
  ollamaAvailable: boolean;
  ollamaModel: string | null;
  queueLength: number;
  processedToday: number;
  uptime: number;
  processingJob: boolean;
  serverUrl: string;
  port: number;
}

interface ExtractionItem {
  kind: string;
  title: string;
  body?: string;
  spaceHint?: string;
  dateExpression?: string;
  confidence: 'high' | 'medium' | 'low';
}

interface ExtractionResult {
  items: ExtractionItem[];
  ollamaModel: string;
  promptVersion: string;
  processingMs: number;
}

interface Job {
  jobId: string;
  inboxItemId: string;
  status: 'queued' | 'running' | 'completed' | 'failed' | 'cancelled';
  attemptCount: number;
  requestedAt: string;
  startedAt?: string;
  completedAt?: string;
  transcript?: string;
  extraction?: ExtractionResult;
  errorCode?: string;
  errorMessage?: string;
  modelVersion?: string;
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

const ipc = (window as any).pblosWorker as {
  getStatus: () => Promise<WorkerStatus>;
  getJobs:   () => Promise<{ jobs: Job[] }>;
  retryJob:  (id: string) => Promise<Job>;
  cancelJob: (id: string) => Promise<Job>;
  refreshAi: () => Promise<WorkerStatus>;
  onStatus:  (cb: (d: WorkerStatus) => void) => void;
} | undefined;

function fmt(isoStr?: string) {
  if (!isoStr) return '—';
  return new Date(isoStr).toLocaleTimeString();
}
function elapsed(ms: number) {
  if (ms < 1000) return `${ms}ms`;
  if (ms < 60000) return `${(ms / 1000).toFixed(1)}s`;
  return `${Math.floor(ms / 60000)}m ${Math.floor((ms % 60000) / 1000)}s`;
}
function uptime(secs: number) {
  const h = Math.floor(secs / 3600), m = Math.floor((secs % 3600) / 60), s = secs % 60;
  return h > 0 ? `${h}h ${m}m` : m > 0 ? `${m}m ${s}s` : `${s}s`;
}

const STATUS_COLOR: Record<Job['status'], string> = {
  queued:    '#7b8fa1',
  running:   '#2858a5',
  completed: '#1a7a4a',
  failed:    '#c0392b',
  cancelled: '#a0a0a0',
};
const STATUS_ICON: Record<Job['status'], string> = {
  queued: '◷', running: '⟳', completed: '✓', failed: '✕', cancelled: '—',
};
const CONF_COLOR: Record<string, string> = { high: '#1a7a4a', medium: '#c07d00', low: '#a0a0a0' };
const KIND_ICON: Record<string, string> = { task: '☑', note: '📝', idea: '💡', space_suggestion: '📁' };

// ─── Components ───────────────────────────────────────────────────────────────

function StatusChip({ ok, label }: { ok: boolean; label: string }) {
  return (
    <span className={`chip ${ok ? 'chip-green' : 'chip-red'}`}>
      <i className="dot" /> {label}
    </span>
  );
}

function JobCard({ job, onRetry, onCancel, expanded, onToggle }: {
  job: Job;
  onRetry: (id: string) => void;
  onCancel: (id: string) => void;
  expanded: boolean;
  onToggle: () => void;
}) {
  return (
    <div className={`job-card job-${job.status}`} onClick={onToggle}>
      <div className="job-header">
        <span className="job-status-icon" style={{ color: STATUS_COLOR[job.status] }}>
          {STATUS_ICON[job.status]}
        </span>
        <span className="job-id mono">{job.jobId.slice(0, 8)}…</span>
        <span className="job-inbox-id mono muted">{job.inboxItemId.slice(0, 8)}</span>
        <span className={`job-badge badge-${job.status}`}>{job.status}</span>
        <span className="job-time muted">{fmt(job.requestedAt)}</span>
        {job.status === 'failed' && (
          <button className="btn-sm btn-retry" onClick={e => { e.stopPropagation(); onRetry(job.jobId); }}>
            Retry
          </button>
        )}
        {job.status === 'queued' && (
          <button className="btn-sm btn-cancel" onClick={e => { e.stopPropagation(); onCancel(job.jobId); }}>
            Cancel
          </button>
        )}
        <span className="expand-arrow">{expanded ? '▲' : '▼'}</span>
      </div>

      {expanded && (
        <div className="job-detail" onClick={e => e.stopPropagation()}>
          {job.modelVersion && <p className="mono muted small">Model: {job.modelVersion}</p>}
          {job.attemptCount > 0 && <p className="mono muted small">Attempts: {job.attemptCount}</p>}

          {job.errorMessage && (
            <div className="error-box">
              <strong>{job.errorCode ?? 'Error'}:</strong> {job.errorMessage}
            </div>
          )}

          {job.transcript && (
            <div className="detail-section">
              <h4>📝 Transcript</h4>
              <p className="transcript-text">{job.transcript}</p>
            </div>
          )}

          {job.extraction && job.extraction.items.length > 0 && (
            <div className="detail-section">
              <h4>✦ Extracted suggestions <span className="muted small">({job.extraction.items.length})</span></h4>
              <p className="mono muted small">
                {job.extraction.ollamaModel} · {job.extraction.promptVersion} · {elapsed(job.extraction.processingMs)}
              </p>
              <div className="suggestion-list">
                {job.extraction.items.map((item, i) => (
                  <div key={i} className="suggestion-item">
                    <div className="suggestion-header">
                      <span className="kind-icon">{KIND_ICON[item.kind] ?? '◈'}</span>
                      <strong>{item.title}</strong>
                      <span className="conf-badge" style={{ color: CONF_COLOR[item.confidence] }}>
                        {item.confidence}
                      </span>
                    </div>
                    {item.body && <p className="suggestion-body">{item.body}</p>}
                    <div className="suggestion-meta">
                      {item.spaceHint && <span className="meta-tag">📁 {item.spaceHint}</span>}
                      {item.dateExpression && <span className="meta-tag">🗓 {item.dateExpression}</span>}
                      <span className="meta-tag kind-tag">{item.kind}</span>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {job.extraction && job.extraction.items.length === 0 && job.transcript && (
            <div className="detail-section muted small">
              <em>No extractable items found in this capture.</em>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

function SetupGuide({ status }: { status: WorkerStatus | null }) {
  if (!status) return null;
  const needsWhisper = !status.whisperAvailable;
  const needsOllama = !status.ollamaAvailable;
  if (!needsWhisper && !needsOllama) return null;

  return (
    <div className="setup-guide">
      <h3>⚙️ Setup Required</h3>
      {needsWhisper && (
        <div className="setup-step">
          <h4>1. Install whisper.cpp <span className="badge-required">Required for transcription</span></h4>
          <p>Download and compile <a href="https://github.com/ggml-org/whisper.cpp" target="_blank" rel="noopener">whisper.cpp</a>, then place the binary at:</p>
          <code className="mono">%USERPROFILE%\whisper.cpp\build\bin\whisper-cli.exe</code>
          <p>Download a model (recommended: <strong>ggml-small.bin</strong>) to:</p>
          <code className="mono">%USERPROFILE%\whisper.cpp\models\ggml-small.bin</code>
          <p className="muted small">Bengali + English accuracy: Small model recommended. Base is faster but less accurate for Bengali.</p>
        </div>
      )}
      {needsOllama && (
        <div className="setup-step">
          <h4>2. Install Ollama <span className="badge-optional">Optional — enables extraction</span></h4>
          <p>Download <a href="https://ollama.com" target="_blank" rel="noopener">Ollama</a> and run it, then pull a model:</p>
          <code className="mono">ollama pull gemma3:1b</code>
          <p className="muted small">Recommended models (fastest first): gemma3:1b, qwen2.5:0.5b, qwen2.5:1.5b, llama3.2:1b</p>
        </div>
      )}
    </div>
  );
}

// ─── Main App ─────────────────────────────────────────────────────────────────

export default function App() {
  const [status, setStatus] = useState<WorkerStatus | null>(null);
  const [jobs, setJobs] = useState<Job[]>([]);
  const [expandedJob, setExpandedJob] = useState<string | null>(null);
  const [refreshing, setRefreshing] = useState(false);
  const [tab, setTab] = useState<'queue' | 'setup' | 'about'>('queue');
  const pollRef = useRef<ReturnType<typeof setInterval> | null>(null);
  const noIpc = !ipc;

  const loadStatus = useCallback(async () => {
    if (!ipc) return;
    try {
      const s = await ipc.getStatus();
      setStatus(s);
    } catch (e) { console.error(e); }
  }, []);

  const loadJobs = useCallback(async () => {
    if (!ipc) return;
    try {
      const { jobs: list } = await ipc.getJobs();
      setJobs(list);
    } catch (e) { console.error(e); }
  }, []);

  useEffect(() => {
    if (!ipc) return;
    ipc.onStatus((data) => { setStatus(data); loadJobs(); });
    loadStatus();
    loadJobs();
    pollRef.current = setInterval(() => { loadStatus(); loadJobs(); }, 5000);
    return () => { if (pollRef.current) clearInterval(pollRef.current); };
  }, [loadStatus, loadJobs]);

  const handleRetry = async (jobId: string) => {
    await ipc?.retryJob(jobId);
    await loadJobs();
  };
  const handleCancel = async (jobId: string) => {
    await ipc?.cancelJob(jobId);
    await loadJobs();
  };
  const handleRefreshAi = async () => {
    setRefreshing(true);
    await ipc?.refreshAi();
    await loadStatus();
    setRefreshing(false);
  };

  const queued   = jobs.filter(j => j.status === 'queued').length;
  const running  = jobs.filter(j => j.status === 'running').length;
  const done     = jobs.filter(j => j.status === 'completed').length;
  const failed   = jobs.filter(j => j.status === 'failed').length;

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand-mark">P&B</div>
        <div className="brand-name">P&B MGT Sys<small>AI WORKER</small></div>

        <nav aria-label="Main navigation">
          <button className={tab === 'queue' ? 'selected' : ''} onClick={() => setTab('queue')}>
            ⟳ Processing Queue
          </button>
          <button className={tab === 'setup' ? 'selected' : ''} onClick={() => setTab('setup')}>
            ⚙️ Setup
          </button>
          <button className={tab === 'about' ? 'selected' : ''} onClick={() => setTab('about')}>
            ♧ About
          </button>
        </nav>

        {status && (
          <div className="sidebar-status">
            <StatusChip ok={status.whisperAvailable} label={status.whisperAvailable ? `Whisper (${status.whisperModel})` : 'Whisper not found'} />
            <StatusChip ok={status.ollamaAvailable} label={status.ollamaAvailable ? `Ollama (${status.ollamaModel})` : 'Ollama not found'} />
            <StatusChip ok={true} label={`Server :${status.port}`} />
          </div>
        )}

        <button className="btn-refresh" onClick={handleRefreshAi} disabled={refreshing}>
          {refreshing ? 'Checking…' : '↻ Check AI Tools'}
        </button>
      </aside>

      <main>
        <div className="topline">
          <span>P&B MGT SYS WORKER</span>
          <span className="status">
            <i className={status?.processingJob ? 'dot-pulse' : 'dot-idle'} />
            {status ? (status.processingJob ? 'Processing…' : `v1.1 · up ${uptime(status.uptime)}`) : 'Starting…'}
          </span>
        </div>

        {noIpc && (
          <div className="notice-banner">
            ⚠️ Running in browser preview. Electron IPC is not available. Launch via <code>npm run desktop:dev</code>.
          </div>
        )}

        {tab === 'queue' && (
          <section className="queue-section">
            <div className="queue-stats">
              <div className="stat"><span className="stat-num">{queued}</span><span className="stat-label">Queued</span></div>
              <div className="stat running"><span className="stat-num">{running}</span><span className="stat-label">Running</span></div>
              <div className="stat done"><span className="stat-num">{done}</span><span className="stat-label">Done</span></div>
              <div className="stat failed"><span className="stat-num">{failed}</span><span className="stat-label">Failed</span></div>
              <div className="stat"><span className="stat-num">{status?.processedToday ?? 0}</span><span className="stat-label">Today</span></div>
            </div>

            {status && (
              <div className="server-url-bar">
                <span className="mono small">Android → </span>
                <code className="mono server-url">{status.serverUrl}/api/jobs</code>
                <span className="muted small"> (POST audio here)</span>
              </div>
            )}

            {jobs.length === 0 ? (
              <div className="empty-state">
                <p>No jobs yet.</p>
                <p className="muted small">When your Android app sends audio, jobs will appear here.</p>
              </div>
            ) : (
              <div className="job-list">
                {jobs.map(job => (
                  <JobCard
                    key={job.jobId}
                    job={job}
                    onRetry={handleRetry}
                    onCancel={handleCancel}
                    expanded={expandedJob === job.jobId}
                    onToggle={() => setExpandedJob(prev => prev === job.jobId ? null : job.jobId)}
                  />
                ))}
              </div>
            )}
          </section>
        )}

        {tab === 'setup' && (
          <section className="setup-section">
            <h2>Setup Guide</h2>
            {status && !status.whisperAvailable && !status.ollamaAvailable ? (
              <SetupGuide status={status} />
            ) : (
              <div>
                <SetupGuide status={status} />
                {status?.whisperAvailable && status?.ollamaAvailable && (
                  <div className="all-good">
                    ✅ Both whisper.cpp and Ollama are detected. The worker is ready to process audio.
                  </div>
                )}
              </div>
            )}
          </section>
        )}

        {tab === 'about' && (
          <section className="about-section-main">
            <h2>P&B MGT Sys — AI Worker v1.1</h2>
            <p>Local AI transcription and extraction for the Personal &amp; Business Life Management System.</p>
            <ul>
              <li>🔒 100% local — no cloud, no telemetry, no subscriptions</li>
              <li>🎙 whisper.cpp — offline Bengali + English transcription</li>
              <li>🤖 Ollama — local LLM extraction (task / note / idea suggestions)</li>
              <li>📱 Android app → HTTP → Worker → suggestions back to phone</li>
              <li>✅ All suggestions require your review before becoming records</li>
            </ul>
            <p className="muted small">Crafted with ❤️ by NasirPintu (Mohammad Nasiruddin Pintu)</p>
          </section>
        )}
      </main>
    </div>
  );
}
