# OffensiveAndroid 

An advanced Android security research, telemetry, and system-auditing application built in Kotlin. This project demonstrates deep Android OS integrations, Content Provider querying, hardware-level media recording (Camera2 & MediaRecorder), location tracking, local shell command execution within the Android application sandbox, and centralized JSON data aggregation for server/C2 communication.

---

##  Features & Architecture

### 1. Robust Permission Orchestration (`PermissionUtils.kt`)
* **Why:** Modern Android requires granular runtime permissions that change across API versions. 
* **What it does:** Centralized permission checking and requesting for SMS, Contacts, Call Logs, Foreground/Background Location, Notifications, Audio/Microphone, and Camera, with full support for Android 14+ foreground service constraints.

### 2. Data Repository & JSON Aggregator (`DataRepository.kt`)
* **Why:** To centralize data retrieval and format telemetry into standardized JSON payloads for server transmission (C2 / HTTP server).
* **What it does:** Aggregates SMS, Call Logs, Contacts, Clipboard data, and Location telemetry, converting them into structured `JSONObject` / `JSONArray` responses with robust error and permission-denied handling.

### 3. Contacts Retriever (`ContactsRetriever.kt`)
* **Why:** To query device address books programmatically.
* **What it does:** Interfaces with `ContactsContract.CommonDataKinds.Phone.CONTENT_URI` via Android's Content Resolver to extract and log all contact names and phone numbers alphabetically.

### 4. Call Log Inspector (`CallLogsRetriever.kt`)
* **Why:** To audit device communication history.
* **What it does:** Queries `CallLog.Calls.CONTENT_URI` using a descending timestamp sort order (`DATE DESC`) to retrieve phone numbers, contact names (`CACHED_NAME`), timestamps, durations, and call types (Incoming, Outgoing, Missed).

### 5. SMS Retriever (`SMSRetriever.kt`)
* **Why:** To inspect messaging databases.
* **What it does:** Queries system SMS content providers to retrieve stored text messages.

### 6. Location Tracking & Settings (`LocationUtils.kt`)
* **Why:** For precise geolocation telemetry and automatic GPS activation.
* **What it does:** Uses Google Play Services Location API (`Priority.PRIORITY_HIGH_ACCURACY`) to stream location updates and automatically prompt users via `ResolvableApiException` if location services are disabled.

### 7. Clipboard Monitor (`GetClipboard.kt`)
* **Why:** To inspect active clipboard content.
* **What it does:** Interacts with `ClipboardManager` to safely read and extract current text data stored on the system clipboard.

### 8. Shell Command Executor (`ShellCmdExe.kt`)
* **Why:** To evaluate system access and process execution within the app sandbox.
* **What it does:** Uses Java's `ProcessBuilder` to execute local shell commands (e.g., `whoami`) and capture combined `stdout` and `stderr` streams via `BufferedReader`.

### 9. Background Audio Recorder (`AudioRecorder.kt`)
* **Why:** For continuous background audio surveillance / recording.
* **What it does:** Implemented as a persistent **Foreground Service** utilizing `MediaRecorder` (`AudioSource.MIC`) to record microphone input into `.3gp` files stored securely in app-private external storage (`getExternalFilesDir`).

### 10. Background Video Recorder (`VideoRecorder.kt`)
* **Why:** For background video capture using device cameras.
* **What it does:** A powerful Foreground Service combining the **Camera2 API** (`CameraDevice`, `CameraCaptureSession`) with `MediaRecorder` (`VideoSource.SURFACE`) to record high-definition `.mp4` video from either the rear (`"0"`) or front (`"1"`) camera.

### 11. Persistent Foreground Service (`MyForegroundService.kt`)
* **Why:** To keep background operations alive and compliant with Android OS lifecycle rules.
* **What it does:** Maintains a persistent notification channel and handles background service start restrictions introduced in Android 12+.

---

##  Tech Stack & Requirements
* **Language:** Kotlin
* **Min SDK:** 24 (Android 7.0)
* **Compile/Target SDK:** 37 (Android 16 / Latest)
* **Key Dependencies:** 
  * AndroidX AppCompat & Core KTX
  * Google Play Services Location (`play-services-location`) & Tasks (`play-services-tasks`)
  * Jetpack Compose & Material 3 UI libraries

---
---