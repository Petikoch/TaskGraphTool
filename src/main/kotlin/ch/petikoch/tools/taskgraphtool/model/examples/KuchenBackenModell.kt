package ch.petikoch.tools.taskgraphtool.model.examples

import ch.petikoch.tools.taskgraphtool.model.Goal
import ch.petikoch.tools.taskgraphtool.model.NodeState
import ch.petikoch.tools.taskgraphtool.model.Problem
import ch.petikoch.tools.taskgraphtool.model.Task
import ch.petikoch.tools.taskgraphtool.model.impl.TaskGraphToolMutableModel

internal val KuchenBackenModell = TaskGraphToolMutableModel().apply {
    val (hunger, ziel) = addAndConnect(Problem("Hunger!"), Goal("Kuchen essen"))

    val (einkaufen, backen, warten) = addAndConnect(
            Task("Zutaten einkaufen", NodeState.DONE),
            Task(text = "Backen", state = NodeState.OPEN),
            Task("Warten")
    )

    connect(backen, warten)
    connect(warten, ziel)
}