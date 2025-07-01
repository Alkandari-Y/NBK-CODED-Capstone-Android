# Firebase Deep Link Integration - Frontend Implementation

This document describes the complete deep link integration for the NBK Capstone Android application, including backend integration, Firebase Cloud Messaging (FCM) notifications, and comprehensive testing.

## Overview

The deep link system provides seamless navigation within the app and supports promotion notifications with direct navigation to specific screens. The integration includes:

- **Backend Integration**: API calls for deep link processing and validation
- **Firebase Notifications**: FCM integration with deep link support
- **Local Fallback**: Offline deep link processing when backend is unavailable
- **Authentication Handling**: Proper auth flow for protected screens
- **Comprehensive Testing**: Utilities for testing all deep link scenarios

## Architecture

### Components

1. **DeepLinkHandler** - Main deep link processing and navigation
2. **DeepLinkServiceProvider** - Backend API integration
3. **DeepLinkUtils** - Utility functions for deep link operations
4. **DeepLinkTester** - Comprehensive testing utilities
5. **PushNotificationService** - Enhanced FCM service with deep link support

### Data Models

- **DeepLinkRequest** - Request DTO for backend API calls
- **DeepLinkResponse** - Response DTO from backend API calls

## Deep Link Formats

### Custom Scheme Deep Links

```
nbkcapstone://{targetScreen}[/{parameters}]
```

### Supported Deep Links

| Target Screen   | Deep Link                       | Parameters          | Auth Required |
| --------------- | ------------------------------- | ------------------- | ------------- |
| Home            | `nbkcapstone://home`            | None                | Yes           |
| Wallet          | `nbkcapstone://wallet`          | None                | Yes           |
| Transfer        | `nbkcapstone://transfer`        | `selectedAccountId` | Yes           |
| Calendar        | `nbkcapstone://calendar`        | None                | Yes           |
| Recommendations | `nbkcapstone://recommendations` | None                | Yes           |
| Profile         | `nbkcapstone://profile`         | None                | Yes           |
| XP History      | `nbkcapstone://xp`              | None                | Yes           |
| Notifications   | `nbkcapstone://notifications`   | None                | Yes           |
| Promotion       | `nbkcapstone://promotion/{id}`  | `promotionId`       | Yes           |
| Login           | `nbkcapstone://login`           | None                | No            |
| Signup          | `nbkcapstone://signup`          | None                | No            |

## Backend Integration

### API Endpoints

The app integrates with the following backend endpoints:

- `POST /api/v1/deeplink/process` - Process deep link and get navigation info
- `POST /api/v1/deeplink/generate` - Generate deep link URLs
- `POST /api/v1/deeplink/validate` - Validate deep link format

### Service Provider

```kotlin
class DeepLinkServiceProvider {
    suspend fun processDeepLink(deepLink: String): Result<DeepLinkResponse>
    suspend fun generateDeepLink(targetScreen: String, parameters: Map<String, String>?): Result<DeepLinkResponse>
    suspend fun validateDeepLink(deepLink: String): Result<DeepLinkResponse>
}
```

### Usage Examples

```kotlin
// Process a deep link
val result = deepLinkService.processDeepLink("nbkcapstone://promotion/123")
result.onSuccess { response ->
    // Navigate based on response.targetScreen and response.parameters
}

// Generate a deep link
val result = deepLinkService.generateDeepLink("promotion", mapOf("promotionId" to "123"))
result.onSuccess { response ->
    val deepLink = response.deepLink // "nbkcapstone://promotion/123"
}
```

## Firebase Notification Integration

### Enhanced FCM Service

The `PushNotificationService` has been enhanced to handle deep link notifications:

```kotlin
override fun onMessageReceived(remoteMessage: RemoteMessage) {
    if (remoteMessage.data.isNotEmpty()) {
        handleDataMessage(remoteMessage.data)
    }
}

private fun handleDataMessage(data: Map<String, String>) {
    val deepLink = data["deepLink"]
    val targetScreen = data["targetScreen"]
    val parameters = data["parameters"]

    // Create notification with deep link intent
    createNotificationWithDeepLink(title, body, deepLink, targetScreen, parameters)

    // Store for navigation if app is in foreground
    if (deepLink != null && targetScreen != null) {
        storeDeepLinkForNavigation(deepLink, targetScreen, parameters)
    }
}
```

