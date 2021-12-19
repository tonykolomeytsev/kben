package kekmech.kben.domain

import kekmech.kben.Kben
import kekmech.kben.TypeHolder
import kekmech.kben.annotations.Bencode
import kekmech.kben.annotations.DefaultValue
import kekmech.kben.domain.dto.BencodeElement.*
import kekmech.kben.mocks.Mocks
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import java.security.MessageDigest
import kotlin.test.assertEquals

internal class DeserializationContextTest {

    private val context get() = DeserializationContext(Kben())

    @Test
    fun `deserialize integers`() {
        Mocks.IntegerPrimitives.IR.zip(Mocks.IntegerPrimitives.LIST).forEach { (irInteger, integer) ->
            assertEquals(
                integer,
                context.fromBencode(irInteger, TypeHolder.Simple(Long::class))
            )
        }
    }

    @Test
    fun `deserialize strings`() {
        Mocks.StringPrimitives.IR.zip(Mocks.StringPrimitives.LIST).forEach { (irString, string) ->
            assertEquals(
                string,
                context.fromBencode(irString, TypeHolder.Simple(String::class))
            )
        }
    }

    @Test
    fun `deserialize list of integers`() {
        assertEquals(
            Mocks.IntegerPrimitives.LIST,
            context.fromBencode(Mocks.ListOfIntegers.IR, TypeHolder.ofList(Long::class))
        )
    }

    @Test
    fun `deserialize list of strings`() {
        assertEquals(
            Mocks.StringPrimitives.LIST,
            context.fromBencode(Mocks.ListOfStrings.IR, TypeHolder.ofList(String::class))
        )
    }

    @Test
    fun `deserialize empty list`() {
        assertEquals(
            emptyList<String>(),
            context.fromBencode(BencodeList(emptyList()), TypeHolder.ofList(String::class))
        )
    }

    @Test
    fun `deserialize dictionary with integer values`() {
        assertEquals(
            Mocks.DictionaryWithIntegers.DICTIONARY,
            context.fromBencode(Mocks.DictionaryWithIntegers.IR, TypeHolder.ofMap(Long::class))
        )
    }

    @Test
    fun `deserialize dictionary with string values`() {
        assertEquals(
            Mocks.DictionaryWithStrings.DICTIONARY,
            context.fromBencode(Mocks.DictionaryWithStrings.IR, TypeHolder.ofMap(String::class))
        )
    }

    @Test
    fun `deserialize empty dictionary`() {
        assertEquals(
            mapOf<String, String>(),
            context.fromBencode(BencodeDictionary(sortedMapOf()), TypeHolder.ofMap(String::class))
        )
    }

    @Test
    fun `deserialize set of integers`() {
        assertEquals(
            Mocks.SetOfIntegers.SET,
            context.fromBencode(Mocks.SetOfIntegers.IR, TypeHolder.ofSet(Long::class))
        )
    }

    @Test
    fun `deserialize set of strings`() {
        assertEquals(
            Mocks.SetOfStrings.SET,
            context.fromBencode(Mocks.SetOfStrings.IR, TypeHolder.ofSet(String::class))
        )
    }

    @Test
    fun `deserialize simple data class instance`() {

        data class Value(
            val property1: String,
            val property2: Long,
        )

        assertEquals(
            Value(
                property1 = "test",
                property2 = 42L,
            ),
            context.fromBencode(
                BencodeDictionary(
                    sortedMapOf(
                        "property1" to BencodeByteString("test"),
                        "property2" to BencodeInteger(42L),
                    )
                ),
                TypeHolder.Parameterized(
                    type = Value::class,
                    parameterTypes = listOf(
                        TypeHolder.Simple(String::class),
                    ),
                )
            ),
        )
    }

    @Test
    fun `deserialize generic data class instance`() {

        data class Value<T>(
            val property1: T,
            val property2: Long,
        )

        assertEquals(
            Value<String>(
                property1 = "test",
                property2 = 42L,
            ),
            context.fromBencode(
                BencodeDictionary(
                    sortedMapOf(
                        "property1" to BencodeByteString("test"),
                        "property2" to BencodeInteger(42L),
                    )
                ),
                TypeHolder.Parameterized(
                    type = Value::class,
                    parameterTypes = listOf(
                        TypeHolder.Simple(String::class),
                    )
                )
            ),
        )
    }

    @Test
    fun `deserialize multi-generic data class instance`() {

        data class Value<T, U, V>(
            val property1: T,
            val property2: Long,
            val property3: U,
            val property4: V,
        )

        assertEquals(
            Value<String, Long, Int>(
                property1 = "test",
                property2 = 42L,
                property3 = -1L,
                property4 = 0
            ),
            context.fromBencode(
                BencodeDictionary(
                    sortedMapOf(
                        "property1" to BencodeByteString("test"),
                        "property2" to BencodeInteger(42L),
                        "property3" to BencodeInteger(-1),
                        "property4" to BencodeInteger(0L),
                    )
                ),
                TypeHolder.Parameterized(
                    type = Value::class,
                    parameterTypes = listOf(
                        TypeHolder.Simple(String::class),
                        TypeHolder.Simple(Long::class),
                        TypeHolder.Simple(Int::class),
                    )
                )
            )
        )
    }

