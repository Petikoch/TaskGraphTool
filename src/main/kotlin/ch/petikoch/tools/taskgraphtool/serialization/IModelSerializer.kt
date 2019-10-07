package ch.petikoch.tools.taskgraphtool.serialization

import ch.petikoch.tools.taskgraphtool.model.IModel

interface IModelSerializer {

    fun serialize(model: IModel): String

    fun deserialize(modelAsString: String): IModel

    fun getFileExtension(): String

}