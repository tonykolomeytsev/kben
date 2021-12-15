package kekmech.kben.converter

import kekmech.kben.Kben
import kekmech.kben.TypeHolder
import kekmech.kben.domain.DeserializationContext
import kekmech.kben.domain.SerializationContext
import kekmech.kben.domain.TypeAdapter
import kekmech.kben.domain.dto.BencodeElement
import kekmech.kben.domain.dto.BencodeElement.BencodeByteArray
import kekmech.kben.domain.dto.BencodeElement.BencodeDictionary
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Rule
import org.junit.jupiter.api.Test
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST
import kotlin.test.assertEquals

internal class KbenConverterFactoryTest {

    data class SomeDataClass(val name: String?)

    class SomeDataClassAdapter : TypeAdapter<SomeDataClass>() {

        override fun toBencode(value: SomeDataClass, context: SerializationContext): BencodeElement {
            return BencodeDictionary(
                listOfNotNull(value.name?.let { "name" to BencodeByteArray(it) }).toMap().toSortedMap()
            )
        }

        override fun fromBencode(
            value: BencodeElement,
            context: DeserializationContext,
            typeHolder: TypeHolder
        ): SomeDataClass {
            return SomeDataClass(
                name = ((value as BencodeDictionary).entries["name"] as? BencodeByteArray)?.asString
            )
        }
    }

    internal interface Service {

        @POST("/")
        fun someDataClass(@Body impl: SomeDataClass): Call<SomeDataClass>
    }

    @Rule
    val server: MockWebServer = MockWebServer()

    private val service: Service = createService()

    private fun createService(): Service {
        val kben =
            Kben(
                typeAdapters = mapOf(
                    SomeDataClass::class to SomeDataClassAdapter(),
                )
            )
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(KbenConverterFactory.create(kben))
            .build()
        return retrofit.create(Service::class.java)
    }

    @Test
    fun someDataClass() {
        server.enqueue(MockResponse().setBody("d4:name5:valuee"))
        val call: Call<SomeDataClass> = service.someDataClass(SomeDataClass("value"))
        val response: Response<SomeDataClass> = call.execute()
        val body: SomeDataClass = response.body()!!
        assertEquals("value", body.name)
        val request: RecordedRequest = server.takeRequest()
        assertEquals("d4:name5:valuee", request.body.readUtf8())
        assertEquals("application/octet-stream", request.getHeader("Content-Type"))
    }

    @Test
    fun serializeUsesConfiguration() {
        server.enqueue(MockResponse().setBody("de"))
        service.someDataClass(SomeDataClass(null)).execute()
        val request: RecordedRequest = server.takeRequest()
        assertEquals("de", request.body.readUtf8()) // Null value was not serialized.
        assertEquals("application/octet-stream", request.getHeader("Content-Type"))
    }

    @Test
    fun deserializeUsesConfiguration() {
        server.enqueue(MockResponse().setBody("de"))
        val response: Response<SomeDataClass> = service.someDataClass(SomeDataClass("value")).execute()
        assertEquals(null, response.body()?.name)
    }
}