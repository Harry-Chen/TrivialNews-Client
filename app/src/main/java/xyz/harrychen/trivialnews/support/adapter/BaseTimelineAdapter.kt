package xyz.harrychen.trivialnews.support.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.news_list_item.view.*
import org.joda.time.LocalDateTime
import xyz.harrychen.trivialnews.R
import xyz.harrychen.trivialnews.models.News
import xyz.harrychen.trivialnews.support.api.BaseApi

class BaseTimelineAdapter(
    private var newsData: MutableList<News> = MutableList(0) {News()}
) : RecyclerView.Adapter<BaseTimelineAdapter.NewsItemViewHolder>() {


    fun setNews(news: Collection<News>) {
        val oldSize = newsData.size
        notifyItemRangeRemoved(0, oldSize)
        newsData = news.toMutableList()
        notifyItemRangeInserted(0,  newsData.size)
    }

    fun addNews(news: Collection<News>) {
        val oldSize = newsData.size
        newsData.addAll(news)
        notifyItemRangeInserted(oldSize, news.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return NewsItemViewHolder(layoutInflater.inflate(
                R.layout.news_list_item, parent, false))
    }

    override fun getItemCount() = newsData.size

    override fun onBindViewHolder(holder: NewsItemViewHolder, position: Int) =
            holder.bind(newsData[position], position)



    inner class NewsItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: News, position: Int): Unit = with(view) {
            news_item_title.text = item.title
            news_item_summary.text = item.summary
            news_item_channel.text = "频道编号：${item.channelId}"
            news_item_date.text = BaseApi.dateTimeFormatter.print(LocalDateTime(item.publishDate))
            news_item_author.text = context.getString(R.string.adapter_author).format(item.author)
            news_item_statistics.text = context.getString(R.string.adapter_statistics).format(item.likeNum, item.commentNum)

            news_item_shadow.visibility = if (item.hasRead) View.VISIBLE else View.INVISIBLE

            when(item.picture.isBlank()) {
                true -> {
                    news_item_picture.visibility = View.GONE
                    news_item_picture.layout(0, 0, 0, 0)
                    Glide.with(this.context).clear(news_item_picture)
                }
                false -> {
                    news_item_picture.visibility = View.VISIBLE
                    Glide.with(this.context).load(item.picture).into(news_item_picture)
                }
            }
        }
    }
}