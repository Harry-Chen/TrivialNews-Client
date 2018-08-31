package xyz.harrychen.trivialnews.models

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

data class News(
        @SerializedName("_id") val id: Int,
        @SerializedName("channel_id") val channelId: Int,
        val title: String,
        val summary: String,
        @SerializedName("comment_num") val commentNum: Int,
        @SerializedName("like_num") val LikeNum: Int,
        val author: String,
        val publishDate: DateTime,
        val link: String,
        val picture: String
)