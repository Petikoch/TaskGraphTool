package ch.petikoch.tools.model2graphtool.renderer

import ch.petikoch.tools.model2graphtool.model.IModel
import guru.nidi.graphviz.engine.Format

interface IModelRenderer {

    fun render(model: IModel, format: Format): ByteArray

}