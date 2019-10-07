package ch.petikoch.tools.taskgraphtool.model.impl

import ch.petikoch.tools.taskgraphtool.model.Connection
import ch.petikoch.tools.taskgraphtool.model.ConnectionType
import ch.petikoch.tools.taskgraphtool.model.IModel
import ch.petikoch.tools.taskgraphtool.model.Node
import org.apache.commons.lang3.NotImplementedException
import java.time.ZonedDateTime

internal class TaskGraphToolMutableModel : IModel {

    private var lastModified = ZonedDateTime.now()
    private var nextNodeId: Int = 1
    private val nodes: MutableSet<Pair<Int, Node>> = mutableSetOf()
    private val connections: MutableSet<Connection> = mutableSetOf()

    override fun nodes(): Iterable<Pair<Int, Node>> = nodes
    override fun connections(): Iterable<Connection> = connections

    override fun deleteNode(nodeIndex: Int) {
        check(nodes.removeIf { it.first == nodeIndex }) { "Could not find node $nodeIndex" }
        connections.removeIf { it.node1Index == nodeIndex || it.node2Index == nodeIndex }
        lastModified = ZonedDateTime.now()
    }

    override fun reset() {
        nextNodeId = 1
        nodes.clear()
        connections.clear()
    }

    override fun disconnect(fromIndex: Int, toIndex: Int) {
        connections.removeIf { it.node1Index == fromIndex && it.node2Index == toIndex }
        lastModified = ZonedDateTime.now()
    }

    override fun loadFrom(other: IModel) {
        nodes.clear()
        nodes.addAll(other.nodes())
        connections.clear()
        connections.addAll(other.connections())
        if (other is TaskGraphToolMutableModel) {
            nextNodeId = other.nextNodeId
            lastModified = other.lastModified
        } else {
            throw NotImplementedException("Don't know how to load from ${other.javaClass}")
        }
    }

    // programmatic API to create an instance
    fun add(node: Node): Pair<Int, Node> {
        val pair = Pair(nextNodeId, node)
        nodes.add(pair)
        nextNodeId++
        lastModified = ZonedDateTime.now()
        return pair
    }

    fun addAndConnect(vararg varArgNodes: Node): List<Node> {
        require(varArgNodes.size >= 2) { "Expected at least 2 nodes as parameter value, actual value: $varArgNodes" }

        varArgNodes.forEach {
            add(it)
        }

        val nodesAsList = varArgNodes.toList()

        // connect all of them "like a chain"
        nodesAsList.zipWithNext().forEach {
            connect(it.first, it.second)
        }

        return nodesAsList
    }

    // like the varargs one, just to have a more typesafe version
    fun addAndConnect(node1: Node, node2: Node): Pair<Node, Node> {
        add(node1)
        add(node2)
        return connect(node1, node2)
    }

    fun connect(
            first: Int,
            second: Int,
            connectionType: ConnectionType = ConnectionType.UNI_DIRECTIONAL
    ) {
        connections.add(Connection(first, second, connectionType))
        lastModified = ZonedDateTime.now()
    }

    fun connect(
            first: Node,
            second: Node,
            connectionType: ConnectionType = ConnectionType.UNI_DIRECTIONAL
    ): Pair<Node, Node> {
        try {
            val firstPair = nodes.single { it.second == first }
            val secondPair = nodes.single { it.second == second }
            connect(firstPair.first, secondPair.first, connectionType)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair(first, second)
    }

    fun githubIssue(
            org: String,
            repo: String,
            issueId: Int
    ): String {
        return "https://github.com/$org/$repo/issues/$issueId"
    }


    fun youtrackIssue(issueId: String): String {
        return "https://youtrack.jetbrains.com/issue/$issueId"
    }
}