package xyz.harrychen.trivialnews.support.api

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.POST
import xyz.harrychen.trivialnews.models.Comment
import xyz.harrychen.trivialnews.models.QueryParameter

interface CommentApi {

    @POST("/comment")
    fun addComment(@Body addComment: QueryParameter.AddComment): Single<Comment>


    @HTTP(method = "DELETE", path = "/comment", hasBody = true)
    fun deleteComment(@Body commentId: QueryParameter.CommentId): Completable


    companion object {
        private fun create(): CommentApi {
            return BaseApi.getRetrofit().create(CommentApi::class.java)
        }

        fun addComment(newsId: Int, content: String): Single<Comment> {
            return BaseApi.observeSingleSubscribableApi(create()
                    .addComment(QueryParameter.AddComment(newsId, content)))
        }

        fun deleteComment(commentId: Int): Completable {
            return BaseApi.observeCompletableApi(create()
                    .deleteComment(QueryParameter.CommentId(commentId)))
        }
    }
}