package ch.petikoch.tools.model2graphtool.model

interface IModel {
    val facts: MutableList<Fact>
    val connections: MutableSet<Pair<Fact, Fact>>
}