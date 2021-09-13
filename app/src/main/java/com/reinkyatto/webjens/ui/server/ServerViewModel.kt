package com.reinkyatto.webjens.ui.server

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.reinkyatto.webjens.arch.BaseViewModel
import com.reinkyatto.webjens.arch.SingleLiveEvent
import com.reinkyatto.webjens.db.local.DataBase
import com.reinkyatto.webjens.db.local.tables.serverlist.Server
import com.reinkyatto.webjens.remote.model.server.ServerData
import com.reinkyatto.webjens.utils.DataLoad
import com.reinkyatto.webjens.utils.ServerAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class ServerViewModel(val dataBase: DataBase) : BaseViewModel() {

    private var attemptsCounter = 0

    private val _navigationEvent = SingleLiveEvent<NavDirections>()
    val navigationEvent: LiveData<NavDirections> = _navigationEvent

    private val _dataSuccess = MutableLiveData<Server>()
    val dataSuccess: LiveData<Server> = _dataSuccess

    private val _actionAnswer = MutableLiveData<Server>()
    val actionAnswer: LiveData<Server> = _actionAnswer

    private val _dataFailed = MutableLiveData<DataLoad>()
    val dataFailed: LiveData<DataLoad> = _dataFailed

    private val _actionSuccess = MutableLiveData<String?>()
    val actionSuccess: LiveData<String?> = _actionSuccess

    private val _actionFailed = MutableLiveData<String?>()
    val actionFailed: LiveData<String?> = _actionFailed

    private val _noInternetConnection = MutableLiveData<String>()
    val noInternetConnection: LiveData<String> = _noInternetConnection

    private val _loadIndicatorVisibility = MutableLiveData<Boolean>()
    val loadIndicatorVisibility: LiveData<Boolean> = _loadIndicatorVisibility

    private val _actualDataLoaded = MutableLiveData(false)
    val actualDataLoaded: LiveData<Boolean> = _actualDataLoaded

    private val _blockUIButtons = MutableLiveData<Boolean>()
    val blockUIButtons: LiveData<Boolean> = _blockUIButtons

    private val _updateData = MutableLiveData<Boolean>()
    val updateData: LiveData<Boolean> = _updateData



    private lateinit var _id: String
    private var _status: Int = 0

    fun setServerId(id: String){
        _id = id
    }

    fun onPowerButtonClick(){
        // todo узнать включен сервер или выключен чтобы отправлять нужную команду
        when(_status){
            2->{
                sendAction(_id,ServerAction.STOP)
            }//online -> off
            1->{
                sendAction(_id,ServerAction.START)
            }//offline -> on
            else->{
                // todo callback "Хуйня, чет пошло не так"
            }//blocked or smt else
        }
    }

    fun onReloadButtonClick(){
        if(_status == 2) sendAction(_id,ServerAction.RESTART)
        else{
            // todo callback "Хуйня, сервер должен быть включен"
        }
    }

    fun getDataFromDB(serverId: String) {
        _actualDataLoaded.value = false
        viewModelScope.launch(Dispatchers.IO) {
            val list = dataBase.serverListDao.getAllServers()
            list.forEach {
                if (it.id == serverId) {
                    _dataSuccess.postValue(it)
                }
            }
        }
    }

    private fun sendAction(serverId: String, action: ServerAction) {
// включить прогресс бар
// оффнуть кнопки
        _loadIndicatorVisibility.value = true
        _blockUIButtons.value = true
        // todo check action

        viewModelScope.launch(Dispatchers.IO) {
            val response = api.sendServerAction(sharedPrefs.getToken(), serverId, action.action)
                .awaitResponse()
            if (response.isSuccessful) {
                val data = response.body()
                Log.i("action", "Data.status -> ${data?.status}")
                when (data?.status) {
                    1 -> {
                        _actionSuccess.postValue(data.message)
                        _blockUIButtons.postValue(false)
                        _updateData.postValue(true)
                        // todo snackBar with data.message
                        // todo unlock buttons
                    }
                    0 -> {
                        _actionFailed.postValue(data.message)
                        _blockUIButtons.postValue(false)
                        // todo snackBar
                        // todo unlock buttons
                    }
                }
            } else {
                // todo msg for bad request
            }
        }
    }

    fun getDataFromAPI(serverId: String) {
        viewModelScope.launch(Dispatchers.IO) {
//            try {
            val response = api.getServer(sharedPrefs.getToken(), serverId)
                .awaitResponse()
            Log.i("server", "Response() -> ${response.isSuccessful}")
            if (response.isSuccessful) {
                val data = response.body()
                Log.i("server", "Data.status -> ${data?.status}")
                when (data?.status) {
                    1 -> {
                        val server = data.server_data
                        Log.i("server", "Start let for status = 1")
                        server?.let {
                            convertDataAndSend(it)
                            _actualDataLoaded.postValue(true)
                            Log.i("server", "data success")
                        }
                        Log.i("server", "End of let for status = 1")
                    }
                    0 -> {
                        val message = data.message
                        Log.i("server", "Start let for status = 0")
                        message?.let {
                            _dataFailed.postValue(DataLoad.FAILED)
                            Log.i("server", "data = 0, fail")
                        }
                        Log.i("server", "End of let for status = 0")
                    }
                }
            } else {
                if (attemptsCounter == 3) {
                    attemptsCounter = 0
                    _dataFailed.postValue(DataLoad.LONG_REFRESH)
                } else {
                    attemptsCounter++
                    _dataFailed.postValue(DataLoad.REFRESH)
                }

            }
//            } catch (e: Exception) {
//                _noInternetConnection.postValue("Походу инет капут")
//            }
        }
    }

    private fun convertDataAndSend(server: ServerData) {
        val convertedServer = server.toServerDBModel()
        _id = convertedServer.id
        _status = convertedServer.status
        _dataSuccess.postValue(convertedServer)
        _loadIndicatorVisibility.postValue(false)
    }
}
