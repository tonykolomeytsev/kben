package kekmech.kben.domain.adapters

import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.SerializationContext
import kekmech.kben.domain.TypeAdapter
import kekmech.kben.domain.dto.BencodeElement

internal class StringTypeAdapter : TypeAdapter<String>() {

    override fun fromBencode(value: BencodeElement, context: DeserializationContext): String {
        return (value as BencodeElement.BencodeByteArray).asString
    }

    override fun toBencode(value: String, context: SerializationContext): BencodeElement {
        return BencodeElement.BencodeByteArray(value)
    }
}