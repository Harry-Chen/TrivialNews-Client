package xyz.harrychen.trivialnews.support.utils

import io.realm.Realm
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
        val CONFIG_USER: RealmConfiguration by lazy {
            RealmConfiguration.Builder()
                    .name("user.realm")
                    .modules(UserModel())
                    .schemaVersion(2)
                    .deleteRealmIfMigrationNeeded()
                    .build()
        }

        val CONFIG_CHANNELS: RealmConfiguration by lazy {
            RealmConfiguration.Builder()
                    .name("channels.realm")
                    .modules(ChannelModel())
                    .schemaVersion(2)
                    .deleteRealmIfMigrationNeeded()
                    .build()
        }

        val CONFIG_NEWS_TIMELINE: RealmConfiguration by lazy {
            RealmConfiguration.Builder()
                    .name("news_timeline.realm")
                    .modules(NewsModel())
                    .schemaVersion(3)
                    .deleteRealmIfMigrationNeeded()
                    .build()
        }

        val CONFIG_NEWS_FAVORITE: RealmConfiguration by lazy {
            RealmConfiguration.Builder()
                    .name("news_favorite.realm")
                    .modules(NewsModel())
                    .schemaVersion(3)
                    .deleteRealmIfMigrationNeeded()
                    .build()
        }

        private fun deleteAllFromConfig(config: RealmConfiguration) {
            with (Realm.getInstance(config)) {
                beginTransaction()
                deleteAll()
                commitTransaction()
            }
        }

        fun cleanUserData() {
            deleteAllFromConfig(CONFIG_USER)
            deleteAllFromConfig(CONFIG_CHANNELS)
            deleteAllFromConfig(CONFIG_NEWS_TIMELINE)
            deleteAllFromConfig(CONFIG_NEWS_FAVORITE)
        }
    }
}