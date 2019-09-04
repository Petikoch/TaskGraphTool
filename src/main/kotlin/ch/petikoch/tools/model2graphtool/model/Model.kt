package ch.petikoch.tools.model2graphtool.model

import java.util.concurrent.atomic.AtomicInteger

object Model : IModel {
    override val facts: MutableList<Fact> = mutableListOf()
    override val connections: MutableSet<Pair<Fact, Fact>> = mutableSetOf()

    init {
        val fact1 = Fact("Hello")
        facts.add(fact1)

        val fact2 = Fact("World")
        facts.add(fact2)

        connections.add(Pair(fact1, fact2))
    }
}

object FactIdGen {
    private val numbers = AtomicInteger()

    fun next(): Int = numbers.incrementAndGet()
}

data class Fact(val text: String, val id: Int = FactIdGen.next())

