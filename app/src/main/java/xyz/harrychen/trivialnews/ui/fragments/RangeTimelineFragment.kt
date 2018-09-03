package xyz.harrychen.trivialnews.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Single
import xyz.harrychen.trivialnews.models.News
import xyz.harrychen.trivialnews.support.api.NewsApi

class RangeTimelineFragment: BaseTimelineFragment() {

    init {
        canRefresh = false
    }

    private lateinit var beforeTime: String
    private lateinit var afterTime: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        beforeTime = arguments!!["beforeTime"] as String
        afterTime = arguments!!["afterTime"] as String
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun loadFromNetwork(page: Int): Single<List<News>> {
        return NewsApi.getRangeTimeline(afterTime, beforeTime, page)
    }

}