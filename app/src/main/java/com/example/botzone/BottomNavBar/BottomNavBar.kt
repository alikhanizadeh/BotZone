package com.example.botzone.BottomNavBar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.botzone.AppNavigation
import com.example.botzone.R
import com.example.botzone.ui.theme.BotZoneTheme
//onItemSelected: (String) -> Unit
@Composable
fun BottomNavBar(
    navController: NavController,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem("Products", "Robotics", R.drawable.ic_store),
        NavItem("Conferences", "Conferences", R.drawable.ic_conference)
    )

    // فقط اگر در یکی از این دو صفحه بودیم، نمایش بده
    if (currentRoute !in items.map { it.route }) return

    NavigationBar(
        containerColor = Color(0xFF0D151A),
        tonalElevation = 0.dp,
        modifier = modifier.height(80.dp)
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // برای جلوگیری از انباشته شدن صفحات
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        modifier = Modifier.size(50.dp),
                        contentDescription = item.title,
                        tint = if (selected) Color(0xFF29B6F6) else Color(0xFF90A4AE)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

data class NavItem(val title: String, val route: String, val icon: Int)



@Preview(showBackground = true)
@Composable
fun BottomNavBarPreview() {
//    BottomNavBar()
}



@Composable
fun CustomBottomNavigation(
    navController: NavController,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem("Products", "Robotics", R.drawable.ic_store),
        NavItem("Conferences", "Conferences", R.drawable.ic_conference)
    )

    if (currentRoute !in items.map { it.route }) return

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 50.dp, vertical = 25.dp),
        color = Color.White,
        shape = RoundedCornerShape(20.dp), // گوشه‌های گرد
        shadowElevation = 6.dp // سایه‌ی سبک مثل تصویر
    ) {
        Row(
            modifier = Modifier
                .background(Color.Transparent)
                .fillMaxWidth()
                .padding(horizontal = 60.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { item ->

                val selected = currentRoute == item.route
                val color = if (selected) Color(0xFF0D6EFD) else Color.Gray

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable{
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                // برای جلوگیری از انباشته شدن صفحات
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.route,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = item.title,
                        color = color,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingNavigation() {
//    CustomBottomNavigation()
}


