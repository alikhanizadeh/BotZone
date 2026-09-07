package com.example.botzone.Room.Store


import android.content.Context
import com.example.botzone.Login.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object ImageDownloader {

    /**
     * دانلود و ذخیره تصویر از سرور
     * @param context کانتکست اپلیکیشن
     * @param imagePath مسیر تصویر روی سرور (مثل: /images/product_123.jpg)
     * @return مسیر محلی فایل ذخیره شده یا null
     */
    suspend fun downloadAndCacheImage(
        context: Context,
        imagePath: String
    ): String? = withContext(Dispatchers.IO) {
        try {
            // بررسی اگر قبلاً دانلود شده
            val cachedFile = getCachedImageFile(context, imagePath)
            if (cachedFile.exists()) {
                return@withContext cachedFile.absolutePath
            }

            // دانلود از سرور
            val fullUrl = "${RetrofitInstance.BASE_URL}${imagePath.removePrefix("/")}"
            val response = RetrofitInstance.productApi.downloadImage(fullUrl)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!

                // ذخیره در حافظه کش
                cachedFile.parentFile?.mkdirs()
                FileOutputStream(cachedFile).use { output ->
                    body.byteStream().use { input ->
                        input.copyTo(output)
                    }
                }

                return@withContext cachedFile.absolutePath
            }

            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * گرفتن فایل کش شده
     */
    private fun getCachedImageFile(context: Context, imagePath: String): File {
        val fileName = imagePath.substringAfterLast("/")
        val cacheDir = File(context.cacheDir, "product_images")
        return File(cacheDir, fileName)
    }

    /**
     * پاک کردن کش تصاویر
     */
    fun clearImageCache(context: Context) {
        val cacheDir = File(context.cacheDir, "product_images")
        cacheDir.deleteRecursively()
    }

    /**
     * بررسی وجود تصویر در کش
     */
    fun isImageCached(context: Context, imagePath: String): Boolean {
        return getCachedImageFile(context, imagePath).exists()
    }
}
