package xyz.harrychen.trivialnews.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class User(
        @PrimaryKey
        var id: Int = 0,
        var username: String = "",
        var token: String = "",
        var subscription: RealmList<Int> = RealmList()
): RealmObject()