package com.example.botzone.Room.Store

import androidx.room.Entity
import androidx.room.PrimaryKey

import kotlinx.serialization.Serializable

@Entity(tableName = "products")
@Serializable
data class ProductEntity(
    @PrimaryKey val id: Int = 0,
    val title: String,
    val subtitle: String,
    val price: String,
    val imagePath: String,  // مسیر سرور: /images/product_123.jpg
    val category: String,
    val cachedImagePath: String? = null  // مسیر کش محلی
) {
    /**
     * گرفتن مسیر نهایی تصویر برای نمایش
     */
    fun getDisplayImagePath(): String? {
        return cachedImagePath  // اگر null باشد، باید پلیس‌هولدر نشان بدیم
    }

    /**
     * آیا تصویر کش شده است؟
     */
    fun isImageCached(): Boolean {
        return !cachedImagePath.isNullOrEmpty()
    }
}

@Suppress("PLUGIN_IS_NOT_ENABLED")
@Serializable
data class ProductResponse(
    val success: Boolean,
    val message: String? = null,
    val data: List<ProductEntity>? = null
)

@Suppress("PLUGIN_IS_NOT_ENABLED")
@Serializable
data class SingleProductResponse(
    val success: Boolean,
    val message: String? = null,
    val data: ProductEntity? = null
)