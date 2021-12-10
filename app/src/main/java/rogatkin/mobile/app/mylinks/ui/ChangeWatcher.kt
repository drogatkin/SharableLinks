package rogatkin.mobile.app.mylinks.ui

import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import rogatkin.mobile.app.mylinks.MainActivity
import rogatkin.mobile.app.mylinks.model.group

class ChangeWatcher(val fragment: Fragment) : TextWatcher {
    var isEmpty = true
    var innerUpdt = false
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (innerUpdt)
            return
        when (s.isNotBlank()) {
            true  -> {
                when (isEmpty) {
                    true -> {
                        fragment.requireActivity().invalidateOptionsMenu()
                        isEmpty = false
                    }
                }
            }
            false -> {
                when (isEmpty) {
                    false -> {
                        val group = group()
                        innerUpdt = true
                        // TODO Replace with version updating only id
                        (fragment.requireActivity() as MainActivity).model.vc.fillView(
                            fragment.requireContext(),
                            fragment.requireActivity(),
                            group
                        )
                        innerUpdt = false
                        fragment.requireActivity().invalidateOptionsMenu()
                        isEmpty = true
                    }
                }
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

    fun reset() { isEmpty = true }
}