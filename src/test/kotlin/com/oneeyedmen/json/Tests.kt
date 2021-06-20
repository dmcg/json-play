package com.oneeyedmen.json

import org.junit.jupiter.api.Test
import kotlin.reflect.KFunction2
import kotlin.reflect.KProperty1
import kotlin.test.assertEquals

class Tests {

    @Test
    fun test() {
        val domain = Domain("fred", 42)
        assertEquals(JDomain("fred", 42), convert(domain))
    }

    private fun convert(domain: Domain): JDomain {
        val constructor: KFunction2<String, Int, JDomain> = ::JDomain
        val p1: KProperty1<Domain, String> = Domain::name
        val p2: KProperty1<Domain, Int> = Domain::count
        val converter: (Domain) -> JDomain = createConverter(constructor, p1, p2)
        return converter(domain)
    }

    private fun <T1, T2, P1, P2> createConverter(
        constructor: (P1, P2) -> T2,
        p1: (T1) -> P1,
        p2: (T1) -> P2
    ): (T1) -> T2 = {
        constructor(p1(it), p2(it))
    }
}

data class Domain(
    val name: String,
    val count: Int
)

data class JDomain(
    val name: String,
    val count: Int
)