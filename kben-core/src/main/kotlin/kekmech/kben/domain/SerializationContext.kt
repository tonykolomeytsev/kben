package kekmech.kben.domain

import kekmech.kben.Kben
import kekmech.kben.domain.dto.BencodeElement
import kekmech.kben.io.BencodeOutputStream
import java.io.ByteArrayOutputStream
import kotlin.reflect.KClass

class SerializationContext(kben: Kben) : AbstractContext(kben.standardTypeAdapters, kben.customTypeAdapters) {

    private val iterableTypeAdapter = kben.iterableTypeAdapter
    private val mapTypeAdapter = kben.mapTypeAdapter
    private val enumTypeAdapter = kben.enumTypeAdapter
    private val anyTypeAdapter = kben.anyTypeAdapter

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> toBencode(obj: T): BencodeElement {
        val typeAdapter = findTypeAdapterFor(obj::class as KClass<T>)
        return when {
            typeAdapter != null ->
                typeAdapter.toBencode(obj, this)
            obj is Iterable<*> ->
                iterableTypeAdapter.toBencode(obj as Iterable<T>, this)
            obj is Map<*, *> ->
                mapTypeAdapter.toBencode(obj as Map<String, T>, this)
            obj is Enum<*> ->
                enumTypeAdapter.toBencode(obj, this)
            else ->
                anyTypeAdapter.toBencode(obj, this)
        }
    }

    internal fun <T : Any> toBencodeByteArray(obj: T): ByteArray =
        ByteArrayOutputStream()
            .apply { BencodeOutputStream(this).write(toBencode(obj)) }
            .toByteArray()
}