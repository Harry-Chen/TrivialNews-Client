package xyz.harrychen.trivialnews.models

import com.google.gson.annotations.SerializedName

data class Channel(
        @SerializedName("_id") val id: Int,
        val name: String,
        val description: String,
        val subscribed: Boolean
);