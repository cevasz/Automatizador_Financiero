package com.finanzas.automatica.data.local.converters

import androidx.room.TypeConverter
import com.finanzas.automatica.domain.model.AgendaOrigin
import com.finanzas.automatica.domain.model.BankEntity
import com.finanzas.automatica.domain.model.ConfirmationState
import com.finanzas.automatica.domain.model.MovementSource
import com.finanzas.automatica.domain.model.MovementType
import com.finanzas.automatica.domain.model.PaymentMethod
import java.time.Instant

class Converters {

    @TypeConverter
    fun fromInstant(value: Long?): Instant? = value?.let { Instant.ofEpochMilli(it) }

    @TypeConverter
    fun toInstant(value: Instant?): Long? = value?.toEpochMilli()

    // MovementType
    @TypeConverter
    fun fromMovementType(value: String?): MovementType? = value?.let { MovementType.valueOf(it) }

    @TypeConverter
    fun toMovementType(value: MovementType?): String? = value?.name

    // PaymentMethod
    @TypeConverter
    fun fromPaymentMethod(value: String?): PaymentMethod? = value?.let { PaymentMethod.valueOf(it) }

    @TypeConverter
    fun toPaymentMethod(value: PaymentMethod?): String? = value?.name

    // MovementSource
    @TypeConverter
    fun fromMovementSource(value: String?): MovementSource? = value?.let { MovementSource.valueOf(it) }

    @TypeConverter
    fun toMovementSource(value: MovementSource?): String? = value?.name

    // ConfirmationState
    @TypeConverter
    fun fromConfirmationState(value: String?): ConfirmationState? = value?.let { ConfirmationState.valueOf(it) }

    @TypeConverter
    fun toConfirmationState(value: ConfirmationState?): String? = value?.name

    // BankEntity
    @TypeConverter
    fun fromBankEntity(value: String?): BankEntity? = value?.let { BankEntity.valueOf(it) }

    @TypeConverter
    fun toBankEntity(value: BankEntity?): String? = value?.name

    // AgendaOrigin
    @TypeConverter
    fun fromAgendaOrigin(value: String?): AgendaOrigin? = value?.let { AgendaOrigin.valueOf(it) }

    @TypeConverter
    fun toAgendaOrigin(value: AgendaOrigin?): String? = value?.name
}