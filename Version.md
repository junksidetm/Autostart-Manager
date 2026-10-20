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

---

### [Production Release v1.0.0-b1] - 2026-10-20 14:54:00 IST
- **Status:** Production Deployed (100%)
- **Release Tag:** v1.0.0-b1
- **GitHub Run:** 39399908039
- **Build Duration:** 3m 34s
- **Keystore Branch:** origin/ci-keystore (Permanent RSA 2048-bit production signing key stored)
- **Artifacts:**
  - Autostart-Manager-universal.apk
  - Autostart-Manager-universal.apk.sha256
- **Release Verification:** Verified against Android Signature Scheme v1 and v2 using apksigner.
- **Direct Download:** https://github.com/junksidetm/Autostart-Manager/releases/tag/v1.0.0-b1

---

### [Production Release v2.0.1] - 2026-10-20 16:25:00 IST
- **Status:** Production Deployed (100%)
- **Release Tag:** v2.0.1
- **Version:** 2.0.1 (versionCode 2)
- **Changes & Improvements:**
  - Official Icon Update: Implemented black foreground on off-white background adaptive icon following 66dp safe zone standards (`AutoStart Manager.svg`).
  - Reboot Autostart Filtering: Strictly queries and displays only packages declaring `RECEIVE_BOOT_COMPLETED` or registered boot broadcast receivers (`BOOT_COMPLETED`, `LOCKED_BOOT_COMPLETED`, `QUICKBOOT_POWERON`).
  - Exclusion of Non-Boot Apps: Removed general non-boot applications (e.g. offline games, static utilities) from the list.
  - Reboot Toggling via Shizuku: Full control of AppOp `BOOT_COMPLETED` (allow/ignore) and broadcast receiver component states (`pm enable/disable`).
  - Updated UI explainer copy clarifying post-reboot autonomous start and force-stop persistence.


---

## [2.1.0] - 2026-10-20 17:02:00
- **Status:** Production Ready (100%)
- **Release Tag:** v2.1.0
- **Version:** 2.1.0 (versionCode 3)
- **Changes & Improvements:**
  - Icon Pure White Background: Changed adaptive icon canvas background and badge mask from #FEF9FF to pure #FFFFFF.
  - Suffix Removal: Removed build number suffix from tags and releases, standardizing on v2.1.0.
  - Equalized Pill Containers: Re-architected pill filter bar with identical 40dp height, equal weight distribution, and auto-scaled label typography so counts for All, Allowed, and Not Allowed remain visible across all screen sizes.
  - Contextual Swipe Animation: Added directional horizontal sliding animations (`AnimatedContent`) when switching between All, Allowed, and Not Allowed tabs based on navigation direction.
  - Switch Color Contrast: Updated on/off switch colors to high-contrast Material 3 Expressive theme specifications.
  - Banner Simplification: Removed "Elevated AppOps..." text from Shizuku connection banner.
  - Square Checkbox Indicator: Enhanced "Show system apps" menu item with a permanently visible square container and checkmark.
  - Empty About Page: Added new About screen scaffold with navigation and back handler, prepared for Figma design integration.
