package xyz.harrychen.trivialnews.models

import com.google.gson.annotations.SerializedName

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

}