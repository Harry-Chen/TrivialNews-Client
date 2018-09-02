package xyz.harrychen.trivialnews.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Token(
        @PrimaryKey
        var id: Int = 0,
        var token: String = ""
): RealmObject()