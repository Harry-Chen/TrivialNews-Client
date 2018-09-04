package xyz.harrychen.trivialnews.support.api

import io.reactivex.Completable
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.POST
import xyz.harrychen.trivialnews.models.QueryParameter

interface CommentApi {

    @POST("/comment")
    fun addComment(@Body addComment: QueryParameter.AddComment): Completable


    @HTTP(method = "DELETE", path = "/comment", hasBody = true)
    fun deleteComment(@Body commentId: QueryParameter.CommentId): Completable


    companion object {
        private fun create(): CommentApi {
            return BaseApi.getRetrofit().create(CommentApi::class.java)
        }

        fun addComment(newsId: Int, content: String): Completable {
            return BaseApi.observeCompletableApi(create()
                    .addComment(QueryParameter.AddComment(newsId, content)))
        }

        fun deleteComment(commentId: Int): Completable {
            return BaseApi.observeCompletableApi(create()
                    .deleteComment(QueryParameter.CommentId(commentId)))
        }
    }
}