package com.creative.nasaapi

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.creative.nasaapi.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://cat-fact.herokuapp.com"
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var TAG = "MainActivity"

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        getCurrentData()
        binding.layoutGenerateNewFact.setOnClickListener {
            getCurrentData()
        }
    }
    private fun getCurrentData(){

        binding.tvTextView.visibility=View.INVISIBLE
        binding.tvTimeStamp.visibility=View.INVISIBLE
        binding.progressBar.visibility=View.VISIBLE

        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiRequests::class.java)
        GlobalScope.launch(Dispatchers.IO) {
            val response = api.getCatFacts().awaitResponse()
            if (response.isSuccessful){
                val data = response.body()!!
                Log.d(TAG, data.text)

                withContext(Dispatchers.Main){
                    binding.tvTextView.visibility=View.VISIBLE
                    binding.tvTimeStamp.visibility=View.VISIBLE
                    binding.progressBar.visibility=View.GONE
                    binding.tvTextView.text=data.text
                    binding.tvTimeStamp.text=data.createdAt
                }
            }
        }

    }
}