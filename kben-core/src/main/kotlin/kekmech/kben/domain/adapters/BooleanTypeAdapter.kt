package kekmech.kben.domain.adapters

import kekmech.kben.TypeHolder
import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.SerializationContext
import kekmech.kben.domain.TypeAdapter
import kekmech.kben.domain.dto.BencodeElement
import kekmech.kben.domain.dto.BencodeElement.BencodeByteString
import kekmech.kben.domain.dto.BencodeElement.BencodeInteger

internal class BooleanTypeAdapter : TypeAdapter<Boolean>() {

    override fun fromBencode(value: BencodeElement, context: DeserializationContext, typeHolder: TypeHolder): Boolean =
        when (value) {
            is BencodeInteger -> value.integer != 0L
            is BencodeByteString ->
                when (value.asString) {
                    "true", "yes" -> true
                    "false", "no" -> false
                    else -> throw cantCastError(value)
                }
            else -> throw cantCastError(value)
        }

    override fun toBencode(value: Boolean, context: SerializationContext): BencodeElement =
        BencodeInteger(if (value) 1L else 0L)

    private fun cantCastError(value: BencodeElement): Throwable =
        IllegalStateException("Can't cast $value to boolean")
}