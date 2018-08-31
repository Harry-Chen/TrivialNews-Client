package xyz.harrychen.trivialnews.support.api

import io.reactivex.Completable
import io.reactivex.CompletableObserver
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.POST

interface CommentApi: BaseApi {

    @POST("/comment")
    fun addComment(@Field("news_id") newsId: Int,
                   @Field("content") content: String): Completable


    @DELETE("/comment")
    fun deleteComment(@Field("news_id") newsId: Int): Completable


    companion object {
        private fun create(): CommentApi {
            return BaseApi.getRetrofit().create(CommentApi::class.java)
        }

        fun addComment(newsId: Int, content: String, observer: CompletableObserver) {
            BaseApi.observeCompletableApi(create().addComment(newsId, content), observer)
        }

        fun deleteComment(newsId: Int, observer: CompletableObserver) {
            BaseApi.observeCompletableApi(create().deleteComment(newsId), observer)
        }
    }
}