package ch.petikoch.tools.taskgraphtool.ui

import com.vaadin.spring.annotation.UIScope
import org.springframework.stereotype.Component
import java.io.Serializable

@Component
@UIScope
internal class Zoomer : Serializable {

    companion object {
        private const val serialVersionUID: Long = 1
        private val zoomModes: List<Float> = listOf(0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.5f, 2.5f, 5.0f, 7.5f, 10f)
    }

    private var current = 1.0f

    fun currentZoomFactor() = current

    fun reset(): Float {
        current = 1.0f
        return current
    }

    fun zoomIn(): Float {
        val currentZoomIndex = zoomModes.indexOf(current)
        if (currentZoomIndex < (zoomModes.size - 1)) {
            current = zoomModes[currentZoomIndex + 1]
        }
        return current
    }

    fun zoomOut(): Float {
        val currentZoomIndex = zoomModes.indexOf(current)
        if (currentZoomIndex > 0) {
            current = zoomModes[currentZoomIndex - 1]
        }
        return current
    }

}