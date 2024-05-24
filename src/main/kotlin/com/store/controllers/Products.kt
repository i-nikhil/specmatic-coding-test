package com.store.controllers

import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import javax.validation.*
import javax.validation.constraints.*
import com.fasterxml.jackson.annotation.*
import org.apache.maven.surefire.shade.org.apache.commons.lang3.StringUtils

@RestController
@RequestMapping("/products")
class ProductsController {

    private val products = mutableListOf(
        Product(1, "Magical Wand", ProductType.GADGET, 2, 80.50),
        Product(2, "Flying broomstick", ProductType.BOOK, 20, 275.00),
        Product(3, "Book of magic spells", ProductType.FOOD, 30, 45.90)
    )

    @GetMapping("")
    fun getProductsByType(@RequestParam type: String?): ResponseEntity<Any> {
        // Validate type parameter
        return if (type == null) {
            ResponseEntity.ok(products)
        } else {
            val productType = ProductType.fromString(type)
            if (productType != null) {
                val filteredProducts = products.filter { it.type == productType }
                if (filteredProducts.isNotEmpty()) {
                    ResponseEntity.ok(filteredProducts)
                } else {
                    ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ErrorResponseBody(
                            timestamp = java.time.LocalDateTime.now().toString(),
                            status = 400,
                            error = "No products found of this type",
                            path = "/products"
                        )
                    )
                }
            } else {
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ErrorResponseBody(
                        timestamp = java.time.LocalDateTime.now().toString(),
                        status = 400,
                        error = "Invalid type parameter",
                        path = "/products"
                    )
                )
            }
        }
    }

    @PostMapping("")
    fun addProduct(@Valid @RequestBody productRequest: ProductRequest): ResponseEntity<Any> {
        // Check if the fields are not null or empty
        if (productRequest.name.isEmpty()
            || productRequest.type == null
            || productRequest.inventory == null
            || productRequest.cost == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponseBody(
                    timestamp = java.time.LocalDateTime.now().toString(),
                    status = 400,
                    error = "Bad Request",
                    path = "/products"
                )
            )
        }

        // Check the name field is not numeric
        if (StringUtils.isNumeric(productRequest.name)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponseBody(
                    timestamp = java.time.LocalDateTime.now().toString(),
                    status = 400,
                    error = "Name must not be a numeric value",
                    path = "/products"
                )
            )
        }

        // Check the name field is not boolean
        if (productRequest.name.equals("true", ignoreCase = true)
            || productRequest.name.equals("false", ignoreCase = true)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponseBody(
                    timestamp = java.time.LocalDateTime.now().toString(),
                    status = 400,
                    error = "Name must not be boolean",
                    path = "/products"
                )
            )
        }

        val newProduct = Product(
            id = products.size + 1,
            name = productRequest.name,
            type = productRequest.type,
            inventory = productRequest.inventory,
            cost = productRequest.cost
        )
        products.add(newProduct)
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductId(newProduct.id))
    }
}

data class Product(val id: Int, val name: String, val type: ProductType, val inventory: Int, val cost: Double)

data class ProductRequest @JsonCreator constructor(
    @field:NotBlank(message = "Name must not be blank") @JsonProperty("name") val name: String,
    @field:NotNull(message = "Type must not be null") @JsonProperty("type") val type: ProductType?,
    @field:NotNull(message = "Inventory must not be null") @JsonProperty("inventory") val inventory: Int?,
    @field:NotNull(message = "Cost must not be null") @JsonProperty("cost") val cost: Double?
)

data class ProductId(val id: Int)

data class ErrorResponseBody(
    val timestamp: String, val status: Int, val error: String, val path: String
)

enum class ProductType(@JsonValue val type: String) {
    BOOK("book"), FOOD("food"), GADGET("gadget"), OTHER("other");

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromString(type: String): ProductType? {
            return entries.find { it.type == type }
        }
    }
}