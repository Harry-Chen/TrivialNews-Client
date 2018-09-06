package xyz.harrychen.trivialnews.ui.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_channel.*
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import xyz.harrychen.trivialnews.R
import xyz.harrychen.trivialnews.models.Category
import xyz.harrychen.trivialnews.models.CategoryExpandable
import xyz.harrychen.trivialnews.models.Channel
import xyz.harrychen.trivialnews.models.User
import xyz.harrychen.trivialnews.support.adapter.ChannelAdapter
import xyz.harrychen.trivialnews.support.api.ChannelApi
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
        snackbar(channel_coordinator, R.string.click_channel_summary_hint)

    }


    private fun setupChannelAdapter(categories: List<CategoryExpandable>) {
        channelAdapter = ChannelAdapter(categories) {
            startActivity<FilteredResultActivity>("type" to "channel",
                    "name" to it.name, "id" to it.id)
        }

        with (channel_list) {
            layoutManager = LinearLayoutManager(this.context)
            adapter = channelAdapter
        }


        var subscription: List<Int>? = null
        with (Realm.getInstance(RealmHelper.CONFIG_USER)) {
            subscription = copyFromRealm(where(User::class.java).findFirst()!!).subscription
        }
        channelAdapter.setSubscription(subscription!!)


        channelAdapter.setChildClickListener { _, checked, group, childIndex ->
            val channelId = (group.items[childIndex] as Channel).id
            val operation = when (checked) {
                true -> ChannelApi.subscribeChannels(listOf(channelId))
                false -> ChannelApi.unsubscribeChannels(listOf(channelId))
            }

            operation.subscribe({
                doAsync {

                    var nowSubscription: List<Int>? = null

                    with(Realm.getInstance(RealmHelper.CONFIG_USER)) {
                        beginTransaction()
                        val user = where(User::class.java).findFirst()!!
                        val userSubscription = user.subscription
                        when (checked) {
                            true -> if (channelId !in userSubscription)
                                userSubscription.add(channelId)
                            false -> userSubscription.remove(channelId)
                        }
                        nowSubscription = copyFromRealm(user).subscription
                        commitTransaction()
                    }

                    uiThread {
                        channelAdapter.setSubscription(nowSubscription!!)
                    }
                }
                snackbar(channel_coordinator, R.string.toggle_subscription_status_success)
            }, {

                snackbar(channel_coordinator, R.string.toggle_subscription_status_failed)
                channelAdapter.notifyDataSetChanged()
            })

        }
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
                    setupChannelAdapter(categories!!)
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