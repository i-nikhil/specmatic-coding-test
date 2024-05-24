package com.store.controllers

import com.store.repositories.*
import org.apache.maven.surefire.shade.org.apache.commons.lang3.StringUtils
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/products")
class ProductsController(private val productRepository: ProductRepository) {

    @GetMapping("")
    fun getProductsByType(@RequestParam type: String?): ResponseEntity<Any> {
        // Check the type parameter for null
        return if (type == null) {
            ResponseEntity.ok(productRepository.getAllProducts())
        } else {
            val productType = ProductType.fromString(type)

            // Check the type parameter is from valid enums
            if (productType != null) {
                val filteredProducts = productRepository.getAllProducts().filter { it.type == productType }
                if (filteredProducts.isNotEmpty()) {
                    ResponseEntity.ok(filteredProducts)
                } else {
                    ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ErrorResponseBody(
                            timestamp = java.time.LocalDateTime.now().toString(),
                            status = 400,
                            error = "No products found of this type",
                            path = "/products?type=$type"
                        )
                    )
                }
            } else {
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ErrorResponseBody(
                        timestamp = java.time.LocalDateTime.now().toString(),
                        status = 400,
                        error = "Invalid type parameter",
                        path = "/products?type=$type"
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
            || productRequest.cost == null
        ) {
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
            || productRequest.name.equals("false", ignoreCase = true)
        ) {
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
            id = productRepository.getAllProducts().size + 1,
            name = productRequest.name,
            type = productRequest.type,
            inventory = productRequest.inventory,
            cost = productRequest.cost
        )
        productRepository.addProduct(newProduct)
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductId(newProduct.id))
    }
}