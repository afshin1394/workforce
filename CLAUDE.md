# CLAUDE.md

This file provides comprehensive guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **Kotlin Multiplatform Mobile (KMM)** project for a workforce management application targeting Android and iOS platforms. The project uses **Compose Multiplatform** for UI and follows **Clean Architecture** principles with comprehensive security, location tracking, and ticket management features.

**Key Information:**
- **Package**: `irancell.nwg.wfm`
- **Current Version**: 0.2.4 (versionCode: 24)
- **Min SDK**: 29 (Android), iOS 15+
- **Target SDK**: 34 (Android)
- **Database Version**: 7

## Build Commands

### Android
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK (signed)
./gradlew assembleRelease

# Install debug APK on connected device
./gradlew installDebug

# Run unit tests
./gradlew test
./gradlew testDebugUnitTest
./gradlew testReleaseUnitTest

# Run Android instrumented tests
./gradlew connectedAndroidTest
./gradlew connectedDebugAndroidTest

# Run lint checks
./gradlew lint
./gradlew lintDebug
./gradlew lintRelease

# Fix lint issues automatically
./gradlew lintFix

# Update lint baseline
./gradlew updateLintBaseline
```

### iOS
```bash
# Build iOS framework for device
./gradlew linkDebugFrameworkIosArm64
./gradlew linkReleaseFrameworkIosArm64

# Build iOS framework for simulator
./gradlew linkDebugFrameworkIosSimulatorArm64
./gradlew linkReleaseFrameworkIosSimulatorArm64

# Build iOS framework for Intel Macs
./gradlew linkDebugFrameworkIosX64
./gradlew linkReleaseFrameworkIosX64

# Open iOS project in Xcode
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

# Run all tests across all platforms
./gradlew allTests

# Check Kotlin Gradle Plugin configuration
./gradlew checkKotlinGradlePluginConfigurationErrors

