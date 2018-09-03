package xyz.harrychen.trivialnews.support.utils

import io.realm.RealmConfiguration
import io.realm.annotations.RealmModule
import xyz.harrychen.trivialnews.models.Category
import xyz.harrychen.trivialnews.models.Channel
import xyz.harrychen.trivialnews.models.News
import xyz.harrychen.trivialnews.models.User

class RealmHelper{

    @RealmModule(classes = [(News::class)])
    private class NewsModel

    @RealmModule(classes = [Category::class, Channel::class])
    private class ChannelModel

    @RealmModule(classes = [User::class])
    private class UserModel

    companion object {
        val CONFIG_USER by lazy {
            RealmConfiguration.Builder()
                    .name("user.realm")
                    .modules(UserModel())
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