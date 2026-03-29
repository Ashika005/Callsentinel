# 📞 CallSentinel

**CallSentinel** is an Android application that protects users from spam, scam, and spoofed phone calls. It monitors incoming calls in real time, calculates a risk score based on multiple heuristics, and alerts users to suspicious activity — all running silently in the background as a foreground service.

---

## ✨ Features

- 🔍 **Real-time call screening** using a background foreground service
- 🧠 **Risk Engine** — scores incoming calls from 0–100 based on:
  - Unknown numbers not in contacts
  - Neighbor spoofing (numbers visually similar to saved contacts)
  - Foreign country codes
  - Repeated calls within a short window
- 🛡️ **Trusted List** — whitelist numbers you always want to allow
- 🚫 **Block List** — block numbers from calling you
- ⚠️ **Overlay Alerts** — `SpoofWarningActivity` pops up on suspicious calls; `IncomingSafeCallActivity` confirms safe ones
- 📊 **Analytics & Reports** — weekly summaries and call pattern insights
- 📋 **Call Log** — full history of screened calls with risk details
- 📱 **Built-in Dialpad** — dial numbers directly from the app

---

## 🏗️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| Architecture | MVVM |
| DI | Hilt (Dagger) |
| Database | Room (SQLite) |
| Navigation | Navigation Component + Safe Args |
| UI | Fragments, BottomNavigationView, Material Design |
| Background | Foreground Service + TelephonyManager |
| Min SDK | API 26 (Android 8.0) |

---

## 🔄 App Workflow

```
User launches app
       ↓
SplashFragment (2s animation)
       ↓ (first launch)                  ↓ (returning user)
OnboardingFragment                   HomeFragment
       ↓
PermissionsFragment
  → Requests: READ_CONTACTS, READ_PHONE_STATE,
              READ_CALL_LOG, ANSWER_PHONE_CALLS, CALL_PHONE
       ↓
HomeFragment
  → Requests ROLE_CALL_SCREENING (Android 10+)
  → Starts CallMonitorService (foreground service)
       ↓
CallMonitorService  ← always running in background
  → Listens for CALL_STATE_RINGING via TelephonyManager
  → On incoming call:
      1. Loads contacts via ContactsHelper
      2. Loads trusted numbers from Room DB
      3. Fetches recent call count from Room DB
      4. Feeds all data into RiskEngine.calculateScore()
            ├─ Checks if number is in contacts
            ├─ Checks if number is trusted
            ├─ Checks for neighbor spoofing (1–2 digit difference)
            ├─ Checks for foreign country code
            ├─ Checks for repeated calls (3+ in short window)
            └─ Returns RiskResult { score, reasons, isSuspicious }
      5. If suspicious  → launches SpoofWarningActivity (overlay)
      6. If safe/known  → launches IncomingSafeCallActivity
       ↓
All call data stored in Room DB
  → Tables: SuspiciousCall | BlockedNumber | TrustedNumber
       ↓
HomeFragment / CallLogFragment / AnalyticsFragment
  → Read from DB via LiveData → ViewModel → UI
```

---

## 🗂️ Project Structure

```
app/src/main/java/com/example/callsentinel/
├── detection/
│   └── RiskEngine.kt              # Core risk scoring logic
├── logic/
│   ├── RiskAssessmentModule.kt    # Risk assessment orchestrator
│   └── ContactManager.kt         # Contact lookup utilities
├── service/
│   ├── CallMonitorService.kt      # Foreground service (background call listener)
│   ├── CallSentinelService.kt
│   └── SentinelScreeningService.kt
├── data/
│   ├── db/
│   │   ├── AppDatabase.kt         # Room database
│   │   └── CallSentinelDao.kt     # DAO for all tables
│   ├── model/
│   │   ├── SuspiciousCall.kt
│   │   ├── BlockedNumber.kt
│   │   ├── TrustedNumber.kt
│   │   └── AppNotification.kt
│   └── repository/
│       └── CallRepository.kt
├── ui/
│   ├── fragments/                 # All screen fragments
│   ├── viewmodels/                # ViewModels for each fragment
│   ├── adapters/                  # RecyclerView adapters
│   └── overlay/                  # Overlay activities for call alerts
└── utils/
    ├── ContactsHelper.kt
    ├── CallLogHelper.kt
    ├── PhoneNumberUtils.kt
    └── PrefsManager.kt
```

---

## 🔐 Permissions Required

| Permission | Purpose |
|---|---|
| `READ_CONTACTS` | Load contacts for risk comparison |
| `READ_PHONE_STATE` | Listen for incoming call events |
| `READ_CALL_LOG` | Check recent call history |
| `ANSWER_PHONE_CALLS` | Call screening integration |
| `CALL_PHONE` | Dialpad functionality |
| `ROLE_CALL_SCREENING` | Android 10+ call screening role |
| `SYSTEM_ALERT_WINDOW` | Display overlay on incoming calls |

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK API 26+
- Kotlin 2.0+

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/CallSentinel.git
   cd CallSentinel
   ```

2. **Open in Android Studio**
   - File → Open → select the `CallSentinel` folder

3. **Build & Run**
   - Connect a physical device (recommended — telephony features don't work on emulators)
   - Click **Run ▶**

> ⚠️ A real device is strongly recommended. Call monitoring via `TelephonyManager` does not function correctly on most emulators.

---

## 📊 Risk Score Breakdown

| Condition | Points Added |
|---|---|
| Number not in contacts | +30 |
| Similar to a contact (spoofing) | +40 |
| Foreign country code | +25 |
| Repeated calls (3+ recently) | +20 |
| Number is trusted / in contacts | Score = 0 (safe) |

A score of **50 or above** marks the call as suspicious and triggers the warning overlay.

---

## 📄 License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.
