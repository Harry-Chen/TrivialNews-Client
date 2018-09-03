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

class BaseTimeLineAdapter(
    var newsData: MutableList<News>
) : RecyclerView.Adapter<BaseTimeLineAdapter.NewsItemViewHolder>() {

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
            news_item_author.text = "作者： ${item.author}"
            news_item_statistics.text = "${item.likeNum} 点赞 · ${item.commentNum} 评论"

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