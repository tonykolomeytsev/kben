package kekmech.kben.domain.adapters

import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.SerializationContext
import kekmech.kben.domain.TypeAdapter
import kekmech.kben.domain.dto.BencodeElement

internal class BencodeIntegerTypeAdapter : TypeAdapter<BencodeElement.BencodeInteger>() {

    override fun fromBencode(value: BencodeElement, context: DeserializationContext): BencodeElement.BencodeInteger {
        return value as BencodeElement.BencodeInteger
    }

    override fun toBencode(value: BencodeElement.BencodeInteger, context: SerializationContext): BencodeElement {
        return value
    }
}