package kekmech.kben.domain

import kekmech.kben.annotations.Bencode
import kekmech.kben.domain.dto.BencodeElement.*
import kekmech.kben.mocks.Mocks
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class SerializationContextTest {

    private val context get() = SerializationContext(StandardTypeAdaptersFactory.createTypeAdapters(), emptyMap())

    @Test
    fun `serialize integers`() {
        Mocks.IntegerPrimitives.LIST.zip(Mocks.IntegerPrimitives.IR).forEach { (integer, irInteger) ->
            assertEquals(
                irInteger,
                context.toBencode(integer)
            )
        }
    }

    @Test
    fun `serialize strings`() {
        Mocks.StringPrimitives.LIST.zip(Mocks.StringPrimitives.IR).forEach { (string, irString) ->
            assertEquals(
                irString,
                context.toBencode(string)
            )
        }
    }

    @Test
    fun `serialize list of integers`() {
        assertEquals(
            Mocks.ListOfIntegers.IR,
            context.toBencode(Mocks.IntegerPrimitives.LIST)
        )
    }

    @Test
    fun `serialize list of strings`() {
        assertEquals(
            Mocks.ListOfStrings.IR,
            context.toBencode(Mocks.StringPrimitives.LIST)
        )
    }

    @Test
    fun `serialize empty list`() {
        assertEquals(
            BencodeList(emptyList()),
            context.toBencode(emptyList<Any>())
        )
    }

    @Test
    fun `serialize dictionary with integer values`() {
        assertEquals(
            Mocks.DictionaryWithIntegers.IR,
            context.toBencode(Mocks.DictionaryWithIntegers.DICTIONARY)
        )
    }

    @Test
    fun `serialize dictionary with string values`() {
        assertEquals(
            Mocks.DictionaryWithStrings.IR,
            context.toBencode(Mocks.DictionaryWithStrings.DICTIONARY)
        )
    }

    @Test
    fun `serialize empty dictionary`() {
        assertEquals(
            BencodeDictionary(sortedMapOf()),
            context.toBencode(mapOf<String, String>())
        )
    }

    @Test
    fun `serialize set of integers`() {
        assertEquals(
            Mocks.SetOfIntegers.IR,
            context.toBencode(Mocks.SetOfIntegers.SET)
        )
    }

    @Test
    fun `serialize set of strings`() {
        assertEquals(
            Mocks.SetOfStrings.IR,
            context.toBencode(Mocks.SetOfStrings.SET)
        )
    }

    @Test
    fun `serialize simple data class instance`() {

        data class Value(
            val property1: String,
            val property2: Long,
        )

        assertEquals(
            BencodeDictionary(
                sortedMapOf(
                    "property1" to BencodeByteString("test"),
                    "property2" to BencodeInteger(42L),
                )
            ),
            context.toBencode(
                Value(
                    property1 = "test",
                    property2 = 42L,
                )
            ),
        )
    }

    @Test
    fun `serialize generic data class instance`() {

        data class Value<T>(
            val property1: T,
            val property2: Long,
        )

        assertEquals(
            BencodeDictionary(
                sortedMapOf(
                    "property1" to BencodeByteString("test"),
                    "property2" to BencodeInteger(42L),
                )
            ),
            context.toBencode(
                Value<String>(
                    property1 = "test",
                    property2 = 42L,
                )
            )
        )
    }

    @Test
    fun `serialize multi-generic data class instance`() {

        data class Value<T, U, V>(
            val property1: T,
            val property2: Long,
            val property3: U,
            val property4: V,
        )

        assertEquals(
            BencodeDictionary(
                sortedMapOf(
                    "property1" to BencodeByteString("test"),
                    "property2" to BencodeInteger(42L),
                    "property3" to BencodeInteger(-1),
                    "property4" to BencodeInteger(0L),
                )
            ),
            context.toBencode(
                Value<String, Long, Int>(
                    property1 = "test",
                    property2 = 42L,
                    property3 = -1L,
                    property4 = 0
                )
            )
        )
    }

    @Test
    fun `serialize data class with @Bencode annotated property`() {

        data class Value(
            @Bencode(name = "first property")
            val property1: String,
            @Bencode(name = "second property")
            val property2: String,
        )

        assertEquals(
            BencodeDictionary(
                sortedMapOf(
                    "first property" to BencodeByteString("hello"),
                    "second property" to BencodeByteString("world"),
                )
            ),
            context.toBencode(
                Value(
                    property1 = "hello",
                    property2 = "world",
                )
            )
        )
    }

    @Test
    fun `serialize data class with @Transient annotated property`() {

        data class Value(
            val property1: String,
            @Transient
            val property2: String = "world",
        )

        assertEquals(
            BencodeDictionary(
                sortedMapOf(
                    "property1" to BencodeByteString("hello"),
                )
            ),
            context.toBencode(
                Value(
                    property1 = "hello",
                    property2 = "ignored value",
                )
            )
        )
    }
}