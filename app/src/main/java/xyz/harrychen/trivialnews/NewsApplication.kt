package xyz.harrychen.trivialnews

import android.support.multidex.MultiDexApplication
import io.realm.Realm

class NewsApplication: MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}