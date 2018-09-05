package xyz.harrychen.trivialnews.ui.activities

import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import android.support.design.internal.BottomNavigationItemView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.jakewharton.rxbinding2.view.clicks
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_content.*
import org.jetbrains.anko.*
import org.joda.time.LocalDate
import xyz.harrychen.trivialnews.R
import xyz.harrychen.trivialnews.support.utils.RealmHelper
import xyz.harrychen.trivialnews.ui.fragments.timeline.BaseTimelineFragment
import xyz.harrychen.trivialnews.ui.fragments.timeline.FavoriteFragment
import xyz.harrychen.trivialnews.ui.fragments.timeline.MainTimelineFragment
import xyz.harrychen.trivialnews.ui.fragments.timeline.RecommendFragment


class MainActivity : AppCompatActivity(), AnkoLogger {

    private var nowFragmentId = -1
    private var fragments: HashMap<Int, BaseTimelineFragment> = HashMap()
    private var menu: Menu? = null

    private fun initNavigation() {

        main_navigation.setOnNavigationItemSelectedListener {
            if (it.itemId != nowFragmentId) {
                switchToFragment(it.itemId)
            } else {
                fragments[it.itemId]!!.scrollAndRefresh()

            }
            nowFragmentId = it.itemId
            true
        }

        findViewById<BottomNavigationItemView>(R.id.navigation_timeline).callOnClick()

    }


    private fun switchToFragment(id: Int) {
        var fragment = fragments[id]
        if (fragment == null) {
            fragment = createFragment(id)
            fragment!!.setCoordinatorLayout(main_coordinator)
        }
        fragments[id] = fragment
        supportFragmentManager.beginTransaction().replace(R.id.main_frame, fragment).commit()
    }


    private fun initDrawer() {

        val toggle = ActionBarDrawerToggle(this,
                main_drawer, main_toolbar, R.string.main_drawer_open, R.string.main_drawer_close)
        main_drawer.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled =true
        toggle.syncState()

        main_drawer_navigation.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.drawer_manage_subscription -> {

                }
                R.id.drawer_logout -> {
                    RealmHelper.cleanUserData()
                    startActivity<LoginActivity>()
                    finish()
                }
            }
            true
        }

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(main_toolbar)

        initNavigation()
        initToolbarAction()
        initDrawer()
        initFloatingActionBar()

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_toolbar, menu)
        this.menu = menu
        main_search.setMenuItem(menu.findItem(R.id.action_search))
        menu.findItem(R.id.action_range).setOnMenuItemClickListener {
            showDateChooserDialog()
            true }
        return true
    }


    override fun onBackPressed() {

        when {
            main_drawer.isDrawerOpen(GravityCompat.START) ->
                main_drawer.closeDrawer(GravityCompat.START)
            main_search.isSearchOpen -> main_search.closeSearch()
            else -> super.onBackPressed()
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
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }


    private fun initFloatingActionBar() {
        main_fab.clicks().bindUntilEvent(this, Lifecycle.Event.ON_DESTROY).subscribe{
            fragments[nowFragmentId]!!.scrollAndRefresh()
        }
    }

}
