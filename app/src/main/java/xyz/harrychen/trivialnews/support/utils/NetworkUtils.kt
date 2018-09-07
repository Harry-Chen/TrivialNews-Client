package xyz.harrychen.trivialnews.support.utils

import android.content.Context
import android.net.ConnectivityManager

class NetworkUtils {
    companion object {
        fun isConnected(context: Context): Boolean {
            val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = manager.activeNetworkInfo
            return network != null && network.isConnected
        }
    }
}