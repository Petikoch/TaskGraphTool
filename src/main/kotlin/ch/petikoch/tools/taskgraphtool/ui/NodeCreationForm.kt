package ch.petikoch.tools.taskgraphtool.ui

import ch.petikoch.tools.taskgraphtool.model.IModel
import ch.petikoch.tools.taskgraphtool.model.Node
import ch.petikoch.tools.taskgraphtool.model.NodeState
import ch.petikoch.tools.taskgraphtool.model.Task
import ch.petikoch.tools.taskgraphtool.model.impl.TaskGraphToolMutableModel
import com.vaadin.server.Page
import com.vaadin.ui.Notification
import com.vaadin.ui.Window
import kotlin.reflect.KFunction
import kotlin.reflect.full.primaryConstructor

class NodeCreationForm(private val window: Window,
                       model: IModel,
                       closeCallback: () -> Unit) : DesignNodeCreationForm() {

    init {
        typeComboBox.setItems(Node::class.sealedSubclasses.mapNotNull { it.simpleName }.sorted())
        typeComboBox.setSelectedItem(Task::class.simpleName)

        stateComboBox.setItems(NodeState.values().map { it.name })
        stateComboBox.setSelectedItem(NodeState.OPEN.name)

        textTextArea.addValueChangeListener {
            submitButton.isEnabled = it.value?.isNotBlank() ?: false
        }
        textTextArea.focus()

        submitButton.isEnabled = false
        submitButton.addClickListener {
            val mutableModel = model as TaskGraphToolMutableModel
            val pair = mutableModel.add(createNodeFromFormValue())
            window.close()
            closeCallback.invoke()
            val notification = Notification(pair.first.toString())
            notification.delayMsec = 2000
            notification.show(Page.getCurrent())
        }
    }

    private fun createNodeFromFormValue(): Node {
        val nodeClass = Node::class.sealedSubclasses.single { it.simpleName == typeComboBox.selectedItem.get() }
        val constructor: KFunction<Node> = nodeClass.primaryConstructor!!
        val externalUrls = mutableSetOf<String>()
        if (externalReferenceUrlTextField.value.isNotBlank()) {
            externalUrls.add(externalReferenceUrlTextField.value.trim())
        }
        return constructor.call(
                textTextArea.value.trim(),
                NodeState.valueOf(stateComboBox.value),
                descriptionTextArea.value.trim(),
                issueTrackerUrlTextField.value.trim(),
                externalUrls
        )
    }
}