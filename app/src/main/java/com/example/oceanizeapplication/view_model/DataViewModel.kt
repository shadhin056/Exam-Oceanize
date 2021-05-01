package com.example.oceanizeapplication.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.oceanizeapplication.model.DataModelResponse
import com.haqueit.question.app.retrofit.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class DataViewModel :ViewModel(){

    private val apiService = ApiService()
    private val disposable = CompositeDisposable()

    //Get List OF DATA FROM API
    var listResponse = MutableLiveData<List<DataModelResponse>>();
    //ANY ERROR WIll Show Here
    var response_error = MutableLiveData<Boolean>();

    fun responseList(){

        disposable.add(apiService.responseList()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<List<DataModelResponse>>() {
                override fun onSuccess(model: List<DataModelResponse>) {
                    model?.let {
                        listResponse.value = model
                    }

                }
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    response_error.value=true
                }

            })
        )
    }


}
