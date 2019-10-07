package ch.petikoch.tools.taskgraphtool.model.examples

import ch.petikoch.tools.taskgraphtool.model.Aufgabe
import ch.petikoch.tools.taskgraphtool.model.NodeState
import ch.petikoch.tools.taskgraphtool.model.Problem
import ch.petikoch.tools.taskgraphtool.model.Ziel
import ch.petikoch.tools.taskgraphtool.model.impl.TaskGraphToolMutableModel

internal val KuchenBackenModell = TaskGraphToolMutableModel().apply {
    val (hunger, ziel) = addAndConnect(Problem("Hunger!"), Ziel("Kuchen essen"))

    val (einkaufen, backen, warten) = addAndConnect(
            Aufgabe("Zutaten einkaufen", NodeState.DONE),
            Aufgabe(text = "Backen", state = NodeState.OPEN),
            Aufgabe("Warten")
    )

    connect(backen, warten)
    connect(warten, ziel)
}