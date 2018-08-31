package xyz.harrychen.trivialnews.support.api

import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.PUT

interface ChannelApi: BaseApi {

    @PUT("/channel/subscribe")
    fun subscribeChannels(@Field("channel_ids") channelIds: List<Int>)

    @DELETE("/channel/subscribe")
    fun unSubscribeChannels(@Field("channel_ids") channelIds: List<Int>)

    companion object {
        fun create(): ChannelApi {
            return BaseApi.getRetrofit().create(ChannelApi::class.java)
        }
    }
}