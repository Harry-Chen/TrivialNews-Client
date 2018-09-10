package xyz.harrychen.trivialnews.support.utils

import android.content.Context
import android.content.Intent
import xyz.harrychen.trivialnews.ui.activities.LoginActivity

fun generateHandler(context: Context): Thread.UncaughtExceptionHandler {
    return Thread.UncaughtExceptionHandler { _, _ ->
        val intent = Intent(context, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK.or(
                Intent.FLAG_ACTIVITY_CLEAR_TOP.or(Intent.FLAG_ACTIVITY_NEW_TASK)))
        intent.putExtra("crash", true)
        context.startActivity(intent)
        System.exit(2)
    }
}