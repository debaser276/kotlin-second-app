package ru.netology.secondapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.edit
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.IOException

class RegistrationActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        btn_registration.setOnClickListener {
            val password = edt_password.text.toString()
            val passwordRepeated = edt_password_repeat.text.toString()
            if (password != passwordRepeated) {
                edt_password.error = resources.getString(R.string.passwords_not_match)
            } else if (!isValid(edt_password.text.toString())) {
                edt_password.error = resources.getString(R.string.password_incorrect)
            } else {
                launch {
                    val dialog = LoadingDialog(this@RegistrationActivity).apply {
                        setTitle(R.string.registration)
                        show()
                    }
                    try {
                        val response = Repository.register(
                            edt_login.text.toString(),
                            edt_password.text.toString()
                        )
                        if (response.isSuccessful) {
                            toast(R.string.success)
                            setUsrAuth(response.body()!!.id, response.body()!!.token)
                            finish()
                        } else {
                            toast(R.string.registration_failed)
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

    private fun setUsrAuth(id: Int, token: String) {
        getSharedPreferences(API_SHARED_FILE, Context.MODE_PRIVATE).edit {
            putString(AUTHENTICATED_SHARED_KEY, token)
            putInt(AUTHENTICATED_ID, id)
        }
    }

    override fun onStop() {
        super.onStop()
        cancel()
    }
}
