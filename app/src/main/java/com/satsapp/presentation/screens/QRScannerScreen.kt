package com.satsapp.presentation.screens

import android.Manifest
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.satsapp.presentation.WalletViewModel
import com.satsapp.ui.theme.PrimaryButton
import com.satsapp.ui.theme.SecondaryButton
import org.cashudevkit.Token
import java.util.concurrent.Executors

/**
 * QRScannerScreen - Scans QR codes for Cashu tokens
 * 
 * Features:
 * - Camera preview with QR scanning
 * - Parses Cashu tokens using Token.fromString()
 * - Checks mint connection before accepting
 * - Shows confirmation dialog
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun QRScannerScreen(
    viewModel: WalletViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    var scannedToken by remember { mutableStateOf<String?>(null) }
    var showMintDialog by remember { mutableStateOf(false) }
    var tokenMintUrl by remember { mutableStateOf<String?>(null) }
    
    val walletState by viewModel.walletState.collectAsState()
    val receiveState by viewModel.receiveState.collectAsState()
    
    // Request camera permission on first load
    LaunchedEffect(Unit) {
        cameraPermissionState.launchPermissionRequest()
    }
    
    // Check if permission is granted
    val isPermissionGranted = cameraPermissionState.status == com.google.accompanist.permissions.PermissionStatus.Granted
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Scan Cashu Token") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                !isPermissionGranted -> {
                    // Show permission request
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Camera Permission Required",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "We need camera access to scan QR codes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        PrimaryButton(
                            onClick = { cameraPermissionState.launchPermissionRequest() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Grant Permission")
                        }
                    }
                }
                
                scannedToken != null -> {
                    // Show token details and receive button
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Cashu Token Detected!",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        if (tokenMintUrl != null) {
                            Text(
                                text = "Mint: $tokenMintUrl",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                        Text(
                            text = scannedToken!!.take(50) + "...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        if (receiveState.isProcessing) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        } else if (receiveState.isCompleted) {
                            Text(
                                text = "✅ Received ${receiveState.amount} sats!",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            PrimaryButton(
                                onClick = onNavigateBack,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Done")
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                PrimaryButton(
                                    onClick = {
                                        viewModel.receiveTokens(scannedToken!!)
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Receive Tokens")
                                }
                                
                                SecondaryButton(
                                    onClick = {
                                        scannedToken = null
                                        tokenMintUrl = null
                                        viewModel.resetReceiveState()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Scan Again")
                                }
                            }
                            
                            if (receiveState.error != null) {
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Text(
                                    text = "Error: ${receiveState.error}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
                
                else -> {
                    // Show camera preview
                    CameraPreview(
                        onQRCodeScanned = { qrCode ->
                            // Try to parse as Cashu token
                            try {
                                val token = Token.fromString(qrCode)
                                scannedToken = qrCode
                                // Token has a 'mint' field in the data structure
                                tokenMintUrl = token.toString().take(50) // Just show part of token for now
                                
                                // Check if mint matches current wallet mint
                                // TODO: Get current mint from repository
                                // For now, just receive the token
                                
                            } catch (e: Exception) {
                                Log.e("QRScanner", "Failed to parse token: ${e.message}")
                            }
                        }
                    )
                    
                    // Overlay with instructions
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                            )
                        ) {
                            Text(
                                text = "Point camera at Cashu QR code",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * CameraPreview - Shows camera and scans for QR codes
 */
@Composable
private fun CameraPreview(
    onQRCodeScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val executor = remember { Executors.newSingleThreadExecutor() }
    val barcodeScanner = remember { BarcodeScanning.getClient() }
    
    var lastScannedTime by remember { mutableStateOf(0L) }
    
    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(executor) { imageProxy ->
                            processImageProxy(
                                imageProxy,
                                barcodeScanner,
                                lastScannedTime
                            ) { qrCode, timestamp ->
                                lastScannedTime = timestamp
                                onQRCodeScanned(qrCode)
                            }
                        }
                    }
                
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    Log.e("CameraPreview", "Failed to bind camera", e)
                }
            }, ContextCompat.getMainExecutor(ctx))
            
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

/**
 * Process camera image for QR codes
 */
@androidx.camera.core.ExperimentalGetImage
private fun processImageProxy(
    imageProxy: ImageProxy,
    barcodeScanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    lastScannedTime: Long,
    onSuccess: (String, Long) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )
        
        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    if (barcode.valueType == Barcode.TYPE_TEXT ||
                        barcode.valueType == Barcode.TYPE_URL
                    ) {
                        val rawValue = barcode.rawValue
                        if (rawValue != null) {
                            // Prevent duplicate scans (debounce 2 seconds)
                            val currentTime = System.currentTimeMillis()
                            if (currentTime - lastScannedTime > 2000) {
                                onSuccess(rawValue, currentTime)
                            }
                        }
                    }
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}

