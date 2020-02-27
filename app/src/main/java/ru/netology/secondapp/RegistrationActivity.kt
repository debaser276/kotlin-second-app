package ru.netology.secondapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class RegistrationActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private var dialog: LoadingDialog? = null

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
                    dialog = LoadingDialog(
                        this@RegistrationActivity,
                        resources.getString(R.string.registration)).apply { show() }
                    val response = Repository.register(
                        edt_login.text.toString(),
                        edt_password.text.toString())
                    dialog?.dismiss()
                    if (response.isSuccessful) {
                        toast(R.string.success)
                        setUsrAuth(response.body()!!.token)
                        startActivity(Intent(this@RegistrationActivity, MainActivity::class.java))
                        finish()
                    } else {
                        toast(R.string.registration_failed)
                    }
                }
            }
        }
    }

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
