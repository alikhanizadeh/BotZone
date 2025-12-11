package com.example.botzone.Products

import android.app.Activity
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil3.compose.AsyncImage
import com.example.botzone.BottomNavBar.BottomNavBar
import com.example.botzone.PortraitCaptureActivity
import com.example.botzone.R
import com.example.botzone.Room.Cart.CartViewModel
import com.example.botzone.Room.Store.StoreViewModel
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoboticsStoreScreen(
    navController: NavController,
    viewModel: StoreViewModel = viewModel(),
    cartViewModel: CartViewModel
) {
    // collect products from ViewModel (ProductEntity)
    val productsEntities by viewModel.allProducts.collectAsState()
    val context = LocalContext.current
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    var showFullSearch by remember { mutableStateOf(false) }
    // ensure initial data exists
    LaunchedEffect(Unit) {
        viewModel.insertInitialProducts()
    }



    // group by category to display category sections (we still keep original UI structure)
    val grouped: Map<String, List<Pair<Product, Int>>> = remember(productsEntities) {
        // produce Map<category, List<(Product, id)>>
        productsEntities.groupBy { it.category }
            .mapValues { entry ->
                entry.value.map { pe ->
                    Pair(
                        Product(pe.title, pe.subtitle, pe.price, pe.image),
                        pe.id // keep id for navigation
                    )
                }
            }
    }

    Scaffold(
        topBar = {
            if (!showFullSearch) {
                RoboticsTopBar(
                    navController = navController,
                    cartViewModel = cartViewModel,
                    onSearchClick = { showFullSearch = true }
                )
            }
        },
        bottomBar = {// باتن نویگیشن در پایین
            BottomNavBar(
                navController = navController,
                currentRoute = currentRoute
            )},

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (!showFullSearch) {
                FilterSection { selected ->
                    // بعداً فیلتر واقعی اعمال میشه
                    println("فیلتر اعمال شد: $selected")
                }

            }






            // سرچ تمام صفحه
            AnimatedVisibility(
                visible = showFullSearch,
                enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { -it },
                exit = fadeOut(tween(300)) + slideOutVertically(tween(300)) { -it }
            ) {
                FullScreenSearch(
                    onDismiss = { showFullSearch = false },
                    onSearch = { /* بعداً */ }
                )
            }


            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                grouped.forEach { (categoryName, productPairs) ->
                    // header
                    item {
                        Text(
                            text = categoryName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 8.dp)
                        )
                    }

                    // horizontal row of up to 2 items
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(start = 12.dp, bottom = 12.dp)
                        ) {
                            // take first 2 products for this category
                            items(productPairs.take(2)) { pair ->
                                val (product, id) = pair
                                // Wrap original ProductItem (which must remain unchanged) inside a clickable Box
                                Column(
                                    modifier = Modifier
                                        .width(160.dp)
                                        .clickable {
                                            // navigate to detail screen with id
                                            navController.navigate("productDetail/${id}")
                                        }
                                ) {
                                    // reuse your existing ProductItem composable by calling it here
                                    ProductItem(product)
                                }
                            }
                        }
                    }
                }

                // If no categories (empty db), you can fallback to showing original hardcoded grid
                if (grouped.isEmpty()) {
                    item {
                        // keep original grid look by reusing ProductGrid on the original hardcoded list
                        // If you have a hardcoded sample list defined elsewhere, call it; otherwise show nothing
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "No products yet. Try restarting the app or check initial data.",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }


        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoboticsTopBar(
    navController: NavController,
    cartViewModel: CartViewModel = viewModel(),
    onSearchClick: () -> Unit
) {
    val cartCount = cartViewModel.getCartCount()
    val customFont = FontFamily(Font(R.font.font1))

    TopAppBar(
        title = { Text("Robotics", fontWeight = FontWeight.Bold, fontFamily = customFont) },
        actions = {
            Box {
                IconButton(onClick = { navController.navigate("cart") }) {
                    Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart")
                }
                if (cartCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(Color(0xFF1193D4), CircleShape)
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(cartCount.toString(), color = Color.White, fontSize = 10.sp)
                    }
                }
            }
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Filled.Search, contentDescription = "Search")
            }
            Box {
                IconButton(onClick = { navController.navigate("UserAccount") }) {
                    Icon(Icons.Outlined.AccountCircle, contentDescription = "Cart")
                }

            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenSearch(
    onDismiss: () -> Unit,
    onSearch: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val customFont = FontFamily(Font(R.font.font1))
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.systemBars) // مهم: از TopBar فاصله نمی‌گیره
    ) {
        Column {
            // کادر جستجو — دقیقاً جای TopBar
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = Color(0xFF92B7C9))
                },
                trailingIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                },
                placeholder = { Text("Search by name, speaker, or topic", fontFamily = customFont) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                singleLine = true
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                item {
                    if (query.isEmpty()) {
                        Text(
                            "",
                             fontFamily = customFont,
                            color = Color.Gray,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        Text(
                            "\"$query\"",
                            color = Color.Gray,
                            fontFamily  = customFont,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        // اینجا نتایج واقعی میاد
                    }
                }
            }
        }
    }
}






