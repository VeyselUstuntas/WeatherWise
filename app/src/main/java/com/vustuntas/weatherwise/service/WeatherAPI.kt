package com.vustuntas.weatherwise.service

import com.vustuntas.weatherwise.model.WeatherModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

//https://api.openweathermap.org/data/2.5/weather?q=erzurum&appid=14033673b96be2f2259af4e8de1b0396&units=metric
interface WeatherAPI {
    @GET("data/2.5/weather?&appid=14033673b96be2f2259af4e8de1b0396&units=metric")
    suspend fun getData(
        @Query("q") cityName : String
    ):Response<WeatherModel>
}