### Notification Data Structure

```json
{
  "data": {
    "title": "New Promotion Available",
    "body": "Check out the latest offers!",
    "deepLink": "nbkcapstone://promotion/123",
    "targetScreen": "promotion",
    "requiresAuth": "true",
    "parameters": "{promotionId=123}"
  },
  "notification": {
    "title": "New Promotion Available",
    "body": "Check out the latest offers!"
  }
}
```

## Deep Link Processing Flow

### 1. Deep Link Reception

```kotlin
// In MainActivity
override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    navController?.let { controller ->
        handleDeepLink(intent, controller)
    }
}
```

### 2. Processing with Backend

```kotlin
private suspend fun processWithBackend(
    deepLink: String,
    navController: NavController,
    context: Context
) {
    val result = deepLinkService.processDeepLink(deepLink)
    result.onSuccess { response ->
        if (response.requiresAuth && !isUserAuthenticated(context)) {
            storeIntendedDestination(deepLink, context)
            navController.navigate(NavRoutes.NAV_ROUTE_LOGIN)
        } else {
            navigateToTargetScreen(response.targetScreen, response.parameters, navController, context)
        }
    }.onFailure { exception ->
        // Fallback to local processing
        handleLocalDeepLink(deepLink, navController, context)
    }
}
```

### 3. Authentication Handling

```kotlin
private fun isUserAuthenticated(context: Context): Boolean {
    return TokenManager.getToken(context) != null &&
           !TokenManager.isAccessTokenExpired(context)
}

private fun storeIntendedDestination(deepLink: String, context: Context) {
    val prefs = context.getSharedPreferences("deeplink_prefs", Context.MODE_PRIVATE)
    prefs.edit().putString("intended_destination", deepLink).apply()
}
```

### 4. Post-Login Navigation

```kotlin
// After successful login
val intendedDestination = DeepLinkHandler.getAndClearIntendedDestination(context)
if (intendedDestination != null) {
    // Process the stored deep link
    DeepLinkHandler.handleDeepLink(intent, navController, context)
}
```

## Utility Functions

### DeepLinkUtils

```kotlin
// Share a deep link
DeepLinkUtils.shareDeepLink(context, "nbkcapstone://promotion/123", "Check this out!")

// Test a deep link
DeepLinkUtils.testDeepLink(context, "nbkcapstone://wallet")

// Validate deep link format
val isValid = DeepLinkUtils.validateDeepLink("nbkcapstone://promotion/123")

// Generate promotion deep link
val deepLink = DeepLinkUtils.generatePromotionDeepLink("123")

// Parse parameters
val params = DeepLinkUtils.parseDeepLinkParameters("nbkcapstone://promotion/123?tab=1")
```

### Deep Link Generation

```kotlin
// Local generation
val localLink = DeepLinkHandler.DeepLinkGenerator.generatePromotionLink("123")

// Backend generation
val backendLink = DeepLinkHandler.DeepLinkGenerator.generateDeepLinkWithBackend(
    targetScreen = "promotion",
    parameters = mapOf("promotionId" to "123")
)
```

## Testing

### Comprehensive Testing

```kotlin
// Test all deep link scenarios
DeepLinkTester.testAllDeepLinks(context)

// Test specific functionality
DeepLinkTester.testParameterParsing()
DeepLinkTester.testAuthenticationRequirements()
DeepLinkTester.testDeepLinkSharing(context)

// Generate test report
val report = DeepLinkTester.generateTestReport()
```

### Test Scenarios

1. **Basic Navigation**

   - Test all supported deep links
   - Verify navigation to correct screens
   - Test parameter passing

2. **Authentication Flow**

   - Test protected screens without auth
   - Test post-login navigation
   - Test public screens

3. **Backend Integration**

   - Test API calls
   - Test fallback behavior
   - Test error handling

4. **Notification Deep Links**

   - Test FCM message processing
   - Test notification tap handling
   - Test foreground/background scenarios

