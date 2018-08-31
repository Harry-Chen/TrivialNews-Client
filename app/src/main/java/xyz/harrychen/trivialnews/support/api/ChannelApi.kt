package xyz.harrychen.trivialnews.support.api

import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Single
import io.reactivex.SingleObserver
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.PUT
import xyz.harrychen.trivialnews.models.Category

interface ChannelApi: BaseApi {

    @GET("/channel/list")
    fun getChannelList(): Single<List<Category>>

    @PUT("/channel/subscribe")
    fun subscribeChannels(@Field("channel_ids") channelIds: List<Int>): Completable

    @DELETE("/channel/subscribe")
    fun unSubscribeChannels(@Field("channel_ids") channelIds: List<Int>): Completable

    companion object {

        private fun create(): ChannelApi {
            return BaseApi.getRetrofit().create(ChannelApi::class.java)
        }

        fun getChannelList(observer: SingleObserver<List<Category>>) {
            BaseApi.observeSingleSubscribableApi(create().getChannelList(), observer)
        }

        fun subscribeChannels(channelIds: List<Int>, observer: CompletableObserver) {
            BaseApi.observeCompletableApi(create().subscribeChannels(channelIds), observer)
        }

        fun unsubscribeChannels(channelIds: List<Int>, observer: CompletableObserver) {
            BaseApi.observeCompletableApi(create().unSubscribeChannels(channelIds), observer)
        }


    }
}