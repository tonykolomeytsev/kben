package kekmech.kben.domain.adapters

import kekmech.kben.TypeHolder
import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.SerializationContext
import kekmech.kben.domain.TypeAdapter
import kekmech.kben.domain.dto.BencodeElement

internal class BencodeByteStringTypeAdapter : TypeAdapter<BencodeElement.BencodeByteString>() {

    override fun fromBencode(
        value: BencodeElement,
        context: DeserializationContext,
        typeHolder: TypeHolder
    ): BencodeElement.BencodeByteString {
        return value as BencodeElement.BencodeByteString
    }

    override fun toBencode(value: BencodeElement.BencodeByteString, context: SerializationContext): BencodeElement {
        return value
    }
}