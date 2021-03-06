package xyz.harrychen.trivialnews.support.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.comment_item.view.*
import xyz.harrychen.trivialnews.R
import xyz.harrychen.trivialnews.models.Comment
import xyz.harrychen.trivialnews.support.utils.toReadableDateTimeString


class CommentAdapter(
        private var comments: MutableList<Comment>
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CommentViewHolder(layoutInflater.inflate(
                R.layout.comment_item, parent, false))
    }

    override fun getItemCount() = comments.size

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) =
            holder.bind(comments[position])

    fun getItem(position: Int): Comment {
        return comments[position]
    }

    fun addItem(comment: Comment) {
        comments.add(comment)
        notifyItemInserted(comments.size - 1)
    }

    fun deleteItem(position: Int) {
        comments.removeAt(position)
        notifyItemRemoved(position)
    }


    inner class CommentViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: Comment): Unit = with(view) {
            comment_author.text = context.getString(R.string.comment_author).format(item.username)
            comment_time.text = item.time.toReadableDateTimeString()
            comment_content.text = item.content
        }
    }
}