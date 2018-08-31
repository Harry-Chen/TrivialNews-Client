package xyz.harrychen.trivialnews.support.api

import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Single
import io.reactivex.SingleObserver
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.POST
import retrofit2.http.PUT
import xyz.harrychen.trivialnews.models.Token

interface UserApi : BaseApi {


    @POST("/user/login")
    fun loginOrRegister(@Field("username") username: String,
                 @Field("password") password: String,
                 @Field("register") register: Boolean = false): Single<Token>


    @PUT("/user/favorite")
    fun addFavoriteNews(@Field("news_id") newsId: Int): Completable


    @DELETE("/user/favorite")
    fun deleteFavoriteNews(@Field("news_id") newsId: Int): Completable


    companion object {
        private fun create(): UserApi {
            return BaseApi.getRetrofit().create(UserApi::class.java)
        }

        fun register(username: String, password: String, observer: SingleObserver<Token>) {
            BaseApi.observeSingleSubscribableApi(create().loginOrRegister(username, password, true), observer)
        }

        fun login(username: String, password: String, observer: SingleObserver<Token>) {
            BaseApi.observeSingleSubscribableApi(create().loginOrRegister(username, password), observer)
        }

        fun addFavoriteNews(newsId: Int, observer: CompletableObserver) {
            BaseApi.observeCompletableApi(create().addFavoriteNews(newsId), observer)
        }

        fun deleteFavoriteNews(newsId: Int, observer: CompletableObserver) {
            BaseApi.observeCompletableApi(create().deleteFavoriteNews(newsId), observer)
        }

    }

}