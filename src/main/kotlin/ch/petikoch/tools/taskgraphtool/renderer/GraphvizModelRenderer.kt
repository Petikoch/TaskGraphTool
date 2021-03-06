package ch.petikoch.tools.taskgraphtool.renderer

import ch.petikoch.tools.taskgraphtool.model.*
import com.google.common.base.Stopwatch
import com.google.common.collect.Iterables
import guru.nidi.graphviz.attribute.Color
import guru.nidi.graphviz.attribute.Rank
import guru.nidi.graphviz.attribute.Shape
import guru.nidi.graphviz.attribute.Style
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import guru.nidi.graphviz.engine.GraphvizJdkEngine
import guru.nidi.graphviz.model.Factory.mutGraph
import guru.nidi.graphviz.model.Factory.mutNode
import guru.nidi.graphviz.model.MutableGraph
import guru.nidi.graphviz.model.MutableNode
import guru.nidi.graphviz.toGraphviz
import org.apache.commons.lang3.NotImplementedException
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.time.ZonedDateTime
import java.util.concurrent.atomic.AtomicReference

internal object GraphvizModelRenderer : IModelRenderer {

    init {
        //Graphviz.useEngine(GraphvizCmdLineEngine())
        Graphviz.useEngine(GraphvizJdkEngine())
    }

    private val logger = LoggerFactory.getLogger(GraphvizModelRenderer::class.java)
    // just some very simple caching approach assuming there is only one concurrent user and that model.getLastModified() works properly
    private val renderCache = AtomicReference<Triple<ZonedDateTime, Format, ByteArray>>()

    override fun render(
            model: IModel,
            format: Format
    ): ByteArray {
        val cachedRenderResult = renderCache.get()
        val modelLastModified = model.getLastModified()
        if (cachedRenderResult != null && cachedRenderResult.first == modelLastModified && cachedRenderResult.second == format) {
            logger.info("Using cached rendering result from $modelLastModified")
            return cachedRenderResult.third
        } else {
            val stopwatch = Stopwatch.createStarted()
            try {
                val mutGraph: MutableGraph = mutGraph("graph").setDirected(true)
                mutGraph.graphAttrs().add(Rank.dir(Rank.RankDir.BOTTOM_TO_TOP))

                val nodes2MutNode = mutableMapOf<Int, MutableNode>()

                model.nodes().forEach { (no, node) ->
                    val mutNode = when (node) {
                        is Task -> {
                            val newTask = mutNode(node.text).add(Shape.RECTANGLE)
                            if (node.state != NodeState.DONE) {
                                newTask.add(Style.FILLED).add(Color.PALEGOLDENROD.fill())
                            }
                            newTask
                        }
                        is Goal -> {
                            val newZiel = mutNode(node.text)
                            if (node.state != NodeState.DONE) {
                                newZiel.add(Style.FILLED).add(Color.FORESTGREEN.fill())
                            }
                            newZiel
                        }
                        is Problem -> {
                            val newProblem = mutNode(node.text).add(Shape.SEPTAGON)
                            if (node.state != NodeState.DONE) {
                                newProblem.add(Style.FILLED).add(Color.CRIMSON.fill())
                            }
                            newProblem
                        }
                        is Decision -> {
                            val newEntscheid = mutNode(node.text).add(Shape.DIAMOND)
                            if (node.state != NodeState.DONE) {
                                newEntscheid.add(Style.FILLED).add(Color.GOLD.fill())
                            }
                            newEntscheid
                        }
                    }
                    if (node.state == NodeState.DONE) {
                        mutNode.add(Color.LIGHTGREY)
                        mutNode.add(Color.GRAY.font())
                    }
                    if (node.state == NodeState.IN_PROGRESS) {
                        mutNode.add(Style.lineWidth(5).and(Style.FILLED))
                    }
                    mutNode.setName("""
                    |$no
                    |${mutNode.name()}
                    |""".trimMargin())
                    mutNode.add("tooltip", node.description ?: "")
                    if (node.issueUrl != null) {
                        if (node.description == null) {
                            mutNode.add("tooltip", node.issueUrl)
                        }
                    }
                    nodes2MutNode[no] = mutNode
                    mutGraph.add(mutNode)
                }

                model.connections().forEach {
                    when (it.type) {
                        ConnectionType.UNI_DIRECTIONAL -> {
                            val mutNode1 = nodes2MutNode[it.node1Index]!!
                            val mutNode2 = nodes2MutNode[it.node2Index]!!
                            val link = mutNode1.linkTo(mutNode2)
                            val fromNode = model.nodes().single { it1 -> it1.first == it.node1Index }.second
                            if (fromNode is Problem) {
                                link.add(Style.DASHED)
                            }
                            val toNode = model.nodes().single { it1 -> it1.first == it.node2Index }.second
                            if (fromNode.state == NodeState.DONE || toNode.state == NodeState.DONE) {
                                link.add(Color.LIGHTGREY)
                            }
                            mutNode1.links().add(link)
                        }
                        else -> throw NotImplementedException(it.type.toString())
                    }
                }

                val baos = ByteArrayOutputStream()
                mutGraph.toGraphviz().render(format).toOutputStream(baos)
                val byteArray = baos.toByteArray()

                val modelLastModifiedAfterRendering = model.getLastModified()
                check(modelLastModified == modelLastModifiedAfterRendering) { "model was changed during rendering... concurrency issue" }
                renderCache.set(Triple(modelLastModified, format, byteArray))

                return byteArray
            } finally {
                logger.info("Rendering ${Iterables.size(model.nodes())} nodes with ${Iterables.size(model.connections())} connections took $stopwatch")
            }
        }
    }
}