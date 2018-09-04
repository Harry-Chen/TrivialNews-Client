package xyz.harrychen.trivialnews.ui.activities

import android.arch.lifecycle.Lifecycle
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.design.chip.Chip
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import kotlinx.android.synthetic.main.activity_news_detail.*
import kotlinx.android.synthetic.main.news_detail_comment.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.sdk25.coroutines.onClick
import xyz.harrychen.trivialnews.R
import xyz.harrychen.trivialnews.support.BAIKE_URI_PREFIX
import xyz.harrychen.trivialnews.support.api.NewsApi
import xyz.harrychen.trivialnews.ui.fragments.WebViewFragment

class NewsDetailActivity : AppCompatActivity(), AnkoLogger {


    private var id: Int = 0
    private lateinit var link: String
    private val netIntent by lazy {
        CustomTabsIntent.Builder()
                .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)
        setupToolbar()

        with (intent.extras!!) {
            id = this["id"] as Int
            link = (this["link"] as String).replace("http://", "https://")
        }

        setupSlidePanel()
        loadNewsPage()
        loadNewsDetails()

    }


    private fun setupSlidePanel() {
        detail_sliding_layout.addPanelSlideListener(object:SlidingUpPanelLayout.PanelSlideListener{
            override fun onPanelSlide(panel: View?,
                                      slideOffset: Float) {
                detail_comment_arrow.rotation = if (slideOffset >= 0.7) 180f
                                                else slideOffset / 0.7f * 180
            }

            override fun onPanelStateChanged(panel: View?,
                                             previousState: SlidingUpPanelLayout.PanelState?,
                                             newState: SlidingUpPanelLayout.PanelState?) {
            }

        })

        detail_sliding_layout.setFadeOnClickListener {
            detail_sliding_layout.panelState =  SlidingUpPanelLayout.PanelState.COLLAPSED
        }
    }


    private fun setupToolbar() {
        setSupportActionBar(detail_toolbar)
        with (supportActionBar!!) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        title = getString(R.string.news_detail_title)
    }


    private fun loadNewsPage() {

        val webFragment = WebViewFragment()
        val bundle = Bundle()
        bundle.putString("link", link)
        webFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
                .replace(R.id.detail_web_fragment, webFragment).commit()

    }


    private fun loadNewsDetails() {
        NewsApi.getNewsDetail(id).bindUntilEvent(this, Lifecycle.Event.ON_STOP)
                .subscribe({
                    detail_favorite_button.isFavorite = it.favorite
                    setKeywords(it.keywords)
                }, {
                    throw(it)
                })
    }


    private fun setKeywords(keywords: List<String>) {
        keywords.forEach {
            val chip = Chip(this)
            chip.text = it
            chip.onClick {
                netIntent.launchUrl(this@NewsDetailActivity,
                        Uri.parse("$BAIKE_URI_PREFIX${chip.text}"))
            }
            detail_keyword_chips.addView(chip)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (detail_sliding_layout.panelState != SlidingUpPanelLayout.PanelState.COLLAPSED) {
            detail_sliding_layout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        } else {
            finish()
        }
    }

}