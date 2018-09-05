package xyz.harrychen.trivialnews.support.api

import io.reactivex.Single
import retrofit2.http.*
import xyz.harrychen.trivialnews.models.News
import xyz.harrychen.trivialnews.models.NewsDetail
import xyz.harrychen.trivialnews.support.NEWS_PER_PAGE

interface NewsApi {

    @GET("/news/list")
    fun getNewsTimeline(@Query("type") type: String,
                        @Query("channel_id") channelId: Int? = null,
                        @Query("before_time") beforeTime: String? = null,
                        @Query("after_time") afterTime: String? = null,
                        @Query("query") query: String? = null,
                        @Query("page") page: Int,
                        @Query("count") count: Int = NEWS_PER_PAGE)
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

        fun getRangeTimeline(afterTime: String, beforeTime: String, page: Int):
                Single<List<News>> {
            return BaseApi.observeSingleSubscribableApi(create().getNewsTimeline(
                    type = "timeline", page = page,
                    afterTime = afterTime, beforeTime = beforeTime
            ))
        }

        fun getChannelContent(channelId: Int, page: Int): Single<List<News>> {
            return BaseApi.observeSingleSubscribableApi(create().getNewsTimeline(
                    type = "channel", page = page, channelId = channelId
            ))
        }

        fun getRecommendedNews(page: Int): Single<List<News>> {
            return BaseApi.observeSingleSubscribableApi(create().getNewsTimeline(
                    type = "recommend", page = page
            ))
        }

        fun getFavoriteNews(): Single<List<News>> {
            return BaseApi.observeSingleSubscribableApi(create().getNewsTimeline(
                    type = "favorite", page = 0, count = Integer.MAX_VALUE
            ))
        }

        fun searchNews(query: String, page: Int)
                : Single<List<News>> {
            return BaseApi.observeSingleSubscribableApi(create().getNewsTimeline(
                    type = "search", page = page, query = query
            ))
        }

        fun getNewsDetail(id: Int): Single<NewsDetail> {
            return BaseApi.observeSingleSubscribableApi(create()
                    .getNewsDetail(id))
        }

    }
}