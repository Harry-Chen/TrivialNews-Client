package xyz.harrychen.trivialnews.ui.fragments.timeline

import io.reactivex.Single
import xyz.harrychen.trivialnews.models.News
import xyz.harrychen.trivialnews.support.api.NewsApi

class RecommendFragment: BaseTimelineFragment() {

    override fun loadFromNetwork(page: Int): Single<List<News>> = NewsApi.getRecommendedNews(page)

}