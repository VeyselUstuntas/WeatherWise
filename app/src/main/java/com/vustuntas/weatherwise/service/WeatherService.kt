package com.vustuntas.weatherwise.service

import com.vustuntas.weatherwise.model.WeatherModel
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class WeatherService {
    //https://api.openweathermap.org/data/2.5/weather?q=erzurum&appid=14033673b96be2f2259af4e8de1b0396&units=metric
    private val BASE_URL = "https://api.openweathermap.org/"

    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(WeatherAPI::class.java)

   suspend fun getApi(cityName : String) : Response<WeatherModel>{
        return api.getData(cityName)
    }
}