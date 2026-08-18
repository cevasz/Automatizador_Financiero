package com.finanzas.automatica.data.local.entity

import java.util.UUID

/**
 * Identidad de una fila **entre dispositivos**.
 *
 * El `id` de Room es un autoincremental local: dos telefonos del mismo usuario
 * generan el id 1 para movimientos distintos, asi que no sirve para decir "esta
 * fila y esta otra son la misma". El `syncId` (un UUID) si: se genera una sola
 * vez, en el dispositivo donde nace la fila, y viaja con ella a la nube y a
 * cualquier otro dispositivo.
 *
 * Se genera en el cliente y no en el servidor a proposito: Kivo es local-first
 * y tiene que poder registrar un movimiento sin red. Esperar a que el servidor
 * asigne un id significaria no tener identidad estable mientras se esta
 * offline, que es justo cuando mas se usa.
 */
fun newSyncId(): String = UUID.randomUUID().toString()
