package com.example.botzone.Room.Cart

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.botzone.Products.CartItem
import com.example.botzone.Room.Store.ProductEntity

class CartViewModel : ViewModel() {

    // سبد خرید به صورت MutableStateList
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> get() = _cartItems

    fun addToCart(item: CartItem) {
        val existing = _cartItems.find { it.id == item.id }
        if (existing != null) {
            existing.quantity += 1
        } else {
            _cartItems.add(item)
        }
    }


    fun increaseQuantity(item: CartItem) {
        val index = _cartItems.indexOf(item)
        if (index != -1) {
            _cartItems[index] = _cartItems[index].copy(quantity = _cartItems[index].quantity + 1)
        }
    }

    fun decreaseQuantity(item: CartItem) {
        val index = _cartItems.indexOf(item)
        if (index != -1 && _cartItems[index].quantity > 1) {
            _cartItems[index] = _cartItems[index].copy(quantity = _cartItems[index].quantity - 1)
        }
    }


    fun addToCartOrIncrease(product: ProductEntity) {
        val existing = _cartItems.find { it.id == product.id }
        if (existing != null) {
            existing.quantity++
        } else {
            _cartItems.add(
                CartItem(
                    id = product.id,
                    name = product.title,
                    price = product.price.filter { c -> c.isDigit() || c == '.' }.toDoubleOrNull() ?: 0.0,
                    imageUrl = product.imagePath,
                    quantity = 1
                )
            )
        }
    }

    fun removeItem(item: CartItem) {
        _cartItems.remove(item)
    }

    fun getTotal(): Double {
        return _cartItems.sumOf { it.price * it.quantity }
    }

    fun isInCart(id: Int): Boolean {
        return _cartItems.any { it.id == id }
    }


    fun removeFromCart(itemId: Int) {
        _cartItems.removeAll { it.id == itemId }
    }

    fun clearCart() {
        _cartItems.clear()
    }

    fun getCartCount(): Int = _cartItems.sumOf { it.quantity }
}
