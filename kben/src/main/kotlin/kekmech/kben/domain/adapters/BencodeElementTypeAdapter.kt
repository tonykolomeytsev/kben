package kekmech.kben.domain.adapters

import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.SerializationContext
import kekmech.kben.domain.TypeAdapter
import kekmech.kben.domain.dto.BencodeElement

private class BencodeElementTypeAdapter : TypeAdapter<BencodeElement>() {

    override fun fromBencode(value: BencodeElement, context: DeserializationContext): BencodeElement {
        return value
    }

    override fun toBencode(value: BencodeElement, context: SerializationContext): BencodeElement {
        return value
    }
}