# Run native tests
./gradlew iosSimulatorArm64Test
./gradlew iosX64Test
```

## Architecture

The project follows **Clean Architecture** with clear separation between layers and comprehensive error handling.

### Layer Structure
- **Domain Layer** (`commonMain/kotlin/domain/`): Business logic, use cases, repository interfaces, domain models
- **Data Layer** (`commonMain/kotlin/data/`): Repository implementations, network services, database operations
- **Presentation Layer** (`commonMain/kotlin/presentation/`): ViewModels, Compose UI components, navigation

### Key Architectural Patterns

#### 1. MVVM with MutableStateFlow
- All ViewModels extend `BaseViewModel` 
- State management using `MutableStateFlow` and `StateFlow`
- Comprehensive lifecycle management
- Built-in error handling and loading states

#### 2. Repository Pattern
- Interfaces defined in `domain/repository/`
- Implementations in `data/` layer
- Clear separation between data sources and business logic

#### 3. Use Case Pattern
- All use cases extend `BaseUseCase<Type, Params>`
- Flow-based error handling with `AsyncResult`
- Built-in retry mechanism (max 2 retries with exponential backoff)
- Comprehensive error categorization with `ResultStatus`

#### 4. Dependency Injection (Koin)
- Modular DI structure in `di/Koin.kt`
- Separate modules: `repositoryModule()`, `useCaseModule()`, `httpModule()`, `viewModelModule()`
- Platform-specific modules in `platformModule.kt`

### State Management

#### BaseViewModel Features
- **Network Connectivity**: Real-time monitoring using Konnectivity
- **GPS State**: Location service status tracking
- **Device Orientation**: Portrait/landscape detection
- **Security States**: VPN detection, root/emulator detection (bypassed in dev mode)
- **Service State**: Background service monitoring
- **Availability Status**: User availability state management

#### State Classes
```kotlin
sealed class ViewStates
sealed interface ServiceState
sealed class VpnDetectionStates
sealed class DeviceSafetyStates
sealed interface GpsState
sealed class NetworkStates
sealed interface OrientationState
sealed class AvailabilityStatus
```

#### Error Handling
- `AsyncResult<T>` sealed class for comprehensive result handling
- `ResultStatus` for detailed error categorization
- Built-in retry logic in `BaseUseCase`
- Network error classification (4xx, 5xx, timeout, etc.)

## Key Dependencies

### Core Libraries
- **UI Framework**: Compose Multiplatform (`1.6.11`)
- **Navigation**: Voyager (`1.0.0`) - navigator, bottom sheet navigator, transitions
- **DI**: Koin (`3.5.3`) with Compose integration (`1.1.0`)
- **Database**: Room (`2.7.0-alpha02`) with SQLite bundled (`2.4.0`)
- **Network**: Ktor (`2.3.7`) - client-core, content-negotiation, logging
- **Serialization**: Kotlinx Serialization (`1.7.3`)
- **Async**: Kotlin Coroutines, Flow
- **Resources**: Moko Resources (`0.23.0`) for multiplatform strings and assets
- **State Management**: Moko MVVM (`0.16.1`)
- **Logging**: Napier (`2.6.1`)

### Platform-Specific Dependencies
#### Android
- **Network Engine**: OkHttp (`4.12.0`)
- **Location Services**: Google Play Services Location (`21.0.1`)
- **Maps**: OSMDroid (`6.1.6`)
- **Permissions**: Accompanist Permissions (`0.32.0`)
- **Image Loading**: Landscapist Coil3 (`2.3.2`)
- **Security**: AndroidX Security Crypto (`1.1.0-alpha06`)

#### iOS
- **Network Engine**: Ktor Darwin client
- **Platform Integration**: Native iOS APIs

### Additional Libraries
- **Utilities**: Arrow Core (`1.2.1`), UUID (`0.5.0`)
- **Connectivity**: Konnectivity (`0.1-alpha01`)
- **Permissions**: Moko Permissions (`0.17.0`)
- **Lifecycle**: Essenty Lifecycle (`2.0.0-dev04`)
- **UI Components**: Calf UI (`0.5.1`)

## Database Schema

**Room Database** (`AppDatabase`) with current **version: 7**

### Entities
1. **TaskEntity** - Ticket/task information
   - `ticket_id` (PK), `ticket_type_id`, `instancePrefix`, `ticket_number`
   - `ticket_state`, `activity_id`, `activity__title`, `properties`

2. **ProfileEntity** - User profile data
   - `pk` (PK), `username`, `email`, `first_name`, `last_name`
   - `company`, `organization`, `national_id`, `phone_number`

3. **RoleEntity** - User roles

4. **PhotoEntity** - Image management
   - `pk` (PK), `ticket_number`, `componentId`, `component_key`
   - `origin_uri`, `edited_uri`, `angle`, `index_row`

5. **GeneralLocationEntity** - Location data

6. **InitialFormEntity** - Form structure data

7. **StepsEntity** - Workflow steps

8. **StepPointerEntity** - Step navigation

9. **SendStepsEntity** - Step submission tracking

10. **SuspendTaskEntity** - Suspended task information

11. **ActivityListEntity** - Activity data

### Type Converters
- `InitFormTypeConverter` for complex data serialization
- Room schema stored in `composeApp/schemas/database.AppDatabase/`

### DAOs
- Individual DAO for each entity
- `DeleteAllTableDao` for database cleanup operations

## Network Layer

### HTTP Clients Configuration
Multiple **Ktor HTTP clients** configured in `httpModule()`:

#### 1. `tokenized` Client
- **Base URL**: `DevelopmentBASEURL` (https://uat.ios.mtnirancell.ir/api/)
- **Authentication**: Bearer token from SharedPreferences
- **Headers**: UUID, Content-Type, Accept
- **Timeout**: 60s request/socket, 10s connect
- **Features**: Content negotiation, logging, retry (2 attempts)

#### 2. `noToken` Client  
- **Purpose**: Public endpoints (login, version check)
- **Configuration**: Same as tokenized but without Authorization header

#### 3. `ipDetection` Client
- **Purpose**: VPN/location detection
- **Features**: Custom user agent, browser-like headers
- **Timeout**: 30s (reduced for faster detection)

### Environment URLs
```kotlin
// Development (current)
const val DevelopmentBASEURL = "https://uat.ios.mtnirancell.ir/api/"

// Production
const val ProductionBASEURL = "https://ios.mtnirancell.ir/api/"

// Deployment
const val DeploymentBASEURL = "http://mobile.ios.mtnirancell.ir/api/"
```

### Network Features
- **Enhanced Logging**: Custom Ktor logger with Napier integration
- **Retry Logic**: Exponential backoff for server errors and timeouts
- **Error Handling**: Comprehensive HTTP status code mapping
- **Request ID**: UUID tracking for each request
- **Timeout Management**: Configurable timeouts per client type

### API Response Patterns
```kotlin
data class ApiResponse<T>(
    val data: T?,
    val message: String = "",
    val success: Boolean = true,
    val statusCode: Int = 200
)

