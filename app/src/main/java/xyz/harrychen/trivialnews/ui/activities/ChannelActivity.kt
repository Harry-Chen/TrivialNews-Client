package xyz.harrychen.trivialnews.ui.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_channel.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import xyz.harrychen.trivialnews.R
import xyz.harrychen.trivialnews.models.Category
import xyz.harrychen.trivialnews.models.CategoryExpandable
import xyz.harrychen.trivialnews.support.adapter.ChannelAdapter
import xyz.harrychen.trivialnews.support.utils.RealmHelper

class ChannelActivity : AppCompatActivity() {

    private lateinit var channelAdapter: ChannelAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel)

        with(supportActionBar!!) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        loadChannels()

        setTitle(R.string.drawer_manage_subscription)

    }

    private fun loadChannels() {
        doAsync {
            var categories: List<CategoryExpandable>? = null

            doAsync {
                with (Realm.getInstance(RealmHelper.CONFIG_CHANNELS)) {
                    categories = copyFromRealm(where(Category::class.java).findAll())
                            .map { c -> CategoryExpandable(c) }
                }

                uiThread {
                    channelAdapter = ChannelAdapter(categories!!)
                    with (channel_list) {
                        layoutManager = LinearLayoutManager(this.context)
                        adapter = channelAdapter
                    }
                }
            }

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}