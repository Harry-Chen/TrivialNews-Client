package xyz.harrychen.trivialnews.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class Comment(
        @SerializedName("_id") val id: Int,
        val username: String,
        val content: String,
        val time: Date
)