data class ErrorResponse(
    val error: String,
    val detail: String? = null,
    val timestamp: Long
)
```

## Platform-Specific Implementations

### Common Interface Definitions (`commonMain/kotlin/irancell/nwg/wfm/`)
- Platform-agnostic interfaces for all platform-specific functionality
- Expected declarations for actual implementations

### Android Implementations (`androidMain/kotlin/irancell/nwg/wfm/`)
Key Platform-Specific Features:
- **Location Services**: Google Play Services integration
- **Background Services**: WorkManager and AlarmManager
- **File Operations**: Android Storage Access Framework
- **Security**: Root detection, VPN detection
- **Hardware**: Camera, GPS, orientation sensors
- **Connectivity**: Network state monitoring
- **SMS/Telephony**: SMS handling and telephony data
- **Intent Handling**: Deep links and file associations

### iOS Implementations
#### `iosArm64Main/` (Device)
- **Location Services**: Core Location integration
- **Background Processing**: iOS background app refresh
- **File System**: iOS document directory access
- **Security**: Jailbreak detection
- **Hardware**: Native camera and sensor access

#### `iosSimulatorArm64Main/` & `iosX64Main/` (Simulators)
- Simulator-specific implementations
- Limited hardware access simulation

### Platform Module Structure
```kotlin
// Common expectation
expect val platformModule: Module

// Android actual
actual val platformModule = module {
    single<AppDatabase> { getAppDatabase(provideAppContext()) }
}

// iOS actual  
actual val platformModule = module {
    single<AppDatabase> { getAppDatabase() }
}
```

## Security Features

### Device Security Detection
- **Root/Jailbreak Detection**: Multi-method detection (bypassed in dev mode)
- **Emulator Detection**: Hardware and environment analysis
- **VPN Detection**: IP geolocation verification for Iran-only access
- **Network Security**: Certificate pinning and secure connections

### Data Security
- **Encrypted Storage**: AndroidX Security Crypto for sensitive data
- **Token Management**: Secure token storage and refresh
- **API Security**: UUID tracking, request signing
- **ProGuard**: Code obfuscation for release builds

### Development Mode Features
```kotlin
// Security bypasses for development/testing
_isDeviceSafeState.update { DeviceSafetyStates.HideBottomSheet }
_vpnDetectionState.update { VpnDetectionStates.HideBottomSheet }
```

## Background Services & Location Tracking

### Service Architecture
- **Background Worker**: Location tracking and data synchronization
- **GPS Tracking**: Continuous location monitoring
- **Service States**: NotRunning, Normal, Suspend, Faulty
- **Availability Management**: Real-time user availability status

### Location Management
```kotlin
object LOCATION_RECORDS {
    const val RED_NUMBER_OF_LOCATION_RECORDS = 500
    const val MAX_NUMBER_OF_LOCATION_RECORDS = 100
}

object AlarmAction {
    object STORE_LOCATION { const val interval = 60000L }
    object SEND_LOCATION { const val interval = 120000L }
    object UPDATE { const val interval = 60000L }
}
```

## Form System & Components

### Form Component Types
```kotlin
object FormViewerTypes {
    const val Group = "group"
    const val TextField = "textfield"  
    const val TextAREA = "textarea"
    const val Checklist = "checklist"
    const val Datetime = "datetime"
    const val Number = "number"
    const val Radio = "radio"
    const val Select = "select"
    const val LatLong = "latlong"
    const val Email = "email"
    const val Phone = "phone"
    const val FileUpload = "fileupload"
    const val GridField = "gridfield"
    const val ImageView = "image"
    const val Date = "date"
    const val Time = "time"
    const val Multi = "multi"
}
```

### Form Logic System
- **Logic Types**: Hide, Required, Disable, ReadOnly, Validate, Calculate, Bind, Auto_fill
- **Operators**: Comprehensive comparison and calculation operators
- **Conditional Logic**: Dynamic form behavior based on user input
- **Validation**: Built-in validation for all component types

## File Management

### File Operations
- **Upload/Download**: Chunked file transfer with progress tracking
- **Image Processing**: Photo capture, editing, rotation, compression
- **ZIP Operations**: Archive creation and extraction
- **Storage Management**: Internal/external storage handling

### Image Editing Features
- **Drawing Tools**: Multiple brush sizes and colors
- **Rotation**: Image rotation with angle preservation
- **Compression**: Size optimization for network transfer
- **Format Support**: JPG, PNG, PDF support

## Testing

### Available Test Commands
```bash
# Unit Tests
./gradlew test                    # All unit tests
./gradlew testDebugUnitTest      # Debug unit tests
./gradlew testReleaseUnitTest    # Release unit tests

# Integration Tests
./gradlew connectedAndroidTest    # Android instrumented tests
./gradlew connectedCheck         # All device checks

# Platform-Specific Tests
./gradlew iosSimulatorArm64Test  # iOS Simulator tests
./gradlew iosX64Test            # iOS x64 tests

