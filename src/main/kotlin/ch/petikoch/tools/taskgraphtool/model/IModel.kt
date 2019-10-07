package ch.petikoch.tools.taskgraphtool.model

import java.time.ZonedDateTime

interface IModel {

    fun nodes(): Iterable<Pair<Int, Node>>

    fun connections(): Iterable<Connection>

    fun loadFrom(other: IModel)

    fun deleteNode(nodeIndex: Int)

    fun disconnect(fromIndex: Int, toIndex: Int)

    fun reset()

    fun getLastModified(): ZonedDateTime

}