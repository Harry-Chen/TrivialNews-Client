package xyz.harrychen.trivialnews.ui.activities

import android.arch.lifecycle.Lifecycle
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.design.chip.Chip
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.realm.Realm
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.android.synthetic.main.activity_news_detail.*
import kotlinx.android.synthetic.main.news_detail_comment.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.sdk25.coroutines.onClick
import xyz.harrychen.trivialnews.R
import xyz.harrychen.trivialnews.models.Comment
import xyz.harrychen.trivialnews.models.News
import xyz.harrychen.trivialnews.support.BAIKE_URI_PREFIX
import xyz.harrychen.trivialnews.support.adapter.CommentAdapter
import xyz.harrychen.trivialnews.support.api.NewsApi
import xyz.harrychen.trivialnews.support.api.UserApi
import xyz.harrychen.trivialnews.support.utils.RealmHelper
import xyz.harrychen.trivialnews.ui.fragments.WebViewFragment

class NewsDetailActivity : AppCompatActivity(), AnkoLogger {


    private var id: Int = 0
    private lateinit var link: String
    private lateinit var commentAdapter: CommentAdapter


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
                detail_comment_arrow.rotation = if (slideOffset >= 0.5) 180f
                                                else slideOffset / 0.5f * 180
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


        detail_favorite_button.setAnimateFavorite(true)
        detail_favorite_button.setAnimateUnfavorite(true)

        detail_favorite_button.setOnFavoriteChangeListener { _, favorite ->
            when (favorite) {
                true -> {
                    UserApi.addFavoriteNews(listOf(id)).subscribe({
                        snackbar(detail_coordinator, R.string.add_favorite_success)
                        doAsync {
                            var news: News? = null

                            with(Realm.getInstance(RealmHelper.CONFIG_NEWS_TIMELINE)) {
                                news = copyFromRealm(this.where(News::class.java)
                                        .equalTo("id", id).findFirst()!!)
                            }

                            with(Realm.getInstance(RealmHelper.CONFIG_NEWS_FAVIROTE)) {
                                beginTransaction()
                                insertOrUpdate(news!!)
                                commitTransaction()
                            }
                        }
                    }, {
                        snackbar(detail_coordinator, R.string.add_favorite_failed)
                        detail_favorite_button.setFavoriteSuppressListener(false)
                    })

                }
                false -> {
                    UserApi.deleteFavoriteNews(listOf(id)).subscribe({
                        snackbar(detail_coordinator, R.string.delete_favorite_success)
                        doAsync {
                            with (Realm.getInstance(RealmHelper.CONFIG_NEWS_FAVIROTE)) {
                                beginTransaction()
                                where(News::class.java).equalTo("id", id)
                                        .findAll().deleteAllFromRealm()
                                commitTransaction()
                            }
                        }
                    },{
                        snackbar(detail_coordinator, R.string.delete_favorite_failed)
                        detail_favorite_button.setFavoriteSuppressListener(true)
                    })
                }
            }
        }
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
                    detail_favorite_button.setFavoriteSuppressListener(it.favorite)
                    setKeywords(it.keywords)
                    loadComments(it.comments)
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


    private fun loadComments(comments: List<Comment>) {
        detail_comment_title.text = getString(R.string.detail_comment_title).format(comments.size)
        commentAdapter = CommentAdapter(comments.toMutableList())
        with (detail_comment_list) {
            itemAnimator = LandingAnimator()
            layoutManager = LinearLayoutManager(this.context)
            adapter = ScaleInAnimationAdapter(commentAdapter)
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