@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSection(
    onApply: (Set<String>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedCategories by remember { mutableStateOf(setOf("All")) }

    val categories = listOf("All", "Drones", "AI Kits", "Sensors")

    Column {
        FilterChip(
            modifier = Modifier.padding(start = 20.dp),
            selected = expanded,
            onClick = { expanded = !expanded },
            label = { Text("Filter") },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Filled.Close else Icons.Filled.Search,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        )

        AnimatedVisibility(visible = expanded) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    categories.forEach { category ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCategories = if (category == "All") {
                                        setOf("All")
                                    } else {
                                        (selectedCategories - "All" + category).toSet()
                                    }
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedCategories.contains(category),
                                onCheckedChange = null,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF1193D4),
                                    checkmarkColor = Color.White
                                )
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(category)
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            onApply(selectedCategories)
                            expanded = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("اعمال فیلتر")
                    }
                }
            }
        }
    }
}


@Composable
fun ProductGrid(products: List<Product>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(160.dp),
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->
            ProductItem(product)
        }
    }
}

@Composable
fun ProductItem(product: Product) {
    Column {
        AsyncImage(
            model = product.image,
            contentDescription = product.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
        )
        Spacer(Modifier.height(6.dp))
        Text(product.title, fontWeight = FontWeight.Medium)
        Text(product.subtitle, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        Text(product.price, fontWeight = FontWeight.Bold)
    }
}

data class Product(
    val title: String,
    val subtitle: String,
    val price: String,
    val image: Int
)



@Composable
fun FloatingMenuRadial(
    context: Context,
    onOpenTrackOrder: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val rotation = remember { Animatable(0f) }
    val distance = remember { Animatable(0f) }

    // موقعیت شعاعی دکمه‌ها (درجه)
    val angles = listOf(80f, 45f, 10f) // بالا، چپ بالا، چپ

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        // رسم دکمه‌های کوچک به صورت شعاعی
        angles.forEachIndexed { index, angle ->
            val radian = Math.toRadians(angle.toDouble())
            val offsetX = (cos(radian) * distance.value).dp
            val offsetY = (sin(radian) * distance.value).dp

            Box(
                modifier = Modifier
                    .padding(end = 24.dp, bottom = 24.dp)
                    .offset(x = -offsetX, y = -offsetY),
                contentAlignment = Alignment.Center
            ) {
                if (distance.value > 10f) {
                    FloatingActionButton(
                        onClick = {
                            when (index) {
                                0 -> startQrScanner(context)
                                1 -> onOpenTrackOrder()
                                2 -> println("Facebook clicked")
                            }
                        },
                        containerColor = when (index) {
                            0 -> Color(0xFFE1306C) // Instagram
                            1 -> Color(0xFF1DA1F2) // Twitter
                            else -> Color(0xFF4267B2) // Facebook
                        },
                        shape = CircleShape,
                        modifier = Modifier.size(56.dp)
                    ) {
                        val icon = when (index) {
                            0 -> Icons.Filled.Search
                            1 -> Icons.Filled.Favorite
                            else -> Icons.Filled.Edit
                        }
                        Icon(icon, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }

        // دکمه اصلی
        FloatingActionButton(
            onClick = {
                expanded = !expanded
                scope.launch {
                    rotation.animateTo(
                        if (expanded) 45f else 0f,
                        animationSpec = tween(300)
                    )
                    distance.animateTo(
                        if (expanded) 110f else 0f, // فاصله دکمه‌های شعاعی
                        animationSpec = tween(400)
                    )
                }
            },
            containerColor = Color(0xFF1193D4),
            shape = CircleShape,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = if (expanded) Icons.Filled.Close else Icons.Filled.Add,
                contentDescription = "Main FAB",
                tint = Color.White,
                modifier = Modifier.rotate(rotation.value)
            )
        }
    }
}



fun startQrScanner(context: Context) {
    val integrator = IntentIntegrator(context as Activity)
    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
    integrator.setPrompt("کد QR را اسکن کنید")
    integrator.setBeepEnabled(true)
    integrator.setOrientationLocked(false)
    integrator.captureActivity = PortraitCaptureActivity::class.java
    integrator.initiateScan()
}



//floatingActionButton = {
//    FloatingMenuRadial(
//        context = context,
//        onOpenTrackOrder = {
//            navController.navigate("trackOrder")
//        }
//    )
//}

