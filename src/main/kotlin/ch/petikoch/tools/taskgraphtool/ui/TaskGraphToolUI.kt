package ch.petikoch.tools.taskgraphtool.ui

import com.vaadin.annotations.PreserveOnRefresh
import com.vaadin.server.VaadinRequest
import com.vaadin.spring.annotation.SpringUI
import com.vaadin.ui.JavaScript
import com.vaadin.ui.UI
import org.springframework.beans.factory.annotation.Autowired

@PreserveOnRefresh
@SpringUI
class TaskGraphToolUI : UI() {

    @Autowired
    private lateinit var singlePageApplicationMain: SinglePageApplication

    override fun init(vaadinRequest: VaadinRequest) {
        content = singlePageApplicationMain
        JavaScript.getCurrent().addFunction("taskgraphtool_nodeselected") {
            singlePageApplicationMain.selectNode(it.asString().toInt())
        }
    }

}