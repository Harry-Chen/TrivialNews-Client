package xyz.harrychen.trivialnews.ui.fragments.timeline

import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import com.jakewharton.rxbinding2.support.v7.widget.scrollEvents
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Single
import io.realm.Realm
import io.realm.RealmConfiguration
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.android.synthetic.main.refreshable_timeline.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.uiThread
import xyz.harrychen.trivialnews.R
import xyz.harrychen.trivialnews.models.News
import xyz.harrychen.trivialnews.support.adapter.BaseTimelineAdapter
import xyz.harrychen.trivialnews.ui.activities.NewsDetailActivity

abstract class BaseTimelineFragment : Fragment(), AnkoLogger {

    private var currentPage = 0

    protected var realmConfig: RealmConfiguration? = null
    protected var infiniteScroll = true
    protected var canRefresh = true
    protected var needNetwork = true

    private lateinit var newsListView: View
    private lateinit var timelineAdapter: BaseTimelineAdapter
    private var snackBarPlace: CoordinatorLayout? = null
    private var fromCache = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        newsListView = inflater.inflate(R.layout.refreshable_timeline, container, false)
        timelineAdapter = BaseTimelineAdapter {
            startActivity<NewsDetailActivity>("id" to it.id, "link" to it.link)
        }

        with(newsListView.timeline_list) {
            itemAnimator = LandingAnimator()
            layoutManager = LinearLayoutManager(this.context)
            adapter = ScaleInAnimationAdapter(timelineAdapter)
        }

        setupRefresher()
        setupLoadMore()
        loadFromCache()

        if (needNetwork) refreshTimeline()

        return newsListView
    }


    val setCoordinatorLayout = {view: CoordinatorLayout ->
        snackBarPlace = view
    }

    fun scrollAndRefresh() {
        with(newsListView.timeline_list.layoutManager!! as LinearLayoutManager) {
            if (itemCount > 0 && findFirstCompletelyVisibleItemPosition() != 0) {
                val scroller = object : LinearSmoothScroller(context) {
                    override fun getVerticalSnapPreference(): Int {
                        return LinearSmoothScroller.SNAP_TO_START
                    }
                }
                scroller.targetPosition = 0
                startSmoothScroll(scroller)
            } else {
                refreshTimeline()
            }
        }
    }

    abstract fun loadFromNetwork(page: Int = 0): Single<List<News>>

    private fun loadFromCache() {

        if (realmConfig == null) return
        doAsync {

            lateinit var cachedNews: MutableList<News>

            Realm.getInstance(realmConfig!!).use {
                cachedNews = it.copyFromRealm(it.where(News::class.java).findAll())
            }

            uiThread {
                timelineAdapter.setNews(cachedNews)
                fromCache = true
                showSnack(getString(R.string.load_more_news_cache).format(cachedNews.size))
            }
        }

    }

    private fun appendToCache(newsList: Collection<News>) {

        if (realmConfig == null) return
        doAsync {
            Realm.getInstance(realmConfig!!).use {
                it.beginTransaction()
                it.insert(newsList)
                it.commitTransaction()
            }
        }

    }

    private fun clearCache() {

        if (realmConfig == null) return
        doAsync {
            Realm.getInstance(realmConfig!!).use {
                it.beginTransaction()
                it.deleteAll()
                it.commitTransaction()
            }
        }

    }

    private var isLoading: Boolean = false
        set(loading) {
            field = loading
            with(newsListView) {
                val animator = timeline_footer.animate()
                when (loading) {
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
            isEnabled = canRefresh
            setColorSchemeColors(
                    ContextCompat.getColor(context!!, R.color.colorPrimary),
                    ContextCompat.getColor(context!!, R.color.colorAccent))
            setOnRefreshListener {
                refreshTimeline()
            }
        }
    }

    private fun setupLoadMore() {
        with(newsListView) {
            timeline_footer.alpha = 0.0f
            timeline_list.scrollEvents()
                    .bindUntilEvent(this@BaseTimelineFragment, Lifecycle.Event.ON_DESTROY)
                    .subscribe { event ->
                        val manager = event.view().layoutManager as LinearLayoutManager
                        var lastItem = manager.findLastCompletelyVisibleItemPosition()
                        if (lastItem == -1) lastItem = manager.findLastVisibleItemPosition()
                        if (!swipe_refresh.isRefreshing
                                && !isLoading
                                && lastItem == manager.itemCount - 1
                                && event.dy() > 0) {
                            if (fromCache || !infiniteScroll) {
                                showSnack(R.string.infinite_scroll_disabled)
                            } else {
                                isLoading = true
                                loadMore()
                            }
                        }
                    }
        }
    }

    private fun showSnack(content: Any) {
        if (snackBarPlace != null) {
            when (content) {
                is Int -> snackbar(snackBarPlace!!, content)
                is String -> snackbar(snackBarPlace!!, content)
            }
        }
    }

    private fun refreshTimeline() {
        currentPage = 0
        newsListView.swipe_refresh.isRefreshing = true
        loadFromNetwork(currentPage)
                .bindUntilEvent(this, Lifecycle.Event.ON_PAUSE).subscribe({ news ->
                clearCache()
                fromCache = false
                timelineAdapter.setNews(news)
                appendToCache(news)
                showSnack(getString(R.string.load_more_news).format(news.size))
            newsListView.swipe_refresh.isRefreshing = false
        }, {
            newsListView.swipe_refresh.isRefreshing = false
            showSnack(R.string.refresh_failed)
        })
    }


    private fun loadMore() {
        loadFromNetwork(currentPage + 1)
                .bindUntilEvent(this, Lifecycle.Event.ON_PAUSE).subscribe({ news ->
            if (news.isNotEmpty()) {
                currentPage++
                timelineAdapter.addNews(news)
                appendToCache(news)
                showSnack(getString(R.string.load_more_news).format(news.size))
            } else {
                showSnack(R.string.no_more)
            }
            isLoading = false
        }, {
            isLoading = false
            showSnack(R.string.load_more_failed)
        })
    }
}