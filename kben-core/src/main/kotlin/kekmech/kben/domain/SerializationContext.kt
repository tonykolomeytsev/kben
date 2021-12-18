package kekmech.kben.domain

import kekmech.kben.domain.adapters.AnyTypeAdapter
import kekmech.kben.domain.adapters.EnumTypeAdapter
import kekmech.kben.domain.adapters.IterableTypeAdapter
import kekmech.kben.domain.adapters.MapTypeAdapter
import kekmech.kben.domain.dto.BencodeElement
import kekmech.kben.io.BencodeOutputStream
import java.io.ByteArrayOutputStream
import kotlin.reflect.KClass

class SerializationContext(
    standardTypeAdapters: Map<KClass<out Any>, TypeAdapter<out Any>>,
    customTypeAdapters: Map<KClass<out Any>, TypeAdapter<out Any>>,
) : AbstractContext(standardTypeAdapters, customTypeAdapters) {

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> toBencode(obj: T): BencodeElement {
        val typeAdapter = findTypeAdapterFor(obj::class as KClass<T>)
        return when {
            typeAdapter != null ->
                typeAdapter.toBencode(obj, this)
            obj is Iterable<*> ->
                IterableTypeAdapter<T>().toBencode(obj as Iterable<T>, this)
            obj is Map<*, *> ->
                MapTypeAdapter<T>().toBencode(obj as Map<String, T>, this)
            obj is Enum<*> ->
                EnumTypeAdapter<T>().toBencode(obj, this)
            else ->
                AnyTypeAdapter<T>().toBencode(obj, this)
        }
    }

    internal fun <T : Any> toBencodeByteArray(obj: T): ByteArray =
        ByteArrayOutputStream()
            .apply { BencodeOutputStream(this).write(toBencode(obj)) }
            .toByteArray()
}