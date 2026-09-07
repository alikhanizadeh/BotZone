package com.example.botzone.Room.Store


import com.example.botzone.Login.RetrofitInstance
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null
)

object ProductApiService {

    suspend fun getAllProducts(): List<ProductEntity> {
        return try {
            val response: ApiResponse<List<ProductEntity>> = RetrofitInstance.client
                .get("${RetrofitInstance.BASE_URL}/products")
                .body()

            response.data ?: emptyList()
        } catch (e: Exception) {
            println("❌ Error fetching products: ${e.message}")
            emptyList()
        }
    }

    suspend fun getProductById(id: Int): ProductEntity? {
        return try {
            val response: ApiResponse<ProductEntity> = RetrofitInstance.client
                .get("${RetrofitInstance.BASE_URL}/products/$id")
                .body()

            response.data
        } catch (e: Exception) {
            println("❌ Error fetching product $id: ${e.message}")
            null
        }
    }

    suspend fun getProductsByCategory(category: String): List<ProductEntity> {
        return try {
            val response: ApiResponse<List<ProductEntity>> = RetrofitInstance.client
                .get("${RetrofitInstance.BASE_URL}/products/category/$category")
                .body()

            response.data ?: emptyList()
        } catch (e: Exception) {
            println("❌ Error fetching category: ${e.message}")
            emptyList()
        }
    }
}
