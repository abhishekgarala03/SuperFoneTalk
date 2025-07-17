# SuperFoneTalk

A simple real‑time “stranger calling” Android application built with Modern Android Development. Users sign in with Google, get randomly matched with another online user via Firestore, and start an call using Jitsi Meet SDK.

---

## App Description

- **Google Sign‑In**  
  Authenticate users with Firebase Authentication.
- **Random Matching**  
  Uses Firestore “waiting” collection to pair two online users in real time.
- **Voice Call**  
  Launches a Jitsi Meet conference room named `call_<userId1><userId2>`.
- **In‑Call Controls**  
  - End Call  
  - (Via Jitsi UI) Mute/Unmute, Speaker toggle, call duration timer  

---

## Tech Stack

- **Language & UI**  
  - Kotlin  
  - Jetpack Compose  
- **Architecture & State**  
  - MVI Architecture 
  - Kotlin Coroutines & Flow  
- **Backend & Data**  
  - Firebase Authentication 
  - Google Firestore (matchmaking)
- **RTC**  
  - [Jitsi Meet SDK](https://jitsi.github.io/handbook/docs/dev-guide/dev-guide-android-sdk)
- **DI & Async**  
  - Hilt for dependency injection  
  - `kotlinx-coroutines-play-services` for Firebase task support  

---

## Requirements

- **IDE & Tools**  
  - Android Studio Koala or later  
  - Kotlin ≥ 1.8.0  
- **Firebase**  
  - A Firebase project with Firestore & Authentication enabled  
  - `google‑services.json` in `app/`  
- **SDKs & Permissions**  
  - Jitsi Meet SDK dependency in `build.gradle`  
  - Runtime permissions:
    - `RECORD_AUDIO`  
    - `INTERNET`  

---

## Project Setup

### Add Firebase config

Copy your #google-services.json into #app/.

In the Firebase console, enable:

Firestore (in test mode or with proper rules)

Authentication → Sign‑in method → Google

### Sync & Build

Open in Android Studio, let Gradle sync.

Ensure Hilt & Play Services coroutines dependencies are resolved.

### Run on Device/Emulator

Grant audio and internet permissions.

Tap Sign In, then Find Stranger.

Accept any runtime mic permissions.

When matched, the Jitsi Meet activity launches call.

---

## Usage Flow
Sign In → Google pop‑up → grants profile & email.

Home Screen → “Find Stranger” button.

Matchmaking → waits for another user → once found, auto‑launch call.

In‑Call → use Jitsi UI to mute/unmute, toggle speaker, and see timer.

End Call → Return to home; ready to find another stranger.
