package com.example.ktor_backend.Product


import com.example.ktor_backend.dbConnection
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.util.*

fun Route.productRoutes() {

    val repository = ProductRepository(dbConnection)


    route("/products") {

        // GET /products - همه محصولات
        get {
            try {
                val products = repository.getAllProducts()
                call.respond(
                    HttpStatusCode.OK,
                    ProductResponse(
                        success = true,
                        data = products,
                        message = "محصولات با موفقیت دریافت شد"
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ProductResponse(
                        success = false,
                        message = "خطا در دریافت محصولات: ${e.message}"
                    )
                )
            }
        }

        // GET /products/{id} - یک محصول خاص
        get("/{id}") {
            try {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        SingleProductResponse(
                            success = false,
                            message = "شناسه محصول نامعتبر است"
                        )
                    )
                    return@get
                }

                val product = repository.getProductById(id)
                if (product == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        SingleProductResponse(
                            success = false,
                            message = "محصول یافت نشد"
                        )
                    )
                    return@get
                }

                call.respond(
                    HttpStatusCode.OK,
                    SingleProductResponse(
                        success = true,
                        data = product,
                        message = "محصول با موفقیت دریافت شد"
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    SingleProductResponse(
                        success = false,
                        message = "خطا در دریافت محصول: ${e.message}"
                    )
                )
            }
        }

        // GET /products/category/{category}
        get("/category/{category}") {
            try {
                val category = call.parameters["category"]
                if (category == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ProductResponse(
                            success = false,
                            message = "دسته‌بندی نامعتبر است"
                        )
                    )
                    return@get
                }

                val products = repository.getProductsByCategory(category)
                call.respond(
                    HttpStatusCode.OK,
                    ProductResponse(
                        success = true,
                        data = products,
                        message = "محصولات دسته $category با موفقیت دریافت شد"
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ProductResponse(
                        success = false,
                        message = "خطا در دریافت محصولات: ${e.message}"
                    )
                )
            }
        }

        // POST /products/upload - آپلود تصویر محصول
        post("/upload") {
            try {
                val multipart = call.receiveMultipart()
                var fileName = ""
                var savedPath = ""

                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            // ساخت نام یونیک برای فایل
                            val originalFileName = part.originalFileName ?: "image.jpg"
                            val extension = originalFileName.substringAfterLast(".", "jpg")
                            fileName = "product_${UUID.randomUUID()}.$extension"

                            // ذخیره فایل در پوشه images
                            val uploadsDir = File("uploads/images")
                            if (!uploadsDir.exists()) {
                                uploadsDir.mkdirs()
                            }

                            val file = File(uploadsDir, fileName)
                            part.streamProvider().use { input ->
                                file.outputStream().buffered().use { output ->
                                    input.copyTo(output)
                                }
                            }

                            savedPath = "/images/$fileName"
                        }
                        else -> {}
                    }
                    part.dispose()
                }

                if (savedPath.isEmpty()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        UploadResponse(
                            success = false,
                            message = "فایلی برای آپلود ارسال نشد"
                        )
                    )
                    return@post
                }

                call.respond(
                    HttpStatusCode.OK,
                    UploadResponse(
                        success = true,
                        message = "تصویر با موفقیت آپلود شد",
                        imagePath = savedPath
                    )
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    UploadResponse(
                        success = false,
                        message = "خطا در آپلود تصویر: ${e.message}"
                    )
                )
            }
        }
    }

    // سرو فایل‌های استاتیک (تصاویر)
    get("/images/{filename}") {
        try {
            val filename = call.parameters["filename"]
            if (filename == null) {
                call.respond(HttpStatusCode.BadRequest, "نام فایل نامعتبر است")
                return@get
            }

            val file = File("uploads/images/$filename")
            if (!file.exists()) {
                call.respond(HttpStatusCode.NotFound, "فایل یافت نشد")
                return@get
            }

            call.respondFile(file)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "خطا در ارسال فایل: ${e.message}")
        }
    }
}
