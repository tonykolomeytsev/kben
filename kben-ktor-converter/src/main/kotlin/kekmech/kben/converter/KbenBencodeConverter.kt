package kekmech.kben.converter

import io.ktor.application.*
import io.ktor.content.*
import io.ktor.features.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.util.pipeline.*
import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*
import kekmech.kben.Kben
import kekmech.kben.TypeHolder
import kekmech.kben.domain.TypeAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.reflect.KClass
import kotlin.reflect.jvm.javaType

class KbenBencodeConverter(private val kben: Kben = Kben()) : ContentConverter {

    override suspend fun convertForSend(
        context: PipelineContext<Any, ApplicationCall>,
        contentType: ContentType,
        value: Any
    ): Any {
        return ByteArrayContent(kben.toBencode(value), contentType.withCharset(context.call.suitableCharset()))
    }

    override suspend fun convertForReceive(
        context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>,
    ): Any? {
        val request = context.subject
        val channel = request.value as? ByteReadChannel ?: return null
        val type = request.typeInfo.javaType

        return withContext(Dispatchers.IO) {
            try {
                kben.fromBencode<Any>(
                    inputStream = channel.toInputStream(),
                    typeHolder = TypeHolder.of(type),
                )
            } catch (e: Exception) {
                null
            } ?: throw UnsupportedNullValuesException()
        }
    }
}

class KbenBuilder {

    private val typeAdapters = mutableMapOf<KClass<out Any>, TypeAdapter<out Any>>()

    fun <T : Any> registerTypeAdapter(kClass: KClass<T>, typeAdapter: TypeAdapter<T>) {
        typeAdapters[kClass] = typeAdapter
    }

    fun create(): Kben = Kben(typeAdapters)
}

fun ContentNegotiation.Configuration.kben(
    contentType: ContentType = ContentType.Application.OctetStream,
    block: KbenBuilder.() -> Unit = {}
) {
    val builder = KbenBuilder()
    builder.apply(block)
    val converter = KbenBencodeConverter(builder.create())
    register(contentType, converter)
}

internal class UnsupportedNullValuesException :
    ContentTransformationException("Receiving null values is not supported")