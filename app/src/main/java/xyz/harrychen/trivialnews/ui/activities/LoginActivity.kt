package xyz.harrychen.trivialnews.ui.activities

import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView
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
import org.jetbrains.anko.uiThread
import xyz.harrychen.trivialnews.R
import xyz.harrychen.trivialnews.models.QueryParameter
import xyz.harrychen.trivialnews.models.User
import xyz.harrychen.trivialnews.support.api.BaseApi
import xyz.harrychen.trivialnews.support.api.UserApi
import xyz.harrychen.trivialnews.support.utils.RealmHelper

class LoginActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createWebView()
        checkToken()
        setTitle(R.string.login_register_title)
        setContentView(R.layout.activity_login)
        initForm()
    }

    private fun createWebView(): WebView {
        return WebView(this)
    }


    private fun checkToken() {
        doAsync {
            var token: User? = null;
            with (Realm.getInstance(RealmHelper.CONFIG_USER)) {
                token = this.where(User::class.java).equalTo("id", 0 as Int).findFirst()
                if (token != null) {
                    token = copyFromRealm(token!!)
                }
            }
            uiThread {
                if (token != null) {
                    toast(R.string.auto_logged_in)
                    setTokenAndStartMain(token!!.token)
                }
            }
        }
    }

    private fun setTokenAndStartMain(token: String) {
        BaseApi.setToken(token)
        startActivity<MainActivity>()
        this.finish()
    }

    private val setButtonState =  { enabled: Boolean ->
        btn_login.isEnabled = enabled
        btn_register.isEnabled = enabled
    }

    private fun initForm() {

        val inputChecker = Observable.combineLatest(input_username.textChanges(), input_password.textChanges(),
                BiFunction<CharSequence, CharSequence, Boolean>{
                    username: CharSequence, password: CharSequence ->
                    val usernameValid = !username.isBlank() && !username.contains(Regex("\\s+"))
                    val passwordValid = !password.isBlank() && !password.contains(Regex("\\s+"))
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

    private fun loginOrRegister(parameter: QueryParameter.Register) {
        UserApi.loginOrRegister(parameter).subscribe({token ->

            doAsync {
                with (Realm.getInstance(RealmHelper.CONFIG_USER)) {
                    beginTransaction()
                    copyToRealm(xyz.harrychen.trivialnews.models.User(username = input_username.text.toString(), token = token.token))
                    commitTransaction()
                }
            }

            toast("${getString(R.string.login_register_success)} ${parameter.username}")
            setTokenAndStartMain(token.token)

        }, { error ->
            toast("${getString(R.string.login_register_failed)} ${error.message}")
            setButtonState(true)
        })
    }
}