package com.finanzas.automatica.data.repository

import com.finanzas.automatica.data.local.entity.CategoryEntity
import com.finanzas.automatica.data.repository.CategoryRepositoryImpl
import com.finanzas.automatica.data.local.FinanzasDatabase

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

    /**
     * Siembra las categorias por defecto -- pero solo si la tabla esta vacia.
     *
     * Bug corregido: esto se llamaba en CADA arranque de la app (FinanzasApplication.
     * onCreate) sin verificar si ya existian. `insert(onConflict = IGNORE)` no evitaba
     * nada porque el id es autogenerado (nunca hay choque de clave primaria) y no hay
     * indice unico en (name, type) -- cada reinicio insertaba las 33 categorias de nuevo,
     * duplicandolas en cualquier selector de categoria. Ver docs/PENDIENTES.md.
     */
    suspend fun seed(database: FinanzasDatabase): List<Long> {
        val repo = CategoryRepositoryImpl(database)
        if (database.categoryDao().count() > 0) return emptyList()
        val allCategories = expenseCategories + incomeCategories
        return repo.insertAll(allCategories)
    }

    /**
     * Corrige categorias que ya quedaron duplicadas en el dispositivo por el bug de
     * arriba (versiones anteriores de la app sembraban en cada arranque). Idempotente:
     * si no hay duplicados no hace nada, seguro de llamar en cada arranque.
     *
     * Para cada grupo de categorias con el mismo (nombre, tipo), conserva la de menor id
     * (la mas antigua) como "canonica" y reapunta a ella los movimientos/reglas/
     * presupuestos que apuntaban a las copias antes de borrarlas -- para no perder
     * clasificaciones ni presupuestos ya hechos por el usuario.
     */
    suspend fun dedupe(database: FinanzasDatabase) {
        val categoryDao = database.categoryDao()
        val movementDao = database.movementDao()
        val ruleDao = database.classificationRuleDao()
        val budgetDao = database.budgetDao()

        val groups = categoryDao.getAll().groupBy { it.name to it.type }
        for (rows in groups.values) {
            if (rows.size <= 1) continue
            val sorted = rows.sortedBy { it.id }
            val canonicalId = sorted.first().id

            for (duplicate in sorted.drop(1)) {
                movementDao.reassignCategory(duplicate.id, canonicalId)
                ruleDao.reassignCategory(duplicate.id, canonicalId)

                // Los presupuestos tienen un indice unico (categoryId, month, year): si
                // la categoria canonica ya tiene un presupuesto para ese mes/año, no se
                // puede reapuntar sin violar el indice -- en ese caso se descarta el
                // presupuesto duplicado (ya hay uno equivalente en la canonica).
                for (budget in budgetDao.getByCategory(duplicate.id)) {
                    try {
                        budgetDao.reassignSingle(budget.id, canonicalId)
                    } catch (e: Exception) {
                        budgetDao.deleteById(budget.id)
                    }
                }

                categoryDao.deleteById(duplicate.id)
            }
        }
    }

    fun getAll(): List<CategoryEntity> {
        return expenseCategories + incomeCategories
    }
}