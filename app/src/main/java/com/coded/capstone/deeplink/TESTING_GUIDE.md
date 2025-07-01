# Deep Link Frontend Testing Guide

This guide provides comprehensive testing methods for the deep link integration in your NBK Capstone Android app.

## 🚀 **Quick Start Testing**

### **1. Access Testing Screen**

Navigate to the testing screen in your app:

```kotlin
// Add this button to any screen for quick access
Button(
    onClick = { navController.navigate("deeplink_testing") }
) {
    Text("Test Deep Links")
}
```

Or use ADB to navigate directly:

```bash
adb shell am start -W -a android.intent.action.VIEW -d "nbkcapstone://deeplink_testing" com.coded.capstone
```

## 📱 **Testing Methods**

### **Method 1: In-App Testing Screen**

The dedicated testing screen provides:

- ✅ Basic deep link testing
- ✅ Backend integration testing
- ✅ Comprehensive test suite
- ✅ Notification deep link simulation
- ✅ Authentication flow testing
- ✅ Real-time results display

### **Method 2: ADB Commands**

Test deep links directly from command line:

```bash
# Basic Navigation
adb shell am start -W -a android.intent.action.VIEW -d "nbkcapstone://home" com.coded.capstone
adb shell am start -W -a android.intent.action.VIEW -d "nbkcapstone://wallet" com.coded.capstone
adb shell am start -W -a android.intent.action.VIEW -d "nbkcapstone://promotion/123" com.coded.capstone

# With Parameters
adb shell am start -W -a android.intent.action.VIEW -d "nbkcapstone://transfer?selectedAccountId=ACC123" com.coded.capstone

# Public Screens (No Auth Required)
adb shell am start -W -a android.intent.action.VIEW -d "nbkcapstone://login" com.coded.capstone
adb shell am start -W -a android.intent.action.VIEW -d "nbkcapstone://signup" com.coded.capstone
```

### **Method 3: Browser Testing**

1. Open any browser on your device
2. Navigate to: `nbkcapstone://wallet`
3. Should open your app and navigate to wallet screen

### **Method 4: External App Testing**

1. Open any app that supports sharing
2. Share a deep link: `nbkcapstone://promotion/123`
3. Tap the shared link to test

## 🧪 **Test Scenarios**

### **1. Basic Navigation Tests**

```kotlin
// Test all supported screens
val testLinks = listOf(
    "nbkcapstone://home",
    "nbkcapstone://wallet",
    "nbkcapstone://transfer",
    "nbkcapstone://calendar",
    "nbkcapstone://recommendations",
    "nbkcapstone://profile",
    "nbkcapstone://xp",
    "nbkcapstone://notifications",
    "nbkcapstone://promotion/123"
)

testLinks.forEach { link ->
    DeepLinkUtils.testDeepLink(context, link)
}
```

### **2. Authentication Flow Tests**

```kotlin
// Test protected screens without authentication
DeepLinkUtils.testDeepLink(context, "nbkcapstone://wallet")
// Should redirect to login screen

// Test public screens
DeepLinkUtils.testDeepLink(context, "nbkcapstone://login")
// Should work without authentication
```

### **3. Parameter Parsing Tests**

```kotlin
// Test various parameter formats
val testLinks = listOf(
    "nbkcapstone://promotion/123",
    "nbkcapstone://vendor/456/789/101",
    "nbkcapstone://wallet?tab=1",
    "nbkcapstone://transfer?selectedAccountId=ACC123"
)

testLinks.forEach { link ->
    val params = DeepLinkUtils.parseDeepLinkParameters(link)
    Log.d("TEST", "Parameters for $link: $params")
}
```

### **4. Backend Integration Tests**

```kotlin
// Test backend API calls
scope.launch {
    // Validate deep link
    val isValid = DeepLinkUtils.validateDeepLink("nbkcapstone://promotion/123", context)
    Log.d("TEST", "Validation result: $isValid")

    // Generate deep link
    val generatedLink = DeepLinkUtils.generatePromotionDeepLink("123", context)
    Log.d("TEST", "Generated link: $generatedLink")
}
```

### **5. Notification Deep Link Tests**

```kotlin
// Simulate notification with deep link
val intent = Intent().apply {
    putExtra("deepLink", "nbkcapstone://promotion/789")
    putExtra("targetScreen", "promotion")
    putExtra("parameters", "{promotionId=789}")
}

DeepLinkHandler.handleDeepLink(intent, navController, context)
```

### **6. Sharing Tests**

```kotlin
// Test deep link sharing
val deepLink = "nbkcapstone://promotion/123"
DeepLinkUtils.shareDeepLink(context, deepLink, "Check out this promotion!")
```

## 🔍 **Debugging & Logs**

### **Enable Debug Logging**

Check logs for deep link processing:

```bash
adb logcat | grep -E "(DeepLinkHandler|DeepLinkUtils|DeepLinkTester|PushNotificationService)"
```

### **Key Log Tags to Monitor**

