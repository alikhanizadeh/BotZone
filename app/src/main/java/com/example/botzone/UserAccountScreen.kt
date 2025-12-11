package com.example.botzone



import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAccountScreen(
    userName: String = "Alexei Volkoff",
    userEmail: String = "alexei.volkoff@example.com",
    profileImageUrl: String =
        "https://lh3.googleusercontent.com/aida-public/AB6AXuCZoaVSM8tps75NQY-vEC2AKJ50PA3C27HtNmGDSSXx2A88TtCqAokfRyPgJkcTw7jWEL7flXvYstltPkAm1RFThRzvoJ8IgvKSAikKQ7kpR8rBxoZjxd2qROOhPJQQ4HjTm3KNGYILi16wSGBYENGoTonzx6W61iDr8mwdFaD9dLicxp4kcWKWaKwJa3qAJ2QBZqyyz7UzK7OYTntJYm2pv35smCH89bzAfw-yAXj1HBz9SBcrHbPL_mpBhLhGBBvAS0q0R8MHBV9a"
) {
    Scaffold(
        topBar = {
            TopAppBar(
                actions = {
                    IconButton(onClick = { /* TODO: Settings */ }) {
                        Icon(Icons.Outlined.Settings, contentDescription = "Settings")
                    }
                },
                title = {}
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            // --- Profile Header ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.img1),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = userEmail,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- List Items ---
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                AccountListItem(
                    icon = Icons.Outlined.Lock,
                    title = "Factors and Privacy",
                    iconBackground = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                    onClick = { /* TODO */ }
                )

                AccountListItem(
                    icon = Icons.Outlined.Lock,
                    title = "Terms of Use",
                    iconBackground = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                    onClick = { /* TODO */ }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- Log Out Button ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp) // فاصله از پایین بدون تغییر سایز دکمه
            ) {

                Button(
                    onClick = { /* TODO: Log Out */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red.copy(alpha = 0.1f),
                        contentColor = Color.Red
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Log Out",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AccountListItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    iconBackground: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        shape = RoundedCornerShape(10.dp),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color.White
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                )
            }

            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Next",
                tint = Color.Gray
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun UserAccountPreview() {
    UserAccountScreen()
}
