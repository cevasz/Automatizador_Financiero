package com.finanzas.automatica.presentation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.AssignmentReturn
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.ElectricalServices
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material.icons.outlined.LocalPharmacy
import androidx.compose.material.icons.outlined.LocalTaxi
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.VideogameAsset
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.Work
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Traduccion de `CategoryEntity.iconName` a un icono real.
 *
 * El campo existia desde el principio y `DefaultCategories` lo rellenaba con 33
 * nombres distintos, pero **ningun composable lo leia**: la app guardaba el dato
 * y nunca lo mostraba. Aqui empieza a significar algo.
 *
 * Se usa un mapa explicito y no reflexion sobre `Icons.Outlined`: la reflexion
 * sobrevive mal a R8 (que renombra), y ademas dejaria pasar cualquier cadena
 * como valida hasta que falle en tiempo de ejecucion.
 */
object CategoryIcons {

    private val porNombre: Map<String, ImageVector> = mapOf(
        "add_circle" to Icons.Outlined.AddCircle,
        "assignment_return" to Icons.Outlined.AssignmentReturn,
        "attach_money" to Icons.Outlined.AttachMoney,
        "build" to Icons.Outlined.Build,
        "card_giftcard" to Icons.Outlined.CardGiftcard,
        "checkroom" to Icons.Outlined.Checkroom,
        "coffee" to Icons.Outlined.Coffee,
        "devices" to Icons.Outlined.Devices,
        "directions_bus" to Icons.Outlined.DirectionsBus,
        "electrical_services" to Icons.Outlined.ElectricalServices,
        "event" to Icons.Outlined.Event,
        "fastfood" to Icons.Outlined.Fastfood,
        "fitness_center" to Icons.Outlined.FitnessCenter,
        "health_and_safety" to Icons.Outlined.HealthAndSafety,
        "home" to Icons.Outlined.Home,
        "local_gas_station" to Icons.Outlined.LocalGasStation,
        "local_pharmacy" to Icons.Outlined.LocalPharmacy,
        "local_taxi" to Icons.Outlined.LocalTaxi,
        "medical_services" to Icons.Outlined.MedicalServices,
        "menu_book" to Icons.Outlined.MenuBook,
        "money" to Icons.Outlined.Payments,
        "more_horiz" to Icons.Outlined.MoreHoriz,
        "movie" to Icons.Outlined.Movie,
        "payments" to Icons.Outlined.Payments,
        "pets" to Icons.Outlined.Pets,
        "restaurant" to Icons.Outlined.Restaurant,
        "school" to Icons.Outlined.School,
        "sell" to Icons.Outlined.Sell,
        "send" to Icons.Outlined.Send,
        "shopping_cart" to Icons.Outlined.ShoppingCart,
        "subscriptions" to Icons.Outlined.Subscriptions,
        "trending_up" to Icons.Outlined.TrendingUp,
        "videogame_asset" to Icons.Outlined.VideogameAsset,
        "wifi" to Icons.Outlined.Wifi,
        "work" to Icons.Outlined.Work
    )

    /** Nombres ofrecidos al crear o editar una categoria propia. */
    val seleccionables: List<String> = porNombre.keys.sorted()

    /**
     * Icono de [nombre], o uno generico si no se reconoce. Nunca lanza: un
     * `iconName` desconocido (de una version futura, o escrito a mano en la web)
     * no puede tumbar la lista de categorias.
     */
    fun resolve(nombre: String?): ImageVector =
        porNombre[nombre] ?: Icons.Outlined.MoreHoriz
}
