package xyz.harrychen.trivialnews.support.api

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*
import xyz.harrychen.trivialnews.models.QueryParameter
import xyz.harrychen.trivialnews.models.User

interface UserApi {


    @POST("/user/login")
    fun loginOrRegister(@Body register: QueryParameter.Register): Single<User>


    @PUT("/user/favorite")
    fun addFavoriteNews(@Body newsId: QueryParameter.NewsIds): Completable


    @HTTP(method = "DELETE", path = "/user/favorite", hasBody = true)
    fun deleteFavoriteNews(@Body newsId: QueryParameter.NewsIds): Completable


    companion object {
        private fun create(): UserApi {
            return BaseApi.getRetrofit().create(UserApi::class.java)
        }


        fun loginOrRegister(parameter: QueryParameter.Register): Single<User> {
            return BaseApi.observeSingleSubscribableApi(create()
                    .loginOrRegister(parameter))
        }

        fun addFavoriteNews(newsIds: List<Int>): Completable {
            return BaseApi.observeCompletableApi(create()
                    .addFavoriteNews(QueryParameter.NewsIds(newsIds)))
        }

        fun deleteFavoriteNews(newsIds: List<Int>): Completable {
            return BaseApi.observeCompletableApi(create()
                    .deleteFavoriteNews(QueryParameter.NewsIds(newsIds)))
        }

    }

}