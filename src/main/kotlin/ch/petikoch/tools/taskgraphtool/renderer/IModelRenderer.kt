package ch.petikoch.tools.taskgraphtool.renderer

import ch.petikoch.tools.taskgraphtool.model.IModel
import guru.nidi.graphviz.engine.Format

interface IModelRenderer {

    fun render(
            model: IModel,
            format: Format
    ): ByteArray

}