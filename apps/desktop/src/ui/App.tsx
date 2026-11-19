import { useEffect, useState } from 'react';

const socials = [
  ['GitHub', '@nasiropintu', 'https://github.com/nasiropintu'],
  ['X (Twitter)', '@nasiropintu', 'https://x.com/nasiropintu'],
  ['Instagram', '@the_np2', 'https://www.instagram.com/the_np2/'],
  ['Facebook', 'mohammadnasiruddin.pintu', 'https://www.facebook.com/mohammadnasiruddin.pintu/'],
];

const collaborators = [
  ['Gemini', 'Google DeepMind', 'Architecture & Agentic Workflows'],
  ['Manus', 'Manus AI', 'Autonomous Planning & Synthesis'],
  ['ChatGPT', 'OpenAI', 'Logic Optimization & Reasoning'],
  ['DeepSeek', 'DeepSeek', 'Algorithmic Engineering & Core Logic'],
  ['Claude', 'Anthropic', 'Systems Architecture & Clean Design'],
  ['Vibe by Mistral', 'Mistral AI', 'Fast Generation & Execution'],
];

const openSource = [
  ['React 19', 'Meta', 'Reusable interface components and application state', 'https://react.dev/'],
  ['React DOM', 'Meta', 'Rendering React interfaces in the desktop window', 'https://react.dev/'],
  ['Electron', 'OpenJS Foundation', 'Desktop runtime powered by Chromium and Node.js', 'https://www.electronjs.org/'],
  ['Vite', 'Vite Team', 'Frontend development server and production bundler', 'https://vite.dev/'],
  ['TypeScript', 'Microsoft', 'Typed application code and shared domain contracts', 'https://www.typescriptlang.org/'],
  ['Jetpack Compose & Material 3', 'AndroidX / Google', 'Native Android interface toolkit and design components', 'https://developer.android.com/jetpack/compose'],
  ['Room', 'AndroidX', 'Structured local database with SQLite-backed persistence', 'https://developer.android.com/training/data-storage/room'],
  ['Lifecycle Runtime Compose', 'AndroidX', 'Lifecycle-aware collection of local capture updates', 'https://developer.android.com/jetpack/androidx/releases/lifecycle'],
  ['Kotlin', 'JetBrains', 'Language used for the Android application', 'https://kotlinlang.org/'],
  ['AndroidX Activity Compose', 'Android Jetpack', 'Compose integration with the Android activity lifecycle', 'https://developer.android.com/jetpack/androidx'],
  ['Gradle', 'Gradle Inc.', 'Build automation and dependency management', 'https://gradle.org/'],
  ['Android Gradle Plugin', 'Google', 'Android application build and packaging support', 'https://developer.android.com/build'],
  ['electron-builder', 'electron-userland community', 'Windows desktop packaging and installer generation', 'https://www.electron.build/'],
];

function AboutDialog({ close }: { close: () => void }) {
  const [photoError, setPhotoError] = useState(false);
  useEffect(() => {
    const onKeyDown = (event: KeyboardEvent) => { if (event.key === 'Escape') close(); };
    window.addEventListener('keydown', onKeyDown);
    return () => window.removeEventListener('keydown', onKeyDown);
  }, [close]);

  return <div className="backdrop" onMouseDown={close}>
    <section className="dialog" role="dialog" aria-modal="true" aria-labelledby="about-title" onMouseDown={event => event.stopPropagation()}>
      <button className="close" aria-label="Close about dialog" onClick={close}>×</button>
      <header className="creator">
        {photoError ? <div className="avatar-fallback">NP</div> : <img src="./nasirpintu.jpg" alt="NasirPintu" onError={() => setPhotoError(true)} />}
        <div><span className="eyebrow coral">♡ Crafted with Love</span><h2 id="about-title">NasirPintu</h2>
          <p className="mono">Mohammad Nasiruddin Pintu · Creator &amp; Architect</p>
          <p>Dedicated to building free, private-by-design, serverless tools that empower people without paywalls, subscriptions, or data tracking.</p>
        </div>
      </header>

      <section className="about-section">
        <h3>Connect with NasirPintu</h3>
        <div className="social-grid">{socials.map(([name, handle, url]) => <a key={name} href={url} target="_blank" rel="noopener noreferrer">
          <span className="social-icon">{name === 'GitHub' ? '◉' : name === 'Instagram' ? '◎' : name === 'Facebook' ? 'f' : '𝕏'}</span>
          <span>{name}<small>{handle}</small></span><span className="external">↗</span>
        </a>)}</div>
      </section>

      <section className="about-section">
        <div className="section-heading"><h3>♧ &nbsp;AI Agent Collaboration Credits</h3><span className="mono muted">Human-Directed · Agent-Assisted</span></div>
        <p>Every feature in this suite is conceived and architected by <strong>NasirPintu</strong>, engineered in active synergy with frontier artificial intelligence models and autonomous coding agents.</p>
        <div className="credits-grid">{collaborators.map(([name, provider, role]) => <article className="credit" key={name}><i /><div><strong>{name}</strong><small>{provider}</small><small>{role}</small></div></article>)}</div>
      </section>

      <section className="about-section">
        <h3>‹/› &nbsp;Open-Source Community Credits</h3>
        <p>P&amp;B MGT Sys runs 100% locally on your machine with zero server costs, built on the shoulders of these open-source giants:</p>
        <div className="oss-grid">{openSource.map(([name, author, description, url]) => <a className="oss-card" href={url} target="_blank" rel="noopener noreferrer" key={name}>
          <div className="oss-head"><strong>{name}</strong><span>by {author} ↗</span></div><p>{description}</p>
        </a>)}</div>
      </section>

      <footer className="privacy-footer"><span className="shield">♧</span><span><strong>Privacy Guarantee:</strong> All document processing stays strictly in your browser. Zero uploads. Zero telemetry.</span></footer>
    </section>
  </div>;
}

export default function App() {
  const [aboutOpen, setAboutOpen] = useState(false);
  return <div className="app-shell">
    <aside className="sidebar"><div className="brand-mark">P&amp;B</div><div className="brand-name">P&amp;B MGT Sys<small>PRIVATE LIFE &amp; WORK ORGANIZER</small></div>
      <nav aria-label="Main navigation"><button className="selected">Overview</button><button disabled>Processing queue</button><button disabled>Settings</button></nav>
      <button className="brand-credit" onClick={() => setAboutOpen(true)}>Crafted with ❤️ by NasirPintu</button>
    </aside>
    <main><div className="topline"><span>P&amp;B MGT SYS</span><span className="status"><i /> Foundation preview</span></div>
      <section className="welcome"><span className="eyebrow">OPTIONAL · LOCAL · PRIVATE</span><h1>Your life and work,<br />in one calm place.</h1>
        <p>P&amp;B MGT Sys is your private life &amp; work organizer. Say it. Save it. Find it. Act on it.</p>
        <div className="notice"><span>✳</span><p><strong>Foundation preview</strong><br />Local capture, processing and synchronization will arrive in later milestones.</p></div></section>
      <section className="features"><article><span>01</span><h2>Private by design</h2><p>Designed for local processing with no required cloud account or paid AI service.</p></article><article><span>02</span><h2>Work from one place</h2><p>Keep personal and business information organized in one workspace.</p></article><article><span>03</span><h2>Stay in control</h2><p>Future AI results will be suggestions for you to review and approve.</p></article></section>
      <footer className="app-footer">P&amp;B MGT Sys <span>v0.1 foundation</span></footer>
    </main>
    {aboutOpen && <AboutDialog close={() => setAboutOpen(false)} />}
  </div>;
}
