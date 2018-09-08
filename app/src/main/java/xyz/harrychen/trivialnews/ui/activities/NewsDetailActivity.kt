package xyz.harrychen.trivialnews.ui.activities

import android.arch.lifecycle.Lifecycle
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.customtabs.CustomTabsIntent
import android.support.design.chip.Chip
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.ShareActionProvider
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.realm.Realm
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.android.synthetic.main.activity_news_detail.*
import kotlinx.android.synthetic.main.news_detail_comment.*
import org.jetbrains.anko.*
import org.jetbrains.anko.design.snackbar
import xyz.harrychen.trivialnews.BuildConfig
import xyz.harrychen.trivialnews.R
import xyz.harrychen.trivialnews.models.Comment
import xyz.harrychen.trivialnews.models.News
import xyz.harrychen.trivialnews.models.User
import xyz.harrychen.trivialnews.support.BAIKE_URI_PREFIX
import xyz.harrychen.trivialnews.support.adapter.CommentAdapter
import xyz.harrychen.trivialnews.support.adapter.NewsItemViewHolder
import xyz.harrychen.trivialnews.support.api.BaseApi
import xyz.harrychen.trivialnews.support.api.CommentApi
import xyz.harrychen.trivialnews.support.api.NewsApi
import xyz.harrychen.trivialnews.support.api.UserApi
import xyz.harrychen.trivialnews.support.utils.*
import xyz.harrychen.trivialnews.ui.fragments.WebViewFragment

class NewsDetailActivity : AppCompatActivity(), AnkoLogger {


    private lateinit var newsToShow: News
    private lateinit var commentAdapter: CommentAdapter
    private var mShareActionProvider: ShareActionProvider? = null

    private var commentsNum: Int = 0
    set(value) {
        field = value
        if (detail_comment_title != null) {
            detail_comment_title.text = getString(R.string.detail_comment_title).format(value)
        }
    }


    private val currentUser by lazy {
        Realm.getInstance(RealmHelper.CONFIG_USER).use {
            it.copyFromRealm(it.where(User::class.java)
                    .equalTo("id", 0 as Int).findFirst()!!)
        }
    }


    private val netIntent by lazy {
        CustomTabsIntent.Builder()
                .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)

        setupToolbar()

        with (intent.extras!!) {
            newsToShow = BaseApi.GSON.fromJson(this["news"] as String, News::class.java)
        }

