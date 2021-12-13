package kekmech.kben.domain.adapters

import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.SerializationContext
import kekmech.kben.domain.TypeAdapter
import kekmech.kben.domain.dto.BencodeElement
import kotlin.reflect.KClass

internal class IterableTypeAdapter <T : Any>(
    private val elementsKClass: KClass<T>,
) : TypeAdapter<Iterable<T>>() {

    override fun fromBencode(value: BencodeElement, context: DeserializationContext): Iterable<T> =
        (value as BencodeElement.BencodeList).elements.map { context.fromBencode(it, elementsKClass) }

    override fun toBencode(value: Iterable<T>, context: SerializationContext): BencodeElement {
        return BencodeElement.BencodeList(value.map { context.toBencode(it) })
    }
}