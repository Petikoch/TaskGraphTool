package ch.petikoch.tools.model2graphtool.renderer

import ch.petikoch.tools.model2graphtool.model.IModel
import com.google.common.base.Stopwatch
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import guru.nidi.graphviz.engine.GraphvizJdkEngine
import guru.nidi.graphviz.model.Factory.mutGraph
import guru.nidi.graphviz.model.Factory.mutNode
import guru.nidi.graphviz.toGraphviz
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream

internal object GraphvizModelRenderer : IModelRenderer {

    private val logger = LoggerFactory.getLogger(GraphvizModelRenderer::class.java)

    init {
        Graphviz.useEngine(GraphvizJdkEngine())
    }

    override fun render(model: IModel,
                        format: Format): ByteArray {
        val stopwatch = Stopwatch.createStarted()
        try {
            val mutGraph = mutGraph("graph").setDirected(true)
            val facts2MutNode = model.facts.map {
                Pair(
                        it,
                        mutNode("""
${it.id}

${it.text}
""".trim()
                        )
                )
            }.toMap()

            facts2MutNode.values.forEach {
                mutGraph.add(it)
            }

            model.connections.forEach { (from, to) ->
                facts2MutNode[from]!!.addLink(facts2MutNode[to])
            }

            val graphviz: Graphviz = mutGraph.toGraphviz()
            val renderer = graphviz.render(format)
            val baos = ByteArrayOutputStream()
            renderer.toOutputStream(baos)
            return baos.toByteArray()
        } finally {
            logger.info("Rendering ${model.facts.size} facts with ${model.connections.size} took $stopwatch")
        }
    }
}