        setKeywords(newsToShow.keywords)
        setupSlidePanel()
        setupPostComment()
        loadNewsDetails()

    }

    private val widthMeasureSpec by lazy {
        View.MeasureSpec.makeMeasureSpec(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                500f,
                resources.displayMetrics
        ).toInt(), View.MeasureSpec.EXACTLY)
    }

    private var thumbnailGenerated = false
    private lateinit var thumbnailPath: String

    private fun drawView(view: View) {

        view.measure(widthMeasureSpec, 0)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        val bitmap = Bitmap.createBitmap(view.measuredWidth,
                view.measuredHeight, Bitmap.Config.ARGB_8888)
        val background = view.background
        Canvas(bitmap).apply {
            background?.draw(this) ?: this.drawColor(Color.WHITE)
            view.draw(this)
        }
        thumbnailPath = MediaStore.Images.Media.insertImage(this@NewsDetailActivity.contentResolver,
                bitmap, newsToShow.title, newsToShow.summary)
        thumbnailGenerated = true
    }

    private fun generateThumbNail() {

        val view = LayoutInflater.from(this).inflate(R.layout.news_list_item, null)
        val holder = NewsItemViewHolder(view)
        view.measure(widthMeasureSpec, 0)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        if (newsToShow.picture.isNotBlank()) {
            holder.bind(newsToShow, object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    doAsync { drawView(view) }
                    return false
                }
            }, true)
        } else {
            holder.bind(newsToShow, null, true)
            doAsync { drawView(view) }
        }
    }

    private fun getPictureUri(url: String): Uri {
        val result = doAsyncResult {
            Glide.with(this@NewsDetailActivity).downloadOnly().load(url)
                    .submit().get()
        }

        return FileProvider.getUriForFile(this,
                BuildConfig.APPLICATION_ID + ".glide", result.get())
    }


    private fun setShareIntent() {

        if (PermissionUtils.PERMISSION_STORAGE) {
            generateThumbNail()
        }

        doAsync {

            val shareIntent = Intent(Intent.ACTION_SEND)

            shareIntent.putExtra(Intent.EXTRA_SUBJECT, newsToShow.title)
            shareIntent.putExtra(Intent.EXTRA_TITLE, newsToShow.title)

            val shareContent = getString(R.string.share_format)
                    .format(newsToShow.title, newsToShow.summary,
                            newsToShow.publishDate.toReadableDateTimeString(),
                            newsToShow.author, newsToShow.link)

            shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent)
            // System SMS
            shareIntent.putExtra("sms_body", shareContent)
            // WeChat < 6.6.7
            shareIntent.putExtra("Kdescription", shareContent)


            if (PermissionUtils.PERMISSION_STORAGE) {
                while (!thumbnailGenerated) {}
                shareIntent.type = "image/jpg,text/plain"
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(thumbnailPath))
            } else {
                snackbar(detail_coordinator, R.string.detail_no_custom_sharing)
                when (NetworkUtils.isConnected(this@NewsDetailActivity)
                        && newsToShow.picture.isNotBlank()) {
                    false -> shareIntent.type = "text/plain"
                    true -> {
                        shareIntent.type = "image/jpg,text/plain"
                        shareIntent.putExtra(Intent.EXTRA_STREAM, getPictureUri(newsToShow.picture))
                    }
                }
            }

            uiThread {
                mShareActionProvider?.setShareIntent(shareIntent)
                invalidateOptionsMenu()
            }

        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.detail_toolbar, menu)

        menu.findItem(R.id.detail_menu_share).also {
            mShareActionProvider = MenuItemCompat.getActionProvider(it) as ShareActionProvider
        }

        return true
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

        handleFavoriteChange()
    }
    


    private fun handleFavoriteChange() {

        detail_favorite_button.setAnimateFavorite(true)
        detail_favorite_button.setAnimateUnfavorite(true)

        detail_favorite_button.setOnFavoriteChangeListener { _, favorite ->
            when (favorite) {
                true -> {
                    UserApi.addFavoriteNews(listOf(newsToShow.id)).subscribe({
                        snackbar(detail_coordinator, R.string.add_favorite_success)
                        doAsync {
                            var news: News? = null

                            Realm.getInstance(RealmHelper.CONFIG_NEWS_TIMELINE).use {
                                news = it.copyFromRealm(it.where(News::class.java)
                                        .equalTo("id", newsToShow.id).findFirst()!!)
                            }

                            Realm.getInstance(RealmHelper.CONFIG_NEWS_FAVORITE).use {
                                it.beginTransaction()
                                it.insertOrUpdate(news!!)
                                it.commitTransaction()
                            }
                        }
                    }, {
                        snackbar(detail_coordinator, R.string.add_favorite_failed)
                        detail_favorite_button.setFavoriteSuppressListener(false)
                    })

                }
                false -> {
                    UserApi.deleteFavoriteNews(listOf(newsToShow.id)).subscribe({
                        snackbar(detail_coordinator, R.string.delete_favorite_success)
                        doAsync {
                            Realm.getInstance(RealmHelper.CONFIG_NEWS_FAVORITE).use {
                                it.beginTransaction()
                                it.where(News::class.java).equalTo("id", newsToShow.id)
                                        .findAll().deleteAllFromRealm()
                                it.commitTransaction()
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

    private fun setupPostComment() {
        comment_input.textChanges().bindUntilEvent(this, Lifecycle.Event.ON_DESTROY)
                .subscribe{ comment_submit.isEnabled = it.isNotBlank() }

        comment_submit.clicks().bindUntilEvent(this, Lifecycle.Event.ON_DESTROY).subscribe{
            CommentApi.addComment(newsToShow.id, comment_input.text.toString().trim())
                    .subscribe({
                        commentsNum++
                        commentAdapter.addItem(it)
                        comment_input.text.clear()
                        snackbar(detail_comment_layout, R.string.comment_add_success)
                    }, {
                        snackbar(detail_comment_layout, R.string.comment_add_failed)
                    })
        }
    }


    private fun loadNewsPage(showSnack: Boolean) {

        val webFragment = WebViewFragment()
        val bundle = Bundle()
        bundle.putString("link", newsToShow.link)
        webFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
                .replace(R.id.detail_web_fragment, webFragment).commit()

        if (showSnack) {
            snackbar(detail_coordinator, R.string.external_website_warning)
        }

    }


    private fun markCachedNewsAsRead() {
        doAsync {
            Realm.getInstance(RealmHelper.CONFIG_NEWS_TIMELINE).use {
                it.beginTransaction()
                it.where(News::class.java).equalTo("id", newsToShow.id)
                        .findFirst()!!.hasRead = true
                it.commitTransaction()
            }
        }
    }


    private fun loadNewsDetails() {
        NewsApi.getNewsDetail(newsToShow.id).bindUntilEvent(this, Lifecycle.Event.ON_STOP)
                .subscribe({
                    detail_favorite_button.setFavoriteSuppressListener(it.favorite)
                    loadComments(it.comments)
                    setOnSwipeHandler()
                    markCachedNewsAsRead()
                    setShareIntent()
                    loadNewsPage(true)
                }, {
                    snackbar(detail_coordinator, R.string.detail_load_failed)
                    loadComments(listOf())
                    setShareIntent()
                    loadNewsPage(false)
                })
    }


    private fun setKeywords(keywords: List<String>) {
        keywords.forEach {
            val chip = Chip(this)
            chip.text = it
            chip.setOnClickListener {
                try {
                    netIntent.launchUrl(this@NewsDetailActivity,
                            Uri.parse("$BAIKE_URI_PREFIX${chip.text}"))
                } catch (e: ActivityNotFoundException) {
                    snackbar(detail_coordinator, R.string.detail_intent_activity_not_found)
                }
            }
            detail_keyword_chips.addView(chip)
        }
    }


    private fun setOnSwipeHandler() {

        val swipeHandler = object:SwipeToDeleteCallback(this, {
            commentAdapter.getItem(it).username == currentUser.username
        }) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val comment = commentAdapter.getItem(viewHolder.adapterPosition)
                alert(R.string.comment_delete_confirm) {
                    isCancelable = false
                    yesButton {
                        CommentApi.deleteComment(comment.id).subscribe({
                            commentsNum--
                            commentAdapter.deleteItem(viewHolder.adapterPosition)
                            snackbar(detail_coordinator, R.string.comment_delete_success)
                        }, {
                            commentAdapter.notifyDataSetChanged()
                            snackbar(detail_coordinator, R.string.comment_delete_failed)
                        })
                    }
                    noButton {
                        commentAdapter.notifyDataSetChanged()
                    }
                }.show()
            }
        }

        ItemTouchHelper(swipeHandler).attachToRecyclerView(detail_comment_list)
    }


    private fun loadComments(comments: List<Comment>) {
        commentsNum= comments.size

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