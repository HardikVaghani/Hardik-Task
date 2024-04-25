package com.hardik.hardiktask.data.api

import com.hardik.hardiktask.data.model.DataResponse
import retrofit2.Response
import retrofit2.http.GET

interface Apis {
//    https://fakestoreapi.com/products
    @GET("/products")
    suspend fun getData() : Response<DataResponse>
}