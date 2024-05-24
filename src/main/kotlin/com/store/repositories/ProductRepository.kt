package com.store.repositories

import org.springframework.stereotype.Repository

@Repository

class ProductRepository {

    private val products = mutableListOf(
        Product(1, "Magical Wand", ProductType.GADGET, 2, 80.50),
        Product(2, "Flying broomstick", ProductType.BOOK, 20, 275.00),
        Product(3, "Book of magic spells", ProductType.FOOD, 30, 45.90)
    )

    fun getAllProducts(): List<Product> {
        return products
    }

    fun addProduct(product: Product) {
        products.add(product)
    }
}