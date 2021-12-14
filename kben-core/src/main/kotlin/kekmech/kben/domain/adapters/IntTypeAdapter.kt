package kekmech.kben.domain.adapters

import kekmech.kben.TypeHolder
import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.SerializationContext
import kekmech.kben.domain.TypeAdapter
import kekmech.kben.domain.dto.BencodeElement

internal class IntTypeAdapter : TypeAdapter<Int>() {

    override fun fromBencode(value: BencodeElement, context: DeserializationContext, typeHolder: TypeHolder): Int {
        return (value as BencodeElement.BencodeInteger).integer.toInt()
    }

    override fun toBencode(value: Int, context: SerializationContext): BencodeElement {
        return BencodeElement.BencodeInteger(value.toLong())
    }
}