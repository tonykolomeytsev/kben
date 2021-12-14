package kekmech.kben.domain.adapters

import kekmech.kben.TypeHolder
import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.SerializationContext
import kekmech.kben.domain.TypeAdapter
import kekmech.kben.domain.dto.BencodeElement

internal class MapTypeAdapter<T : Any> : TypeAdapter<Map<String, T>>() {

    override fun fromBencode(
        value: BencodeElement,
        context: DeserializationContext,
        typeHolder: TypeHolder
    ): Map<String, T> =
        (value as BencodeElement.BencodeDictionary).entries.mapValues { (_, bencodeElement) ->
            context.fromBencode(
                bencodeElement = bencodeElement,
                typeHolder = (typeHolder as TypeHolder.Parameterized).parameterTypes.last()
            )
        }

    override fun toBencode(value: Map<String, T>, context: SerializationContext): BencodeElement {
        return BencodeElement.BencodeDictionary(
            value
                .mapValues { (_, value) -> context.toBencode(value) }
                .toSortedMap()
        )
    }
}