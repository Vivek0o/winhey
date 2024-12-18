package com.avfusionapps.winhey.ui.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.avfusionapps.winhey.data.models.Constants
import com.avfusionapps.winhey.utils.WinHeyUtil

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {
    fun checkInternetAndPerformAction(action: () -> Unit, failureAction: () -> Unit) {
        if (WinHeyUtil.isInternetAvailable(getApplication())) {
            action()
        } else {
            failureAction()
            Toast.makeText(getApplication(), Constants.NO_INTERNET_ERROR, Toast.LENGTH_LONG).show()
        }
    }
}