package xyz.harrychen.trivialnews.ui.activities

import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import xyz.harrychen.trivialnews.R
import xyz.harrychen.trivialnews.models.Category
import xyz.harrychen.trivialnews.models.QueryParameter
import xyz.harrychen.trivialnews.models.User
import xyz.harrychen.trivialnews.support.api.BaseApi
import xyz.harrychen.trivialnews.support.api.ChannelApi
import xyz.harrychen.trivialnews.support.api.UserApi
import xyz.harrychen.trivialnews.support.utils.ChannelLookup
import xyz.harrychen.trivialnews.support.utils.RealmHelper

class LoginActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkToken()
        setTitle(R.string.login_register_title)
        setContentView(R.layout.activity_login)
        initForm()
    }


    private fun checkToken() {

        var user: User?
        with (Realm.getInstance(RealmHelper.CONFIG_USER)) {
            user = this.where(User::class.java).equalTo("id", 0 as Int).findFirst()
            if (user != null) {
                toast(R.string.auto_logged_in)
                setTokenAndStartMain(user!!.token)
            } else {
                fetchChannels()
            }
        }
    }

    private fun setTokenAndStartMain(token: String) {
        BaseApi.setToken(token)
        initChannelList()
        startActivity<MainActivity>()
        this.finish()
    }

    private val setButtonState =  { enabled: Boolean ->
        btn_login.isEnabled = enabled
        btn_register.isEnabled = enabled
    }

    private fun initForm() {

        val inputChecker = Observable.combineLatest(input_username.textChanges(),
                input_password.textChanges(),
                BiFunction<CharSequence, CharSequence, Boolean>{
                    username: CharSequence, password: CharSequence ->
                    val usernameValid = !username.isBlank() &&
                            !username.contains(Regex("\\s+"))
                    val passwordValid = !password.isBlank() &&
                            !password.contains(Regex("\\s+"))
                    usernameValid && passwordValid
                }).bindUntilEvent(this, Lifecycle.Event.ON_DESTROY)
        inputChecker.subscribe(setButtonState)

        btn_register.clicks().bindUntilEvent(this, Lifecycle.Event.ON_DESTROY).subscribe{
            setButtonState(false)
            loginOrRegister(QueryParameter.Register(input_username.text.toString(),
                    input_password.text.toString(), true))
        }

        btn_login.clicks().bindUntilEvent(this, Lifecycle.Event.ON_DESTROY).subscribe{
            setButtonState(false)
            loginOrRegister(QueryParameter.Register(input_username.text.toString(),
                    input_password.text.toString(), false))
        }

    }



    private fun fetchChannels() {
        ChannelApi.getChannelList().subscribe({

            with (Realm.getInstance(RealmHelper.CONFIG_CHANNELS)) {
                beginTransaction()
                deleteAll()
                copyToRealm(it)
                commitTransaction()
            }

            toast(R.string.login_fetch_channel_success)
        }, {
            toast(R.string.login_fetch_channel_failed)
        })
    }


    private fun initChannelList() {
        with (Realm.getInstance(RealmHelper.CONFIG_CHANNELS)) {
            ChannelLookup.updateChannelInfo(where(Category::class.java).findAll())
        }
    }


    private fun loginOrRegister(parameter: QueryParameter.Register) {
        UserApi.loginOrRegister(parameter).subscribe({user ->

            doAsync {
                with (Realm.getInstance(RealmHelper.CONFIG_USER)) {
                    beginTransaction()
                    copyToRealm(user)
                    commitTransaction()
                }
            }

            toast("${getString(R.string.login_register_success)} ${user.username}")
            setTokenAndStartMain(user.token)

        }, { error ->
            toast("${getString(R.string.login_register_failed)} ${error.message}")
            setButtonState(true)
        })
    }
}