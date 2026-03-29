# 🛡️ CallSentinel

### *Your personal call bodyguard — real-time scam & spoof detection for Android*

> CallSentinel runs silently in the background, analyzing every incoming call in real time and alerting you before you even pick up.

<br/>

[![GitHub stars](https://img.shields.io/github/stars/Ashika005/CallSentinel?style=social)](https://github.com/Ashika005/CallSentinel)
[![GitHub forks](https://img.shields.io/github/forks/Ashika005/CallSentinel?style=social)](https://github.com/Ashika005/CallSentinel)

### 🎬 [Click here to watch the Live Demo](https://drive.google.com/drive/folders/1pz7v3JrH-R2RKrgq2ran6l0I_pm3tTuU)

</div>

---

## 📱 What is CallSentinel?

Every day, millions of people fall victim to **spam calls, scam calls, and neighbor spoofing** — where fraudsters disguise their number to look like someone you know. CallSentinel is an Android app that fights back.

It monitors every incoming call, runs it through a **Risk Engine**, calculates a threat score, and shows you a warning overlay — all **before you answer**.

---

## 🛡️ How It Works

CallSentinel runs a **silent background service** that intercepts every incoming call and scores it for risk before you answer.

When your phone rings, it instantly:
- Checks if the number matches your contacts
- Detects **neighbor spoofing** (numbers that look like your contacts but aren't)
- Flags **foreign country codes** and **repeat callers**
- Calculates a **risk score (0–100)**

**Score < 50** → ✅ Safe call overlay shown  
**Score ≥ 50** → ⚠️ Spoof warning overlay shown

All analysis happens **on-device, offline, in milliseconds.**

---

### 🚀 When You First Open the App

When you launch CallSentinel for the first time, you're welcomed with a smooth **splash screen animation**, followed by an **onboarding screen** that explains what the app does. You're then guided through a **permissions screen** where the app requests access to your contacts, call log, and phone state. Once permissions are granted, you land on the **Home screen**, where the app requests the **Call Screening role** (Android 10+) and starts a **background service** that runs continuously.

---

### 📡 What Happens in the Background

The heart of CallSentinel is the **CallMonitorService** — a foreground service that runs silently at all times. It listens for incoming calls using Android's **TelephonyManager**. The moment your phone rings, the service springs into action.

---

### 🧠 How a Call Gets Analyzed

When an incoming call is detected, CallSentinel runs it through a **5-step risk analysis** in milliseconds:

1. **Loads your contacts** from the phone
2. **Checks your trusted numbers** list from the local database
3. **Counts how many times** this number has called recently
4. **Feeds everything** into the **RiskEngine** which checks for spoof patterns, country codes, and repeat behavior
5. **Returns a risk score** between 0 and 100

---

### ⚠️ What You See on Screen

Based on the risk score, one of two things happens:

- **Score below 50** → A green **"Safe Call"** overlay appears confirming the caller is known
- **Score 50 or above** → A red **"Spoof Warning"** overlay pops up showing exactly why the call is suspicious

All of this happens **before you even decide to pick up.**

---

### 🔒 Your Data, Your Device

Everything stays **on your device**. CallSentinel doesn't send your contacts or call data to any server. It works entirely **offline** using your local contacts and call history.

---

## ✨ Key Features

| Feature | Description |
|---|---|
| 🔍 **Real-time Screening** | Analyzes every incoming call instantly via background service |
| 🧠 **Smart Risk Engine** | Scores calls 0–100 based on multiple threat signals |
| 👥 **Neighbor Spoof Detection** | Catches numbers that look like your contacts but aren't |
| 🌍 **Foreign Number Alert** | Flags calls from unexpected country codes |
| 🔁 **Repeat Call Detection** | Identifies numbers that call multiple times in a short window |
| 🛡️ **Trusted List** | Whitelist numbers you always want to allow |
| 🚫 **Block List** | Permanently block known spam numbers |
| 📊 **Analytics Dashboard** | Weekly reports and call pattern insights |
| 📋 **Full Call Log** | History of every screened call with risk details |
| 📱 **Built-in Dialpad** | Make calls directly from the app |

---

## 🔄 App Workflow

```
┌─────────────────────────────────────────────────────────────┐
│                      USER LAUNCHES APP                       │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      ▼
          ┌───────────────────────┐
          │   SplashFragment      │  2s animated splash
          └───────────┬───────────┘
                      │
          ┌───────────┴───────────┐
          │                       │
    First Launch             Returning User
          │                       │
          ▼                       ▼
┌──────────────────┐    ┌──────────────────────┐
│OnboardingFragment│    │     HomeFragment      │
└────────┬─────────┘    └──────────────────────┘
         │
         ▼
┌──────────────────────────────────────────────┐
│              PermissionsFragment              │
│  READ_CONTACTS · READ_PHONE_STATE             │
│  READ_CALL_LOG · ANSWER_PHONE_CALLS           │
│  CALL_PHONE                                   │
└────────────────────┬─────────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────────┐
│                HomeFragment                   │
│  → Requests ROLE_CALL_SCREENING (API 29+)    │
│  → Starts CallMonitorService                 │
└────────────────────┬─────────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────────┐
│         CallMonitorService (always on)        │
│                                               │
│  Incoming call detected via TelephonyManager  │
│              │                                │
│              ▼                                │
│   1. Load contacts (ContactsHelper)           │
│   2. Load trusted numbers (Room DB)           │
│   3. Fetch recent call count (Room DB)        │
│   4. Feed into RiskEngine.calculateScore()    │
│              │                                │
│    ┌─────────┴──────────┐                    │
│    │                    │                     │
│  SAFE ✅           SUSPICIOUS ⚠️             │
│    │                    │                     │
│    ▼                    ▼                     │
│ IncomingSafe      SpoofWarning                │
│ CallActivity       Activity                   │
└──────────────────────────────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────────┐
│              Room Database                    │
│  SuspiciousCall · BlockedNumber · Trusted     │
└────────────────────┬─────────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────────┐
│   LiveData → ViewModel → UI Fragments        │
│   Home · CallLog · Analytics · Profile       │
└──────────────────────────────────────────────┘
```

---

## 🧠 Risk Score Breakdown

```
┌────────────────────────────────────────┬────────┬──────────────────────┐
│ Signal                                 │ Score  │ Reason               │
├────────────────────────────────────────┼────────┼──────────────────────┤
│ Number not in your contacts            │  +30   │ Unknown caller       │
│ Similar to a contact (spoof attempt)   │  +40   │ Neighbor spoofing    │
│ Foreign / unexpected country code      │  +25   │ International scam   │
│ 3+ calls in a short time window        │  +20   │ Harassment pattern   │
│ Number is in your Trusted List         │   = 0  │ Always safe          │
│ Exact match in your contacts           │   = 0  │ Known person         │
└────────────────────────────────────────┴────────┴──────────────────────┘

  Score ≥ 50  →  ⚠️  SpoofWarningActivity overlay shown
  Score < 50  →  ✅  IncomingSafeCallActivity shown
```

---

## 🏗️ Tech Stack

```
📦 CallSentinel
 ┣ 🔤 Language          →  Kotlin 2.0
 ┣ 🏛️ Architecture      →  MVVM (Model-View-ViewModel)
 ┣ 💉 Dependency Inject →  Hilt (Dagger)
 ┣ 🗄️ Database          →  Room (SQLite)
 ┣ 🧭 Navigation        →  Navigation Component + Safe Args
 ┣ 🎨 UI                →  Material Design 3 + Fragments
 ┣ ⚙️ Background        →  Foreground Service + TelephonyManager
 ┣ 📡 Reactive          →  LiveData + ViewModel
 ┗ 📱 Min SDK           →  API 26 (Android 8.0 Oreo)
```

---

## 🗂️ Project Structure

```
app/src/main/java/com/example/callsentinel/
│
├── 🔍 detection/
│   └── RiskEngine.kt                  # Core scoring logic
│
├── 🧠 logic/
│   ├── RiskAssessmentModule.kt        # Risk orchestrator
│   └── ContactManager.kt             # Contact utilities
│
├── ⚙️ service/
│   ├── CallMonitorService.kt          # Foreground service
│   ├── CallSentinelService.kt
│   └── SentinelScreeningService.kt
│
├── 🗄️ data/
│   ├── db/
│   │   ├── AppDatabase.kt             # Room database
│   │   └── CallSentinelDao.kt         # Data access
│   ├── model/
│   │   ├── SuspiciousCall.kt
│   │   ├── BlockedNumber.kt
│   │   ├── TrustedNumber.kt
│   │   └── AppNotification.kt
│   └── repository/
│       └── CallRepository.kt
│
├── 🎨 ui/
│   ├── fragments/                     # All screens
│   ├── viewmodels/                    # Screen logic
│   ├── adapters/                      # RecyclerView adapters
│   └── overlay/                       # Call alert overlays
│
└── 🛠️ utils/
    ├── ContactsHelper.kt
    ├── CallLogHelper.kt
    ├── PhoneNumberUtils.kt
    └── PrefsManager.kt
```

---

## 🔐 Required Permissions

```
READ_CONTACTS          →  Compare incoming numbers with your contacts
READ_PHONE_STATE       →  Detect incoming call events
READ_CALL_LOG          →  Check recent call history for patterns
ANSWER_PHONE_CALLS     →  Integrate with Android call screening
CALL_PHONE             →  Dialpad functionality
ROLE_CALL_SCREENING    →  Android 10+ native call screening role
SYSTEM_ALERT_WINDOW    →  Show overlay alert on incoming calls
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- Android device with API 26+
- ⚠️ **Physical device recommended** — TelephonyManager doesn't work on emulators

### Installation

```bash
# 1. Clone the repository
git clone https://github.com/Ashika005/CallSentinel.git

# 2. Open in Android Studio
# File → Open → select the CallSentinel folder

# 3. Let Gradle sync finish

# 4. Connect your Android device (enable USB Debugging)

# 5. Click Run ▶
```

---

## 🤝 Contributing

Contributions are welcome! Feel free to:
- 🐛 Report bugs via [Issues](https://github.com/Ashika005/CallSentinel/issues)
- 💡 Suggest new features
- 🔧 Submit pull requests

---

## 📄 License

```
MIT License — feel free to use, modify, and distribute.
See LICENSE file for full details.
```

---

<div align="center">

**Made with ❤️ by [Ashika005](https://github.com/Ashika005)**

*If you found this useful, please consider giving it a ⭐*

</div>
