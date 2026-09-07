package com.example.botzone.Room.Store


import android.app.Application
import kotlinx.coroutines.flow.flow
import android.content.Context
import android.util.Log
import com.example.botzone.Login.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class ProductRepository(
    private val productDao: ProductDao,
    private val context: Context
) {
    private val apiClient = RetrofitInstance.instance

    companion object {
        private const val TAG = "ProductRepo"
    }

    /**
     * دریافت تمام محصولات (ابتدا از کش، سپس سینک با سرور)
     */
    fun getAllProducts(): Flow<Result<List<ProductEntity>>> = flow {
        try {
            Log.d(TAG, "🔍 شروع بارگذاری محصولات...")

            // 1️⃣ ابتدا از Room بخون
            val localProducts = productDao.getAllProducts()
            Log.d(TAG, "📱 محصولات محلی: ${localProducts.size}")

            if (localProducts.isNotEmpty()) {
                emit(Result.success(localProducts))
            }

            // 2️⃣ سینک با سرور
            try {
                Log.d(TAG, "🌐 درحال اتصال به سرور...")
                val serverProducts = apiClient.getProducts()
                Log.d(TAG, "✅ محصولات سرور: ${serverProducts.size}")

                if (serverProducts.isNotEmpty()) {
                    // پاک کردن کش قدیمی
                    productDao.deleteAll()

                    // ذخیره محصولات جدید
                    val entities = serverProducts.map { it }
                    productDao.insertAll(entities)
                    Log.d(TAG, "💾 ذخیره شد: ${entities.size} محصول")

                    emit(Result.success(entities))
                } else {
                    // اگر سرور خالی بود، از کش قبلی استفاده کن
                    if (localProducts.isEmpty()) {
                        Log.w(TAG, "⚠️ هیچ محصولی یافت نشد")
                        emit(Result.success(emptyList()))
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ خطا در اتصال به سرور: ${e.message}", e)

                // در صورت خطا، اگر کش داریم بفرست
                if (localProducts.isNotEmpty()) {
                    emit(Result.success(localProducts))
                } else {
                    emit(Result.failure(e))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ خطای کلی: ${e.message}", e)
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO) as Flow<Result<List<ProductEntity>>>

    /**
     * رفرش کردن محصولات از سرور
     */
    suspend fun refreshProducts(): Result<List<ProductEntity>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "🔄 رفرش محصولات...")

            val serverProducts = apiClient.getProducts()
            Log.d(TAG, "✅ دریافت شد: ${serverProducts.size} محصول")

            if (serverProducts.isNotEmpty()) {
                productDao.deleteAll()
                val entities = serverProducts.map { it }
                productDao.insertAll(entities)
                Log.d(TAG, "💾 ذخیره شد")
                Result.success(entities)
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ خطا در رفرش: ${e.message}", e)
            Result.failure(e)
        } as Result<List<ProductEntity>>
    }

    /**
     * دریافت محصول با ID
     */
    fun getProductById(id: Int): Flow<Result<ProductEntity?>> = flow {
        try {
            Log.d(TAG, "🔍 جستجوی محصول: $id")

            val product = productDao.getProductById(id)
            if (product != null) {
                emit(Result.success(product))
            } else {
                emit(Result.failure(Exception("محصول یافت نشد")))
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ خطا در جستجو: ${e.message}", e)
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * دریافت محصولات بر اساس دسته‌بندی
     */
    fun getProductsByCategory(category: String): Flow<Result<List<ProductEntity>>> = flow {
        try {
            Log.d(TAG, "📂 فیلتر دسته: $category")

            val products = productDao.getProductsByCategory(category)
            emit(Result.success(products))
        } catch (e: Exception) {
            Log.e(TAG, "❌ خطا در فیلتر: ${e.message}", e)
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * جستجوی محصولات
     */
    fun searchProducts(query: String): Flow<List<ProductEntity>> = flow {
        try {
            Log.d(TAG, "🔎 جستجو: $query")

            val products = productDao.searchProducts("%$query%")
            emit(products)
        } catch (e: Exception) {
            Log.e(TAG, "❌ خطا در جستجو: ${e.message}", e)
//            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO) as Flow<List<ProductEntity>>

    /**
     * پاک کردن کش
     */
    suspend fun clearCache() = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "🗑️ پاک کردن کش...")
            productDao.deleteAll()
        } catch (e: Exception) {
            Log.e(TAG, "❌ خطا در پاک کردن: ${e.message}", e)
        }
    }
}

