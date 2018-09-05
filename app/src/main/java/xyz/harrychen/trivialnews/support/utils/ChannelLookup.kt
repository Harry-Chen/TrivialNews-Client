package xyz.harrychen.trivialnews.support.utils

import xyz.harrychen.trivialnews.models.Category

class ChannelLookup{
    companion object {
        private var channelNameMap = HashMap<Int, Pair<String, String>>()

        fun updateChannelInfo(categories: List<Category>) {
            categories.forEach { category ->
                category.channels.forEach{ channel ->
                    channelNameMap[channel.id] = Pair(category.name, channel.name)
                }
            }
        }

        fun getChannelName(id: Int): Pair<String, String> {
            return channelNameMap[id]!!
        }

    }
}