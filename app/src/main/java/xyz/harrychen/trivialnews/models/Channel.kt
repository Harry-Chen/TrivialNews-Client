package xyz.harrychen.trivialnews.models

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Channel(
        @PrimaryKey
        @SerializedName("_id")
        var id: Int = 0,
        var name: String = "",
        var description: String = ""

): RealmObject()