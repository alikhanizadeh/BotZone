package com.example.botzone.Conferences


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.example.botzone.R
import com.example.botzone.Room.Conferences.ConferenceEntity
import com.example.botzone.Room.Conferences.ConferenceViewModel
import kotlinx.coroutines.flow.firstOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConferenceDetailsScreen(
    navController: NavController,
    conferenceId: Int,
    conferenceViewModel: ConferenceViewModel

    ) {

    val conference by produceState<ConferenceEntity?>(null, conferenceId) {
        value = conferenceViewModel.allConferences
            .firstOrNull()
            ?.find { it.id == conferenceId }
    }


    // اگر پیدا نشد
    if (conference == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Conference not found!", color = Color.Red)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Conference Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack()}) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Bookmark */ }) {
                        Icon(painterResource(R.drawable.ic_save), contentDescription = "Bookmark")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Price", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                        Text(
                            "$499.00",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    Button(
                        onClick = { navController.navigate("conferenceRegistration/$conferenceId")},
                        modifier = Modifier.height(50.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Register Now", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header Image
            AsyncImage(
                model = conference!!.imageUrl,
                contentDescription = "Conference Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            // Title
            Text(
                text = conference!!.title,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            // Info Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ConferenceInfoCard(painterResource(R.drawable.ic_cale), conference!!.date, modifier = Modifier.weight(1f))
                ConferenceInfoCard(painterResource(R.drawable.ic_clock), "9:00 AM PST", modifier = Modifier.weight(1f))
                ConferenceInfoCard(painterResource(R.drawable.ic_loc), conference!!.location, modifier = Modifier.weight(1f))
                ConferenceInfoCard(painterResource(R.drawable.hourglass_top), "8 hours", modifier = Modifier.weight(1f))
            }

            // About Section
            SectionHeader("About the Conference")
            Text(
                text = "Dive deep into the future of automation. This conference explores the latest breakthroughs in artificial intelligence and their application in creating truly autonomous robotic systems.",
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )

            // Speaker
            SectionHeader("Speaker Profile")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = conference!!.imageUrl,
                    contentDescription = "Speaker Image",
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(conference!!.speaker, fontWeight = FontWeight.Bold)
                    Text("Lead AI Researcher, Cognito Dynamics", color = MaterialTheme.colorScheme.primary)
                    Text(
                        "Dr. Sharma is a pioneer in neural network architecture for robotic navigation.",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ConferenceInfoCard(icon: Painter, text: String,modifier: Modifier) {
    Column(
        modifier = modifier
            .padding(4.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = text, tint = MaterialTheme.colorScheme.primary)
        Text(text, color = Color.Gray, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewConferenceDetails() {
//    ConferenceDetailsScreen()
}
