package rogatkin.mobile.app.mylinks.ui.line

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import rogatkin.mobile.app.mylinks.model.group
import rogatkin.mobile.app.mylinks.model.line

class LineViewModel : ViewModel() {
    private val line = MutableLiveData<group>()
    private val dot = MutableLiveData<line>()

    fun getLines(): LiveData<group> {
        return line
    }

    fun setLines(group: group) {
        line.value = group
    }

    fun getLink(): LiveData<line> {
        return dot
    }

    fun setLink(line: line) {
        dot.value = line
    }

}