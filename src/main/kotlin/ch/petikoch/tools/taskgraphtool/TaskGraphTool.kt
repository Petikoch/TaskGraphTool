package ch.petikoch.tools.taskgraphtool

import ch.petikoch.tools.taskgraphtool.model.IModel
import ch.petikoch.tools.taskgraphtool.model.examples.KuchenBackenModell
import ch.petikoch.tools.taskgraphtool.renderer.GraphvizModelRenderer
import ch.petikoch.tools.taskgraphtool.renderer.IModelRenderer
import ch.petikoch.tools.taskgraphtool.serialization.IModelSerializer
import ch.petikoch.tools.taskgraphtool.serialization.XStreamModelSerializer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File

fun main(args: Array<String>) {
    runApplication<TaskGraphTool>(*args)
}

//TODO also log to files

@SpringBootApplication(scanBasePackages = ["ch.petikoch.tools.taskgraphtool"])
class TaskGraphTool {

    @Configuration
    class AppConfiguration {

        @Bean
        fun model(modelSerializer: IModelSerializer): IModel {
            //TODO ads bedeuted das man im Moment eigentlich nur immer auf einem Model arbeiten kann
            // d.h. wenn ein neuer Benutzer eine UI Instanz öffnet, sieht er das letzte Modell eines anderen Benutzers
            val autosaveFolder = File("./autosave")
            if (autosaveFolder.isDirectory) {
                val filesSortedByNewest: List<File>? = autosaveFolder.listFiles()?.toList()?.filter { it.name.endsWith(".xml") }?.sortedBy { it.lastModified() }?.reversed()
                if (filesSortedByNewest != null && filesSortedByNewest.isNotEmpty()) {
                    val newestModelFile = filesSortedByNewest.first()
                    return modelSerializer.deserialize(newestModelFile.readBytes().toString(Charsets.UTF_8))
                }
            }
            return KuchenBackenModell
        }

        @Bean
        fun modelRenderer(): IModelRenderer = GraphvizModelRenderer

        @Bean
        fun modelSerializer(): IModelSerializer = XStreamModelSerializer
    }
}