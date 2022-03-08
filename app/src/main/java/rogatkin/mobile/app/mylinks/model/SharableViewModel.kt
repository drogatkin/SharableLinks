package rogatkin.mobile.app.mylinks.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharableViewModel : ViewModel() {
    private val line = MutableLiveData<group?>()
    private val dot = MutableLiveData<line>()

    fun getLines(): MutableLiveData<group?> {
        return line
    }

    fun setLines(group: group?) {
        line.value = group
    }

    fun clearLines() = {line.value = null}

    fun getLink(): LiveData<line> {
        return dot
    }

    fun setLink(line: line) {
        dot.value = line
    }
}