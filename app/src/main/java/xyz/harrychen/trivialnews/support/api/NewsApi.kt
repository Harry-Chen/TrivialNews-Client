package xyz.harrychen.trivialnews.support.api

import io.reactivex.Single
import io.reactivex.SingleObserver
import org.joda.time.DateTime
import retrofit2.http.*
import xyz.harrychen.trivialnews.models.News
import xyz.harrychen.trivialnews.models.NewsDetail
import xyz.harrychen.trivialnews.support.NEWS_PER_LOAD

interface NewsApi: BaseApi {

    @GET("/news/list")
    fun getNewsTimeline(
            @Query("type") type: String,
            @Query("channel_id") channelId: Int? = null,
            @Query("before_time") beforeTime: DateTime? = null,
            @Query("after_time") afterTime: DateTime? = null,
            @Query("query") query: String? = null,
            @Query("page") page: Int,
            @Query("count") count: Int = NEWS_PER_LOAD
    ): Single<List<News>>


    @GET("/news/detail")
    fun getNewsDetail(@Query("news_id") id: Int): Single<NewsDetail>


    companion object {

        private fun create(): NewsApi {
            return BaseApi.getRetrofit().create(NewsApi::class.java)
        }

        fun getTimeline(page: Int, observer: SingleObserver<List<News>>) {
            BaseApi.observeSingleSubscribableApi(create().getNewsTimeline(
                    type = "timeline", page = page
            ), observer)
        }

        fun getChannelContent(channelId: Int, page: Int, observer: SingleObserver<List<News>>) {
            BaseApi.observeSingleSubscribableApi(create().getNewsTimeline(
                    type = "timeline", page = page, channelId = channelId
            ), observer)
        }

        fun getRecommendedNews(page: Int, observer: SingleObserver<List<News>>) {
            BaseApi.observeSingleSubscribableApi(create().getNewsTimeline(
                    type = "recommend", page = page
            ), observer)
        }

        fun getFavoriteNews(page: Int, observer: SingleObserver<List<News>>) {
            BaseApi.observeSingleSubscribableApi(create().getNewsTimeline(
                    type = "favorite", page = page
            ), observer)
        }

        fun searchNews(query: String, afterTime: DateTime, beforeTime:DateTime, page: Int, observer: SingleObserver<List<News>>) {
            BaseApi.observeSingleSubscribableApi(create().getNewsTimeline(
                    type = "search", page = page,
                    query = query, afterTime = afterTime, beforeTime = beforeTime
            ), observer)
        }

        fun getNewsDetail(id: Int, observer: SingleObserver<NewsDetail>) {
            BaseApi.observeSingleSubscribableApi(create().getNewsDetail(id), observer)
        }

    }
}