package com.example.botzone

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.botzone.Conferences.ConferenceDetailsScreen
import com.example.botzone.Conferences.ConferenceRegistrationScreen
import com.example.botzone.Conferences.PaymentScreen
import com.example.botzone.Conferences.RoboticsConferencesScreen
import com.example.botzone.Fake.FakeOrderViewModel
import com.example.botzone.Login.AuthRepository
import com.example.botzone.Login.LoginScreen
import com.example.botzone.Login.LoginViewModel
import com.example.botzone.Login.UserPreferences
import com.example.botzone.Products.InvoiceScreen
import com.example.botzone.Products.ProductDetailScreen
import com.example.botzone.Products.PurchaseCompleteScreen
import com.example.botzone.Products.RoboticsStoreScreen
import com.example.botzone.Products.ShoppingCartScreen
import com.example.botzone.Products.TrackOrderScreen
import com.example.botzone.Room.AppDatabase
import com.example.botzone.Room.Cart.CartViewModel
import com.example.botzone.Room.Conferences.ConferenceRepository
import com.example.botzone.Room.Conferences.ConferenceViewModel
import com.example.botzone.Room.Conferences.ConferenceViewModelFactory
import com.example.botzone.Room.Order.OrderRepository
import com.example.botzone.Room.Order.OrderViewModel
import com.example.botzone.Room.Order.OrderViewModelFactory
import com.example.botzone.Room.Payment.PaymentRepository
import com.example.botzone.Room.Payment.PaymentViewModel
import com.example.botzone.Room.Payment.PaymentViewModelFactory
import com.example.botzone.Room.Registration.RegistrationRepository
import com.example.botzone.Room.Registration.RegistrationViewModel
import com.example.botzone.Room.Registration.RegistrationViewModelFactory
import com.example.botzone.SplashScreen.SplashScreen
import com.example.botzone.SplashScreen.SplashViewModel
import com.example.botzone.ui.theme.BotZoneTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BotZoneTheme {
                AppNavigation()
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current // ✅ لازم برای گرفتن دیتابیس

    // Database & ViewModels
    val db = AppDatabase.getInstance(context)
    val orderRepository = OrderRepository(db.orderDao())
    val factory = OrderViewModelFactory(orderRepository)
    val orderViewModel: OrderViewModel = viewModel(factory = factory)
    val cartViewModel: CartViewModel = viewModel()
    val Fakeviewmodel: FakeOrderViewModel = viewModel()
    val repository = ConferenceRepository(db.conferenceDao())
    val conferenceFactory = ConferenceViewModelFactory(repository)
    val conferenceViewModel: ConferenceViewModel = viewModel(factory = conferenceFactory)
    val registrationRepository = RegistrationRepository(db.registrationDao())
    val registrationFactory = RegistrationViewModelFactory(registrationRepository)
    val registrationViewModel : RegistrationViewModel = viewModel(factory = registrationFactory)
    val paymentRepository = PaymentRepository(db.paymentDao())
    val paymentFactory = PaymentViewModelFactory(paymentRepository)
    val paymentViewModel : PaymentViewModel = viewModel(factory = paymentFactory)
    val repo = AuthRepository()
    val prefs = UserPreferences(context)
    val loginViewModel = LoginViewModel(repo,prefs)
    val splashViewModel = SplashViewModel(prefs)

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        // صفحه plash screen
        composable("splash") {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate("Robotics") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                viewModel = splashViewModel
            )
        }
        
        
        // 🔹 صفحه لاگین
        composable("login") {
            LoginScreen(navController = navController
            , viewModel = loginViewModel)
        }

        // 🔹 صفحه فروشگاه

        composable("Robotics") {
            RoboticsStoreScreen(
                navController = navController,
//                viewModel = Fakeviewmodel,
                cartViewModel = cartViewModel
            )
        }
        // 🔹 جزئیات محصول
        composable(
            route = "productDetail/{productId}",
            arguments = listOf(
                navArgument("productId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: -1
            ProductDetailScreen(
                navController = navController,
                productId = productId,
                cartViewModel = cartViewModel
            )
        }

        // 🔹 سبد خرید
        composable("cart") {
            ShoppingCartScreen(
                navController = navController,
                cartViewModel = cartViewModel
            )
        }

        // 🔹 صفحه تکمیل خرید
        composable("purchaseComplete") {
            PurchaseCompleteScreen(
                navController = navController,
                cartViewModel = cartViewModel,
                orderViewModel = orderViewModel
            )
        }

        // 🔹 صفحه پیگیری سفارش
        composable("trackOrder") {
            TrackOrderScreen()
        }

        // 🔹 صفحه حساب کاربری
        composable("UserAccount") {
            UserAccountScreen()
        }

        // 🔹 فاکتور خرید
        composable(
            route = "invoice/{trackingCode}",
            arguments = listOf(
                navArgument("trackingCode") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val trackingCode = backStackEntry.arguments?.getString("trackingCode") ?: ""
            InvoiceScreen(
                navController = navController,
                trackingCode = trackingCode,
                orderViewModel = orderViewModel
            )
        }

        // داخل NavHost، بعد از صفحه فروشگاه
        composable("Conferences") {
            RoboticsConferencesScreen(navController = navController,conferenceViewModel = conferenceViewModel)
        }

        // داخل NavHost
        composable(
            route = "conferenceDetail/{conferenceId}",
            arguments = listOf(
                navArgument("conferenceId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val conferenceId = backStackEntry.arguments?.getInt("conferenceId") ?: -1
            ConferenceDetailsScreen(
                navController = navController,
                conferenceId = conferenceId,
                conferenceViewModel = conferenceViewModel
            )
        }

        // داخل NavHost، بعد از صفحه فروشگاه
        composable(
            route = "conferenceRegistration/{conferenceId}",
            arguments = listOf(navArgument("conferenceId") { type = NavType.IntType })
        ) {backStackEntry ->
            val conferenceId = backStackEntry.arguments?.getInt("conferenceId") ?: -1
            ConferenceRegistrationScreen(navController = navController,conferenceId = conferenceId,registrationViewModel = registrationViewModel)
        }

        composable(
            route = "payment/{conferenceId}/{registrationId}",
            arguments = listOf(
                navArgument("conferenceId") { type = NavType.IntType },
                navArgument("registrationId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val conferenceId = backStackEntry.arguments?.getInt("conferenceId") ?: -1
            val registrationId = backStackEntry.arguments?.getInt("registrationId") ?: -1
            PaymentScreen(
                navController = navController,
                conferenceId = conferenceId,
                registrationId = registrationId,
                paymentViewModel = paymentViewModel
            )
        }


//        // داخل NavHost، بعد از صفحه فروشگاه
//        composable("Popup") {
//            RegistrationConfirmedPopup(navController = navController,true,{},{})
//        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BotZoneTheme {
        AppNavigation()
    }
}
