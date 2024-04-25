package com.hardik.hardiktask.data.repository

import android.util.Log
import com.hardik.hardiktask.data.api.RetrofitInstance
import com.hardik.hardiktask.data.model.DataResponse
import retrofit2.Response
import kotlin.math.log

class RepositoryInstance {
    val TAG = RepositoryInstance::class.java.simpleName

    // Data
    suspend fun getData(): Response<DataResponse> {
        Log.e(TAG, "getData: ", )
        return RetrofitInstance.api.getData()
    }
}