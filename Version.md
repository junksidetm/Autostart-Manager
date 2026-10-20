# Version & Changelog History

## Project: Autostart Manager
**Created:** 2026-10-20 14:48:00 IST
**Architecture:** Native Android (Kotlin + Jetpack Compose)
**Design Language:** Material 3 Expressive
**Privilege Bridge:** Shizuku API (with Shevery & Standalone ADB support)

---

### [Initial Version] - 2026-10-20 14:48:00 IST
- **Status:** Initialized (100%)
- **Target SDK:** 35 (Android 15)
- **Min SDK:** 26 (Android 8.0)
- **Compile SDK:** 35
- **Libraries & Tools:**
  - Gradle: 8.9
  - Android Gradle Plugin: 8.7.2
  - Kotlin: 2.0.21
  - Jetpack Compose BOM: 2024.10.01
  - Material 3: 1.3.1
  - Coroutines: 1.9.0
  - Shizuku API & Provider: 13.1.5
- **Components Implemented:**
  - NativeSettingsOpener: Multi-tier OEM autostart settings resolution cascade with toast fallback.
  - ShizukuManager: Elevated AppOps reader and setter (BOOT_COMPLETED, RUN_IN_BACKGROUND).
  - AutostartViewModel: StateFlow management with real-time package querying and filter evaluation.
  - PillFilterRow: Material 3 pill containers for All, Allowed, and Not Allowed categories.
  - AppAutostartRow: Clean flat list row with asynchronous app icon loading and instant toggle switch.
  - HomeScreen: Center-aligned top bar, overflow menu with system app filter, Shizuku banner, and explainer section.
  - Build Workflow: GitHub Actions production pipeline with persistent 2048-bit RSA signing.
