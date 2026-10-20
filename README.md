# Autostart Manager

A focused, native Android utility built with **Jetpack Compose** and **Material 3 Expressive** to open hidden OEM autostart settings and manage application background execution permissions via **Shizuku** and **Shevery**.

---

## Technical Overview

Modern Android operating systems (such as Realme UI, ColorOS, HyperOS, and One UI) employ customized background management engines that frequently restrict background app initialization. 

Autostart Manager provides a two-phase solution:
1. **Direct OEM Settings Launch**: Attempts an automated intent resolution cascade across manufacturer-specific startup manager components. If the OEM ROM does not expose an accessible startup activity, a system notification informs the user.
2. **Elevated Shizuku / Shevery Bridge**: Utilizes non-root privileged binder IPC (UID 2000) to inspect and toggle AppOps permissions (`BOOT_COMPLETED`, `RUN_IN_BACKGROUND`, `RUN_ANY_IN_BACKGROUND`) directly on device.

---

## Core Specifications

| Attribute | Specification |
| :--- | :--- |
| **Platform** | Native Android (100% Kotlin) |
| **UI Framework** | Jetpack Compose (BOM 2024.10.01) |
| **Design System** | Material 3 Expressive |
| **Target SDK** | Android 15 (API 35) |
| **Min SDK** | Android 8.0 (API 26) |
| **Privilege Bridge** | Shizuku API 13.1.5 (Compatible with Shevery) |
| **Signing** | Production RSA 2048-bit with Signature Schemes v1 & v2 |

---

## Architecture & Implementation

### 1. Intent Resolution Cascade
The app queries the device package manager across known startup activity signatures:
- **Realme UI / ColorOS**: `com.coloros.safecenter.permission.startup.StartupAppListActivity`, `com.oplus.battery.settings.AppBatteryManagerActivity`
- **Xiaomi HyperOS / MIUI**: `com.miui.permcenter.autostart.AutoStartManagementActivity`
- **Huawei / Honor**: `com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity`
- **Vivo OriginOS / FuntouchOS**: `com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity`
- **Samsung One UI**: `com.samsung.android.sm.battery.ui.BatteryActivity`

If an activity is unexported, the app automatically attempts privileged invocation via Shizuku shell (`am start -n`). If no target activity exists, a standard toast notification is emitted.

### 2. Privileged AppOps Management
When connected to Shizuku or Shevery, the application reads and modifies runtime operation modes:
- **Allowed**: Grants `BOOT_COMPLETED` and `RUN_IN_BACKGROUND` permissions, whitelisting the package in device idle policies.
- **Not Allowed**: Sets `BOOT_COMPLETED` and `RUN_IN_BACKGROUND` to `ignore` mode.

---

## Build and Release Verification

All release artifacts are compiled and signed in a remote CI/CD environment using GitHub Actions. Local binary builds are intentionally disabled to conserve host resources.

### Verification Commands
```bash
# Verify production signature
apksigner verify --verbose Autostart-Manager-universal.apk
```

---

## License

This project is licensed under the Apache License 2.0.
