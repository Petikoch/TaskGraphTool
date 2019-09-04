package ch.petikoch.tools.model2graphtool

import ch.petikoch.tools.model2graphtool.model.IModel
import ch.petikoch.tools.model2graphtool.model.Model
import ch.petikoch.tools.model2graphtool.renderer.GraphvizModelRenderer
import ch.petikoch.tools.model2graphtool.renderer.IModelRenderer
import guru.nidi.graphviz.engine.Format
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File

fun main(args: Array<String>) {
    runApplication<Model2GraphTool>(*args)
}

@SpringBootApplication(scanBasePackages = ["ch.petikoch.tools.model2graphtool"])
class Model2GraphTool {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun commandLineRunner(ctx: ApplicationContext,
                          model: IModel,
                          modelRenderer: IModelRenderer): CommandLineRunner {
        return CommandLineRunner {
            val svgContent = modelRenderer.render(model, Format.SVG)
            val file = File("build/graphs/graph.svg")
            file.parentFile.mkdirs()
            file.writeBytes(svgContent)
            logger.info("Generated ${file.path}")
            SpringApplication.exit(ctx)
        }
    }

    @Configuration
    class AppConfiguration {

        @Bean
        fun model(): IModel = Model

        @Bean
        fun modelRenderer(): IModelRenderer = GraphvizModelRenderer
    }
}