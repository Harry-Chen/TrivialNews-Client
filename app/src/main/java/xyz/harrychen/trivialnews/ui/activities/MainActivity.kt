package xyz.harrychen.trivialnews.ui.activities

import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import android.support.design.internal.BottomNavigationItemView
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.jakewharton.rxbinding2.view.clicks
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import org.joda.time.LocalDate
import xyz.harrychen.trivialnews.R
import xyz.harrychen.trivialnews.support.LOCAL_TIME_ZONE
import xyz.harrychen.trivialnews.ui.fragments.timeline.BaseTimelineFragment
import xyz.harrychen.trivialnews.ui.fragments.timeline.FavoriteFragment
import xyz.harrychen.trivialnews.ui.fragments.timeline.MainTimelineFragment
import xyz.harrychen.trivialnews.ui.fragments.timeline.RecommendFragment


class MainActivity : AppCompatActivity(), AnkoLogger {

    private var nowFragmentId = -1
    private var fragments: HashMap<Int, BaseTimelineFragment> = HashMap()

    private fun initNavigation() {
        main_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        findViewById<BottomNavigationItemView>(R.id.navigation_timeline).callOnClick()
    }

    private val createFragment = { id: Int ->
        when (id) {
            R.id.navigation_timeline -> {
                // message.setText(R.string.title_home)
                MainTimelineFragment()
            }
            R.id.navigation_favorite -> {
                // message.setText(R.string.title_dashboard)
                FavoriteFragment()
            }
            R.id.navigation_recommend -> {
                // message.setText(R.string.title_notifications)
                RecommendFragment()
            }
            else -> null
        }
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        if (nowFragmentId == item.itemId) {
            fragments[nowFragmentId]!!.scrollAndRefresh()
        } else {
            var fragment = fragments[item.itemId]
            if (fragment == null) {
                fragment = createFragment(item.itemId)
                fragment!!.setCoordinatorLayout(main_coordinator)
            }
            fragments[item.itemId] = fragment
            supportFragmentManager.beginTransaction().replace(R.id.main_frame, fragment).commit()
        }

        nowFragmentId = item.itemId
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(main_toolbar)

        initNavigation()
        initToolbarAction()
        initFloatingActionBar()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_toolbar, menu)
        main_search.setMenuItem(menu.findItem(R.id.action_search))
        menu.findItem(R.id.action_range).setOnMenuItemClickListener {
            showDateChooserDialog()
            true }
        return true
    }

    override fun onBackPressed() {
        when (main_search.isSearchOpen) {
            true -> main_search.closeSearch()
            false -> super.onBackPressed()
        }
    }

    private val filterDateRange = { _: DatePickerDialog,
                                    year: Int, month: Int, day: Int,
                                    yearEnd: Int, monthEnd: Int, dayEnd: Int ->
        val begin = LocalDate(year, month + 1, day)
                .toDateTimeAtStartOfDay()
        val end = LocalDate(yearEnd, monthEnd + 1, dayEnd)
                .toDateTimeAtStartOfDay()
        if (!end.isBefore(begin)) {
            startActivity<FilteredResultActivity>("type" to "range",
                    "beforeDate" to end, "afterDate" to begin)
        } else {
            alert(R.string.date_range_error){
                isCancelable = false
                yesButton {  }
            }.show()
        }
        Unit
    }

    private fun showDateChooserDialog() {
        val today = LocalDate.now()
        val dialog = DatePickerDialog.newInstance(DatePickerDialog.OnDateSetListener(filterDateRange),
                today.year, today.monthOfYear - 1, today.dayOfMonth)
        dialog.setStartTitle(getString(R.string.date_begin))
        dialog.setEndTitle(getString(R.string.date_end))
        @Suppress("DEPRECATION")
        dialog.show(fragmentManager, "DateRangePicker")
    }

    private fun initToolbarAction() {
        main_search.setOnQueryTextListener(object:MaterialSearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                startActivity<FilteredResultActivity>("type" to "search", "query" to query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

    }

    private fun initFloatingActionBar() {
        main_fab.clicks().bindUntilEvent(this, Lifecycle.Event.ON_DESTROY).subscribe{
            fragments[nowFragmentId]!!.scrollAndRefresh()
        }
    }

}
