package xyz.harrychen.trivialnews.ui.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_search_result.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import xyz.harrychen.trivialnews.R
import xyz.harrychen.trivialnews.support.api.BaseApi
import xyz.harrychen.trivialnews.ui.fragments.timeline.BaseTimelineFragment
import xyz.harrychen.trivialnews.ui.fragments.timeline.RangeTimelineFragment
import xyz.harrychen.trivialnews.ui.fragments.timeline.SearchResultFragment
import xyz.harrychen.trivialnews.ui.fragments.timeline.SingleChannelFragment


class FilteredResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        with(supportActionBar!!) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        lateinit var fragment: BaseTimelineFragment
        val bundle = Bundle()

        with (intent.extras!!) {
            when(this["type"]) {
                "search" -> {
                    val query = this["query"] as String
                    bundle.putString("query", query)
                    title = getString(R.string.search_result_title).format(query)
                    fragment = SearchResultFragment()
                }
                "range" -> {
                    val beforeDate = this["beforeDate"] as DateTime
                    val afterDate = this["afterDate"] as DateTime

                    val formatter = DateTimeFormat.forPattern("yyyy/MM/dd")

                    title = getString(R.string.range_result_title)
                            .format(formatter.print(afterDate), formatter.print(beforeDate))

                    // API format is [startDate, endDate)
                    val realBeforeDate = beforeDate.plusDays(1)

                    bundle.putString("beforeDate", realBeforeDate.toString())
                    bundle.putString("afterDate", afterDate.toString())
                    fragment = RangeTimelineFragment()
                }
                "channel" -> {
                    val name = this["name"] as String
                    val id = this["id"] as Int

                    title = getString(R.string.channel_detail_title).format(name)

                    bundle.putInt("id", id)
                    fragment = SingleChannelFragment()
                }
            }
        }

        fragment.arguments = bundle
        fragment.setCoordinatorLayout(search_coordinator)
        supportFragmentManager.beginTransaction().replace(R.id.frame_search_result, fragment).commit()
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