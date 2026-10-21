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


---

## [3.2.0] - 2026-10-20 17:26:50
- **Status:** Production Ready (100%)
- **Release Tag:** v3.2.0
- **Version:** 3.2.0 (versionCode 4)
- **Changes & Improvements:**
  - Decoupled Autostart from Battery State: Verified and reinforced that reboot autostart operations (BOOT_COMPLETED AppOp and boot receiver components) operate completely independent of runtime background battery permissions.
  - Strict Boundary Isolation: Ensured no runtime AppOps (such as RUN_ANY_IN_BACKGROUND) or Doze whitelists are ever modified by Autostart Manager, preserving user-configured Unrestricted or Optimized battery modes in Battery Mode Checker.

## [2026-10-21 00:30:00 IST] - Comprehensive Repository Directory & File System Index
- **Action**: Fully indexed all repository directories (26) and files (46), mapping structural hierarchy and defining the functional responsibility of each component.
- **Directory Structure & Purpose**:
  - `.github/`: GitHub platform repository configuration and community templates.
  - `.github/workflows/`: GitHub Actions CI/CD workflows for builds, releases, and tests.
  - `app/`: Primary Android application module housing manifests, source code, and resources.
  - `app/src/`: Application source code root divided into main and test variants.
  - `app/src/main/`: Production source set containing Kotlin/Java application code and resources.
  - `app/src/main/java/`: Android Kotlin/Java source root directory.
  - `app/src/main/java/com/`: Android Kotlin/Java package: `com`.
  - `app/src/main/java/com/autostart/`: Android Kotlin/Java package: `com/autostart`.
  - `app/src/main/java/com/autostart/manager/`: Android Kotlin/Java package: `com/autostart/manager`.
  - `app/src/main/java/com/autostart/manager/model/`: Android Kotlin/Java package: `com/autostart/manager/model`.
  - `app/src/main/java/com/autostart/manager/service/`: Android Kotlin/Java package: `com/autostart/manager/service`.
  - `app/src/main/java/com/autostart/manager/ui/`: Android Kotlin/Java package: `com/autostart/manager/ui`.
  - `app/src/main/java/com/autostart/manager/ui/components/`: Android Kotlin/Java package: `com/autostart/manager/ui/components`.
  - `app/src/main/java/com/autostart/manager/ui/screens/`: Android Kotlin/Java package: `com/autostart/manager/ui/screens`.
  - `app/src/main/java/com/autostart/manager/ui/theme/`: Android Kotlin/Java package: `com/autostart/manager/ui/theme`.
  - `app/src/main/java/com/autostart/manager/util/`: Android Kotlin/Java package: `com/autostart/manager/util`.
  - `app/src/main/java/com/autostart/manager/viewmodel/`: Android Kotlin/Java package: `com/autostart/manager/viewmodel`.
  - `app/src/main/res/`: Android application UI resources root.
  - `app/src/main/res/drawable/`: Android vector and raster drawable assets.
  - `app/src/main/res/mipmap-anydpi-v26/`: Android application launcher icons for various device densities.
  - `app/src/main/res/values/`: Android XML resource values (colors, themes, strings, dimensions).
  - `app/src/main/res/xml/`: Android system XML configuration specifications.
  - `assets/`: Visual assets, iconography, vector drawables, images, and fonts.
  - `assets/images/`: Asset directory storing images resources.
  - `gradle/`: Gradle wrapper distribution configurations and wrapper JAR binary.
  - `gradle/wrapper/`: Gradle wrapper bootstrapping properties and executable JAR.