- `DeepLinkHandler` - Main deep link processing
- `DeepLinkUtils` - Utility operations
- `DeepLinkTester` - Testing operations
- `PushNotificationService` - FCM notifications

### **Common Log Messages**

```
DeepLinkHandler: Processing deep link: nbkcapstone://promotion/123
DeepLinkHandler: Backend processed deep link: promotion
DeepLinkHandler: Stored intended destination: nbkcapstone://wallet
DeepLinkUtils: Generated promotion link: nbkcapstone://promotion/123
```

## 🚨 **Troubleshooting**

### **Issue 1: Deep Links Not Working**

**Symptoms**: App doesn't open or navigate
**Solutions**:

1. Check AndroidManifest.xml intent filter
2. Verify scheme matches exactly: `nbkcapstone://`
3. Test with ADB first
4. Check if app is installed and accessible

### **Issue 2: Navigation Not Working**

**Symptoms**: App opens but doesn't navigate to correct screen
**Solutions**:

1. Verify NavController is properly initialized
2. Check route definitions in AppHost
3. Test with local deep links first
4. Check authentication requirements

### **Issue 3: Backend Integration Failing**

**Symptoms**: Network errors or API failures
**Solutions**:

1. Check network connectivity
2. Verify API endpoints are correct
3. Check authentication tokens
4. Test with fallback local processing

### **Issue 4: Notifications Not Opening App**

**Symptoms**: FCM notifications don't navigate
**Solutions**:

1. Check FCM configuration
2. Verify notification channel setup
3. Test notification tap handling
4. Check deep link data in notification payload

## 📊 **Testing Checklist**

### **Pre-Testing Setup**

- [ ] App is installed and running
- [ ] Backend services are accessible
- [ ] FCM is configured
- [ ] Authentication tokens are valid
- [ ] Network connectivity is stable

### **Basic Functionality**

- [ ] App opens from deep links
- [ ] Navigation works correctly
- [ ] Parameters are parsed properly
- [ ] Authentication flow works
- [ ] Public screens are accessible

### **Backend Integration**

- [ ] Deep link validation works
- [ ] Deep link generation works
- [ ] API calls are successful
- [ ] Error handling works
- [ ] Fallback processing works

### **Notification Integration**

- [ ] FCM messages are received
- [ ] Deep link data is extracted
- [ ] Notification tap navigation works
- [ ] Foreground/background handling works
- [ ] Parameter parsing works

### **Advanced Features**

- [ ] Deep link sharing works
- [ ] Analytics logging works
- [ ] Error recovery works
- [ ] Performance is acceptable
- [ ] Edge cases are handled

## 🎯 **Performance Testing**

### **Response Time Testing**

```kotlin
val startTime = System.currentTimeMillis()
DeepLinkUtils.testDeepLink(context, "nbkcapstone://wallet")
val endTime = System.currentTimeMillis()
val responseTime = endTime - startTime
Log.d("PERFORMANCE", "Deep link response time: ${responseTime}ms")
```

### **Memory Usage Testing**

Monitor memory usage during deep link operations:

```bash
adb shell dumpsys meminfo com.coded.capstone
```

## 🔧 **Automated Testing**

### **Unit Tests**

Create unit tests for deep link utilities:

```kotlin
@Test
fun testDeepLinkParsing() {
    val params = DeepLinkUtils.parseDeepLinkParameters("nbkcapstone://promotion/123")
    assertEquals("123", params["promotionId"])
}
```

### **Integration Tests**

Test full deep link flow:

```kotlin
@Test
fun testDeepLinkNavigation() {
    // Test complete deep link flow
    val intent = Intent().apply {
        data = Uri.parse("nbkcapstone://promotion/123")
    }
    DeepLinkHandler.handleDeepLink(intent, navController, context)
    // Verify navigation occurred
}
```

## 📈 **Analytics & Monitoring**

### **Track Deep Link Usage**

```kotlin
DeepLinkUtils.logDeepLinkAnalytics(
    deepLink = "nbkcapstone://promotion/123",
    source = "testing",
    context = context
)
```

### **Monitor Success Rates**

Track deep link success/failure rates:

- Successful navigations
- Failed attempts
- Authentication redirects
- Backend API failures

## 🎉 **Success Criteria**

Your deep link integration is working correctly when:

1. ✅ All supported deep links open the app
2. ✅ Navigation works for all target screens
3. ✅ Parameters are correctly parsed and passed
4. ✅ Authentication flow works properly
5. ✅ Backend integration functions correctly
6. ✅ Notifications with deep links work
7. ✅ Error handling and fallbacks work
8. ✅ Performance is acceptable
9. ✅ Sharing functionality works
10. ✅ All edge cases are handled

## 🚀 **Next Steps**

After successful testing:

1. **Remove Testing Screen** - Remove the testing screen from production builds
2. **Add Analytics** - Implement proper analytics tracking
3. **Performance Optimization** - Optimize based on test results
4. **User Testing** - Test with real users
5. **Production Deployment** - Deploy to production environment

Happy Testing! 🎯
