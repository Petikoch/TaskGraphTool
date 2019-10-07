package ch.petikoch.tools.taskgraphtool.ui

import ch.petikoch.tools.taskgraphtool.model.Node
import ch.petikoch.tools.taskgraphtool.model.NodeState

class NodeEditForm : DesignNodeCreationForm() {

    init {
        typeComboBox.setItems(Node::class.sealedSubclasses.mapNotNull { it.simpleName }.sorted())
        typeComboBox.isReadOnly = true
        stateComboBox.setItems(NodeState.values().map { it.name })
        setMargin(false)
        submitButton.isDisableOnClick = false // TODO doppelclicks behandeln
    }

}