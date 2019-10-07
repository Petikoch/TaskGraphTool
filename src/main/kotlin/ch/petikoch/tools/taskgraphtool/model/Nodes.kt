package ch.petikoch.tools.taskgraphtool.model

sealed class Node {
    abstract var text: String
    abstract var state: NodeState
    abstract var description: String?
    abstract var issueUrl: String?
    abstract var externalUrls: MutableSet<String>?
}

data class Aufgabe(
        override var text: String,
        override var state: NodeState = NodeState.OPEN,
        override var description: String? = null,
        override var issueUrl: String? = null,
        override var externalUrls: MutableSet<String>? = null
) : Node()

data class Ziel(
        override var text: String,
        override var state: NodeState = NodeState.OPEN,
        override var description: String? = null,
        override var issueUrl: String? = null,
        override var externalUrls: MutableSet<String>? = null
) : Node()

data class Problem(
        override var text: String,
        override var state: NodeState = NodeState.OPEN,
        override var description: String? = null,
        override var issueUrl: String? = null,
        override var externalUrls: MutableSet<String>? = null
) : Node()

data class Entscheid(
        override var text: String,
        override var state: NodeState = NodeState.OPEN,
        override var description: String? = null,
        override var issueUrl: String? = null,
        override var externalUrls: MutableSet<String>? = null
) : Node()

enum class NodeState {
    OPEN,
    IN_PROGRESS,
    DONE
}