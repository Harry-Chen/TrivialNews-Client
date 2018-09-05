package xyz.harrychen.trivialnews.ui.fragments.timeline

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Single
import io.realm.Realm
import kotlinx.android.synthetic.main.refreshable_timeline.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.yesButton
import xyz.harrychen.trivialnews.R
import xyz.harrychen.trivialnews.models.News
import xyz.harrychen.trivialnews.support.api.NewsApi
import xyz.harrychen.trivialnews.support.api.UserApi
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

        handleFavoriteRemove(view!!.timeline_list)

        return view
    }

    private fun handleFavoriteRemove(list: RecyclerView) {

        val adapter = timelineAdapter

        val swipeHandler = object:SwipeToDeleteCallback(context!!, {true}) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val position = viewHolder.adapterPosition

                val newsToDelete = adapter.getNews(position)

                alert(R.string.comment_delete_confirm) {
                    isCancelable = false
                    yesButton {
                        UserApi.deleteFavoriteNews(listOf(newsToDelete.id)).subscribe({

                            doAsync {
                                with(Realm.getInstance(RealmHelper.CONFIG_NEWS_FAVORITE)) {
                                    beginTransaction()
                                    where(News::class.java).equalTo("id", newsToDelete.id)
                                            .findAll().deleteAllFromRealm()
                                    commitTransaction()
                                }
                            }

                            adapter.deleteNews(position)
                            showSnack(R.string.delete_favorite_success)
                        }, {
                            adapter.notifyDataSetChanged()
                            showSnack(R.string.delete_favorite_failed)
                        })
                    }
                    noButton {
                        adapter.notifyDataSetChanged()
                    }
                }.show()
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(list)
    }

}