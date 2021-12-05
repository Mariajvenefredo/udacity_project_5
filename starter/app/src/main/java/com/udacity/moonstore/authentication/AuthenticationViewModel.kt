package com.udacity.moonstore.authentication

import android.app.Application
import androidx.lifecycle.map
import com.udacity.moonstore.base.BaseViewModel

class AuthenticationViewModel(val app: Application) : BaseViewModel(app) {

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.NOT_AUTHENTICATED
        }
    }
}