# MarketPulse AI - Mobile Client

An autonomous AI system that monitors financial news, market signals, and stock chart patterns to identify trading opportunities. This Android application serves as the mobile frontend and executive dashboard for the MarketPulse AI backend.

## 📱 Application Overview

The application is built around a modern, three-screen architecture designed to provide raw system transparency, high-level executive insights, and a verifiable history of operations:

### 1. Autonomous Ingestion Terminal (Terminal Screen)
Provides a live, terminal-themed output stream of the AI's internal thought process and data ingestion.
- **Features:** Real-time data ingestion logs, signal processing outputs, and raw AI decision matrices.
- **Purpose:** Gives users complete transparency into what the AI is currently analyzing (e.g., parsing news articles, recognizing chart patterns, monitoring social sentiment).

### 2. Executive Portfolio Dashboard (Dashboard Screen)
A high-level visualization of the system's performance and current portfolio state.
- **Features:** Real-time KPI data visualization, dynamic charting (via Vico), win-rate metrics, active positions, and overall portfolio value.
- **Purpose:** Allows quick assessment of the AI's trading efficacy and overall market posture at a glance.

### 3. Historical Audit Trail (History Screen)
A comprehensive, expandable log of all past actions taken by the AI.
- **Features:** Detailed trade audit logs, including the specific signals that triggered a trade, entry/exit points, and profit/loss calculations.
- **Purpose:** Ensures complete accountability and reviewability of the autonomous system's historical decisions.

## 🚀 Key Features & Workflows

### AI Backend Integration
- **Live Market Data:** Integrates directly with the MarketPulse backend API to stream real-time analysis, indicators, and trade signals.
- **Local Testing Support:** The network security configuration explicitly allows cleartext HTTP traffic to facilitate seamless local backend development and testing.



## 🛠 Tech Stack

- **UI Framework:** 100% Jetpack Compose with Material 3.
- **Architecture:** MVVM (Model-View-ViewModel).
- **Dependency Injection:** Dagger-Hilt.
- **Networking:** Retrofit & OkHttp (with logging interceptors).
- **Local Data & Persistence:** Room Database for offline caching and DataStore for user preferences.
- **Charting & Animations:** Vico for dynamic compose charts, Lottie for fluid UI animations.


## ⚙️ Setup & Installation

1. Clone the repository and open it in Android Studio.
2. Ensure you have the Android SDK for API 36 installed.
3. Sync the Gradle project to download all dependencies.
4. **Backend Configuration:** The app is configured to connect to a local MarketPulse backend. If your backend is running locally, ensure your emulator or physical device has access to the local network.
5. Run the app on an emulator or physical device.
