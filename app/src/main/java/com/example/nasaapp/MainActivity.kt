package com.example.nasaapp

import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val astronomyViewModel: AstronomyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getAstronomyData()
        observeModels()
        handleNetworkChanges()
    }

    private fun getAstronomyData() {
        astronomyViewModel.getAstronomyResponse()
    }

    private fun handleNetworkChanges() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.let {
                NetworkUtils.getNetworkLiveData(it).observe(this, { isConnected ->
                    if (isConnected) {
                         getAstronomyData()
                    } else {
                        Toast.makeText(this,getString(R.string.no_internet),Toast.LENGTH_LONG).show()
                    }
                })
            }
        }
    }


    private fun observeModels() {

        astronomyViewModel.astronomyData.observe( this, { state ->
            when (state) {
                is State.Success -> {
                  state.data.let {
                      pgBar.visibility = View.GONE
                      tvTitle.text = it.title
                      tvExplaination.text = it.explanation
                      Picasso.get().load(it.hdurl).placeholder(R.drawable.ic_broken_image)
                          .error(R.drawable.ic_broken_image).into(ivImage)
                  }
                }
                is State.Loading -> {
                    pgBar.visibility = View.VISIBLE
                }
                is State.Error -> {
                   Toast.makeText(this,getString(R.string.somethin_wrong),Toast.LENGTH_LONG).show()
                }
            }
        })
    }

}