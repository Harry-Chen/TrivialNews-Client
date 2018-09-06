package xyz.harrychen.trivialnews.models

import com.thoughtbot.expandablecheckrecyclerview.models.MultiCheckExpandableGroup
import io.realm.RealmList

data class CategoryExpandable(
        var id: Int = 0,
        var name: String = "",
        var channels: RealmList<Channel> = RealmList(),
        var expanded: Boolean = false
) : MultiCheckExpandableGroup(name, channels) {

    constructor(category: Category) :
        this(category.id, category.name, category.channels)

}