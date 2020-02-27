package ru.netology.secondapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private var dialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (isAuthenticated()) {
            val feedActivityIntent = Intent(this@MainActivity, FeedActivity::class.java)
            startActivity(feedActivityIntent)
            finish()
        } else {
            btn_login.setOnClickListener {
                if (!isValid(edt_password.text.toString())) {
                    edt_password.error = "Password is incorrect"
                } else {
                    launch {
                        dialog = LoadingDialog(
                            this@MainActivity,
                            resources.getString(R.string.authentication)).apply { show() }
                        val response = Repository.authenticate(
                            edt_login.text.toString(),
                            edt_password.text.toString())
                        dialog?.dismiss()
                        if (response.isSuccessful) {
                            toast(R.string.success)
                            setUsrAuth(response.body()!!.token)
                            val feedActivityIntent = Intent(this@MainActivity, FeedActivity::class.java)
                            startActivity(feedActivityIntent)
                            finish()
                        } else {
                            toast(R.string.authentication_failed)
                        }
                    }
                }
            }
        }


        btn_registration.setOnClickListener {
            val registrationIntent = Intent(this, RegistrationActivity::class.java)
            startActivity(registrationIntent)
            finish()
        }
    }

    private fun isAuthenticated() = getSharedPreferences(API_SHARED_file, Context.MODE_PRIVATE)
        .getString(AUTHENTICATED_SHARED_KEY, "")?.isNotEmpty() ?: false

    private fun setUsrAuth(token: String) =
        getSharedPreferences(API_SHARED_file, Context.MODE_PRIVATE)
            .edit()
            .putString(AUTHENTICATED_SHARED_KEY, token)
            .apply()

    override fun onStop() {
        super.onStop()
        cancel()
        dialog?.dismiss()
    }
}
