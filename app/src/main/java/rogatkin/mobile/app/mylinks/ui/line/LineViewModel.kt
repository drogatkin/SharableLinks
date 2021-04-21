package rogatkin.mobile.app.mylinks.ui.line

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import rogatkin.mobile.app.mylinks.model.line

class LineViewModel : ViewModel() {
    private val lines : MutableLiveData<List<line>> by lazy {
        MutableLiveData<List<line>>().also {
            loadLines()
        }
    }

    fun getLines(): LiveData<List<line>> {
        return lines
    }

    private fun loadLines() {
        // Do an asynchronous operation to fetch groups.
    }
}