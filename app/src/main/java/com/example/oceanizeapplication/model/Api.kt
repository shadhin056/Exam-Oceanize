package com.haqueit.question.app.retrofit

import com.example.oceanizeapplication.model.DataModelResponse
import io.reactivex.Single
import retrofit2.http.*


interface Api {

    @GET("api/v1/ssh")
    fun responseList(

    ): Single<List<DataModelResponse>>


}