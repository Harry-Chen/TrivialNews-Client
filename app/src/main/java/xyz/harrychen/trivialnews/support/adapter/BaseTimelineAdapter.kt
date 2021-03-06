package xyz.harrychen.trivialnews.support.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import xyz.harrychen.trivialnews.R
import xyz.harrychen.trivialnews.models.News


class BaseTimelineAdapter(
    private var newsData: MutableList<News> = MutableList(0) {News()},
    private val newsClickHandler: (news: News) -> Unit
) : RecyclerView.Adapter<NewsItemViewHolder>() {


    fun setNews(news: Collection<News>) {
        newsData = news.toMutableList()
        notifyDataSetChanged()
    }

    fun addNews(news: Collection<News>) {
        val oldSize = newsData.size
        newsData.addAll(news)
        notifyItemRangeInserted(oldSize, news.size)
    }

    fun deleteNews(position: Int) {
        newsData.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getNews(position: Int): News {
        return newsData[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val holder =  NewsItemViewHolder(layoutInflater.inflate(
                R.layout.news_list_item, parent, false))
        holder.setNewsClickHandler(newsClickHandler)
        return holder
    }

    override fun getItemCount() = newsData.size

    override fun onBindViewHolder(holder: NewsItemViewHolder, position: Int) =
            holder.bind(newsData[position])



}