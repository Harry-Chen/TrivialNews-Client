package xyz.harrychen.trivialnews.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Channel(
        @PrimaryKey
        @SerializedName("_id")
        var id: Int = 0,
        var name: String = "",
        var description: String = ""

): RealmObject(), Parcelable {

        constructor(parcel: Parcel) : this(
                parcel.readInt(),
                parcel.readString()!!,
                parcel.readString()!!)

        override fun writeToParcel(dest: Parcel?, flags: Int) {
                dest?.writeInt(id)
                dest?.writeString(name)
                dest?.writeString(description)
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<Channel> {
                override fun createFromParcel(parcel: Parcel): Channel {
                        return Channel(parcel)
                }

                override fun newArray(size: Int): Array<Channel?> {
                        return arrayOfNulls(size)
                }
        }
}