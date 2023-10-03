package com.vustuntas.weatherwise.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vustuntas.weatherwise.model.WeatherModel
import com.vustuntas.weatherwise.service.WeatherService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel() {
    val weather = MutableLiveData<WeatherModel>()
    val weatherErrorMessage = MutableLiveData<Boolean>()
    val weatherProgressBar = MutableLiveData<Boolean>()
    private var weatherApiService = WeatherService()
    private lateinit var job : Job
    private var result : WeatherModel?=null

    fun refreshData(cityName: String){
        getDataAPI(cityName)
    }

    private fun getDataAPI(cityName:String){
        try {
            job = CoroutineScope(Dispatchers.IO).launch {
                async {
                    val response = weatherApiService.getApi(cityName)
                    if(response.isSuccessful){
                        response.body()?.let {weatherObject->
                            result = weatherObject
                            weather.postValue(result!!)
                            weatherErrorMessage.postValue(false)
                            weatherProgressBar.postValue(false)
                        }
                    }
                }.await()
            }
        }
        catch (E:Exception){
            weatherErrorMessage.postValue(true)
            weatherProgressBar.postValue(false)
            println("HATA!!!!!!!")
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

}