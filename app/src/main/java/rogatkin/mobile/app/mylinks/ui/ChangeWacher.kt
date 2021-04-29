package rogatkin.mobile.app.mylinks.ui

import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment

class ChangeWacher(val fragment: Fragment) : TextWatcher {
    var prevState = false
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        when (s.isNotBlank()) {
            true and !prevState -> {
                fragment.requireActivity().invalidateOptionsMenu()
                prevState = true
            }
            false and prevState -> {
                fragment.requireActivity().invalidateOptionsMenu()
                prevState = false
            }
        }

    }

    override fun beforeTextChanged(
        s: CharSequence, start: Int, count: Int,
        after: Int
    ) {
    }

    override fun afterTextChanged(s: Editable) {
    }
}