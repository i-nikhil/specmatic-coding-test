package com.store.repositories

import com.fasterxml.jackson.annotation.*
import javax.validation.constraints.*

data class Product(
    val id: Int,
    val name: String,
    val type: ProductType,
    val inventory: Int,
    val cost: Double
)

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