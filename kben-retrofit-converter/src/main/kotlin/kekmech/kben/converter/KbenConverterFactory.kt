package kekmech.kben.converter

import kekmech.kben.Kben
import kekmech.kben.TypeHolder
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class KbenConverterFactory private constructor(
    private val kben: Kben,
): Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> =
        KbenResponseBodyConverter<Any>(kben, TypeHolder.Companion.from(type))

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody> =
        KbenRequestBodyConverter<Any>(kben)

    companion object {

        fun create(kben: Kben = Kben()): KbenConverterFactory =
            KbenConverterFactory(kben)
    }
}