package xyz.harrychen.trivialnews.ui.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import xyz.harrychen.trivialnews.R
import xyz.harrychen.trivialnews.ui.fragments.SearchResultFragment


class SearchResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        with(supportActionBar) {
            this?.setDisplayHomeAsUpEnabled(true)
            this?.setDisplayShowHomeEnabled(true)
        }

        val query = intent.extras!!["query"] as String

        title = getText(R.string.search_result_title).toString().format(query)

        val fragment = SearchResultFragment()
        val bundle = Bundle()

        bundle.putString("query", query)
        fragment.arguments = bundle
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