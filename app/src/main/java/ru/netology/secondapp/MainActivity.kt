package ru.netology.secondapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.edit
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.IOException

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (isAuthenticated()) {
            val token = getSharedPreferences(API_SHARED_FILE, Context.MODE_PRIVATE).getString(
                AUTHENTICATED_SHARED_KEY, "")
            Repository.createRetrofitWithAuthToken(token!!)
            val feedActivityIntent = Intent(this@MainActivity, FeedActivity::class.java)
            startActivity(feedActivityIntent)
            finish()
        } else {
            btn_login.setOnClickListener {
                if (!isValid(edt_password.text.toString())) {
                    edt_password.error = resources.getString(R.string.password_incorrect)
                } else {
                    launch {
                        val dialog = LoadingDialog(this@MainActivity).apply {
                            setTitle(getString(R.string.authentication))
                            show()
                        }
                        try {
                            val response = Repository.authenticate(
                                edt_login.text.toString(),
                                edt_password.text.toString()
                            )
                            if (response.isSuccessful) {
                                toast(R.string.success)
                                setUsrAuth(response.body()!!.id, response.body()!!.token)
                                Repository.createRetrofitWithAuthToken(response.body()!!.token)
                                val feedActivityIntent =
                                    Intent(this@MainActivity, FeedActivity::class.java)
                                startActivity(feedActivityIntent)
                                finish()
                            } else {
                                toast(R.string.authentication_failed)
                            }
                        } catch (e: IOException) {
                            toast(R.string.error_occured)
                        } finally {
                            dialog.dismiss()
                        }
                    }
                }
            }
        }


        btn_registration.setOnClickListener {
            val registrationIntent = Intent(this, RegistrationActivity::class.java)
            startActivity(registrationIntent)
        }
    }

    private fun isAuthenticated() = getSharedPreferences(API_SHARED_FILE, Context.MODE_PRIVATE)
        .getString(AUTHENTICATED_SHARED_KEY, "")?.isNotEmpty() ?: false

    private fun setUsrAuth(id: Int, token: String) {
        getSharedPreferences(API_SHARED_FILE, Context.MODE_PRIVATE).edit {
            putString(AUTHENTICATED_SHARED_KEY, token)
            putInt(AUTHENTICATED_ID, id)
        }
    }

    override fun onStart() {
        super.onStart()
        if (isAuthenticated()) {
            val token = getSharedPreferences(API_SHARED_FILE, Context.MODE_PRIVATE)
                .getString(AUTHENTICATED_SHARED_KEY, "")
            Repository.createRetrofitWithAuthToken(token!!)
            startActivity(Intent(this, FeedActivity::class.java))
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        cancel()
    }
}
