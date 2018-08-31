package xyz.harrychen.trivialnews.support.api

import org.joda.time.DateTime
import retrofit2.http.*

interface NewsApi: BaseApi {

    @GET("/news/list")
    fun getNewsTimeLine(
            @Query("page") page: Int = 0,
            @Query("count") count: Int = 20,
            @Query("type") type: String = "timeline"
    )

    @GET("/channel/list")
    fun getChannelContent(
            @Query("channel_id") channelId: Int,
            @Query("page") page: Int = 0,
            @Query("count") count: Int = 20,
            @Query("type") type: String = "timeline"
    )

    @GET("/news/list")
    fun searchNews(
            @Query("query") query: String,
            @Query("before_time") beforeTime: DateTime,
            @Query("after_time") afterTime: DateTime,
            @Query("page") page: Int = 0,
            @Query("count") count: Int = 20,
            @Query("type") type: String = "search"
    )


    @GET("/news/list")
    fun getFavoriteNews(
            @Query("page") page: Int = 0,
            @Query("count") count: Int = 20,
            @Query("type") type: String = "favorite"
    )


    @GET("/news/list")
    fun getRecommendedNews(
            @Query("page") page: Int = 0,
            @Query("count") count: Int = 20,
            @Query("type") type: String = "recommend"
    )


    @GET("/news/detail")
    fun getNewsDetail(@Query("news_id") id: Int)


    @PUT("/channel/subscribe")
    fun addLike(@Field("news_id") id: Int)


    @DELETE("/channel/subscribe")
    fun deleteLike(@Field("news_id") id: Int)



    companion object {
        fun create(): NewsApi {
            return BaseApi.getRetrofit().create(NewsApi::class.java)
        }
    }
}