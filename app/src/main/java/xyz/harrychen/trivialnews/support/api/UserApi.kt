package xyz.harrychen.trivialnews.support.api

import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.POST
import retrofit2.http.PUT

interface UserApi : BaseApi {

    @POST("/user/login")
    fun login(@Field("username") username: String, @Field("password") password: String)

    @POST("/user/login")
    fun register(@Field("username") username: String, @Field("password") password: String, @Field("register") register: Boolean = true)

    @PUT("/user/favorite")
    fun addFavoriteNews(@Field("news_id") newsId: Int)

    @DELETE("/channel/subscribe")
    fun deleteFavoriteNews(@Field("news_id") newsId: Int)


    companion object {
        fun create(): UserApi {
            return BaseApi.getRetrofit().create(UserApi::class.java)
        }
    }

}