@file:JvmName("Constants")

package xyz.harrychen.trivialnews.support

import org.joda.time.DateTimeZone

const val API_BASE_URL = "https://news.harrychen.xyz:5001/"
const val NEWS_PER_LOAD = 20
val LOCAL_TIME_ZONE = DateTimeZone.forID( "Asia/Shanghai" )