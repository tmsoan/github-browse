# GitBrowse

GitBrowse is a small Android app that lets users browse public GitHub repositories and view detailed information such as stars, forks, and last update.

---

## 🧩 Tech Stack
- **Kotlin** + **Jetpack Compose (Material3)**
- **Clean Architecture & MVVM**
- **Koin** (+ Koin Annotations/KSP) for dependency injection
- **Retrofit** for API requests
- **Landscapist Glide** for image loading
- **Navigation Compose** for screen transitions

---

## 🏗️ Architecture Overview
- **Clean Architecture**
- **Modularization**:
  - `app`: Application entry point and navigation
  - `core`: Shared modules (`common`, `model`, `ui`)
  - `data`, `domain`: Data and business logic layers
  - `feature`: UI features (`home`, `details`)

---

## 🔧 Features (planned)
- [x] Browse public repositories in 2-column grid
- [x] Tap to view repository details
- [x] Search by name, description (filter)
- [x] Pull-to-refresh (home screen)
- [x] Pagination (infinite scroll)
- [x] Offline cache (Room, for previously viewed repos list)
- [x] Loading skeletons for details screen
- [x] Error handling (network, API limits - retrofit cache)

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 17+

### Build & Run
```bash
./gradlew assembleDebug
```
Open the project in Android Studio and run the app on an emulator or device.

---

## 📦 Main Dependencies
- androidx.compose.material3
- androidx.navigation.compose
- koin-android / koin-compose-viewmodel / koin-annotations
- retrofit2
- Landscapist Glide
- Room
- kotlinx.coroutines

---

## 📁 Project Structure
```
app/           # Application module
core/          # Shared modules (common, model, ui)
data/          # Data layer
feature/       # Feature modules (home, details)
domain/        # Business logic (use cases, repositories)
```

---

## 📝 License
MIT
