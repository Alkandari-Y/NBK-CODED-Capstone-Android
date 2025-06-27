# Deep Link Setup Documentation

## 🔗 **Available Deep Links**

### **Custom Scheme (nbkcapstone://)**

- **Wallet**: `nbkcapstone://wallet`
- **Transfer**: `nbkcapstone://transfer`
- **Map**: `nbkcapstone://map`
- **KYC**: `nbkcapstone://kyc`
- **XP**: `nbkcapstone://xp`
- **Calendar**: `nbkcapstone://calendar`
- **Recommendations**: `nbkcapstone://recommendations`
- **Home**: `nbkcapstone://home`
- **Login**: `nbkcapstone://login`
- **Signup**: `nbkcapstone://signup`

### **HTTPS Scheme (https://your-domain.com/)**

- **Wallet**: `https://your-domain.com/wallet`
- **Transfer**: `https://your-domain.com/transfer`
- **Map**: `https://your-domain.com/map`
- **KYC**: `https://your-domain.com/kyc`
- **XP**: `https://your-domain.com/xp`
- **Calendar**: `https://your-domain.com/calendar`
- **Recommendations**: `https://your-domain.com/recommendations`
- **Home**: `https://your-domain.com/home`
- **Login**: `https://your-domain.com/login`
- **Signup**: `https://your-domain.com/signup`

## 🧪 **Testing**

### **ADB Commands**

```bash
# Test custom scheme
adb shell am start -W -a android.intent.action.VIEW -d "nbkcapstone://wallet" com.coded.capstone

# Test HTTPS scheme
adb shell am start -W -a android.intent.action.VIEW -d "https://your-domain.com/wallet" com.coded.capstone
```

### **Code Testing**

```kotlin
// Test individual deep link
DeepLinkTester.testDeepLink(context, "nbkcapstone://wallet")

// Test all deep links
DeepLinkTester.testAllDeepLinks(context)

// Test HTTPS deep links
DeepLinkTester.testHttpsDeepLinks(context)
```

### **Browser Testing**

Navigate to any of the deep link URLs in your device's browser.

## 🔧 **Setup Steps**

1. **Replace Domain**: Update `your-domain.com` with your actual domain in:

   - `AndroidManifest.xml`
   - `DeepLinkTester.kt`

2. **Connect NavController**: In `AppHost.kt`, pass the NavController to MainActivity for deep link handling.

3. **Test**: Use the testing methods above to verify deep links work.

## 📱 **Usage Examples**

### **Sharing Deep Links**

```kotlin
// Generate deep link for sharing
val walletLink = DeepLinkHandler.DeepLinkGenerator.generateWalletLink()

// Share via Intent
val shareIntent = Intent().apply {
    action = Intent.ACTION_SEND
    type = "text/plain"
    putExtra(Intent.EXTRA_TEXT, walletLink)
}
startActivity(Intent.createChooser(shareIntent, "Share Wallet Link"))
```

### **Push Notifications**

```kotlin
// In your notification payload
{
    "notification": {
        "title": "Check your wallet",
        "body": "Tap to view your wallet"
    },
    "data": {
        "deep_link": "nbkcapstone://wallet"
    }
}
```

## ⚠️ **Important Notes**

1. **Domain Verification**: For HTTPS deep links, you need to set up domain verification on your website.
2. **App Links**: The `android:autoVerify="true"` attribute enables automatic app link verification.
3. **Fallback**: If the app is not installed, HTTPS links will open in the browser.
4. **Testing**: Always test on real devices, not just emulators.

## 🚀 **Next Steps**

1. Replace `your-domain.com` with your actual domain
2. Set up domain verification for HTTPS deep links
3. Connect the NavController in AppHost
4. Test all deep links thoroughly
5. Implement deep link analytics if needed
