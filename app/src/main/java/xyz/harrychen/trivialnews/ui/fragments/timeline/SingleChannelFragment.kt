package xyz.harrychen.trivialnews.ui.fragments.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Single
import xyz.harrychen.trivialnews.models.News
import xyz.harrychen.trivialnews.support.api.NewsApi

class SingleChannelFragment : BaseTimelineFragment() {

    private var channelId: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        channelId = arguments!!["id"] as Int
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun loadFromNetwork(page: Int): Single<List<News>> {
        return NewsApi.getChannelContent(channelId, page)
    }

}