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
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import xyz.harrychen.trivialnews.R
import xyz.harrychen.trivialnews.models.QueryParameter
import xyz.harrychen.trivialnews.models.User
import xyz.harrychen.trivialnews.support.api.BaseApi
import xyz.harrychen.trivialnews.support.api.UserApi
import xyz.harrychen.trivialnews.support.utils.RealmHelper

class LoginActivity: AppCompatActivity() {

    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getInstance(RealmHelper.CONFIG_USER)
        checkToken()
        setTitle(R.string.login_register_title)
        setContentView(R.layout.activity_login)
    }

    override fun onStart() {
        super.onStart()
        initForm()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    private fun checkToken() {
        val token = realm.where(User::class.java).equalTo("id", 0 as Int).findFirst()
        if (token != null) {
            toast(R.string.auto_logged_in)
            setTokenAndStartMain(token.token)
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
                }).bindUntilEvent(this, Lifecycle.Event.ON_STOP)
        inputChecker.subscribe(setButtonState)

        btn_register.clicks().bindUntilEvent(this, Lifecycle.Event.ON_STOP).subscribe{
            setButtonState(false)
            loginOrRegister(QueryParameter.Register(input_username.text.toString(),
                    input_password.text.toString(), true))
        }

        btn_login.clicks().bindUntilEvent(this, Lifecycle.Event.ON_STOP).subscribe{
            setButtonState(false)
            loginOrRegister(QueryParameter.Register(input_username.text.toString(),
                    input_password.text.toString(), false))
        }

    }

    private fun loginOrRegister(parameter: QueryParameter.Register) {
        UserApi.loginOrRegister(parameter).subscribe({token ->
            realm.beginTransaction()
            realm.copyToRealm(User(username=input_username.text.toString(), token=token.token))
            realm.commitTransaction()
            toast("${getString(R.string.login_register_success)} ${parameter.username}")
            setTokenAndStartMain(token.token)
        }, { error ->
            toast("${getString(R.string.login_register_failed)} ${error.message}")
            setButtonState(true)
        })
    }
}