5. **Parameter Parsing**
   - Test various parameter formats
   - Test query parameters
   - Test path parameters

## Error Handling

### Network Errors

```kotlin
result.onFailure { exception ->
    Log.e(TAG, "Failed to process deep link with backend", exception)
    // Fallback to local processing
    handleLocalDeepLink(deepLink, navController, context)
}
```

### Invalid Deep Links

```kotlin
try {
    val uri = Uri.parse(deepLink)
    if (uri.scheme == "nbkcapstone") {
        handleCustomScheme(uri, navController, context)
    }
} catch (e: Exception) {
    Log.e(TAG, "Failed to parse deep link: $deepLink", e)
}
```

### Authentication Errors

```kotlin
if (response.requiresAuth && !isUserAuthenticated(context)) {
    // Store intended destination and redirect to login
    storeIntendedDestination(deepLink, context)
    navController.navigate(NavRoutes.NAV_ROUTE_LOGIN)
}
```

## Configuration

### AndroidManifest.xml

```xml
<activity android:name=".MainActivity">
    <!-- Custom Scheme Deep Link Intent Filter -->
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="nbkcapstone" />
    </intent-filter>
</activity>
```

### Firebase Configuration

Ensure your `google-services.json` is properly configured and the FCM service is registered:

```xml
<service android:name=".Services.PushNotificationService">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>
```

## Best Practices

### 1. Always Provide Fallbacks

```kotlin
// Try backend first, fallback to local processing
val result = deepLinkService.processDeepLink(deepLink)
result.onSuccess { response ->
    // Use backend response
}.onFailure { exception ->
    // Fallback to local processing
    handleLocalDeepLink(deepLink, navController, context)
}
```

### 2. Handle Authentication Properly

```kotlin
// Store intended destination for post-login navigation
if (requiresAuth && !isAuthenticated) {
    storeIntendedDestination(deepLink, context)
    navigateToLogin()
}
```

### 3. Log Deep Link Analytics

```kotlin
// Track deep link usage for analytics
DeepLinkUtils.logDeepLinkAnalytics(deepLink, "notification", context)
```

### 4. Validate Input

```kotlin
// Always validate deep link format
val isValid = DeepLinkUtils.validateDeepLink(deepLink)
if (!isValid) {
    Log.w(TAG, "Invalid deep link format: $deepLink")
    return
}
```

### 5. Handle Edge Cases

```kotlin
// Handle null or empty parameters
val promotionId = parameters["promotionId"] ?: return
if (promotionId.isBlank()) {
    Log.w(TAG, "Empty promotion ID")
    return
}
```

## Troubleshooting

### Common Issues

1. **Deep links not working**

   - Check AndroidManifest.xml intent filter
   - Verify scheme matches exactly
   - Test with adb: `adb shell am start -W -a android.intent.action.VIEW -d "nbkcapstone://wallet"`

2. **Notifications not opening app**

   - Check FCM configuration
   - Verify notification channel setup
   - Test notification tap handling

3. **Backend integration failing**

   - Check network connectivity
   - Verify API endpoints
   - Check authentication tokens

4. **Navigation not working**
   - Verify NavController is properly initialized
   - Check route definitions
   - Test with local deep links first

### Debug Logging

Enable debug logging to troubleshoot issues:

```kotlin
Log.d(TAG, "Processing deep link: $deepLink")
Log.d(TAG, "Target screen: $targetScreen")
Log.d(TAG, "Parameters: $parameters")
```

## Future Enhancements

1. **Analytics Integration**

   - Track deep link conversion rates
   - Measure notification effectiveness
   - User journey analysis

2. **Dynamic Deep Links**

   - Server-side deep link generation
   - A/B testing support
   - Personalized deep links

3. **Advanced Features**

   - Deep link analytics dashboard
   - Automated testing
   - Performance monitoring

4. **Security Enhancements**
   - Deep link signing
   - Fraud detection
   - Rate limiting

## Support

For issues or questions regarding the deep link integration:

1. Check the logs for error messages
2. Use the testing utilities to verify functionality
3. Test with the provided examples
4. Review the backend API documentation

The deep link system is designed to be robust, scalable, and maintainable, providing a seamless user experience across all navigation scenarios.
