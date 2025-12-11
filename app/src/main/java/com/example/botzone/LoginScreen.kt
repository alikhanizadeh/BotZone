package com.example.botzone

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.botzone.Login.UserPreferences
import com.example.botzone.authentication.authenticateUser
import kotlinx.coroutines.launch

//import coil.compose.AsyncImage



@Composable
fun LoginScreen(
    navController: NavController,
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefs = remember { UserPreferences(context) }



    val isDark = isSystemInDarkTheme()
    val backgroundColor =
        if (isDark) Color(0xFF101C22) else Color(0xFFF6F7F8)
    val textColor =
        if (isDark) Color.White else Color.Black
    val secondaryText =
        if (isDark) Color(0xFF9CA3AF) else Color(0xFF4B5563)
    val fieldBackground =
        if (isDark) Color(0xFF1F2937) else Color(0xFFF3F4F6)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var savedEmail by remember { mutableStateOf<String?>(null) }
    var savedPass by remember { mutableStateOf<String?>(null) }
    var isLoggedIn by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        prefs.clearUser()
        val (u, p) = prefs.getUser()
        savedEmail = u
        savedPass = p
        isLoggedIn = prefs.isLoggedIn()


        if (isLoggedIn && !u.isNullOrEmpty()) {
            // مستقیماً وارد اپ شو
            Toast.makeText(context, "Welcome back, $u!", Toast.LENGTH_SHORT).show()
            navController.navigate("Robotics") {
                popUpTo("login") { inclusive = true }
            }
        }
    }


    // اگر قبلاً لاگین کرده، احراز هویت بیومتریک انجام بده
    if (isLoggedIn) {
        LaunchedEffect(Unit) {
            val activity = context as MainActivity
            authenticateUser(
                activity = activity,
                onSuccess = {
                    Toast.makeText(context, "Welcome back, $email!", Toast.LENGTH_SHORT).show()
                    navController.navigate("Robotics") {
                        popUpTo("login") { inclusive = true } // حذف صفحه لاگین از استک
                    }
                },
                onError = { error ->
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }


    if (!isLoggedIn) {

        Scaffold(
            containerColor = backgroundColor
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // --- Logo ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = Color(0xFF1193D4),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "ROBOSTORE",
                        color = textColor,
                        fontSize = 22.sp,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                // --- Header Image ---
                Image(
                    painterResource(R.drawable.img_login),
                    contentDescription = "Robotics header",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(24.dp))

                // --- Welcome Text ---
                Text(
                    text = "Welcome Back",
                    fontSize = 30.sp,
                    color = textColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                )
                Text(
                    text = "Sign in to continue to the robotics store.",
                    color = secondaryText,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- Email Field ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = "Username or Email",
                        color = textColor,
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Enter your username or email") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Outlined.Person, contentDescription = null)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = fieldBackground,
                            unfocusedContainerColor = fieldBackground,
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                            focusedBorderColor = Color(0xFF1193D4),
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Password Field ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Password", color = textColor, fontSize = 16.sp)
                        TextButton(onClick = { /* TODO */ }) {
                            Text("Forgot Password?", color = Color(0xFF1193D4))
                        }
                    }
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Enter your password") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Outlined.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = Icons.Outlined.MailOutline,
                                    contentDescription = null,
                                    tint = if (showPassword) Color(0xFF1193D4)
                                    else secondaryText
                                )
                            }
                        },
                        visualTransformation =
                            if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = fieldBackground,
                            unfocusedContainerColor = fieldBackground,
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                            focusedBorderColor = Color(0xFF1193D4),
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- Login Button ---
                Button(
                    onClick = {
                        scope.launch {
                            // اگر هنوز هیچ کاربر ذخیره نشده، یعنی اولین لاگین است
                            if (savedEmail.isNullOrEmpty()) {
                                if (email.isNotEmpty() && password.isNotEmpty()) {
                                    prefs.saveUser(email, password)
                                    prefs.setLoggedIn(true)
                                    Toast.makeText(context, "Welcome, $email!", Toast.LENGTH_SHORT).show()
                                    navController.navigate("Robotics") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    Toast.makeText(context, "Please enter all fields", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                // اگر از قبل کاربر ثبت‌شده، فقط با همون می‌تونه وارد بشه
                                if (email == savedEmail && password == savedPass) {
                                    prefs.setLoggedIn(true)
                                    Toast.makeText(context, "Welcome back, $email!", Toast.LENGTH_SHORT).show()
                                    navController.navigate("Robotics") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    Toast.makeText(context, "You are already logged in with another account.", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1193D4)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Login", color = Color.White, fontSize = 18.sp)
                }

                // --- Divider ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(
                        color = secondaryText.copy(alpha = 0.3f),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "  Or continue with  ",
                        color = secondaryText,
                        fontSize = 14.sp
                    )
                    Divider(
                        color = secondaryText.copy(alpha = 0.3f),
                        modifier = Modifier.weight(1f)
                    )
                }

                // --- Social Buttons ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { /* Google */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isDark) Color(0x80222D3D) else Color.White
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                    ) {
                        Text("Google", color = textColor)
                    }

                    OutlinedButton(
                        onClick = { /* Apple */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isDark) Color(0x80222D3D) else Color.White
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                    ) {
                        Text("Apple", color = textColor)
                    }
                }

                // --- Register Link ---
                Text(
                    text = "Don't have an account? Register",
                    color = secondaryText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

    }


}




@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
        LoginScreen(rememberNavController())
}
