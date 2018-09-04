package xyz.harrychen.trivialnews.support.api

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*
import xyz.harrychen.trivialnews.models.Category
import xyz.harrychen.trivialnews.models.QueryParameter

interface ChannelApi {

    @GET("/channel/list")
    fun getChannelList(): Single<List<Category>>

    @PUT("/channel/subscribe")
    fun subscribeChannels(@Body channelIds: QueryParameter.ChannelIds): Completable

    @HTTP(method = "DELETE", path = "/channel/subscribe", hasBody = true)
    fun unSubscribeChannels(@Body channelIds: QueryParameter.ChannelIds): Completable

    companion object {

        private fun create(): ChannelApi {
            return BaseApi.getRetrofit().create(ChannelApi::class.java)
        }

        fun getChannelList(): Single<List<Category>> {
            return BaseApi.observeSingleSubscribableApi(create().getChannelList())
        }

        fun subscribeChannels(channelIds: List<Int>): Completable {
            return BaseApi.observeCompletableApi(create()
                    .subscribeChannels(QueryParameter.ChannelIds(channelIds)))
        }

        fun unsubscribeChannels(channelIds: List<Int>): Completable {
            return BaseApi.observeCompletableApi(create()
                    .unSubscribeChannels(QueryParameter.ChannelIds(channelIds)))
        }


    }
}