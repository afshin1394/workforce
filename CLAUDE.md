# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Kotlin Multiplatform project for a workforce management application targeting Android and iOS platforms. The project uses Compose Multiplatform for UI and follows Clean Architecture principles.

## Build Commands

### Android
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug

# Run unit tests
./gradlew test

# Run Android instrumented tests
./gradlew connectedAndroidTest
```

### iOS
```bash
# Build iOS framework
./gradlew linkDebugFrameworkIosArm64

# Open in Xcode
open iosApp/iosApp.xcodeproj
```

### Common
```bash
# Clean build
./gradlew clean

# Build all targets
./gradlew build

# Run all checks (tests, lint, etc.)
./gradlew check
```

## Architecture

The project follows Clean Architecture with clear separation between layers:

### Layer Structure
- **Domain Layer** (`commonMain/kotlin/domain/`): Business logic, use cases, repository interfaces, domain models
- **Data Layer** (`commonMain/kotlin/data/`): Repository implementations, network services, database operations
- **Presentation Layer** (`commonMain/kotlin/presentation/`): ViewModels, Compose UI components, navigation

### Key Patterns
1. **MVVM**: ViewModels extend `BaseViewModel` using MutableStateFlow for state management
2. **Repository Pattern**: Interfaces in domain layer, implementations in data layer
3. **Use Case Pattern**: All use cases extend `BaseUseCase<Type, Params>` with Flow-based error handling
4. **Dependency Injection**: Koin modules defined in `di/Koin.kt`

### State Management
- `BaseViewModel` provides common functionality:
  - Network connectivity monitoring (Konnectivity)
  - GPS state tracking
  - Device orientation detection
  - Loading states and error handling
- `AsyncResult` sealed class for comprehensive error handling
- `ViewStates` for UI state representation

## Key Dependencies

### Core Libraries
- **UI**: Compose Multiplatform, Voyager (navigation)
- **DI**: Koin
- **Database**: Room with SQLite
- **Network**: Ktor with Kotlinx Serialization
- **Async**: Kotlin Coroutines, Flow
- **Resources**: Moko Resources

### Platform-Specific Code
- Platform implementations in: `androidMain/`, `iosMain/`, `iosArm64Main/`, `iosSimulatorArm64Main/`, `iosX64Main/`
- Common interfaces defined in `commonMain/kotlin/irancell/nwg/wfm/`

## Database Schema

Room database (`AppDatabase`) with current version: 6
- Entities: Task, Profile, Role, Photo, Steps, StepPointer, GeneralLocation, InitialForm, SuspendTask, SendSteps
- Type converters for complex data serialization

## Network Layer

Multiple Ktor HTTP clients configured in `httpModule()`:
- `tokenizedIoHttpClient`: Authenticated requests with bearer token
- `noTokenHttpClient`: Public endpoints
- `ipDetectionHttpClient`: IP detection service

All clients include:
- Content negotiation with Kotlinx Serialization
- Logging in debug mode
- Timeout configurations (60s)
- Custom headers

## Important Considerations

1. **Sensitive Data**: The project contains hardcoded credentials in gradle.properties (STORE_PASSWORD, KEY_PASSWORD, MAPBOX_DOWNLOADS_TOKEN). These should be moved to environment variables or secure storage.

2. **Platform Detection**: The app includes VPN detection, root/jailbreak detection, and emulator detection features.

3. **Background Services**: Implements background workers for location tracking and other periodic tasks.

4. **File Operations**: Extensive file handling capabilities including download, upload, zip/unzip operations.

5. **Multiplatform Resources**: Uses Moko Resources for shared string resources between platforms.

## Testing

The project structure suggests test directories but specific test commands depend on the test framework implementation. Check for:
- Unit tests in `test/` directories
- UI tests using Compose testing framework
- Integration tests for repositories and use cases

## Common Development Tasks

When implementing new features:
1. Define domain models in `domain/models/`
2. Create repository interface in `domain/repository/`
3. Implement repository in `data/`
4. Create use case in `domain/usecase/`
5. Add to Koin modules
6. Create ViewModel extending `BaseViewModel`
7. Build UI with Compose components