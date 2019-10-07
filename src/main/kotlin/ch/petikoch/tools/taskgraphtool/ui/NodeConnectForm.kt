package ch.petikoch.tools.taskgraphtool.ui

import ch.petikoch.tools.taskgraphtool.model.IModel
import ch.petikoch.tools.taskgraphtool.model.impl.TaskGraphToolMutableModel
import com.vaadin.data.HasValue
import com.vaadin.ui.Window

class NodeConnectForm(private val window: Window,
                      model: IModel,
                      connect: Boolean,
                      closeCallback: () -> Unit) : DesignNodeConnectForm() {

    init {
        if (connect) {
            connectButton.caption = "Connect"
        } else {
            connectButton.caption = "Disconnect"
        }

        val elements = model.nodes().map { it.first.toString() }
        fromComboBox.setItems(elements)
        toComboBox.setItems(elements)

        val comboBoxSelectionListener: (HasValue.ValueChangeEvent<String>) -> Unit = {
            connectButton.isEnabled = fromComboBox.selectedItem != toComboBox.selectedItem
        }
        fromComboBox.addValueChangeListener(comboBoxSelectionListener)
        toComboBox.addValueChangeListener(comboBoxSelectionListener)

        fromComboBox.addValueChangeListener {
            val node = model.nodes().single { it.first == fromComboBox.value.toInt() }.second
            fromTextField.value = "${node::class.simpleName} - ${node.text}"
        }
        toComboBox.addValueChangeListener {
            val node = model.nodes().single { it.first == toComboBox.value.toInt() }.second
            toTextField.value = "${node::class.simpleName} - ${node.text}"
        }

        fromComboBox.setSelectedItem(elements.first())
        toComboBox.setSelectedItem(elements.last())

        fromComboBox.focus()

        connectButton.addClickListener {
            val mutableModel = model as TaskGraphToolMutableModel
            val fromIndex = fromComboBox.selectedItem.get().toInt()
            val toIndex = toComboBox.selectedItem.get().toInt()
            if (connect) {
                mutableModel.connect(fromIndex, toIndex)
            } else {
                mutableModel.disconnect(fromIndex, toIndex)
            }
            window.close()
            closeCallback.invoke()
        }
    }
}