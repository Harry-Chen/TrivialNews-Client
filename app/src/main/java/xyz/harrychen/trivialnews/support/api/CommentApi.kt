package xyz.harrychen.trivialnews.support.api

import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.POST

interface CommentApi: BaseApi {

    @POST("/comment")
    fun addComment(@Field("news_id") newsId: Int,
                   @Field("content") content: String)


    @DELETE("/comment")
    fun deleteComment(@Field("news_id") newsId: Int)


    companion object {
        fun create(): CommentApi {
            return BaseApi.getRetrofit().create(CommentApi::class.java)
        }
    }
}