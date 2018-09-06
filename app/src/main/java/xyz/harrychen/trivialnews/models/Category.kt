package xyz.harrychen.trivialnews.models

import com.google.gson.annotations.SerializedName
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Category(
        @PrimaryKey
        @SerializedName("_id")
        var id: Int = 0,
        var name: String = "",
        var channels: RealmList<Channel> = RealmList()
): RealmObject()