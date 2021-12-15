package kekmech.kben.domain

import kekmech.kben.TypeHolder
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

abstract class AbstractContext(
    private val standardTypeAdapters: Map<KClass<out Any>, TypeAdapter<out Any>>,
    private val customTypeAdapters: Map<KClass<out Any>, TypeAdapter<out Any>>,
) {

    @Suppress("UNCHECKED_CAST")
    protected fun <T : Any> findTypeAdapterFor(typeHolder: TypeHolder): TypeAdapter<T>? =
        findTypeAdapterFor(
            type = when (typeHolder) {
                is TypeHolder.Simple -> typeHolder.type
                is TypeHolder.Parameterized -> typeHolder.type
            } as KClass<T>
        )

    @Suppress("UNCHECKED_CAST")
    protected fun <T : Any> findTypeAdapterFor(type: KClass<T>): TypeAdapter<T>? {
        val standardTypeAdapter = standardTypeAdapters[type] as? TypeAdapter<T>
        return if (standardTypeAdapter != null) {
            standardTypeAdapter
        } else {
            val customTypeAdapterKey = customTypeAdapters.keys
                .firstOrNull { it == type || type.isSubclassOf(it) }
            customTypeAdapters[customTypeAdapterKey] as? TypeAdapter<T>
        }
    }
}