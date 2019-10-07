package ch.petikoch.tools.taskgraphtool.ui

import ch.petikoch.tools.taskgraphtool.model.IModel
import ch.petikoch.tools.taskgraphtool.model.impl.TaskGraphToolMutableModel
import com.vaadin.ui.Window

class NodeDeletionForm(private val window: Window,
                       model: IModel,
                       closeCallback: () -> Unit) : DesignNodeDeletionForm() {
    init {
        val elements = model.nodes().map { it.first.toString() }
        val selectedElement = elements.last()
        comboBox.setItems(elements)
        comboBox.setSelectedItem(selectedElement)
        textField.value = model.nodes().last().second.toString()
        comboBox.addSelectionListener {
            textField.value = model.nodes().single { it.first == comboBox.value.toInt() }.toString()
        }

        comboBox.focus()

        deleteButton.addClickListener {
            val mutableModel = model as TaskGraphToolMutableModel
            val nodeIndex = comboBox.selectedItem.get().toInt()
            mutableModel.deleteNode(nodeIndex)
            window.close()
            closeCallback.invoke()
        }
    }
}
