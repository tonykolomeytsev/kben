package kekmech.kben.domain.adapters

import kekmech.kben.TypeHolder
import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.SerializationContext
import kekmech.kben.domain.TypeAdapter
import kekmech.kben.domain.dto.BencodeElement

internal class BencodeByteArrayTypeAdapter : TypeAdapter<BencodeElement.BencodeByteArray>() {

    override fun fromBencode(
        value: BencodeElement,
        context: DeserializationContext,
        typeHolder: TypeHolder
    ): BencodeElement.BencodeByteArray {
        return value as BencodeElement.BencodeByteArray
    }

    override fun toBencode(value: BencodeElement.BencodeByteArray, context: SerializationContext): BencodeElement {
        return value
    }
}