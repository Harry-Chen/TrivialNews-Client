package xyz.harrychen.trivialnews.ui.fragments.timeline

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Single
import kotlinx.android.synthetic.main.refreshable_timeline.view.*
import xyz.harrychen.trivialnews.models.News
import xyz.harrychen.trivialnews.support.api.NewsApi
import xyz.harrychen.trivialnews.support.utils.RealmHelper
import xyz.harrychen.trivialnews.support.utils.SwipeToDeleteCallback

class FavoriteFragment: BaseTimelineFragment() {

    init {
        realmConfig = RealmHelper.CONFIG_NEWS_FAVORITE
        infiniteScroll = false
        needNetwork = false
        dataInvalidateAfterStop = true
    }

    override fun loadFromNetwork(page: Int): Single<List<News>> = NewsApi.getFavoriteNews()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  super.onCreateView(inflater, container, savedInstanceState)

        with (view!!.timeline_list) {
            val swipeHandler = object:SwipeToDeleteCallback(context, {true}) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                }
            }
            ItemTouchHelper(swipeHandler).attachToRecyclerView(this)
        }

        return view
    }

}