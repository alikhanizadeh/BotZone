package com.example.botzone.Conferences


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun RegistrationConfirmedPopup(
    show: Boolean,
    onDismiss: () -> Unit,
    onDownloadCertificate: () -> Unit,
    isDownloaded: Boolean
) {
    if (show) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // دکمه بستن
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Default.Close, "Close", tint = Color.Gray)
                    }

                    Spacer(Modifier.height(8.dp))

                    // آیکون موفقیت
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = Color(0xFF34C759),
                        modifier = Modifier.size(64.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        "پرداخت تأیید شد!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "درخواست شما با موفقیت ثبت شد. گواهی حضور در همایش آماده دانلود است.",
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = onDownloadCertificate,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDownloaded) Color.Gray else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(if (isDownloaded) "دانلود شد" else "دانلود گواهی")
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PaymentPreview() {
//    RegistrationConfirmedPopup(rememberNavController(),true,{},{})
}

