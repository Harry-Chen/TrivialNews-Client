package xyz.harrychen.trivialnews.ui.fragments.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Single
import xyz.harrychen.trivialnews.models.News
import xyz.harrychen.trivialnews.support.api.NewsApi

class SearchResultFragment : BaseTimelineFragment() {

    init {
        canRefresh = false
    }

    private lateinit var query: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        query = arguments!!["query"] as String
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun loadFromNetwork(page: Int): Single<List<News>> = NewsApi.searchNews(query, page)

}