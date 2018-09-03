package xyz.harrychen.trivialnews.ui.fragments

import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import com.jakewharton.rxbinding2.support.v7.widget.scrollEvents
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.android.synthetic.main.refreshable_timeline.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.toast
import xyz.harrychen.trivialnews.R
import xyz.harrychen.trivialnews.models.News
import xyz.harrychen.trivialnews.support.adapter.BaseTimeLineAdapter
import xyz.harrychen.trivialnews.support.api.NewsApi

class BaseTimeLineFragment: Fragment(), AnkoLogger {

    private var newsList: MutableList<News> = MutableList(0) {News()}
    private var currentPage = 0

    private lateinit var newsListView: View
    private lateinit var timelineAdapter: BaseTimeLineAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        newsListView = inflater.inflate(R.layout.refreshable_timeline, container, false)
        timelineAdapter = BaseTimeLineAdapter(newsList)

        with (newsListView.timeline_list) {
            itemAnimator = LandingAnimator()
            layoutManager = LinearLayoutManager(this.context)
            adapter = ScaleInAnimationAdapter(timelineAdapter)
        }

        setupRefresher()
        setupLoadMore()
        return newsListView
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private var isLoading: Boolean = false
        set(loading) {
        field = loading
        with (newsListView) {
            val animator = timeline_footer.animate()
            when(loading) {
                true -> animator.alpha(1.0f)
                false -> animator.alpha(0.0f)
            }
            animator.setDuration(300)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
            invalidate()
        }
    }


    private fun setupRefresher() {
        with(newsListView.swipe_refresh) {
            setColorSchemeColors(
                    ContextCompat.getColor(context!!, R.color.colorPrimary),
                    ContextCompat.getColor(context!!, R.color.colorAccent))
            setOnRefreshListener {
                refreshTimeLine()
            }
        }
    }

    private fun setupLoadMore() {
        with (newsListView) {
            timeline_footer.alpha = 0.0f
            timeline_list.scrollEvents()
                    .bindUntilEvent(this@BaseTimeLineFragment, Lifecycle.Event.ON_DESTROY)
                    .subscribe{ event ->
                        val manager = event.view().layoutManager as LinearLayoutManager
                        var lastItem = manager.findLastCompletelyVisibleItemPosition()
                        if (lastItem == -1) lastItem = manager.findLastVisibleItemPosition()
                        if (!swipe_refresh.isRefreshing
                                && !isLoading
                                && lastItem == manager.itemCount - 1
                                && event.dy() > 0) {
                            isLoading = true
                            loadMore()
                        }
                    }
        }
    }

    private fun refreshTimeLine() {
        currentPage = 0
        newsListView.swipe_refresh.isRefreshing = true
        NewsApi.getTimeline(currentPage).subscribe({ news ->
            if (news.isNotEmpty()) {
                timelineAdapter.notifyItemRangeRemoved(0, timelineAdapter.newsData.size)
                timelineAdapter.newsData = news.toMutableList()
                timelineAdapter.notifyItemRangeInserted(0, news.size)
            } else {
                toast(R.string.no_more)
            }
            newsListView.swipe_refresh.isRefreshing = false
        },{
            e -> throw(e)
        })
    }


    private fun loadMore() {
        NewsApi.getTimeline(currentPage + 1).subscribe({ news ->
            if (news.isNotEmpty()) {
                currentPage++
                val oldSize = timelineAdapter.newsData.size
                timelineAdapter.newsData.addAll(news)
                timelineAdapter.notifyItemRangeInserted(oldSize, news.size)
            } else {
                toast(R.string.no_more)
            }
            isLoading = false
        },{
            e -> throw(e)
        })
    }
}