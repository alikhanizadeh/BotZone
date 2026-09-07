package com.example.botzone.Login


import android.net.http.HttpResponseCache.install
import android.util.Log
import coil.util.Logger
import com.example.botzone.Login.network.AuthApi
import com.example.botzone.Room.Store.ProductApi
import com.example.botzone.Room.Store.ProductEntity
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import okhttp3.internal.concurrent.TaskRunner.Companion.logger
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*


//ایجاد ارتباط HTTP با سرور

object RetrofitInstance {

    const val BASE_URL = "https://tall-things-teach.loca.lt/"

    val instance: ApiService by lazy {
        ApiServiceImpl(client)
    }


    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Auth API
    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    // Product API
    val productApi: ProductApi by lazy {
        retrofit.create(ProductApi::class.java)
    }

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            })
        }
    }
}





interface ApiService {
    suspend fun getProducts(): List<ProductEntity>
}

class ApiServiceImpl(private val client: HttpClient) : ApiService {
    override suspend fun getProducts(): List<ProductEntity> {
        return try {
            Log.d("ApiService", "📡 درخواست محصولات از: /api/products")
            client.get("/api/products").body()
        } catch (e: Exception) {
            Log.e("ApiService", "❌ خطا در دریافت محصولات: ${e.message}", e)
            throw e
        }
    }
}
