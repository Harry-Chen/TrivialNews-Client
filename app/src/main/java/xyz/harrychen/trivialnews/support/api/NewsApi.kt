package xyz.harrychen.trivialnews.support.api

import io.reactivex.Single
import org.joda.time.DateTime
import retrofit2.http.*
import xyz.harrychen.trivialnews.models.News
import xyz.harrychen.trivialnews.models.NewsDetail
import xyz.harrychen.trivialnews.support.NEWS_PER_LOAD

interface NewsApi {

    @GET("/news/list")
    fun getNewsTimeline(@Query("type") type: String,
                        @Query("channel_id") channelId: Int? = null,
                        @Query("before_time") beforeTime: String? = null,
                        @Query("after_time") afterTime: String? = null,
                        @Query("query") query: String? = null,
                        @Query("page") page: Int,
                        @Query("count") count: Int = NEWS_PER_LOAD)
            : Single<List<News>>


    @GET("/news/detail")
    fun getNewsDetail(@Query("news_id") newsId: Int): Single<NewsDetail>


    companion object {

        private fun create(): NewsApi {
            return BaseApi.getRetrofit().create(NewsApi::class.java)
        }

        fun getTimeline(page: Int): Single<List<News>> {
            return BaseApi.observeSingleSubscribableApi(create().getNewsTimeline(
                    type = "timeline", page = page
            ))
        }

        fun getChannelContent(channelId: Int, page: Int): Single<List<News>> {
            return BaseApi.observeSingleSubscribableApi(create().getNewsTimeline(
                    type = "timeline", page = page, channelId = channelId
            ))
        }

        fun getRecommendedNews(page: Int): Single<List<News>> {
            return BaseApi.observeSingleSubscribableApi(create().getNewsTimeline(
                    type = "recommend", page = page
            ))
        }

        fun getFavoriteNews(page: Int): Single<List<News>> {
            return BaseApi.observeSingleSubscribableApi(create().getNewsTimeline(
                    type = "favorite", page = page
            ))
        }

        fun searchNews(query: String, afterTime: DateTime, beforeTime: DateTime, page: Int)
                : Single<List<News>> {
            return BaseApi.observeSingleSubscribableApi(create().getNewsTimeline(
                    type = "search", page = page, query = query,
                    afterTime = BaseApi.dateTimeFormatter.print(afterTime),
                    beforeTime = BaseApi.dateTimeFormatter.print(beforeTime)
            ))
        }

        fun getNewsDetail(id: Int): Single<NewsDetail> {
            return BaseApi.observeSingleSubscribableApi(create()
                    .getNewsDetail(id))
        }

    }
}