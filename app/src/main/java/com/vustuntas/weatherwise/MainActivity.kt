package com.vustuntas.weatherwise.view

import android.content.SharedPreferences
import android.inputmethodservice.InputMethodService
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.vustuntas.weatherwise.R
import com.vustuntas.weatherwise.databinding.ActivityMainBinding
import com.vustuntas.weatherwise.model.Main
import com.vustuntas.weatherwise.viewmodel.MainActivityViewModel
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var weatherViewModel : MainActivityViewModel

    private lateinit var GET : SharedPreferences
    private lateinit var SET : SharedPreferences.Editor

    private var decimalFormat = DecimalFormat("###.####")
    private var decimalFormatDegree = DecimalFormat("##.#")

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        GET = this.getSharedPreferences(packageName, MODE_PRIVATE)
        SET = GET.edit()

        weatherViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        var cName = GET.getString("cityName","istanbul")
        binding.cityNameEditText.setText(cName)

        weatherViewModel.refreshData(cName!!)

        obserLiveData()

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            binding.dataViewLinearLayout.visibility = View.GONE
            binding.errorMessageTextView.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            var cityName = GET.getString("cityName",cName)
            binding.cityNameEditText.setText(cityName)
            weatherViewModel.refreshData(cityName!!)
            obserLiveData()
        }

        binding.citySearchImageView.setOnClickListener {
            var inputMethod : InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethod.hideSoftInputFromWindow(currentFocus?.windowToken,0)
            val cityName = binding.cityNameEditText.text.toString()
            SET.putString("cityName",cityName)
            SET.commit()
            weatherViewModel.refreshData(cityName)
            obserLiveData()

        }

    }

    private fun obserLiveData(){
        weatherViewModel.weather.observe(this, Observer {data->
            data?.let {
                binding.dataViewLinearLayout.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                binding.degreeTextView.text = "${decimalFormatDegree.format(it.main.temp)}°C"
                binding.countryCodeTextView.text = it.sys.country
                binding.cityNameTextView.text = it.name
                binding.humidityTextView.text = "%${it.main.humidity}"
                binding.speedTextView.text = "${it.wind.speed} km/h"
                binding.latTextView.text = decimalFormat.format(it.coord.lat)
                binding.lonTextView.text = decimalFormat.format(it.coord.lon)
                var imageView : ImageView = binding.weatherIconImageView
                val icon = it.weather.get(0).icon
                var imageUrl = "http://openweathermap.org/img/wn/$icon@2x.png"
                Glide.with(this)
                    .load(imageUrl)
                    .into(imageView)

            }

        })

        weatherViewModel.weatherErrorMessage.observe(this, Observer {
            it?.let {
                if(it){
                    binding.errorMessageTextView.visibility = View.VISIBLE
                    binding.dataViewLinearLayout.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                }
                else{
                    binding.errorMessageTextView.visibility = View.GONE
                }
            }
        })

        weatherViewModel.weatherProgressBar.observe(this, Observer {
            if(it){
                binding.errorMessageTextView.visibility = View.GONE
                binding.dataViewLinearLayout.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }
            else{
                binding.progressBar.visibility = View.GONE
            }

        })

    }

}