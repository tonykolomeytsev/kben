package kekmech.kben

import org.junit.jupiter.api.Test
import kotlin.reflect.full.primaryConstructor

class TypeHolderTest {

    data class Id<A, B, C>(
        val a: A,
        val b: B,
        val c: C,
    )

    data class TestClass(
        val id: Id<List<Long>, Int, String>,
        val b: List<Map<String, List<Long>>>,
    )

    @Test
    fun test1() {
        TestClass::class.primaryConstructor!!.parameters.forEach {
            println(TypeHolder.from(it))
        }
    }
}