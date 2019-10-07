package ch.petikoch.tools.taskgraphtool.ui

import ch.petikoch.tools.taskgraphtool.model.IModel
import ch.petikoch.tools.taskgraphtool.model.Node
import ch.petikoch.tools.taskgraphtool.model.NodeState
import ch.petikoch.tools.taskgraphtool.renderer.IModelRenderer
import ch.petikoch.tools.taskgraphtool.serialization.IModelSerializer
import com.vaadin.server.FileDownloader
import com.vaadin.server.StreamResource
import com.vaadin.spring.annotation.UIScope
import com.vaadin.ui.*
import guru.nidi.graphviz.engine.Format
import org.joox.JOOX
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.atomic.AtomicReference

@org.springframework.stereotype.Component
@UIScope
class SinglePageApplication : DesignSinglePageApplication(), InitializingBean {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    @Autowired
    private lateinit var model: IModel
    @Autowired
    private lateinit var modelRenderer: IModelRenderer
    @Autowired
    private lateinit var modelSerializer: IModelSerializer
    @Autowired
    private lateinit var zoomer: Zoomer

    private var svgWrapperPanel: Panel? = null

    override fun afterPropertiesSet() {
        refresh(1.0f)
        initToolButtons()
        initNodeEditForm()
    }

    private fun initToolButtons() {
        addNodeButton.addClickListener {
            showModelWindow("Add node") { window -> NodeCreationForm(window, model) { refresh(zoomer.currentZoomFactor()) } }
        }
        addConnectionButton.addClickListener {
            showModelWindow("Connect nodes") { window -> NodeConnectForm(window, model, true) { refresh(zoomer.currentZoomFactor()) } }
        }
        deleteNodeButton.addClickListener {
            showModelWindow("Delete node") { window -> NodeDeletionForm(window, model) { refresh(zoomer.currentZoomFactor()) } }
        }
        deleteConnectionButton.addClickListener {
            showModelWindow("Delete connection between nodes") { window -> NodeConnectForm(window, model, false) { refresh(zoomer.currentZoomFactor()) } }
        }

        val streamResource = StreamResource(
                { ByteArrayInputStream(modelSerializer.serialize(model).toByteArray()) },
                "TaskGraphTool-model.${modelSerializer.getFileExtension()}"
        )
        val fileDownloader = FileDownloader(streamResource)
        fileDownloader.extend(saveButton)

        val receiver = object : Upload.Receiver, Upload.SucceededListener {
            private val baos = ByteArrayOutputStream()
            private val filenameRef = AtomicReference<String>()
            private val mimeTypeRef = AtomicReference<String>()

            override fun receiveUpload(filename: String?, mimeType: String?): OutputStream {
                baos.reset()
                filenameRef.set(filename)
                mimeTypeRef.set(mimeType)
                return baos
            }

            override fun uploadSucceeded(event: Upload.SucceededEvent?) {
                logger.info("Thanks for uploading ${filenameRef.get()}")
                val uploadedModel = modelSerializer.deserialize(baos.toByteArray().toString(StandardCharsets.UTF_8))
                model.loadFrom(uploadedModel)
                refresh(zoomer.reset())
                logger.info("Uploaded model has ${uploadedModel.nodes().toList().size} nodes")
            }
        }
        upload.receiver = receiver
        upload.addSucceededListener(receiver)

        resetButton.addClickListener {
            initNodeEditForm()
            model.reset()
            refresh(zoomer.reset())
        }

        zoomIn.addClickListener {
            refresh(zoomer.zoomIn())
        }
        zoomOut.addClickListener {
            refresh(zoomer.zoomOut())
        }
    }

