package com.example.botzone.Products

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.botzone.Room.Order.OrderViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.compose.rememberNavController
import com.example.botzone.Fake.FakeOrderViewModel
import android.graphics.Bitmap.createBitmap
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.core.graphics.set
import com.example.botzone.Login.UserPreferences
import com.example.botzone.Room.Order.OrderEntity
import com.example.botzone.Room.Store.ProductEntity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceScreen(
    navController: NavController,
    trackingCode: String,
    orderViewModel: OrderViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val prefs = remember { UserPreferences(context) }
    var savedEmail by remember { mutableStateOf<String?>(null) }
    var savedPass by remember { mutableStateOf<String?>(null) }

    val orderState by orderViewModel.latestOrder.collectAsState()

    // load when trackingCode changes
    LaunchedEffect(trackingCode) {
        orderViewModel.loadOrderByCode(trackingCode)
        val (u, p) = prefs.getUser()
        savedEmail = u
        savedPass = p
    }


    var isDownloaded by remember { mutableStateOf(false) }

    // parse products from itemsJson (ProductEntity list)
    val products = remember(orderState) {
        orderState?.itemsJson?.let {
            try {
                val type = object : TypeToken<List<ProductEntity>>() {}.type
                Gson().fromJson<List<ProductEntity>>(it, type)
            } catch (e: Exception) {
                emptyList<ProductEntity>()
            }
        } ?: emptyList()
    }

    val primary = Color(0xFF1193D4)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Invoice",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("Robotics") {
                        popUpTo("Robotics") { inclusive = true }
                    } }) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = {
                            // download full invoice as image
                            coroutineScope.launch {
                                val order = orderState
                                if (order == null) {
                                    Toast.makeText(context, "Order not loaded yet", Toast.LENGTH_SHORT).show()
                                    return@launch
                                }
                                // capture the InvoiceCaptureView as bitmap
                                val bitmap = captureInvoiceAsBitmap(context, order, products, order.total)
                                if (bitmap != null) {
                                    val saved = saveBitmapToGallery(context, bitmap, order.trackingCode)
                                    if (saved) {
                                        isDownloaded = true
                                    }
                                } else {
                                    Toast.makeText(context, "Failed to capture invoice", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isDownloaded) Color.Gray else primary)
                    ) {
                        Text(if (isDownloaded) "Downloaded ✓" else "Download")
                    }

                    OutlinedButton(
                        onClick = {
                            navController.navigate("Robotics") {
                                popUpTo("Robotics") { inclusive = true }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Shopping")
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    ) { innerPadding ->
        // Main invoice UI
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    "Order Confirmed!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    "Thank you for your purchase. Here is a summary of your order.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )
            }

            // Order details
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        DetailRow("Order Number", trackingCode)
                        Divider()
                        DetailRow("Order Date", orderState?.date ?: "/")
                    }
                }
            }

            // Billed to
            item {
                Spacer(Modifier.height(24.dp))
                Text(
                        "sold to",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        savedEmail?.let { Text(it, fontWeight = FontWeight.Medium) }
                        Text(
                            "456 Innovation Drive, Suite 200, Metropolis, CA 90210",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        savedPass?.let {
                            Text(
                                it,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Items header
            item {
                Spacer(Modifier.height(24.dp))
                Text(
                    "Items Purchased",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(8.dp))
            }

            // items list from saved order
            items(products) { product ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = product.image,
                        contentDescription = null,
                        modifier = Modifier
                            .size(64.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(product.title, fontWeight = FontWeight.Medium)
                        Text(
                            product.subtitle,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        "$${product.price}",
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Pricing summary
            item {
                Spacer(Modifier.height(24.dp))
                Divider(Modifier.padding(vertical = 12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("$${orderState?.total ?: 0.0}", color = primary, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                }
            }

            // QR Code centered
            item {
                Spacer(Modifier.height(32.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val qrBitmap = remember(orderState) {
                            orderState?.trackingCode?.let { generateQRCode(it) }
                        }
                        qrBitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.size(200.dp)
                            )
                        }
                        Text(
                            "Scan for Order Tracking",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}

/** QR generation (unchanged) */
fun generateQRCode(content: String): Bitmap {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
    val bitmap = createBitmap(512, 512, Bitmap.Config.RGB_565)
    for (x in 0 until 512) {
        for (y in 0 until 512) {
            bitmap[x, y] =
                if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
        }
    }
    return bitmap
}

/**
 * Render InvoiceCaptureView offscreen and return a Bitmap.
 * This runs on the main thread (UI) because ComposeView must be used on UI thread.
 */
suspend fun captureInvoiceAsBitmap(
    context: Context,
    order: OrderEntity,
    products: List<ProductEntity>,
    total: Double
): Bitmap? {
    return withContext(Dispatchers.Main) {
        val activity = context as? Activity
        if (activity == null) {
            Log.e("InvoiceCapture", "Context is not an Activity")
            return@withContext null
        }

        val rootView = activity.findViewById<ViewGroup>(android.R.id.content)
        if (rootView == null) {
            Log.e("InvoiceCapture", "Could not find root content view")
            return@withContext null
        }

        val composeView = ComposeView(context).apply {
            setContent {
                MaterialTheme {
                    InvoiceCaptureView(order, products, total)
                }
            }
        }

        // Set layout params: full width, wrap height
        val widthPx = context.resources.displayMetrics.widthPixels
        val layoutParams = FrameLayout.LayoutParams(
            widthPx,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        composeView.layoutParams = layoutParams
        composeView.visibility = View.INVISIBLE  // Invisible so it layouts but doesn't show

        // Add to root
        rootView.addView(composeView)

        // Wait for layout to complete
        suspendCancellableCoroutine<Bitmap?> { continuation ->
            val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (composeView.measuredHeight > 0 && composeView.measuredWidth > 0) {
                        composeView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                        // Capture bitmap
                        val bitmap = try {
                            Bitmap.createBitmap(
                                composeView.measuredWidth,
                                composeView.measuredHeight,
                                Bitmap.Config.ARGB_8888
                            ).apply {
                                val canvas = Canvas(this)
                                composeView.draw(canvas)
                            }
                        } catch (e: Exception) {
                            Log.e("InvoiceCapture", "Bitmap creation failed", e)
                            null
                        }

                        // Clean up
                        rootView.removeView(composeView)
                        continuation.resume(bitmap)
                    }
                }
            }

            composeView.viewTreeObserver.addOnGlobalLayoutListener(listener)

            // Trigger layout if needed
            composeView.requestLayout()
            rootView.requestLayout()

            // Cleanup on cancellation
            continuation.invokeOnCancellation {
                composeView.viewTreeObserver.removeOnGlobalLayoutListener(listener)
                rootView.removeView(composeView)
            }
        }
    }
}

/** Save bitmap to gallery using MediaStore; returns true if saved */
fun saveBitmapToGallery(context: Context, bitmap: Bitmap, filenamePrefix: String): Boolean {
    return try {
        val filename = "$filenamePrefix.png"
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Invoices")
        }
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            Toast.makeText(context, "Invoice saved to gallery ✅", Toast.LENGTH_SHORT).show()
            true
        } ?: run {
            Toast.makeText(context, "Error saving image", Toast.LENGTH_SHORT).show()
            false
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to save: ${e.message}", Toast.LENGTH_SHORT).show()
        false
    }
}

/** Offscreen composable used for capturing the full invoice layout */
@Composable
fun InvoiceCaptureView(order: OrderEntity, products: List<ProductEntity>, total: Double) {
    val primary = Color(0xFF1193D4)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            "Invoice Summary",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(Modifier.height(8.dp))

        Text("Tracking Code: ${order.trackingCode}", color = Color.Black)
        Text("Date: ${order.date}", color = Color.Black)
        Spacer(Modifier.height(12.dp))

        Text("Items:", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))

        products.forEach {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(it.title, color = Color.DarkGray)
                Text("$${it.price}", color = Color.Black)
            }
        }

        Spacer(Modifier.height(12.dp))
        Divider()
        Spacer(Modifier.height(8.dp))

        Text("Total: $${total}", color = primary, fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(24.dp))
        val qrBitmap = remember(order.trackingCode) { generateQRCode(order.trackingCode) }
        Image(
            bitmap = qrBitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            "Scan to track your order",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color.DarkGray,
            fontSize = 14.sp
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun InvoicePreview() {
    val fakeViewModel = FakeOrderViewModel()
    InvoiceScreen(
        rememberNavController(),
        trackingCode = "RBTX-DEMO-1234", orderViewModel = fakeViewModel,
    )
}
