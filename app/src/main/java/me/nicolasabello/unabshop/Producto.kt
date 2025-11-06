package me.nicolasabello.unabshop.model

data class Producto(
    val id: String? = null,
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0
)