package com.example.botzone.Conferences


import android.Manifest
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.botzone.Products.generateQRCode
import com.example.botzone.Products.saveBitmapToGallery
import com.example.botzone.Room.Payment.PaymentEntity
import com.example.botzone.Room.Payment.PaymentViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import androidx.core.graphics.createBitmap
import kotlin.coroutines.resume


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavController,
    conferenceId: Int,
    registrationId: Int, // از مرحله قبل میاد
    paymentViewModel: PaymentViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // وضعیت‌ها
    var trackingCode by remember { mutableStateOf("") }
    var receiptUri by remember { mutableStateOf<Uri?>(null) }
    var receiptPath by remember { mutableStateOf<String?>(null) } // مسیر ذخیره شده
    var trackingError by remember { mutableStateOf<String?>(null) }
    var showPopup by remember { mutableStateOf(false) }
    var isCertificateDownloaded by remember { mutableStateOf(false) }

// مجوز
    val permissionState = rememberPermissionState(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE
    )


    // تابع کپی عکس
    suspend fun saveImageToInternalStorage(context: Context, uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)!!
        val file = File(context.filesDir, "receipt_${System.currentTimeMillis()}.jpg")
        inputStream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file.absolutePath
    }

    // لانچر انتخاب عکس
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            receiptUri = it
            scope.launch {
                receiptPath = saveImageToInternalStorage(context, it)
            }
        }
    }



    // پاپاپ
    RegistrationConfirmedPopup(
        show = showPopup,
        onDismiss = {
            showPopup = false
            navController.navigate("conferences") {
                popUpTo("conferences") { inclusive = true }
            }
        },
        onDownloadCertificate = {
            scope.launch {
                val bitmap = generateCertificateBitmap(
                    context = context,
                    conferenceId = conferenceId,
                    registrationId = registrationId.toLong(),
                    trackingCode = trackingCode
                )
                if (bitmap != null && saveBitmapToGallery(context, bitmap, "Certificate_$trackingCode")) {
                    withContext(Dispatchers.Main) {
                        isCertificateDownloaded = true
                        Toast.makeText(context, "گواهی دانلود شد", Toast.LENGTH_LONG).show()
                    }
                }
            }
        },
        isDownloaded = isCertificateDownloaded
    )


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Conference Registration",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* Back action */ }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            BottomActions(
                onConfirmClick = {
                    // اعتبارسنجی
                    trackingError = if (trackingCode.isBlank()) "کد رهگیری الزامی است" else null
                    if (receiptPath == null) {
                        Toast.makeText(context, "لطفاً رسید را آپلود کنید", Toast.LENGTH_SHORT).show()
                        return@BottomActions
                    }
                    if (trackingError == null) {
                        scope.launch {
                            val payment = PaymentEntity(
                                conferenceId = conferenceId,
                                registrationId = registrationId,
                                receiptImagePath = receiptPath!!,
                                trackingCode = trackingCode
                            )
                            paymentViewModel.savePayment(payment)
                            // ۲. نمایش پاپاپ
                            withContext(Dispatchers.Main) {
                                showPopup = true
                            }
                        }
                    }
                },
                onHelpClick = { /* کمک */ }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // Stepper / Page Indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StepSItem(active = true, title = "Details", modifier = Modifier.weight(1f))
                StepSItem(active = true, title = "Payment", modifier = Modifier.weight(1f))
                StepSItem(active = false, title = "Confirmation", modifier = Modifier.weight(1f))
            }

            // Main content
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Complete Your Payment",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Please deposit the amount to the provided account, then upload the receipt and enter the tracking code below to finalize your registration.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Payment Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column {
                            Text(
                                "Total Amount Due",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                "$499.00",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Divider()
                        Column {
                            Text(
                                "Beneficiary",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                "ali khanizadeh",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Column {
                            Text(
                                "Card Number",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "5892-1012-5970-5776",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                IconButton(onClick = { /* copy */ }) {
                                    Icon(
                                        Icons.Default.Clear,
                                        "Copy",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            // آپلود رسید
            Text("Submit Your Proof", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            // داخل Box آپلود رسید
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .border(
                        2.dp,
                        if (receiptPath == null) Color.Gray.copy(0.3f) else Color(0xFF34C759),
                        RoundedCornerShape(16.dp)
                    )
                    .background(
                        if (receiptPath == null) MaterialTheme.colorScheme.surfaceVariant else Color(0xFF34C759).copy(alpha = 0.1f)
                    )
                    .clickable {
                        when {
                            permissionState.status.isGranted -> launcher.launch("image/*")
                            else -> permissionState.launchPermissionRequest()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (receiptPath != null) {
                    // فقط نشان‌دهنده آپلود موفق
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Uploaded",
                            tint = Color(0xFF34C759),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("رسید آپلود شد", color = Color(0xFF34C759), fontWeight = FontWeight.Bold)
                        Text(
                            "کلیک برای تغییر",
                            color = Color.Gray,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Check, "Upload", tint = Color.Gray, modifier = Modifier.size(36.dp))
                        Text("کلیک کنید برای آپلود رسید", color = Color.Gray)
                        Text("PNG, JPG (حداکثر 5MB)", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            // کد رهگیری
            OutlinedTextField(
                value = trackingCode,
                onValueChange = { trackingCode = it },
                label = { Text("Transaction Tracking Code") },
                placeholder = { Text("مثلاً TRK123456789") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = trackingError != null,
                supportingText = { trackingError?.let { Text(it, color = Color.Red) } }
            )

            Spacer(Modifier.height(80.dp))
        }
    }
}
@Composable
fun BottomActions(
    onConfirmClick: () -> Unit,
    onHelpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth().navigationBarsPadding(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Button(
                onClick = onConfirmClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Confirm Payment", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = onHelpClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Need Help?", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun StepSItem(active: Boolean, title: String,modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .height(8.dp)
                .fillMaxWidth()
                .background(
                    if (active) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(4.dp)
                )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            color = if (active) MaterialTheme.colorScheme.primary else Color.Gray,
            style = MaterialTheme.typography.labelSmall
        )
    }
}



suspend fun generateCertificateBitmap(
    context: Context,
    conferenceId: Int,
    registrationId: Long,
    trackingCode: String
): Bitmap? = withContext(Dispatchers.Main) {
    val activity = context as? Activity
    if (activity == null) {
        Log.e("Certificate", "Context is not an Activity")
        return@withContext null
    }

    val rootView = activity.findViewById<ViewGroup>(android.R.id.content)
    if (rootView == null) {
        Log.e("Certificate", "Root view not found")
        return@withContext null
    }

    val composeView = ComposeView(context).apply {
        setContent {
            MaterialTheme {
                CertificateCaptureView(
                    conferenceId = conferenceId,
                    registrationId = registrationId,
                    trackingCode = trackingCode
                )
            }
        }
    }

    val widthPx = context.resources.displayMetrics.widthPixels
    val layoutParams = FrameLayout.LayoutParams(widthPx, FrameLayout.LayoutParams.WRAP_CONTENT)
    composeView.layoutParams = layoutParams
    composeView.visibility = View.INVISIBLE

    rootView.addView(composeView)

    suspendCancellableCoroutine<Bitmap?> { continuation ->
        val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (composeView.measuredHeight > 0 && composeView.measuredWidth > 0) {
                    composeView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val bitmap = try {
                        createBitmap(composeView.measuredWidth, composeView.measuredHeight).apply {
                            val canvas = Canvas(this)
                            composeView.draw(canvas)
                        }
                    } catch (e: Exception) {
                        Log.e("Certificate", "Bitmap creation failed", e)
                        null
                    }

                    rootView.removeView(composeView)
                    continuation.resume(bitmap)
                }
            }
        }

        composeView.viewTreeObserver.addOnGlobalLayoutListener(listener)
        composeView.requestLayout()
        rootView.requestLayout()

        continuation.invokeOnCancellation {
            composeView.viewTreeObserver.removeOnGlobalLayoutListener(listener)
            rootView.removeView(composeView)
        }
    }
}

@Composable
fun CertificateCaptureView(conferenceId: Int, registrationId: Long, trackingCode: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(32.dp)
    ) {
        Text("گواهی حضور در همایش", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(Modifier.height(16.dp))
        Text("شماره ثبت‌نام: $registrationId", color = Color.DarkGray)
        Text("کد رهگیری: $trackingCode", color = Color.DarkGray)
        Text("همایش شماره: $conferenceId", color = Color.DarkGray)
        Spacer(Modifier.height(32.dp))
        // QR کد
        val qr = remember(trackingCode) { generateQRCode(trackingCode) }
        Image(bitmap = qr.asImageBitmap(), contentDescription = null, modifier = Modifier.size(200.dp).align(Alignment.CenterHorizontally))
    }
}



