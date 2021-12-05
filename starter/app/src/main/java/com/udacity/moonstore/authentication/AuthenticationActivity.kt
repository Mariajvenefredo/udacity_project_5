package com.udacity.moonstore.authentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.udacity.moonstore.R
import com.udacity.moonstore.locationreminders.RemindersActivity

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    private val viewModel by viewModels<AuthenticationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.authenticationState.observe(this, Observer { authState ->
            if (authState.equals(AuthenticationState.AUTHENTICATED)) {
                startRemindersActivity()
                finish()
            }
        })

        setContentView(R.layout.activity_authentication)

    }

    private fun startRemindersActivity() {
        val intent = Intent(this, RemindersActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    companion object {
        const val SIGN_IN_RESULT_CODE = 11
    }
}
