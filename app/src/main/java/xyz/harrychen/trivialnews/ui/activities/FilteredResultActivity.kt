package xyz.harrychen.trivialnews.ui.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_search_result.*
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import xyz.harrychen.trivialnews.R
import xyz.harrychen.trivialnews.support.api.BaseApi
import xyz.harrychen.trivialnews.ui.fragments.BaseTimelineFragment
import xyz.harrychen.trivialnews.ui.fragments.RangeTimelineFragment
import xyz.harrychen.trivialnews.ui.fragments.SearchResultFragment


class FilteredResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        with(supportActionBar) {
            this?.setDisplayHomeAsUpEnabled(true)
            this?.setDisplayShowHomeEnabled(true)
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

                    val realBeforeDate = beforeDate.plusDays(1) // API format is [startDate, endDate)

                    bundle.putString("beforeDate", BaseApi.dateTimeFormatter.print(realBeforeDate))
                    bundle.putString("afterDate", BaseApi.dateTimeFormatter.print(afterDate))
                    fragment = RangeTimelineFragment()
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