package ch.petikoch.tools.taskgraphtool.serialization

import ch.petikoch.tools.taskgraphtool.model.IModel
import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.xml.DomDriver

internal object XStreamModelSerializer : IModelSerializer {

    private val xstream = XStream(DomDriver()).apply {
        classLoader = XStreamModelSerializer::class.java.classLoader // seems to be necessary for deserialization
    }

    override fun serialize(model: IModel): String {
        return xstream.toXML(model)
    }

    override fun deserialize(modelAsString: String): IModel {
        val any = xstream.fromXML(modelAsString)
        return any as IModel
    }

    override fun getFileExtension(): String {
        return "xml"
    }

}