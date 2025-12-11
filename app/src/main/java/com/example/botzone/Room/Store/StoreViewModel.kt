package com.example.botzone.Room.Store



import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.botzone.R
import com.example.botzone.Room.AppDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class StoreViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProductRepository

    init {
        val db = AppDatabase.getInstance(application)

        val dao = db.productDao()
        repository = ProductRepository(dao)
    }

    val allProducts: StateFlow<List<ProductEntity>> = repository
        .getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun insertInitialProducts() {
        viewModelScope.launch {
            val currentProducts = repository.getAllProducts().first()
            if (currentProducts.isEmpty()) {
                val sampleData = listOf(
                    ProductEntity(title = "Autonomous Rover Kit", subtitle = "AI Starter Kit", price = "$499.99", image = R.drawable.img1, category = "AI Kits"),
                    ProductEntity(title = "AI Vision Sensor", subtitle = "ML Camera", price = "$120.00", image =  R.drawable.img2, category = "Sensors"),
                    ProductEntity(title = "Pro-Grade Quadcopter", subtitle = "4K Drone", price = "$1,299.00", image =  R.drawable.img3, category = "Drones"),
                    ProductEntity(title = "Hexapod Spider Bot", subtitle = "6-Leg Robot", price = "$349.50", image =  R.drawable.img4, category = "AI Kits"),
                    ProductEntity(title = "High-Precision Servo", subtitle = "Industrial Motor", price = "$75.00", image =  R.drawable.img5, category = "Parts"),
                    ProductEntity(title = "Industrial Arm MK.II", subtitle = "6-Axis Arm", price = "$4,500.00", image =  R.drawable.img6, category = "Industrial")
                )
                repository.insertProducts(sampleData)
            }
        }
    }

    suspend fun getProductById(id: Int): ProductEntity? {
        return repository.getProductById(id)
    }

    fun getProductsByCategory(category: String): Flow<List<ProductEntity>> {
        return repository.getProductsByCategory(category)
    }

}