package xyz.harrychen.trivialnews.support.api

import io.reactivex.Single
import org.joda.time.DateTime
import retrofit2.http.*
import xyz.harrychen.trivialnews.models.News
import xyz.harrychen.trivialnews.models.NewsDetail
import xyz.harrychen.trivialnews.models.QueryParameter

interface NewsApi {

    @GET("/news/list")
    fun getNewsTimeline(@Body timeline: QueryParameter.Timeline): Single<List<News>>


    @GET("/news/detail")
    fun getNewsDetail(@Body newsId: QueryParameter.NewsId): Single<NewsDetail>


    companion object {

        private fun create(): NewsApi {
            return BaseApi.getRetrofit().create(NewsApi::class.java)
        }

        fun getTimeline(page: Int): Single<List<News>> {
            return BaseApi.observeSingleSubscribableApi(create()
                    .getNewsTimeline(QueryParameter.Timeline(
                            type = "timeline", page = page
                    )))
        }

        fun getChannelContent(channelId: Int, page: Int): Single<List<News>> {
            return BaseApi.observeSingleSubscribableApi(create()
                    .getNewsTimeline(QueryParameter.Timeline(
                            type = "timeline", page = page, channelId = channelId
                    )))
        }

        fun getRecommendedNews(page: Int): Single<List<News>> {
            return BaseApi.observeSingleSubscribableApi(create()
                    .getNewsTimeline(QueryParameter.Timeline(
                            type = "recommend", page = page
                    )))
        }

        fun getFavoriteNews(page: Int): Single<List<News>> {
            return BaseApi.observeSingleSubscribableApi(create()
                    .getNewsTimeline(QueryParameter.Timeline(
                            type = "favorite", page = page
                    )))
        }

        fun searchNews(query: String, afterTime: DateTime, beforeTime: DateTime, page: Int)
                : Single<List<News>> {
            return BaseApi.observeSingleSubscribableApi(create()
                    .getNewsTimeline(QueryParameter.Timeline(
                            type = "search", page = page,
                            query = query, afterTime = afterTime, beforeTime = beforeTime
                    )))
        }

        fun getNewsDetail(id: Int): Single<NewsDetail> {
            return BaseApi.observeSingleSubscribableApi(create()
                    .getNewsDetail(QueryParameter.NewsId(id)))
        }

    }
}