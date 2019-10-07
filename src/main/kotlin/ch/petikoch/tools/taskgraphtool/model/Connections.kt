package ch.petikoch.tools.taskgraphtool.model

enum class ConnectionType {
    UNI_DIRECTIONAL, BI_DIRECTIONAL, UNDIRECTED
}

data class Connection(
        val node1Index: Int,
        val node2Index: Int,
        val type: ConnectionType
)