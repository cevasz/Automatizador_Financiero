package com.finanzas.automatica.data.repository

import com.finanzas.automatica.data.local.entity.CategoryEntity
import com.finanzas.automatica.data.repository.CategoryRepositoryImpl

object DefaultCategories {

    // Categorías de EGRESOS (gastos)
    private val expenseCategories = listOf(
        // Alimentación
        CategoryEntity(0, "Supermercado", "EXPENSE", "shopping_cart", false, null, 10),
        CategoryEntity(0, "Restaurantes", "EXPENSE", "restaurant", false, null, 20),
        CategoryEntity(0, "Comida rápida", "EXPENSE", "fastfood", false, null, 30),
        CategoryEntity(0, "Café/Desayuno", "EXPENSE", "coffee", false, null, 40),
        
        // Transporte
        CategoryEntity(0, "Transporte público", "EXPENSE", "directions_bus", false, null, 100),
        CategoryEntity(0, "Gasolina/Combustible", "EXPENSE", "local_gas_station", false, null, 110),
        CategoryEntity(0, "Taxi/Uber/Didi", "EXPENSE", "local_taxi", false, null, 120),
        CategoryEntity(0, "Mantenimiento vehículo", "EXPENSE", "build", false, null, 130),
        
        // Servicios
        CategoryEntity(0, "Servicios públicos", "EXPENSE", "electrical_services", false, null, 200),
        CategoryEntity(0, "Internet/Telefonía", "EXPENSE", "wifi", false, null, 210),
        CategoryEntity(0, "Suscripciones", "EXPENSE", "subscriptions", false, null, 220),
        
        // Salud
        CategoryEntity(0, "Farmacia", "EXPENSE", "local_pharmacy", false, null, 300),
        CategoryEntity(0, "Médico/Dentista", "EXPENSE", "medical_services", false, null, 310),
        CategoryEntity(0, "Seguro médico", "EXPENSE", "health_and_safety", false, null, 320),
        
        // Educación
        CategoryEntity(0, "Colegiatura/Cursos", "EXPENSE", "school", false, null, 400),
        CategoryEntity(0, "Libros/Materiales", "EXPENSE", "menu_book", false, null, 410),
        
        // Entretenimiento
        CategoryEntity(0, "Streaming/Películas", "EXPENSE", "movie", false, null, 500),
        CategoryEntity(0, "Videojuegos", "EXPENSE", "videogame_asset", false, null, 510),
        CategoryEntity(0, "Eventos/Conciertos", "EXPENSE", "event", false, null, 520),
        
        // Compras
        CategoryEntity(0, "Ropa/Calzado", "EXPENSE", "checkroom", false, null, 600),
        CategoryEntity(0, "Tecnología", "EXPENSE", "devices", false, null, 610),
        CategoryEntity(0, "Hogar/Decoración", "EXPENSE", "home", false, null, 620),
        
        // Otros
        CategoryEntity(0, "Efectivo/Retiros", "EXPENSE", "money", false, null, 900),
        CategoryEntity(0, "Transferencias enviadas", "EXPENSE", "send", false, null, 910),
        CategoryEntity(0, "Otros gastos", "EXPENSE", "more_horiz", false, null, 990),
    )

    // Categorías de INGRESOS
    private val incomeCategories = listOf(
        CategoryEntity(0, "Salario", "INCOME", "payments", false, null, 10),
        CategoryEntity(0, "Freelance/Independiente", "INCOME", "work", false, null, 20),
        CategoryEntity(0, "Inversiones", "INCOME", "trending_up", false, null, 30),
        CategoryEntity(0, "Devoluciones", "INCOME", "assignment_return", false, null, 40),
        CategoryEntity(0, "Préstamos recibidos", "INCOME", "attach_money", false, null, 50),
        CategoryEntity(0, "Ventas", "INCOME", "sell", false, null, 60),
        CategoryEntity(0, "Regalos/Donaciones", "INCOME", "card_giftcard", false, null, 70),
        CategoryEntity(0, "Otros ingresos", "INCOME", "add_circle", false, null, 990),
    )

    fun seed(database: FinanzasDatabase): List<Long> {
        val repo = CategoryRepositoryImpl(database)
        val allCategories = expenseCategories + incomeCategories
        return repo.insertAll(allCategories)
    }

    fun getAll(): List<CategoryEntity> {
        return expenseCategories + incomeCategories
    }
}