- **File System Manifest & Functional Roles**:
  - `.github/workflows/build.yml`: Automated CI/CD workflow (`Build & Publish Production Android APK`) managing pipeline stages, builds, tests, or releases.
  - `.gitignore`: Git exclusion specifications ignoring build artifacts, local secrets, and IDE temporary caches.
  - `app/build.gradle.kts`: Gradle build script defining compiler plugins, dependencies, and build configurations.
  - `app/proguard-rules.pro`: R8 / ProGuard code shrinking, obfuscation, and optimization rules.
  - `app/src/main/AndroidManifest.xml`: Android application manifest defining package identity, permissions, services, and activities.
  - `app/src/main/java/com/autostart/manager/MainActivity.kt`: Defines `MainActivity` implementing application logic, UI composable, or data entity.
  - `app/src/main/java/com/autostart/manager/model/AutostartAppInfo.kt`: Defines `AutostartAppInfo` implementing application logic, UI composable, or data entity.
  - `app/src/main/java/com/autostart/manager/model/AutostartFilter.kt`: Defines `AutostartFilter` implementing application logic, UI composable, or data entity.
  - `app/src/main/java/com/autostart/manager/service/ShizukuManager.kt`: Executes an ADB shell command via Shizuku reflection to support all Android versions.
  - `app/src/main/java/com/autostart/manager/ui/components/AppAutostartRow.kt`: App Name and Details.
  - `app/src/main/java/com/autostart/manager/ui/components/AppIcon.kt`: Defines `AppIcon` implementing application logic, UI composable, or data entity.
  - `app/src/main/java/com/autostart/manager/ui/components/PillFilterRow.kt`: Defines `PillFilterRow` implementing application logic, UI composable, or data entity.
  - `app/src/main/java/com/autostart/manager/ui/components/ShizukuBanner.kt`: Defines `ShizukuBanner` implementing application logic, UI composable, or data entity.
  - `app/src/main/java/com/autostart/manager/ui/screens/AboutScreen.kt`: Defines `AboutScreen` implementing application logic, UI composable, or data entity.
  - `app/src/main/java/com/autostart/manager/ui/screens/HomeScreen.kt`: Defines `HomeScreen` implementing application logic, UI composable, or data entity.
  - `app/src/main/java/com/autostart/manager/ui/theme/Color.kt`: Primary Indigo / Expressive Azure.
  - `app/src/main/java/com/autostart/manager/ui/theme/Theme.kt`: Defines `AutostartManagerTheme` implementing application logic, UI composable, or data entity.
  - `app/src/main/java/com/autostart/manager/ui/theme/Type.kt`: Kotlin source implementation file.
  - `app/src/main/java/com/autostart/manager/util/NativeSettingsOpener.kt`: Exhaustive registry of OEM autostart / auto-launch activities.
  - `app/src/main/java/com/autostart/manager/viewmodel/AutostartViewModel.kt`: Defines `AutostartUiState` implementing application logic, UI composable, or data entity.
  - `app/src/main/res/drawable/ic_android_logo.xml`: Android XML layout, value dictionary, or system configuration definition.
  - `app/src/main/res/drawable/ic_jetpack_compose_logo.xml`: Android XML layout, value dictionary, or system configuration definition.
  - `app/src/main/res/drawable/ic_launcher_background.xml`: Android XML layout, value dictionary, or system configuration definition.
  - `app/src/main/res/drawable/ic_launcher_foreground.xml`: Android XML layout, value dictionary, or system configuration definition.
  - `app/src/main/res/drawable/ic_launcher_monochrome.xml`: Android XML layout, value dictionary, or system configuration definition.
  - `app/src/main/res/drawable/ic_shevery_logo.xml`: Android XML layout, value dictionary, or system configuration definition.
  - `app/src/main/res/drawable/ic_shizuku_logo.xml`: Android XML layout, value dictionary, or system configuration definition.
  - `app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml`: Android XML layout, value dictionary, or system configuration definition.
  - `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml`: Android XML layout, value dictionary, or system configuration definition.
  - `app/src/main/res/values/strings.xml`: Android XML layout, value dictionary, or system configuration definition.
  - `app/src/main/res/values/themes.xml`: Android XML layout, value dictionary, or system configuration definition.
  - `app/src/main/res/xml/backup_rules.xml`: Android XML layout, value dictionary, or system configuration definition.
  - `app/src/main/res/xml/data_extraction_rules.xml`: Android XML layout, value dictionary, or system configuration definition.
  - `assets/images/AutoStart Manager.svg`: Scalable vector graphic resource for UI iconography and branding.
  - `assets/images/ic_launcher_foreground.svg`: Scalable vector graphic resource for UI iconography and branding.
  - `build.gradle.kts`: Gradle build script defining compiler plugins, dependencies, and build configurations.
  - `gradle/libs.versions.toml`: Shizuku & Shevery privilege bridge.
  - `gradle/wrapper/gradle-wrapper.jar`: Gradle wrapper Java runtime binary used to bootstrap Gradle.
  - `gradle/wrapper/gradle-wrapper.properties`: Configures the pinned Gradle distribution version and download URL for the wrapper.
  - `gradle.properties`: Gradle daemon runtime flags, JVM memory allocation limits, and AndroidX enablement.
  - `gradlew`: Unix shell executable for the Gradle build wrapper.
  - `gradlew.bat`: Windows batch script executable for the Gradle build wrapper.
  - `LICENSE`: Open-source software license defining terms of use, distribution, and attribution.
  - `README.md`: Repository documentation, architectural overview, usage instructions, and downstream integration guidelines.
  - `settings.gradle.kts`: Gradle settings script defining repository sources and module inclusion.
  - `Version.md`: Permanent, immutable project ledger tracking historical changelogs, architecture evolution, and component manifests.