# All Tests
./gradlew allTests              # Cross-platform test execution
```

### Test Structure
- **Unit Tests**: Common business logic testing
- **Integration Tests**: Database and repository testing  
- **UI Tests**: Compose UI testing framework
- **Platform Tests**: Platform-specific functionality testing

## Important Security Considerations

### Sensitive Data (⚠️ SECURITY ALERT)
The project contains **hardcoded credentials** in `gradle.properties`:
```properties
STORE_PASSWORD=IrancellNWG2024
KEY_PASSWORD=IrancellNWG2024  
KEY_ALIAS=key0
STORE_FILE=/Users/mtnirancell/downloads/iTicketKeystore.jks
MAPBOX_DOWNLOADS_TOKEN=sk.eyJ1IjoiYWZzaGluMTk5NDEzNzMiLCJhIjoiY2xycWl0dmR2MDMwNjJqbnh2aGhjeGJkcyJ9.7yZ1kENCAIy0Fn0_453aYA
```

**⚠️ CRITICAL: These credentials should be moved to:**
- Environment variables for CI/CD
- Secure credential storage systems  
- Local properties files (git-ignored)
- Hardware security modules for production

### Network Security
- **Base URLs**: Multiple environment configurations
- **Token-based Authentication**: JWT-style tokens
- **Request Validation**: UUID tracking and timestamp verification
- **Geographic Restriction**: Iran-only access enforcement

## Common Development Tasks

### Implementing New Features
1. **Define Domain Models**: Create entities in `domain/models/`
2. **Create Repository Interface**: Add to `domain/repository/`
3. **Implement Repository**: Create implementation in `data/`
4. **Create Use Case**: Add to `domain/usecase/`
5. **Register in Koin**: Add to appropriate modules in `di/Koin.kt`
6. **Create ViewModel**: Extend `BaseViewModel` with required use cases
7. **Build UI**: Create Compose components in `presentation/`
8. **Add Navigation**: Register screens in `presentation/nav/`

### Error Handling Best Practices
```kotlin
// Use BaseUseCase for automatic error handling
class YourUseCase : BaseUseCase<YourResult, YourParams>() {
    override suspend fun run(params: YourParams): YourResult {
        // Implementation with automatic retry and error mapping
    }  
}

// Handle errors in ViewModel
viewModel.handleError(result.status, result.message)
```

### Adding Platform-Specific Code
1. **Define Expected Declaration**: In `commonMain/kotlin/irancell/nwg/wfm/`
2. **Implement Android Actual**: In `androidMain/kotlin/irancell/nwg/wfm/`
3. **Implement iOS Actual**: In platform-specific iOS directories
4. **Register in Platform Module**: Add to `platformModule.kt`

### Database Migrations
- **Update Entity**: Modify entity class
- **Increment Version**: Update `AppDatabase` version number
- **Create Migration**: Add migration strategy if needed
- **Update Schemas**: Generate new schema files

## Debugging & Logging

### Logging Configuration  
- **Framework**: Napier with multiple log levels
- **Tags**: Structured logging with component-specific tags
- **Sentry Integration**: Error reporting and crash analytics
- **Network Logging**: Detailed HTTP request/response logging

### Debug Features
- **Development Mode**: Security checks bypassed
- **Network Inspection**: Detailed Ktor logging
- **State Inspection**: ViewModel state debugging
- **Error Tracking**: Comprehensive error categorization

## Build Optimization

### ProGuard Configuration (`proguard-rules.pro`)
- **Keep Rules**: Preserve reflection-based code
- **Obfuscation**: Code protection for release builds
- **Library Preservation**: Keep essential library classes
- **Kotlin Metadata**: Preserve for proper functionality

### Build Features
- **Code Shrinking**: R8/ProGuard for release builds
- **Resource Shrinking**: Unused resource removal
- **Signing Configuration**: Automatic APK signing
- **Multi-flavor Support**: Debug/release configurations

---

## Quick Reference

### Key File Locations
- **Main Module**: `composeApp/src/commonMain/kotlin/`
- **Android Module**: `composeApp/src/androidMain/kotlin/`
- **iOS Modules**: `composeApp/src/ios*/kotlin/`
- **Resources**: `composeApp/src/commonMain/resources/`
- **Database Schemas**: `composeApp/schemas/`
- **Build Scripts**: `composeApp/build.gradle.kts`

### Essential Constants
- **Base URLs**: Defined in `utils/Constant.kt`
- **SharedPref Keys**: Token, Availability, SessionId, etc.
- **Form Types**: Complete form component type definitions
- **Task States**: Draft, Running, Completed, Cancelled, etc.

### Performance Considerations
- **Database**: Room with efficient queries and indexing
- **Network**: Connection pooling and request caching
- **Images**: Compression and efficient loading
- **Memory**: Proper lifecycle management and cleanup
- **Background**: Optimized location tracking intervals

This documentation provides comprehensive context for understanding and working with the workforce management application codebase. All security features, architectural patterns, and implementation details are covered to ensure efficient development and maintenance.