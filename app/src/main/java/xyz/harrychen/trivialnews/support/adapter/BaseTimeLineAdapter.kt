package xyz.harrychen.trivialnews.support.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import io.realm.Realm
import kotlinx.android.synthetic.main.news_list_item.view.*
import xyz.harrychen.trivialnews.R
import xyz.harrychen.trivialnews.models.Category
import xyz.harrychen.trivialnews.models.News
import xyz.harrychen.trivialnews.support.utils.ChannelLookup
import xyz.harrychen.trivialnews.support.utils.RealmHelper
import xyz.harrychen.trivialnews.support.utils.toReadableDateTimeString


class BaseTimelineAdapter(
    private var newsData: MutableList<News> = MutableList(0) {News()},
    private var newsClickHandler: (news: News) -> Unit
) : RecyclerView.Adapter<BaseTimelineAdapter.NewsItemViewHolder>() {


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
        return NewsItemViewHolder(layoutInflater.inflate(
                R.layout.news_list_item, parent, false))
    }

    override fun getItemCount() = newsData.size

    override fun onBindViewHolder(holder: NewsItemViewHolder, position: Int) =
            holder.bind(newsData[position], position)



    inner class NewsItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: News, position: Int): Unit = with(view) {

            val channelInfo = ChannelLookup.getChannelName(item.channelId)

            news_item_title.text = item.title
            news_item_summary.text = item.summary
            news_item_channel.text = context.getString(R.string.adapter_channel)
                    .format(channelInfo.first, channelInfo.second)
            news_item_date.text = item.publishDate.toReadableDateTimeString()
            news_item_author.text = context.getString(R.string.adapter_author).format(item.author)
            news_item_statistics.text = context.getString(R.string.adapter_statistics)
                    .format(item.readNum, item.commentNum)

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

            this.setOnClickListener { newsClickHandler(item) }
        }
    }
}