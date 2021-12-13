package kekmech.kben.domain.adapters

import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.SerializationContext
import kekmech.kben.domain.TypeAdapter
import kekmech.kben.domain.dto.BencodeElement

internal class LongTypeAdapter : TypeAdapter<Long>() {

    override fun fromBencode(value: BencodeElement, context: DeserializationContext): Long {
        return (value as BencodeElement.BencodeInteger).integer
    }

    override fun toBencode(value: Long, context: SerializationContext): BencodeElement {
        return BencodeElement.BencodeInteger(value)
    }
}