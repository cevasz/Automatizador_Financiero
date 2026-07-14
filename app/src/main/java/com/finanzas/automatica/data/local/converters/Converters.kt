package com.finanzas.automatica.data.local.converters

import androidx.room.TypeConverter
import com.finanzas.automatica.data.local.entity.*
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

// Enums
enum class MovementType { INCOME, EXPENSE }
enum class PaymentMethod { NEQUI, BANCOLOMBIA, DAVIPLATA, NU, LULO, PSE, QR, CASH, CARD, OTHER }
enum class MovementSource { NOTIFICATION, OCR, MANUAL, OPEN_FINANCE }
enum class ConfirmationState { PENDING, CONFIRMED, REJECTED, AUTO_CONFIRMED }
enum class BankEntity { NEQUI, BANCOLOMBIA, DAVIPLATA, NU, LULO, UNKNOWN }
enum class AgendaOrigin { MANUAL, COMMUNITY_SUGGESTED, AUTO_DETECTED }