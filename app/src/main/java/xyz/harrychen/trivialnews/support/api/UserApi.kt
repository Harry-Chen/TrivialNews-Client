package xyz.harrychen.trivialnews.support.api

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*
import xyz.harrychen.trivialnews.models.QueryParameter
import xyz.harrychen.trivialnews.models.Token

interface UserApi {


    @POST("/user/login")
    fun loginOrRegister(@Body register: QueryParameter.Register): Single<Token>


    @PUT("/user/favorite")
    fun addFavoriteNews(@Body newsId: QueryParameter.NewsId): Completable


    @DELETE("/user/favorite")
    fun deleteFavoriteNews(@Body newsId: QueryParameter.NewsId): Completable


    companion object {
        private fun create(): UserApi {
            return BaseApi.getRetrofit().create(UserApi::class.java)
        }


        fun loginOrRegister(parameter: QueryParameter.Register): Single<Token> {
            return BaseApi.observeSingleSubscribableApi(create()
                    .loginOrRegister(parameter))
        }

        fun addFavoriteNews(newsId: Int): Completable {
            return BaseApi.observeCompletableApi(create()
                    .addFavoriteNews(QueryParameter.NewsId(newsId)))
        }

        fun deleteFavoriteNews(newsId: Int): Completable {
            return BaseApi.observeCompletableApi(create()
                    .deleteFavoriteNews(QueryParameter.NewsId(newsId)))
        }

    }

}