# 🔍 Debugging Guide - Understanding the UI & Logs

## 📱 Understanding the Two Numbers on Screen

### **What You See:**
```
┌─────────────────────────┐
│  190 sat          ⚙️    │  ← HEADER: Your actual balance (updates when you receive)
├─────────────────────────┤
│                         │
│      0 sat              │  ← INPUT: Amount you're ENTERING (for send/request)
│                         │
│   [1] [2] [3]          │  ← Number pad (tap to change input amount)
│   [4] [5] [6]          │
│   [7] [8] [9]          │
│   [ ] [0] [⌫]          │
│                         │
│ [Request] [QR] [Pay]   │  ← Buttons
└─────────────────────────┘
```

### **The Two Numbers Are DIFFERENT:**

1. **Header Balance (Black)** - `190 sat`
   - This is your **actual wallet balance** from CDK
   - Updates when you receive tokens
   - Fetched from `wallet.totalBalance()`
   
2. **Input Amount (Orange)** - `0 sat`
   - This is what you're **typing** to send or request
   - Changes when you tap number pad buttons
   - NOT your balance - it's an input field!

### **This is CORRECT Behavior!**
- ✅ After receiving tokens, **balance (header)** updates
- ✅ Input amount **stays at what you typed** (doesn't auto-update)

Think of it like:
- Balance = Your bank account total
- Input = Amount you're typing to send

---

## 🔍 Debugging the HTTP Error

### **The Error:**
```
Http transport error None: error sending request for url 
(https://fake.thesimplekid.dev/v1/keysets)
```

### **What This Means:**
- ✅ Wallet IS using real CDK (not fake)
- ✅ Trying to contact the mint
- ❌ HTTP request failing when receiving tokens

### **Likely Causes:**

1. **Missing Ktor HTTP Engine** (MOST LIKELY)
   - CDK uses Ktor for HTTP
   - Android needs specific engine: `ktor-client-android`
   - ✅ Already added in build.gradle.kts

2. **CDK Internal HTTP Client Not Configured**
   - CDK might create its own HttpClient
   - Might need specific configuration
   - Need to check CDK documentation

3. **Network Security or Emulator Issue**
   - Emulator might have network restrictions
   - Try on real device?

---

## 📊 How to Debug

### **Step 1: Watch Logs When App Starts**

```bash
# Logs are already running! Watch for:
```

**Look for these log lines:**
```
🔄 Starting wallet initialization...
📍 Mint URL: https://fake.thesimplekid.dev
✅ Wallet object created successfully
```

**If you see these, wallet initialization worked!**

### **Step 2: Test Mint Info**

1. **Open app** → Tap **Settings** (⚙️)
2. **Scroll to "Mint Information"**
3. **Tap "Fetch Mint Info"**

**Watch logs for:**
```
🔍 Fetching mint info...
🔍 Expected endpoint: https://fake.thesimplekid.dev/v1/info
✅ Wallet exists, calling getMintInfo()...
```

**If getMintInfo() returns NULL:**
- This means CDK can't connect to mint
- HTTP client issue confirmed

**If getMintInfo() succeeds:**
- You'll see: Name, Version, Description
- Mint connection is working!
- Token receive issue is something else

### **Step 3: Test Token Scanning**

1. **Tap QR Scanner**
2. **Scan a token**

**Watch logs for:**
```
📥 Starting token receive...
✅ Token parsed successfully using Token.fromString()
🔄 Calling wallet.receive()...
```

**This will show WHERE it fails:**
- Parse error? → Token format issue
- Receive error? → HTTP/CDK issue

---

## 🎯 What to Share

**After testing, share:**

1. **Full log output** from the terminal
2. **What happens when you:**
   - Open the app (initialization)
   - Fetch mint info (does it work?)
   - Scan a token (where does it fail?)

3. **Screenshots** of any errors

This will tell us exactly what's broken!

---

## 💡 Quick Fixes to Try

### **Fix 1: Ensure Ktor is Available**
Already done - added to build.gradle.kts:
```gradle
implementation("io.ktor:ktor-client-android:2.3.7")
```

### **Fix 2: Test on Real Device**
If using emulator, try a real Android phone:
```bash
adb devices  # Check device is connected
./gradlew installDebug
```

### **Fix 3: Check CDK Version Compatibility**
Current: `cdk-kotlin:v0.13.1`

The mint is running: `cdk-mintd/0.13.0`

Should be compatible! ✅

---

## 🔧 Next Steps

1. **Watch the terminal** (logs are running)
2. **Open the app** on your phone/emulator
3. **Try fetching mint info** in settings
4. **Share the log output** with me

The logs will tell us exactly where the HTTP error happens! 🔍

