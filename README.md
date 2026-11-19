# P&B MGT Sys

**Full project name:** Personal & Business Life Management System  
**Descriptor:** Private life & work organizer  
**Tagline:** Your life and work, in one calm place.  
**Functional line:** Say it. Save it. Find it. Act on it.

An Android-first, offline-capable personal and business management system with an optional Windows desktop worker. Product requirements and roadmap are documented in [the master specification](Personal_Business_Life_OS_Project_Details_v1.0.md) and [the execution roadmap](Personal_Business_Life_OS_Version_Based_Development_Roadmap_v1.0.md).

## Milestone status

The repository contains the completed **v0.1** foundation and **v0.2** Local Capture Core. The Android app features Room-backed text/voice capture, app-private audio storage, playback, and interrupted-recording recovery, built and verified on device (OPPO A3x). See [HANDOVER.md](HANDOVER.md) for details and next actions.

## Project structure

- `apps/android` — Android capture and organizer app (Kotlin/Jetpack Compose)
- `apps/desktop` — Windows local worker shell (Electron/React/TypeScript)
- `packages/shared-types` — cross-platform domain types
- `database/migrations` — versioned SQLite schema
- `docs` — requirements, architecture and decisions

## Development

The Android app is built with Gradle and the desktop app with npm. From the repository root, run `npm install`, `npm run build`, and (with Gradle/Android SDK installed) `npm run build:android`. Do not treat an unverified scaffold as a working build.

---

## Creator & Vision

**NasirPintu** (Mohammad Nasiruddin Pintu)  
*Creator, Product Architect & Open-Source Builder*

* **GitHub:** [@nasiropintu](https://github.com/nasiropintu)
* **X (Twitter):** [@nasiropintu](https://x.com/nasiropintu)
* **Instagram:** [@the_np2](https://www.instagram.com/the_np2/)
* **Facebook:** [mohammadnasiruddin.pintu](https://www.facebook.com/mohammadnasiruddin.pintu/)

## Human + AI Collaboration Credits

Architected and guided by **NasirPintu** in collaboration with AI assistants:

* **Gemini** (Google DeepMind) — Architecture and agentic workflows.
* **Manus** (Manus AI) — Autonomous planning and synthesis.
* **ChatGPT** (OpenAI) — Logic synthesis and reasoning.
* **DeepSeek** — Algorithmic problem-solving.
* **Claude** (Anthropic) — Systems reasoning and technical drafting.
* **Vibe by Mistral** (Mistral AI) — Fast generation and execution.

## Open-source acknowledgements

The About & Credits panel links to the projects used by the current application shells:

* **React** and **React DOM** — Meta; desktop interface components and rendering.
* **Electron** — OpenJS Foundation; Windows desktop runtime.
* **Vite** — Vite Team; frontend development and production bundling.
* **TypeScript** — Microsoft; typed desktop code and shared contracts.
* **Kotlin** — JetBrains; Android application language.
* **Jetpack Compose**, **Material 3**, **AndroidX Activity Compose**, **Room**, and **Lifecycle Runtime Compose** — AndroidX/Google; Android UI, lifecycle, and local persistence.
* **Gradle** and the **Android Gradle Plugin** — build automation and Android packaging.
* **electron-builder** — Windows application packaging and installer generation.

The About & Credits cards link to each project’s official home or documentation page.

**Crafted with ❤️ by NasirPintu**
