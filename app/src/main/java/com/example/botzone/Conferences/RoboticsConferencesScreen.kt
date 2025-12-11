package com.example.botzone.Conferences


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.botzone.BottomNavBar.BottomNavBar
import com.example.botzone.Products.FullScreenSearch
import com.example.botzone.Products.RoboticsTopBar
import com.example.botzone.R
import com.example.botzone.Room.Cart.CartViewModel
import com.example.botzone.Room.Conferences.ConferenceViewModel

data class Conference(
    val title: String,
    val date: String,
    val location: String,
    val speaker: String,
    val imageUrl: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoboticsConferencesScreen(
    navController: NavController,
    conferenceViewModel: ConferenceViewModel
) {

    val conferences by conferenceViewModel.allConferences.collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    var showFullSearch by remember { mutableStateOf(false) }

    // فقط یکبار داده اولیه وارد بشه
    LaunchedEffect(Unit) {
        conferenceViewModel.insertInitialData()
    }

    // اگر هنوز داده نداریم، لودینگ نشون بده
    if (conferences.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            if (!showFullSearch) {
                ConferenceTopBar(
                    navController = navController,
                    onSearchClick = { showFullSearch = true }
                )
            }
        },
        bottomBar = {
            BottomNavBar(
            navController = navController,
            currentRoute = currentRoute
        )}
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            // Chips
            if (!showFullSearch) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "AI & ML", "Automation", "Healthcare", "Autonomous Vehicles").forEachIndexed { index, text ->
                        val selected = index == 0
                        AssistChip(
                            onClick = { /* TODO */ },
                            label = { Text(text) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (selected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.4f),
                                labelColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
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

            // Conference List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(conferences) { conf ->
                    ConferenceCard(
                        conference = Conference(
                            title = conf.title,
                            date = conf.date,
                            location = conf.location,
                            speaker = conf.speaker,
                            imageUrl = conf.imageUrl
                        ),
                        onClick = {
                            navController.navigate("conferenceDetail/${conf.id}")
                        }
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConferenceTopBar(
    navController: NavController,
    onSearchClick: () -> Unit
) {
    val customFont = FontFamily(Font(R.font.font1))

    TopAppBar(
        title = { Text("Robotics Conferences", fontWeight = FontWeight.Bold,fontFamily = customFont) },
        actions = {
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


@Composable
fun ConferenceCard(
    conference: Conference,
    onClick: () -> Unit

) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AsyncImage(
                    model = conference.imageUrl,
                    contentDescription = conference.title,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = conference.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.ic_calendar), contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(conference.date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(conference.location, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.ic_mic), contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(conference.speaker, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onClick() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Registration")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRoboticsConferences() {
//    RoboticsConferencesScreen(rememberNavController())
}
