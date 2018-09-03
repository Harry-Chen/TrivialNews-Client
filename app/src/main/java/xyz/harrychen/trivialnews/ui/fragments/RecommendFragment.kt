package xyz.harrychen.trivialnews.ui.fragments

import io.reactivex.Single
import xyz.harrychen.trivialnews.models.News
import xyz.harrychen.trivialnews.support.api.NewsApi

class RecommendFragment: BaseTimeLineFragment() {

    override fun loadFromNetwork(page: Int): Single<List<News>> = NewsApi.getRecommendedNews(page)

}