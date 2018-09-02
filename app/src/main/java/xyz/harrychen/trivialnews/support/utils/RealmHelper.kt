package xyz.harrychen.trivialnews.support.utils

import io.realm.RealmConfiguration
import io.realm.annotations.RealmModule
import xyz.harrychen.trivialnews.models.Category
import xyz.harrychen.trivialnews.models.Channel
import xyz.harrychen.trivialnews.models.News
import xyz.harrychen.trivialnews.models.Token

class RealmHelper{

    @RealmModule(classes = [(News::class)])
    private class NewsModel

    @RealmModule(classes = [Category::class, Channel::class])
    private class ChannelModel

    @RealmModule(classes = [Token::class])
    private class TokenModel

    companion object {
        val CONFIG_TOKEN by lazy {
            RealmConfiguration.Builder()
                    .name("token.realm")
                    .modules(TokenModel())
                    .schemaVersion(1)
                    .deleteRealmIfMigrationNeeded()
                    .build()
        }

        val CONFIG_CHANNELS by lazy {
            RealmConfiguration.Builder()
                    .name("channels.realm")
                    .modules(ChannelModel())
                    .schemaVersion(1)
                    .deleteRealmIfMigrationNeeded()
                    .build()
        }

        val CONFIG_NEWS_TIMELINE by lazy {
            RealmConfiguration.Builder()
                    .name("news_timeline.realm")
                    .modules(NewsModel())
                    .schemaVersion(1)
                    .deleteRealmIfMigrationNeeded()
                    .build()
        }

        val CONFIG_NEWS_FAVIROTE by lazy {
            RealmConfiguration.Builder()
                    .name("news_favorite.realm")
                    .modules(NewsModel())
                    .schemaVersion(1)
                    .deleteRealmIfMigrationNeeded()
                    .build()
        }

    }
}