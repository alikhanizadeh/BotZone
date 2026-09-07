package com.example.botzone.Room.Store

import kotlinx.serialization.Serializable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Streaming
import retrofit2.http.Url

interface ProductApi {

    @GET("products")
    suspend fun getAllProducts(): Response<ProductResponse>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Response<SingleProductResponse>

    @GET("products/category/{category}")
    suspend fun getProductsByCategory(@Path("category") category: String): Response<ProductResponse>

    @Multipart
    @POST("products/upload")
    suspend fun uploadImage(@Part image: MultipartBody.Part): Response<UploadResponse>

    // دانلود تصویر
    @GET
    @Streaming
    suspend fun downloadImage(@Url imageUrl: String): Response<ResponseBody>
}

@Serializable
data class UploadResponse(
    val success: Boolean,
    val message: String,
    val imagePath: String? = null
)