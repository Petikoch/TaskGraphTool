package ch.petikoch.tools.taskgraphtool.model

interface IModel {

    fun nodes(): Iterable<Pair<Int, Node>>

    fun connections(): Iterable<Connection>

    fun loadFrom(other: IModel)

    fun deleteNode(nodeIndex: Int)

    fun disconnect(fromIndex: Int, toIndex: Int)

    fun reset()

}