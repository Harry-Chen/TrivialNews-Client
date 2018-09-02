package xyz.harrychen.trivialnews.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.refreshable_timeline.*
import kotlinx.android.synthetic.main.refreshable_timeline.view.*
import org.jetbrains.anko.AnkoLogger
import xyz.harrychen.trivialnews.R
import xyz.harrychen.trivialnews.models.News
import xyz.harrychen.trivialnews.support.adapter.BaseTimeLineAdapter
import xyz.harrychen.trivialnews.support.api.NewsApi

class BaseTimeLineFragment: Fragment(), AnkoLogger {

    var newsList: MutableList<News> = MutableList(1) {News()}

    private lateinit var newsListView: View
    private lateinit var timelineAdapter: BaseTimeLineAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        newsListView = inflater.inflate(R.layout.refreshable_timeline, container, false)
        newsListView.timeline_list.layoutManager = LinearLayoutManager(this.context)
        timelineAdapter = BaseTimeLineAdapter(newsList)
        newsListView.timeline_list.adapter = timelineAdapter
        fetchNews()
        return newsListView
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun fetchNews(){
        NewsApi.getTimeline(0).subscribe({
            news -> timelineAdapter.newsData = news.toMutableList()
            timelineAdapter.notifyDataSetChanged()
        },{
            e -> throw(e)
        })
    }
}