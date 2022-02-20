package rogatkin.mobile.app.mylinks.ui.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import rogatkin.mobile.app.mylinks.model.Model
import rogatkin.mobile.app.mylinks.model.group

class GroupViewModel(val model: Model) : ViewModel() {

    private val groups: MutableLiveData<ArrayList<group>> by lazy {
        MutableLiveData<ArrayList<group>>().also {
            it.value = model.load(null, group::class.java, null)
        }
    }

    fun getGroups(): LiveData<ArrayList<group>> {
        return groups
    }

    fun setGroups(upGroups: ArrayList<group>) {
        groups.value = upGroups
    }

}

class ViewModelModelFactory(val model: Model) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Model::class.java).newInstance(model)
    }

}