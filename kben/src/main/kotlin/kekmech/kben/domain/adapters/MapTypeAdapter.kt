package kekmech.kben.domain.adapters

import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.SerializationContext
import kekmech.kben.domain.TypeAdapter
import kekmech.kben.domain.dto.BencodeElement
import kotlin.reflect.KClass

internal class MapTypeAdapter<T : Any>(
    private val valuesKClass: KClass<T>
) : TypeAdapter<Map<String, T>>() {

    override fun fromBencode(value: BencodeElement, context: DeserializationContext): Map<String, T> =
        (value as BencodeElement.BencodeDictionary).entries
            .mapValues { (_, value) -> context.fromBencode(value, valuesKClass) }

    override fun toBencode(value: Map<String, T>, context: SerializationContext): BencodeElement {
        return BencodeElement.BencodeDictionary(
            value
                .mapValues { (_, value) -> context.toBencode(value) }
                .toSortedMap()
        )
    }
}