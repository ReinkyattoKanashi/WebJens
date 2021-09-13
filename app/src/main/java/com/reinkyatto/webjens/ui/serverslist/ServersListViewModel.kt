package com.reinkyatto.webjens.ui.serverslist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.reinkyatto.webjens.arch.BaseViewModel
import com.reinkyatto.webjens.arch.SingleLiveEvent
import com.reinkyatto.webjens.db.local.DataBase
import com.reinkyatto.webjens.db.local.tables.serverlist.Server
import com.reinkyatto.webjens.remote.model.serverslist.ServerInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class ServersListViewModel(private val dataBase: DataBase) : BaseViewModel() {

    private val _navigationEvent = SingleLiveEvent<NavDirections>()
    val navigationEvent: LiveData<NavDirections> = _navigationEvent

    private val _dataSuccess = MutableLiveData<List<Server>>()
    val dataSuccess: LiveData<List<Server>> = _dataSuccess

    private val _dataFailed = MutableLiveData<String>()
    val dataFailed: LiveData<String> = _dataFailed

    private val _noInternetConnection = MutableLiveData<String>()
    val noInternetConnection: LiveData<String> = _noInternetConnection

    private val _loadIndicatorVisibility = MutableLiveData<Boolean>(true)
    val loadIndicatorVisibility: LiveData<Boolean> = _loadIndicatorVisibility


    fun onLogOutClick() {
        sharedPrefs.saveToken("")
        _navigationEvent.value =
            ServersListFragmentDirections.actionServersListFragmentToSplashFragment()
    }


    fun getDataFromDB() {
        viewModelScope.launch(Dispatchers.IO) {
            _dataSuccess.postValue(dataBase.serverListDao.getAllServers())
        }
    }

    private var attemptsCounter = 0

    fun getDataFromAPI() {
        viewModelScope.launch(Dispatchers.IO) {
//            try {
            val response = api.getServersList(sharedPrefs.getToken())
                .awaitResponse()
            Log.i("Retrofit", "Response() -> ${response.isSuccessful}")
            if (response.isSuccessful) {
                val data = response.body()
                Log.i("Retrofit", "Data.status -> ${data?.status}")
                when (data?.status) {
                    1 -> {
                        val servers = data.servers
                        Log.i("Retrofit", "Start let for status = 1")
                        servers?.let {
                            convertDataAndSend(it)
                            Log.i("Retrofit", "data success")
                        }
                        Log.i("Retrofit", "End of let for status = 1")
                    }
                    0 -> {
                        val message = data.message
                        Log.i("Retrofit", "Start let for status = 0")
                        message?.let {
                            _dataFailed.postValue(it)
                            Log.i("Retrofit", "data = 0, fail")
                        }
                        Log.i("Retrofit", "End of let for status = 0")
                    }
                }
            }else{
                if(attemptsCounter == 3){
                    attemptsCounter = 0
                    _dataFailed.postValue("wait&refresh")
                }else{
                    attemptsCounter++
                    _dataFailed.postValue("refresh")
                }

            }
//            } catch (e: Exception) {
//                _noInternetConnection.postValue("Походу инет капут")
//            }
        }
    }

    private fun convertDataAndSend(list: List<ServerInfo>) {
        val newList = mutableListOf<Server>()
        list.forEach { server ->
            newList.add(server.toServerDBModel())
        }
        _dataSuccess.postValue(newList)
        _loadIndicatorVisibility.postValue(false)
        saveToDB(newList)
    }

    private fun saveToDB(newList: MutableList<Server>) {
        dataBase.serverListDao.removeAllData()
        newList.forEach { server ->
            dataBase.serverListDao.insert(server.toDB())
        }
        //}
    }

    fun setDefaultValuesForLivaData() {
        _loadIndicatorVisibility.value = true
        _dataFailed.value = "none"
    }

}