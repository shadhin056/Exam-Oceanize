package com.example.oceanizeapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.oceanizeapplication.view_model.DataViewModel
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var dataViewModel: DataViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)
        observeViewModel()
        getResponse()
    }
    fun observeViewModel() {
        dataViewModel.response_error .observe(this, androidx.lifecycle.Observer {
            it?.let {
                if(it){
                    Log.e("ButtonList","-------------Error")
                }else{
                    Log.e("ButtonList","-------------No Error")
                }

            }
        })

        dataViewModel.listResponse .observe(this, androidx.lifecycle.Observer {

            it?.let {

                for (i in 0 until it.size) {
                    Log.e("ButtonList",it.get(i).name.toString())
                }
            }


        })

    }
    private fun getResponse() {


        this.let { it1 -> dataViewModel.responseList() }
    }
}