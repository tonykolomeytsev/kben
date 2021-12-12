package kekmech.kben.domain

import kekmech.kben.Kben
import kekmech.kben.domain.dto.BencodeElement
import java.lang.reflect.Type
import kotlin.reflect.KClass

abstract class TypeAdapter<T : Any>(private val kClass: KClass<T>) {

    open fun toBencode(src: T, genericType: Type?, context: SerializationContext): BencodeElement {
        throw NotImplementedError("Serialization for class ${kClass.java} not implemented")
    }

    open fun fromBencode(src: BencodeElement, genericType: Type?, kben: Kben): T {
        throw NotImplementedError("Serialization for class ${kClass.java} not implemented")
    }
}