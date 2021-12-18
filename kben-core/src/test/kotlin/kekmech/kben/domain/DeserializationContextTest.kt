package kekmech.kben.domain

import kekmech.kben.TypeHolder
import kekmech.kben.domain.dto.BencodeElement.*
import kekmech.kben.mocks.Mocks
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class DeserializationContextTest {

    private val context get() = DeserializationContext(StandardTypeAdaptersFactory.createTypeAdapters(), emptyMap())

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
        assertEquals(
            Mocks.SimpleDataClass.INSTANCE,
            context.fromBencode(Mocks.SimpleDataClass.IR, TypeHolder.Simple(Mocks.SimpleDataClass.User::class))
        )
    }

    @Test
    fun `deserialize data class instance with generic`() {
        assertEquals(
            Mocks.DataClassWithGeneric.INSTANCE,
            context.fromBencode(Mocks.DataClassWithGeneric.IR,
                TypeHolder.Parameterized(Mocks.DataClassWithGeneric.Container::class,
                    listOf(TypeHolder.Simple(String::class))))
        )
    }

    data class Container<A, B, C>(
        val a: C,
        val b: B,
        val c: A,
        val d: String = "test",
    )

    @Test
    fun `super complex generic test`() {

        val instance = Container<Long, Int, String>("42", 1, 999L)

        assertEquals(
            instance,
            context.fromBencode(
                BencodeDictionary(
                    sortedMapOf(
                        "a" to BencodeByteString("42"),
                        "b" to BencodeInteger(1),
                        "c" to BencodeInteger(999L),
                        "d" to BencodeByteString("test"),
                    )
                ),
                TypeHolder.Parameterized(
                    Container::class,
                    listOf(
                        TypeHolder.Simple(Long::class),
                        TypeHolder.Simple(Int::class),
                        TypeHolder.Simple(String::class),
                    )
                )
            )
        )
    }
}