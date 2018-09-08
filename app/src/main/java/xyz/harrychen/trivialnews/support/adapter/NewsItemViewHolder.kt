package xyz.harrychen.trivialnews.support.adapter

import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import kotlinx.android.synthetic.main.news_list_item.view.*
import xyz.harrychen.trivialnews.R
import xyz.harrychen.trivialnews.models.News
import xyz.harrychen.trivialnews.support.utils.ChannelLookup
import xyz.harrychen.trivialnews.support.utils.toReadableDateTimeString

class NewsItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    private var newsClickHandler: (news: News) -> Unit = {}

    fun setNewsClickHandler(handler: (news: News) -> Unit) {
        newsClickHandler = handler
    }

    fun bind(
            item: News,
            glideLoadListener: RequestListener<Drawable>? = null,
            share: Boolean = false
    ): Unit = with(view) {

        val channelInfo = ChannelLookup.getChannelName(item.channelId)

        news_item_title.text = item.title
        news_item_summary.text = item.summary
        news_item_channel.text = context.getString(R.string.adapter_channel)
                .format(channelInfo.first, channelInfo.second)
        news_item_date.text = item.publishDate.toReadableDateTimeString()
        news_item_author.text = context.getString(R.string.adapter_author).format(item.author)
        news_item_statistics.text = context.getString(R.string.adapter_statistics)
                .format(item.readNum, item.commentNum)

        news_item_shadow.visibility = if (item.hasRead && !share) View.VISIBLE else View.INVISIBLE

        news_item_from_app.visibility = if (share) View.VISIBLE else View.INVISIBLE

        when(item.picture.isBlank()) {
            true -> {
                news_item_picture.visibility = View.GONE
                news_item_picture.layout(0, 0, 0, 0)
                Glide.with(this.context).clear(news_item_picture)
            }
            false -> {
                news_item_picture.visibility = View.VISIBLE
                Glide.with(this.context).load(item.picture)
                        .listener(glideLoadListener)
                        .into(news_item_picture)
            }
        }

        this.setOnClickListener { newsClickHandler(item) }
    }
}