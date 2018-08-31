package xyz.harrychen.trivialnews.models

import com.google.gson.annotations.SerializedName

data class Category(
        @SerializedName("_id") val id: Int,
        val name: String,
        val channels: List<Channel>
);