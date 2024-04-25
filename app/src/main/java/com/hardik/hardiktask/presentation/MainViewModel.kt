package com.hardik.hardiktask.presentation

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hardik.hardiktask.ApplicationInstance
import com.hardik.hardiktask.data.model.DataResponse
import com.hardik.hardiktask.data.repository.RepositoryInstance
import com.hardik.hardiktask.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class MainViewModel (app: Application, private val repositoryInstance: RepositoryInstance) : AndroidViewModel(app) {
    val TAG = MainViewModel::class.java.simpleName

    val data : MutableLiveData<Resource<DataResponse>> = MutableLiveData()
    private var dataResponse : DataResponse? = null

    init {
//        getData()
    }
    // Data API methods
    fun getData() = viewModelScope.launch {
        Log.d(TAG, "getData: viewModelScope")
        safeDataCall()
    }
    private suspend fun safeDataCall() {
        data.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = repositoryInstance.getData()
                data.postValue(handleDataResponse(response))
            }else{
                data.postValue(Resource.Error("No internet Connection"))
            }

        }catch (t : Throwable){
            when(t){
                is IOException -> data.postValue(Resource.Error("Network failure!!!"))
                else -> data.postValue(Resource.Error("Conversion error!!!"))
            }
        }
    }

    // Check internet connection
    @SuppressLint("ObsoleteSdkInt")
    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<ApplicationInstance>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when{
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }else{
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

    private fun handleDataResponse(response: Response<DataResponse>): Resource<DataResponse> {
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                Log.d(TAG, "handleDataResponse: ")
                if (dataResponse == null){
                    dataResponse = resultResponse
                }else{
                    val oldData = dataResponse
                    val newData = resultResponse
                    oldData?.addAll(newData)
                }
                return Resource.Success(data = dataResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}