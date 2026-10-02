# Android Debug APK

## Automatic build

Every push to the `main` branch starts `.github/workflows/android-debug.yml`. The workflow uses JDK 17 and Gradle 8.10, builds `:app:assembleDebug`, uploads the APK as a workflow artifact, and creates a prerelease named `FLOXIN NET Debug Build <run number>` with the APK attached.

The workflow requires the repository's default `GITHUB_TOKEN` to have **read and write** permission for repository contents. This is configured in the workflow with `permissions: contents: write`. No signing key is needed for a debug APK.

## Download

1. Open the repository's **Actions** tab and select **Android Debug APK**.
2. Open a successful run and download the artifact `floxin-net-debug-apk-<run number>`, or open the **Releases** page and download `app-debug.apk` from the matching prerelease.
3. Transfer the APK to the Android device and allow installation from the file manager when Android asks.

## Install with adb

```bash
adb install -r app-debug.apk
```

The app is a debug build and is intended for testing, not Play Store distribution.

## Backend setup on Termux

The current app does **not** start the backend automatically. The reliable first-run path is:

```bash
pkg update
pkg install python curl dnsutils net-tools
cd ~/floxin-net
./install.sh
FLOXIN api start
```

The Android app then connects to `http://127.0.0.1:8080` and uses the Bearer token saved by the user in the app's DataStore settings.

A future Termux integration will use the documented `RUN_COMMAND` service rather than the internal `TermuxService` action suggested in the initial plan. It requires the Android permission `com.termux.permission.RUN_COMMAND`, Termux's `allow-external-apps=true`, and user approval in Android's additional permissions. The app will provide a fallback instruction when those prerequisites are unavailable.
