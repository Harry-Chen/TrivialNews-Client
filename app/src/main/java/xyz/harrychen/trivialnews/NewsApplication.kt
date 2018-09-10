package xyz.harrychen.trivialnews

import android.support.multidex.MultiDexApplication
import io.realm.Realm
import xyz.harrychen.trivialnews.support.utils.generateHandler

@Suppress("unused")
class NewsApplication: MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        Thread.setDefaultUncaughtExceptionHandler(generateHandler(this))
    }
}