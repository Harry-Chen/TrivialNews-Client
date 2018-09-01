package xyz.harrychen.trivialnews.models

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime
import xyz.harrychen.trivialnews.support.NEWS_PER_LOAD

object QueryParameter {
    data class NewsId(
            @SerializedName("news_id") val id: Int
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
            @SerializedName("before_time") val beforeTime: DateTime? = null,
            @SerializedName("after_time") val afterTime: DateTime? = null,
            val query: String? = null,
            val page: Int,
            val count: Int = NEWS_PER_LOAD
    )


}