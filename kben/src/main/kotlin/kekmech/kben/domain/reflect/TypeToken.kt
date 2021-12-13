package kekmech.kben.domain.reflect

import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter

sealed class TypeToken {

    data class SimpleTypeToken<T : Any>(val type: KClass<T>) : TypeToken()
    data class ParametrizedTypeToken<T : Any, P : Any>(val type: KClass<T>, val parameter: KClass<P>) : TypeToken()

    companion object {

        fun from(property: KProperty<*>): TypeToken {
            val genericType = property.javaField!!.type
            return when (genericType) {
                List::class.java -> ParametrizedTypeToken(List::class, property.getTypeArgument())
                Map::class.java -> ParametrizedTypeToken(Map::class, property.getTypeArgument())
                else -> SimpleTypeToken(property.javaField!!.type.kotlin)
            }
        }

        private fun KProperty<*>.getTypeArgument(): KClass<*> {
            val returnType = (javaGetter as Method).genericReturnType
            val parameterizedType = (returnType as ParameterizedType)
            return (parameterizedType.actualTypeArguments[0] as Class<*>).kotlin
        }
    }
}



//fun <T : Any> KProperty<T>.getTypeArgument1(): KClass<T> {
//    val method = javaGetter as Method
//    val returnType = method.genericReturnType
//    val type = (returnType as ParameterizedType)
//    return (type.actualTypeArguments[0] as Class<T>).kotlin
//}
//
//fun <T : Any> KProperty<T>.getTypeArgument2(): KClass<T> {
//    val method = javaField as Field
//    val returnType = method.genericType
//    val type = (returnType as ParameterizedType)
//    return (type.actualTypeArguments[0] as Class<T>).kotlin
//}