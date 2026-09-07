package com.example.ktor_backend.Product




import java.sql.Connection
import java.sql.Statement

class ProductRepository(private val connection: Connection) {

    companion object {
        private const val CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS products (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                subtitle TEXT NOT NULL,
                price TEXT NOT NULL,
                image_path TEXT NOT NULL,
                category TEXT NOT NULL
            );
        """
        private const val INSERT_PRODUCT = """
            INSERT INTO products (title, subtitle, price, image_path, category) 
            VALUES (?, ?, ?, ?, ?)
        """
        private const val SELECT_ALL = "SELECT * FROM products"
        private const val SELECT_BY_ID = "SELECT * FROM products WHERE id = ?"
        private const val SELECT_BY_CATEGORY = "SELECT * FROM products WHERE category = ?"
    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(CREATE_TABLE)
        insertInitialProducts()
    }

    private fun insertInitialProducts() {
        val count = connection.prepareStatement("SELECT COUNT(*) FROM products").use { stmt ->
            val rs = stmt.executeQuery()
            if (rs.next()) rs.getInt(1) else 0
        }

        if (count == 0) {
            // محصولات نمونه با مسیرهای محلی
            val products = listOf(
                Product(
                    title = "Autonomous Rover Kit",
                    subtitle = "AI Starter Kit",
                    price = "$499.99",
                    imagePath = "/images/rover_kit.jpg",
                    category = "AI Kits"
                ),
                Product(
                    title = "AI Vision Sensor",
                    subtitle = "ML Camera",
                    price = "$120.00",
                    imagePath = "/images/vision_sensor.jpg",
                    category = "Sensors"
                ),
                Product(
                    title = "Pro-Grade Quadcopter",
                    subtitle = "4K Drone",
                    price = "$1,299.00",
                    imagePath = "/images/quadcopter.jpg",
                    category = "Drones"
                ),
                Product(
                    title = "Hexapod Spider Bot",
                    subtitle = "6-Leg Robot",
                    price = "$349.50",
                    imagePath = "/images/spider_bot.jpg",
                    category = "AI Kits"
                ),
                Product(
                    title = "High-Precision Servo",
                    subtitle = "Industrial Motor",
                    price = "$75.00",
                    imagePath = "/images/servo_motor.jpg",
                    category = "Parts"
                ),
                Product(
                    title = "Industrial Arm MK.II",
                    subtitle = "6-Axis Arm",
                    price = "$4,500.00",
                    imagePath = "/images/industrial_arm.jpg",
                    category = "Industrial"
                )
            )

            products.forEach { insertProduct(it) }
        }
    }

    private fun insertProduct(product: Product) {
        connection.prepareStatement(INSERT_PRODUCT).use { stmt ->
            stmt.setString(1, product.title)
            stmt.setString(2, product.subtitle)
            stmt.setString(3, product.price)
            stmt.setString(4, product.imagePath)
            stmt.setString(5, product.category)
            stmt.executeUpdate()
        }
    }

    fun getAllProducts(): List<Product> {
        val products = mutableListOf<Product>()
        connection.prepareStatement(SELECT_ALL).use { stmt ->
            val rs = stmt.executeQuery()
            while (rs.next()) {
                products.add(
                    Product(
                        id = rs.getInt("id"),
                        title = rs.getString("title"),
                        subtitle = rs.getString("subtitle"),
                        price = rs.getString("price"),
                        imagePath = rs.getString("image_path"),
                        category = rs.getString("category")
                    )
                )
            }
        }
        return products
    }

    fun getProductById(id: Int): Product? {
        connection.prepareStatement(SELECT_BY_ID).use { stmt ->
            stmt.setInt(1, id)
            val rs = stmt.executeQuery()
            if (rs.next()) {
                return Product(
                    id = rs.getInt("id"),
                    title = rs.getString("title"),
                    subtitle = rs.getString("subtitle"),
                    price = rs.getString("price"),
                    imagePath = rs.getString("image_path"),
                    category = rs.getString("category")
                )
            }
        }
        return null
    }

    fun getProductsByCategory(category: String): List<Product> {
        val products = mutableListOf<Product>()
        connection.prepareStatement(SELECT_BY_CATEGORY).use { stmt ->
            stmt.setString(1, category)
            val rs = stmt.executeQuery()
            while (rs.next()) {
                products.add(
                    Product(
                        id = rs.getInt("id"),
                        title = rs.getString("title"),
                        subtitle = rs.getString("subtitle"),
                        price = rs.getString("price"),
                        imagePath = rs.getString("image_path"),
                        category = rs.getString("category")
                    )
                )
            }
        }
        return products
    }

    fun addProduct(product: Product): Int {
        connection.prepareStatement(INSERT_PRODUCT, Statement.RETURN_GENERATED_KEYS).use { stmt ->
            stmt.setString(1, product.title)
            stmt.setString(2, product.subtitle)
            stmt.setString(3, product.price)
            stmt.setString(4, product.imagePath)
            stmt.setString(5, product.category)
            stmt.executeUpdate()

            val generatedKeys = stmt.generatedKeys
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1)
            }
        }
        return -1
    }
}
