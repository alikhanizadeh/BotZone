package com.example.botzone.Products

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.botzone.Room.Cart.CartViewModel
import com.example.botzone.Room.Store.ProductEntity
import com.example.botzone.Room.Store.StoreViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Int?,
    navController: NavController?,
    viewModel: StoreViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    var product by remember { mutableStateOf<ProductEntity?>(null) }
    var loading by remember { mutableStateOf(true) }
    val isAdded = cartViewModel.isInCart(product?.id ?: 0)

    LaunchedEffect(productId) {
        if (productId == null) {
            loading = false
            return@LaunchedEffect
        }
        scope.launch {
//            val p = viewModel.getProductById(productId,{})
            product = product
            loading = false
        }
    }

    val darkTheme = isSystemInDarkTheme()
    val backgroundColor =
        if (darkTheme) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surface

    Scaffold(
        bottomBar = {
            BottomAppBar(
                tonalElevation = 3.dp,
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Price",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = product?.price ?: "-",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    }
                    Button(
                        onClick = {
//                            if (!isAdded) {
//                                val item = product?.let {
//                                    CartItem(
//                                        id = it.id,
//                                        name = it.title,
//                                        price = it.price.toDoubleOrNull() ?: 3.0,
//                                        imageUrl = it.image,
//                                        quantity = 1
//                                    )
//                                }
//                                if (item != null) {
//                                    cartViewModel.addToCart(item)
//                                }
//                            }
                            cartViewModel.addToCartOrIncrease(product!!)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAdded) Color.Gray else Color(0xFF1193D4)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isAdded) "Added ✓" else "Add to Cart")
                    }
                }
            }
        },
        content = { paddingValues ->
            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@Scaffold
            }

            if (product == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Product not found")
                }
                return@Scaffold
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(paddingValues)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp))
                    ) {
                        // عکس
                        AsyncImage(
                            model = product!!.imagePath,
                            contentDescription = product!!.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // آیکون بازگشت — بالا چپ
                        IconButton(
                            onClick = { navController?.popBackStack() },
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp)
                                .size(48.dp)
                                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        // آیکون اشتراک‌گذاری — بالا راست
                        IconButton(
                            onClick = { /* Share action */ },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                                .size(48.dp)
                                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White
                            )
                        }
                    }
                }
                item {
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Text(
                            product!!.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            product!!.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                item {
                    // Rating row (static or from model if you add rating)
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(4) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFC107)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("4.7 (1,288 reviews)", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                item {
                    ExpandableSection(
                        title = "Description",
                        text = "This description comes dynamically from the database. If you want, I can provide longer text or use the actual description field."
                    )
                }

                item {
                    ExpandableSection(
                        title = "Technical Specifications",
                        text = "• Dimensions: 35cm x 35cm x 9.7cm\n• Weight: 3.5 kg\n• Battery Life: Up to 180 minutes\n• Connectivity: Wi-Fi 2.4GHz\n• Sensors: LiDAR, Cliff, Infrared"
                    )
                }

                item {
                    ReviewSection()
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    )
}

@Composable
fun ExpandableSection(title: String, text: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(Modifier.clickable { expanded = !expanded }.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded)
                        Icons.Default.ArrowBack
//                            .copy(defaultWidth = 24.dp, defaultHeight = 24.dp)
                    else
                        Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.rotate(if (expanded) 90f else 270f)
                )
            }
            if (expanded) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ReviewSection() {
    Column(Modifier.padding(16.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Reviews", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("View All", color = MaterialTheme.colorScheme.primary)
        }

        Spacer(Modifier.height(12.dp))

        val reviews = listOf(
            Pair("Alex Johnson", "A total game-changer for our busy household. It's quiet, efficient, and the mapping feature is incredibly accurate. Highly recommend!"),
            Pair("Samantha Lee", "Works great on hardwood floors. It sometimes gets stuck on my high-pile rug, but overall I'm very satisfied with the purchase.")
        )

        reviews.forEach { (name, comment) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(name, fontWeight = FontWeight.Bold)
                    Row {
                        repeat(5) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(comment, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}




//@Preview(showBackground = true)
//@Composable
//fun ProductDetailScreenPreview() {
//        ProductDetailScreen()
//}
