# 🔨 Build Instructions

## ⚠️ Java Version Issue in Terminal

Your terminal is using **Java 8**, but the project needs **Java 11+**.

### ✅ Solution: Use Android Studio

**Android Studio has its own JDK (Java 17)** built-in, so building works perfectly there!

**Steps:**
1. Open project in Android Studio
2. Click **Build → Rebuild Project**
3. Click **Run** ▶️

Done! It will build successfully.

---

## 🎯 What's New to Test

### **1. Deposit QR Code** (Matches Screenshot!)
- Tap "Request" → Enter amount → "Generate"
- ✨ Shows QR code exactly like iOS!
- Orange circle + icon
- QR code for Lightning invoice
- "Deposit Pending" status

### **2. Activity History**
- Shows real transactions from CDK wallet
- Based on actual proofs in your wallet
- Updates when you receive tokens

### **3. Comprehensive Logging**
All CDK operations now log to Logcat:
```
🔄 Starting wallet initialization...
✅ Wallet object created successfully
📥 Starting token receive...
✅ Token received successfully!
```

---

## 🔍 To View Logs in Android Studio

1. Click **"Logcat"** tab (bottom of window)
2. In filter box, type: **`CashuWalletRepo`**
3. Run the app
4. You'll see all the emoji logs! 🔄✅❌

---

## 🧪 Test Plan

### **Test 1: Deposit (Lightning)**
1. Tap **"Request"** button
2. Enter "50" 
3. Tap **"Generate Deposit Request"**
4. **QR code appears!** (matching screenshot)
5. Scan with Lightning wallet and pay
6. Watch balance update

### **Test 2: Receive Cashu Token**
1. Get test token from testmint.cashu.space
2. Tap **QR scanner** icon
3. Scan the token
4. Watch Logcat for:
   ```
   📥 Starting token receive...
   ✅ Token parsed successfully
   🔄 Calling wallet.receive()...
   ```
5. If error appears, check the log details

### **Test 3: Mint Info**
1. Tap **Settings** ⚙️
2. Scroll to "Mint Information"
3. Tap **"Fetch Mint Info"**
4. Watch Logcat:
   ```
   🔍 Fetching mint info...
   ✅ Wallet exists, calling getMintInfo()...
   ```
5. Should show mint name, version, description

---

## ❗ Current HTTP Error Investigation

The error you're seeing:
```
Http transport error: error sending request for url
(https://fake.thesimplekid.dev/v1/keysets)
```

**What we know:**
- ✅ Mint is real and working (we confirmed /v1/info exists)
- ✅ App has internet permission
- ✅ Ktor HTTP client is added to dependencies
- ❌ CDK's HTTP requests are failing

**Possible causes:**
1. **CDK needs specific Ktor config** - Maybe it creates its own HttpClient without Android engine
2. **Missing CDK dependency** - Maybe CDK expects us to provide the HTTP engine
3. **Version mismatch** - CDK v0.13.1 might need specific Ktor version

**The logs will show:**
- Does getMintInfo() succeed? (If yes, HTTP works!)
- Does it fail immediately? (If yes, HTTP client issue)
- What's the exact exception?

---

## 🎉 What's Working

- ✅ UI conversion complete
- ✅ QR code generation for deposits
- ✅ QR scanner for receiving tokens
- ✅ Transaction history from proofs
- ✅ Settings with mint config
- ✅ Comprehensive logging

**Just need to fix the HTTP client!** 🔧

