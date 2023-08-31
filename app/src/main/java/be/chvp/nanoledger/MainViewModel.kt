package be.chvp.nanoledger

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val preferencesDataSource: PreferencesDataSource,
    private val ledgerRepository: LedgerRepository
) : AndroidViewModel(application) {
    private val _isRefreshing = MutableLiveData<Boolean>(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    val fileUri = preferencesDataSource.fileUri
    val fileContents = ledgerRepository.fileContents

    fun refresh() {
        _isRefreshing.value = true
        viewModelScope.launch(IO) {
            fileUri.value?.let { ledgerRepository.readFrom(it) { _isRefreshing.postValue(false) } }
        }
    }
}
