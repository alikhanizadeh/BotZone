package com.example.botzone.Room.Store



import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.botzone.R
import com.example.botzone.Room.AppDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class StoreViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProductRepository

    private val _allProducts = MutableStateFlow<List<ProductEntity>>(emptyList())
    val allProducts: StateFlow<List<ProductEntity>> = _allProducts.asStateFlow()

    private val _uiState = MutableStateFlow<StoreUiState>(StoreUiState.Loading)
    val uiState: StateFlow<StoreUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    init {
        Log.d("StoreViewModel", "🚀 ViewModel ساخته شد")
        val database = AppDatabase.getInstance(application)
        repository = ProductRepository(database.productDao(), application)

        // شروع دریافت محصولات
        loadProducts()
    }

    /**
     * بارگذاری محصولات
     */
    fun loadProducts() {
        Log.d("StoreViewModel", "⏳ loadProducts() صدا زده شد")
        viewModelScope.launch {
            _uiState.value = StoreUiState.Loading

            repository.getAllProducts()
                .collect { result ->
                    result.onSuccess { products ->
                        Log.d("StoreViewModel", "✅ دریافت شد: ${products.size} محصول")
                        _allProducts.value = products
                        _uiState.value = if (products.isEmpty()) {
                            StoreUiState.Empty("هیچ محصولی یافت نشد")
                        } else {
                            StoreUiState.Success(products)
                        }
                    }.onFailure { error ->
                        Log.e("StoreViewModel", "❌ خطا: ${error.message}", error)
                        _uiState.value = StoreUiState.Error(
                            error.message ?: "خطای نامشخص"
                        )
                    }
                }
        }
    }

    /**
     * رفرش محصولات
     */
    fun refreshProducts() {
        viewModelScope.launch {
            _uiState.value = StoreUiState.Refreshing

            val result = repository.refreshProducts()
            result.onSuccess { products ->
                _allProducts.value = products
                _uiState.value = if (products.isEmpty()) {
                    StoreUiState.Empty("هیچ محصولی یافت نشد")
                } else {
                    StoreUiState.Success(products)
                }
            }.onFailure { error ->
                _uiState.value = StoreUiState.Error(
                    error.message ?: "خطا در رفرش محصولات"
                )
            }
        }
    }

    /**
     * فیلتر بر اساس دسته‌بندی
     */
    fun filterByCategory(category: String) {
        _selectedCategory.value = category

        viewModelScope.launch {
            _uiState.value = StoreUiState.Loading

            if (category == "All") {
                loadProducts()
            } else {
                repository.getProductsByCategory(category)
                    .collect { result ->
                        result.onSuccess { products ->
                            _allProducts.value = products
                            _uiState.value = if (products.isEmpty()) {
                                StoreUiState.Empty("محصولی در این دسته یافت نشد")
                            } else {
                                StoreUiState.Success(products)
                            }
                        }.onFailure { error ->
                            _uiState.value = StoreUiState.Error(
                                error.message ?: "خطا در فیلتر محصولات"
                            )
                        }
                    }
            }
        }
    }

    /**
     * جستجو در محصولات
     */
    fun searchProducts(query: String) {
        _searchQuery.value = query

        viewModelScope.launch {
            if (query.isBlank()) {
                loadProducts()
            } else {
                repository.searchProducts(query)
                    .collect { products ->
                        _allProducts.value = products
                        _uiState.value = if (products.isEmpty()) {
                            StoreUiState.Empty("نتیجه‌ای برای جستجوی شما یافت نشد")
                        } else {
                            StoreUiState.Success(products)
                        }
                    }
            }
        }
    }

    /**
     * پاک کردن کش
     */
    fun clearCache() {
        viewModelScope.launch {
            repository.clearCache()
            loadProducts()
        }
    }

    /**
     * گرفتن محصول خاص
     */
    fun getProductById(id: Int, onResult: (ProductEntity?) -> Unit) {
        viewModelScope.launch {
            repository.getProductById(id)
                .collect { result ->
                    result.onSuccess { product ->
                        onResult(product)
                    }.onFailure {
                        onResult(null)
                    }
                }
        }
    }
}

/**
 * State های UI
 */
sealed class StoreUiState {
    object Loading : StoreUiState()
    object Refreshing : StoreUiState()
    data class Success(val products: List<ProductEntity>) : StoreUiState()
    data class Empty(val message: String) : StoreUiState()
    data class Error(val message: String) : StoreUiState()
}



class StoreViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoreViewModel::class.java)) {
            return StoreViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
