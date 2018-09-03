package xyz.harrychen.trivialnews.ui.fragments

import io.reactivex.Single
import xyz.harrychen.trivialnews.models.News
import xyz.harrychen.trivialnews.support.api.NewsApi
import xyz.harrychen.trivialnews.support.utils.RealmHelper

class FavoriteFragment: BaseTimelineFragment() {

    init {
        realmConfig = RealmHelper.CONFIG_NEWS_FAVIROTE
        infiniteScroll = false
    }

    override fun loadFromNetwork(page: Int): Single<List<News>> = NewsApi.getFavoriteNews()

}