- **Status**: 100% (Completed & Verified across repository)


## [2026-10-21 00:52:00 IST] - Strategic Roadmap & Architecture Realignment Plan
- **Action**: Formulated comprehensive architectural alignment plan addressing elevated Shizuku component inspection, OEM Auto-Launch context for Realme UI 6.0, and strict preservation of system app filtering.
- **Scope & Objectives**:
  1. **Privileged Component State Resolution (`ShizukuManager.kt` / `AutostartViewModel.kt`)**:
     - Upgrade component disabled state verification to utilize elevated Shizuku shell querying (`pm list packages -d` or `pm query-receivers -d`) rather than unprivileged context `packageManager.getComponentEnabledSetting()`.
     - Eliminate false positives where components disabled via ADB/Shizuku report as default enabled to unprivileged apps.
  2. **Preservation of Intentional System App Filtering Architecture**:
     - Retain intentional default isolation of third-party user apps from pre-installed system apps, maintaining the active square-checkbox menu toggle as intentionally designed.
  3. **Realme UI 6.0 Auto-Launch Integration Context (`HomeScreen.kt` / `NativeSettingsOpener.kt`)**:
     - Document and surface educational context regarding Realme UI 6.0 / ColorOS 15 `com.oplus.safecenter` broadcast filtering.
     - Reinforce that disabling boot receivers via Shizuku's `pm disable` remains the universal, immutable mechanism that stops boot execution regardless of OEM settings.
- **Status**: 100% (Plan Formulated & Recorded)


## [2026-10-21 00:58:00 IST] - Elevated Shizuku Component State Resolution & Realme UI Guidance
- **Action**: Upgraded boot receiver disabled-state detection to elevated Shizuku shell querying and enriched OEM Auto-Launch context.
- **Components Modified**:
  - `app/src/main/java/com/autostart/manager/service/ShizukuManager.kt`:
    - Added `getDisabledReceiverComponents()` utilizing elevated `pm query-receivers -d` via Shizuku shell to discover disabled boot receivers across all packages with system authority.
  - `app/src/main/java/com/autostart/manager/viewmodel/AutostartViewModel.kt`:
    - Integrated `disabledComponentsFromShizuku` into `loadBootCapableApps()`, resolving component states with elevated shell authority and falling back gracefully to context package manager when Shizuku is disconnected.
  - `app/src/main/java/com/autostart/manager/ui/screens/HomeScreen.kt`:
    - Enriched header explainer copy with specific technical context regarding Realme UI / ColorOS Auto-Launch and package-manager level component disabling.
- **Status**: 100% (Completed & Verified)


## [2026-10-21 01:05:00 IST] - Version 3.3.0 Release Preparation
- **Action**: Bumped application release version to v3.3.0 (versionCode 5).
- **Files Modified**:
  - `app/build.gradle.kts`: Configured `versionCode = 5` and `versionName = "3.3.0"`.
  - `Version.md`: Appended tracking entry.
- **Status**: 100% (Ready for Release Publication)


## [2026-10-21 10:14:00 IST] - Version 3.4.0: Home Screen Dark Mode Icon Color Flipping & Scale Optimization
- **Action**: Upgraded application release version to v3.4.0 (versionCode 6) with dynamic dark mode color flipping for top-left brand navigation icon and safe-area scale compensation.
- **Components Created & Modified**:
  - `app/src/main/res/drawable/ic_launcher_foreground_dark.xml`: Created inverted SVG vector drawable where phone frame, stacked layers, and reload icon flip from black (#000000) to pure white (#FFFFFF), and badge background flips from white (#FFFFFF) to black (#000000).
  - `app/src/main/res/drawable-night/ic_launcher_foreground.xml`: Configured Android night qualifier resource for system-level dark mode resolution.
  - `app/src/main/java/com/autostart/manager/ui/screens/HomeScreen.kt`: Added `navigationIcon` in `CenterAlignedTopAppBar` utilizing `isSystemInDarkTheme()` to dynamically swap between light and dark foreground assets, enlarged icon frame to 52dp, and applied 1.3x scale to compensate for the 66px circular safe zone margins.
  - `app/build.gradle.kts`: Bumped `versionCode = 6` and `versionName = "3.4.0"`.
- **Status**: 100% (Completed & Verified)
