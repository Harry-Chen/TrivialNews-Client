package xyz.harrychen.trivialnews.models

import com.google.gson.annotations.SerializedName
import xyz.harrychen.trivialnews.support.NEWS_PER_PAGE
import java.util.*

object QueryParameter {
    data class NewsIds(
            @SerializedName("news_ids") val ids: List<Int>
    )

    data class CommentId(
            @SerializedName("comment_id") val id: Int
    )

    data class Register(
            val username: String,
            val password: String,
            val register: Boolean = false
    )
    
    data class AddComment(
            @SerializedName("news_id") val newsId: Int,
            val content: String
    )
    
    data class ChannelIds(
            @SerializedName("channel_ids") val channelIds: List<Int>
    )
    
    data class Timeline(
            val type: String,
            @SerializedName("channel_id") val channelId: Int? = null,
            @SerializedName("before_time") val beforeTime: Date? = null,
            @SerializedName("after_time") val afterTime: Date? = null,
            val query: String? = null,
            val page: Int,
            val count: Int = NEWS_PER_PAGE
    )


}