    @Test
    fun `deserialize data class with @Bencode annotated property`() {

        data class Value(
            @Bencode(name = "first property")
            val property1: String,
            @Bencode(name = "second property")
            val property2: String,
        )

        assertEquals(
            Value(
                property1 = "hello",
                property2 = "world",
            ),
            context.fromBencode(
                BencodeDictionary(
                    sortedMapOf(
                        "first property" to BencodeByteString("hello"),
                        "second property" to BencodeByteString("world"),
                    )
                ),
                TypeHolder.Simple(Value::class)
            )
        )
    }

    @Test
    fun `deserialize data class with @Transient annotated property`() {

        data class Value(
            val property1: String,
            @Transient
            val property2: String = "world",
        )

        assertEquals(
            Value(
                property1 = "hello",
            ),
            context.fromBencode(
                BencodeDictionary(
                    sortedMapOf(
                        "property1" to BencodeByteString("hello"),
                        "property2" to BencodeByteString("ignored value"),
                    )
                ),
                TypeHolder.Simple(Value::class)
            )
        )
    }

    @Test
    fun `deserialize boolean`() {
        assertEquals(
            true,
            context.fromBencode(BencodeInteger(1L), TypeHolder.Simple(Boolean::class)),
        )
        assertEquals(
            false,
            context.fromBencode(BencodeInteger(0L), TypeHolder.Simple(Boolean::class)),
        )
    }

    private enum class TestEnum1 {
        OPTION_1, OPTION_2
    }

    @Test
    fun `deserialize enum`() {
        assertEquals(
            TestEnum1.OPTION_1,
            context.fromBencode(BencodeByteString("OPTION_1"), TypeHolder.Simple(TestEnum1::class))
        )
        assertEquals(
            TestEnum1.OPTION_2,
            context.fromBencode(BencodeByteString("OPTION_2"), TypeHolder.Simple(TestEnum1::class))
        )
    }

    private enum class TestEnum2 {
        @Bencode(name = "first option") OPTION_1,
        @Bencode(name = "second option") OPTION_2
    }

    @Test
    fun `deserialize enum with @Bencode annotated options`() {
        assertEquals(
            TestEnum2.OPTION_1,
            context.fromBencode(BencodeByteString("first option"), TypeHolder.Simple(TestEnum2::class)),
        )
        assertEquals(
            TestEnum2.OPTION_2,
            context.fromBencode(BencodeByteString("second option"), TypeHolder.Simple(TestEnum2::class)),
        )
    }

    private enum class TestEnum3 {
        OPTION_1,
        OPTION_2,

        @DefaultValue UNKNOWN,
    }

    @Test
    fun `deserialize enum with @DefaultValue annotated option`() {
        assertEquals(
            TestEnum3.UNKNOWN,
            context.fromBencode(BencodeByteString("OPTION_3"), TypeHolder.Simple(TestEnum3::class)),
        )
    }

    @Test
    fun `deserialize bencode integer to any`() {
        val expected: Any = 42L
        val actual: Any = context.fromBencode(BencodeInteger(42L), TypeHolder.Simple(Any::class))
        assertEquals(expected, actual)
    }

    @Test
    fun `deserialize bencode byte string to any (correct UTF-8 string)`() {
        val expected: Any = "i love kotlin"
        val actual: Any = context.fromBencode(BencodeByteString("i love kotlin"), TypeHolder.Simple(Any::class))
        assertEquals(expected, actual)
    }

    @Test
    fun `deserialize bencode byte string to any (not a UTF-8 string)`() {
        val byteArray = MessageDigest
            .getInstance("SHA-256")
            .digest("test".toByteArray())

        val expected: Any = byteArray
        val actual: Any = context.fromBencode(BencodeByteString(byteArray), TypeHolder.Simple(Any::class))
        assertArrayEquals(expected as ByteArray, actual as ByteArray)
    }

    @Test
    fun `deserialize bencode list to list of any`() {
        assertEquals(
            listOf<Any>(-1L, "hello"),
            context.fromBencode(
                BencodeList(
                    elements = listOf(
                        BencodeInteger(-1L),
                        BencodeByteString("hello"),
                    )
                ),
                TypeHolder.ofList(Any::class)
            )
        )
    }

    @Test
    fun `deserialize bencode dictionary to map of any`() {
        assertEquals(
            mapOf<String, Any>(
                "property1" to -1L,
                "property2" to "hello",
            ),
            context.fromBencode(
                BencodeDictionary(
                    entries = sortedMapOf(
                        "property1" to BencodeInteger(-1L),
                        "property2" to BencodeByteString("hello"),
                    )
                ),
                TypeHolder.ofMap(Any::class)
            )
        )
    }
}