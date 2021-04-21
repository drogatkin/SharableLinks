package rogatkin.mobile.app.mylinks.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import rogatkin.mobile.app.mylinks.model.line

class DotViewModel : ViewModel() {

    private val _dot = MutableLiveData<line>().apply {
        value = line()
    }
    val dot: LiveData<line> = _dot
}