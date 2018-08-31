package xyz.harrychen.trivialnews.models

data class NewsDetail(
        val like: Boolean,
        val favorite: Boolean,
        val keywords: List<String>,
        val comments: List<Comment>
)