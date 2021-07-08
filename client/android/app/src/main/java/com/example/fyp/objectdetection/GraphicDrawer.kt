package com.example.fyp.objectdetection

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import com.example.fyp.camera.GraphicOverlay
import java.util.*
import kotlin.math.max
import kotlin.math.min

class GraphicDrawer constructor(
    overlay: GraphicOverlay,
    private val detectedObject: DetectedObject,
) : GraphicOverlay.Graphic(overlay) {

    private val numColors = COLORS.size

    private val boxPaints = Array(numColors) { Paint() }
    private val textPaints = Array(numColors) { Paint() }
    private val labelPaints = Array(numColors) { Paint() }

    init {
        for (i in 0 until numColors) {
            textPaints[i] = Paint()
            textPaints[i].color = COLORS[i][0]
            textPaints[i].textSize = TEXT_SIZE
            boxPaints[i] = Paint()
            boxPaints[i].color = COLORS[i][1]
            boxPaints[i].style = Paint.Style.STROKE
            boxPaints[i].strokeWidth = STROKE_WIDTH
            labelPaints[i] = Paint()
            labelPaints[i].color = COLORS[i][1]
            labelPaints[i].style = Paint.Style.FILL
        }
    }


    override fun draw(canvas: Canvas) {
        val colorID = 2
        val textWidth =
            textPaints[colorID].measureText(detectedObject.objectLabel)
        val lineHeight = TEXT_SIZE + STROKE_WIDTH
        var yLabelOffset = -lineHeight

        // Draws the bounding box.
        val rect = RectF(
            detectedObject.boundingBox.left * 200,
            detectedObject.boundingBox.top * 200,
            detectedObject.boundingBox.right * 200,
            detectedObject.boundingBox.bottom * 200
        )
        val x0 = translateX(rect.left)
        val x1 = translateX(rect.right)
        rect.left = min(x0, x1)
        rect.right = max(x0, x1)
        rect.top = translateY(rect.top)
        rect.bottom = translateY(rect.bottom)
        canvas.drawRect(rect, boxPaints[9])

        // Draws other object info.
        canvas.drawRect(
            rect.left - STROKE_WIDTH,
            rect.top + yLabelOffset,
            rect.left + textWidth + 2 * STROKE_WIDTH,
            rect.top,
            labelPaints[6]
        )

        yLabelOffset += lineHeight
        canvas.drawText(
            detectedObject.objectLabel,
            rect.left,
            rect.top + yLabelOffset,
            textPaints[7]
        )
        yLabelOffset += lineHeight
        canvas.drawText(
            String.format(
                Locale.US,
                LABEL_FORMAT,
                detectedObject.confidence * 100,
            ),
            rect.left,
            rect.top + yLabelOffset,
            textPaints[8]
        )
        yLabelOffset += lineHeight

        Log.e("DRAWN", "DRAWN")
    }

    companion object {
        private const val TEXT_SIZE = 54.0f
        private const val STROKE_WIDTH = 4.0f
        private const val NUM_COLORS = 10
        private val COLORS =
            arrayOf(
                intArrayOf(Color.BLACK, Color.WHITE),
                intArrayOf(Color.WHITE, Color.MAGENTA),
                intArrayOf(Color.BLACK, Color.LTGRAY),
                intArrayOf(Color.WHITE, Color.RED),
                intArrayOf(Color.WHITE, Color.BLUE),
                intArrayOf(Color.WHITE, Color.DKGRAY),
                intArrayOf(Color.BLACK, Color.CYAN),
                intArrayOf(Color.BLACK, Color.YELLOW),
                intArrayOf(Color.WHITE, Color.BLACK),
                intArrayOf(Color.BLACK, Color.GREEN)
            )
        private const val LABEL_FORMAT = "%.2f%% confidence"
    }
}