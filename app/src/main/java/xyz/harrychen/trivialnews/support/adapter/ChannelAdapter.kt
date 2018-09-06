package xyz.harrychen.trivialnews.support.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import com.thoughtbot.expandablecheckrecyclerview.CheckableChildRecyclerViewAdapter
import com.thoughtbot.expandablecheckrecyclerview.models.CheckedExpandableGroup
import com.thoughtbot.expandablecheckrecyclerview.viewholders.CheckableChildViewHolder
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import kotlinx.android.synthetic.main.channel_list_category.view.*
import kotlinx.android.synthetic.main.channel_list_item.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.warn
import xyz.harrychen.trivialnews.R
import xyz.harrychen.trivialnews.models.CategoryExpandable
import xyz.harrychen.trivialnews.models.Channel

class ChannelAdapter(
        categories: List<CategoryExpandable>,
        private var channelSummaryOnClickHandler: (Channel) -> Unit
) : CheckableChildRecyclerViewAdapter<ChannelAdapter.CategoryViewHolder,
        ChannelAdapter.ChannelViewHolder>(categories), AnkoLogger {

    private var subscription: List<Int> = listOf()

    fun setSubscription(state: List<Int>) {
        warn { "New subscription: $state" }
        subscription = state
        notifyDataSetChanged()
    }

    override fun onBindCheckChildViewHolder(holder: ChannelViewHolder?, flatPosition: Int,
                                            group: CheckedExpandableGroup?, childIndex: Int) {
        holder!!.bind(group!!.items[childIndex] as Channel)
    }

    override fun onCreateGroupViewHolder(parent: ViewGroup?, viewType: Int): CategoryViewHolder {
        val view = parent!!.context.layoutInflater
                .inflate(R.layout.channel_list_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onCreateCheckChildViewHolder(parent: ViewGroup?, viewType: Int):
            ChannelViewHolder {
        val view = parent!!.context.layoutInflater
                .inflate(R.layout.channel_list_item, parent, false)
        return ChannelViewHolder(view)
    }

    override fun onBindGroupViewHolder(holder: CategoryViewHolder?, flatPosition: Int,
                                       group: ExpandableGroup<*>?) {
        holder!!.bind(group as CategoryExpandable)
    }


    inner class CategoryViewHolder(view: View): GroupViewHolder(view) {

        private lateinit var storedCategory: CategoryExpandable

        fun bind(category: CategoryExpandable) {
            storedCategory = category
            itemView.channel_category_title.text = category.title
            itemView.channel_category_number.text = category.channels.size.toString()
            itemView.channel_category_arrow.rotation =
                    if (category.expanded) 180f else 0f
        }

        private fun rotateArrow() {
            itemView.channel_category_arrow.animate()
                    .rotationBy(180f)
                    .setDuration(300)
                    .start()
        }

        override fun expand() {
            super.expand()
            storedCategory.expanded = true
            rotateArrow()

        }

        override fun collapse() {
            super.collapse()
            storedCategory.expanded = false
            rotateArrow()
        }

    }


    inner class ChannelViewHolder(view: View): CheckableChildViewHolder(view) {


        override fun getCheckable(): Checkable {
            return itemView.channel_item_text
        }

        fun bind(channel: Channel) {
            with (itemView) {
                channel_item_text.text = channel.name
                channel_item_summary.text = channel.description
                channel_item_summary.setOnClickListener {
                    channelSummaryOnClickHandler(channel)
                }
                channel_item_text.isChecked = channel.id in subscription
            }
        }

    }

}