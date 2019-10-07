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

fun main(args: Array<String>) {
    runApplication<TaskGraphTool>(*args)
}

//TODO also log to files

@SpringBootApplication(scanBasePackages = ["ch.petikoch.tools.taskgraphtool"])
class TaskGraphTool {

    @Configuration
    class AppConfiguration {

        @Bean
        fun model(): IModel = KuchenBackenModell

        @Bean
        fun modelRenderer(): IModelRenderer = GraphvizModelRenderer

        @Bean
        fun modelSerializer(): IModelSerializer = XStreamModelSerializer
    }
}