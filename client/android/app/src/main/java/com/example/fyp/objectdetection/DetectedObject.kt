package com.example.fyp.objectdetection

import android.graphics.RectF
import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson

@JsonClass(generateAdapter = true)
data class DetectedObject(
    @Json(name = "detection_class")
    val objectLabel: String,
    @Json(name = "detection_box")
    val boundingBox: RectF,
    @Json(name = "detection_score")
    val confidence: Float,
    @Json(name = "calories")
    val calories: Int?,
)

@JsonClass(generateAdapter = true)
data class DetectedObjectList(
    @Json(name = "predictions")
    var objectList: List<DetectedObject>,
)

class DetectedObjectConverter {
    @ToJson
    fun rectToList(rect: RectF): List<Float> {
        return listOf(rect.top, rect.bottom, rect.left, rect.right)
    }

    @FromJson
    fun listToRect(floatList: List<Float>): RectF {
        return RectF(floatList[0], floatList[1], floatList[2], floatList[3])
    }
}