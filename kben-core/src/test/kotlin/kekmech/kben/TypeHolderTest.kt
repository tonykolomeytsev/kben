package kekmech.kben

import org.junit.jupiter.api.Test
import kotlin.reflect.full.primaryConstructor
import kotlin.test.assertEquals

internal class TypeHolderTest {

    data class A(
        val x: B<List<Long>, Int, String>,
        val y: List<Map<String, Set<Long>>>,
    )

    data class B<A, B, C>(
        val a: A,
        val b: B,
        val c: C,
    )

    @Test
    fun `complex test 1`() {
        assertEquals(
            TypeHolder.Parameterized(
                type = B::class,
                parameterTypes = listOf(
                    TypeHolder.Parameterized(
                        type = List::class,
                        parameterTypes = listOf(
                            TypeHolder.Simple(Long::class),
                        ),
                    ),
                    TypeHolder.Simple(Int::class),
                    TypeHolder.Simple(String::class),
                ),
            ),
            TypeHolder.from(A::class.primaryConstructor!!.parameters[0]),
        )
    }

    @Test
    fun `complex test 2`() {
        assertEquals(
            TypeHolder.Parameterized(
                type = List::class,
                parameterTypes = listOf(
                    TypeHolder.Parameterized(
                        type = Map::class,
                        parameterTypes = listOf(
                            TypeHolder.Simple(String::class),
                            TypeHolder.Parameterized(
                                type = Set::class,
                                parameterTypes = listOf(
                                    TypeHolder.Simple(Long::class)
                                ),
                            ),
                        ),
                    ),
                ),
            ),
            TypeHolder.from(A::class.primaryConstructor!!.parameters[1]),
        )
    }
}