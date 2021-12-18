package kekmech.kben.domain.adapters

import kekmech.kben.TypeHolder
import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.SerializationContext
import kekmech.kben.domain.TypeAdapter
import kekmech.kben.domain.dto.BencodeElement

internal class IterableTypeAdapter<T : Any> : TypeAdapter<Iterable<T>>() {

    override fun fromBencode(
        value: BencodeElement,
        context: DeserializationContext,
        typeHolder: TypeHolder
    ): Iterable<T> {
        val iterable = (value as BencodeElement.BencodeList).elements.map<BencodeElement, T> { bencodeElement ->
            context.fromBencode(
                bencodeElement = bencodeElement,
                typeHolder = (typeHolder as TypeHolder.Parameterized).parameterTypes.first(),
            )
        }
        return iterable.toProperIterableType(typeHolder)
    }

    private fun List<T>.toProperIterableType(typeHolder: TypeHolder): Iterable<T> =
        if (typeHolder.type == Set::class) toHashSet() else this

    override fun toBencode(value: Iterable<T>, context: SerializationContext): BencodeElement {
        return BencodeElement.BencodeList(value.map { context.toBencode(it) })
    }
}