    private fun initNodeEditForm(selectedNodeIndex: Int? = null,
                                 selectedNode: Node? = null) {
        val nodeEditForm = NodeEditForm()
        nodeEditForm.submitButton.addClickListener {
            val no = detailsLabel.value.toInt()
            val node = model.nodes().single { it.first == no }.second
            node.text = nodeEditForm.textTextArea.value.trim()
            node.state = NodeState.valueOf(nodeEditForm.stateComboBox.selectedItem.get())
            node.issueUrl = nodeEditForm.issueTrackerUrlTextField.value.trim()
            if (nodeEditForm.externalReferenceUrlTextField.value.isNotBlank()) {
                node.externalUrls = mutableSetOf(nodeEditForm.externalReferenceUrlTextField.value.trim())
            }
            node.description = nodeEditForm.descriptionTextArea.value.trim()
            refresh(zoomer.currentZoomFactor())
        }
        nodeEditForm.submitButton.isEnabled = selectedNodeIndex != null
        if (selectedNode != null) {
            nodeEditForm.typeComboBox.setSelectedItem(selectedNode::class.simpleName)
            nodeEditForm.textTextArea.value = selectedNode.text
            nodeEditForm.stateComboBox.setSelectedItem(selectedNode.state.name)
            nodeEditForm.issueTrackerUrlTextField.value = selectedNode.issueUrl ?: ""
            val externalUrls = selectedNode.externalUrls?.toSet()
            if (externalUrls != null && externalUrls.isNotEmpty()) {
                nodeEditForm.externalReferenceUrlTextField.value = externalUrls.first()
            }
            nodeEditForm.descriptionTextArea.value = selectedNode.description ?: ""
        }

        detailsPanel.removeAllComponents()
        detailsPanel.addComponent(nodeEditForm)

        detailsLabel.value = selectedNodeIndex?.toString() ?: "Node details"
    }

    private fun refresh(zoomFactor: Float) {
        if (svgWrapperPanel != null) {
            imageWrapper.removeComponent(svgWrapperPanel)
        }

        val imageBytes = modelRenderer.render(model, Format.SVG)

        val document = JOOX.`$`(ByteArrayInputStream(imageBytes)).document()
        // add onclick on g elements of all nodes
        JOOX.`$`(document).xpath("//*[name()='g' and @class='node']").forEach {
            val nodeTitle = JOOX.`$`(it).find("title").text()
            val nodeNumber = Regex("(\\d+).*").find(nodeTitle)!!.groups[1]!!.value
            JOOX.`$`(it).attr("onclick", "top.taskgraphtool_nodeselected(\"$nodeNumber\");")
        }

        // zoom
        val svgRootNode = JOOX.`$`(document).xpath("/*[name()='svg']")
        val originalWidth = svgRootNode.attr("width").let { it.substring(0, it.length - 2) }
        val originalHeight = svgRootNode.attr("height").let { it.substring(0, it.length - 2) }
        val zoomedWidth = (originalWidth.toInt() * zoomFactor).toInt()
        val zoomedHeight = (originalHeight.toInt() * zoomFactor).toInt()
        svgRootNode.attr("width", "${zoomedWidth}px")
        svgRootNode.attr("height", "${zoomedHeight}px")
        val modifiedSvg = ByteArrayOutputStream()
        JOOX.`$`(document).write(modifiedSvg)

        //https://github.com/mstahv/svgexamples/blob/master/src/main/java/org/vaadin/beta82test/FileExample.java
        //https://raw.githubusercontent.com/mstahv/svgexamples/master/src/main/resources/pull.svg
        //http://srufaculty.sru.edu/david.dailey/svg/intro/PartF_B.html
        //https://vaadin.com/blog/the-state-of-svg-scalable-vector-graphics-in-the-modern-web
        val image = Embedded()
        image.source = StreamResource({ ByteArrayInputStream(modifiedSvg.toByteArray()) }, "${UUID.randomUUID()}.svg")

        val newPanel = Panel(image)
        newPanel.setSizeFull()
        val oldPanel = svgWrapperPanel

        svgWrapperPanel = newPanel

        if (oldPanel != null) {
            // TODO does not work yet
            try {
                newPanel.scrollLeft = oldPanel.scrollLeft
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            try {
                newPanel.scrollTop = oldPanel.scrollTop
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        imageWrapper.addComponent(newPanel)
    }

    fun selectNode(nodeNumber: Int) {
        model.nodes().single {
            it.first == nodeNumber
        }.run {
            initNodeEditForm(first, second)
        }
    }

    private fun showModelWindow(windowTitle: String,
                                windowContentProvider: (Window) -> Component) {
        val window = Window(windowTitle)
        window.content = windowContentProvider.invoke(window)
        window.isModal = true
        window.setWidth("70%")
        window.center()
        ui.addWindow(window)
    }
}