package com.example.botzone.Products

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.botzone.R
import com.example.botzone.Room.Cart.CartViewModel
import com.example.botzone.Room.Order.OrderViewModel
import com.google.gson.Gson
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseCompleteScreen(navController:NavController,
                           cartViewModel: CartViewModel,
                           orderViewModel: OrderViewModel
) {

    val context = LocalContext.current
    val trackingCode = remember { orderViewModel.generateTrackingCode() }

    val primaryColor = Color(0xFF1193D4)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ✅ Success Icon
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(primaryColor.copy(alpha = 0.2f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = "Success",
                    tint = primaryColor,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ✅ Title
            Text(
                text = "Success!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ✅ Description
            Text(
                text = "Your order is confirmed and a receipt has been sent to your email.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ✅ Tracking Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Your Tracking Code",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = trackingCode,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(  onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Tracking Code", trackingCode)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Code copied!", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            painterResource(R.drawable.ic_copy),
                            contentDescription = "Copy",
                            tint = primaryColor
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Copy Code",
                            color = primaryColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Log.e("trackingCode", trackingCode)
            // ✅ Buttons
            val scope = rememberCoroutineScope()
            Button(
                onClick = {
                    scope.launch {
                    val savedOrder = orderViewModel.saveOrder(cartViewModel.cartItems, cartViewModel.getTotal(), null)
                    navController.navigate("invoice/${savedOrder.trackingCode}")
                        Log.e("saveOrder", "🧾 Saving order with ${cartViewModel.cartItems.size} items")
                        Log.e("saveOrder", "🧾 Items: ${Gson().toJson(cartViewModel.cartItems)}")
//                        cartViewModel.clearCart()
                }},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor
                )
            ) {
                Text("View Order Details")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { navController.navigate("Robotics") {
                    popUpTo("Robotics") { inclusive = true }
                }},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Continue Shopping")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PurchaseCompletePreview() {
//    InvoiceScreen()
}
