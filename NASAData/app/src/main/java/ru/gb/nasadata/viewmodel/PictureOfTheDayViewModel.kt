package ru.gb.nasadata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.gb.nasadata.BuildConfig
import ru.gb.nasadata.viewmodel.PictureOfTheDayData

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.gb.nasadata.model.PODRetrofitImpl
import ru.gb.nasadata.model.PODServerResponseData
import java.text.SimpleDateFormat
import java.util.*


const val YOU_NEED_API_KEY_ERROR_TEXT = "You need API key"
const val UNIDENTIFIED_ERROR_TEXT = "Unidentified error"

class PictureOfTheDayViewModel(
    private val liveDataForViewToObserve: MutableLiveData<PictureOfTheDayData> = MutableLiveData(),
    private val retrofitImpl: PODRetrofitImpl = PODRetrofitImpl()
) :
    ViewModel() {

    fun getData(date: Date? = null): LiveData<PictureOfTheDayData> {
        sendServerRequest(date)
        return liveDataForViewToObserve
    }

    private fun sendServerRequest(date: Date?) {
        liveDataForViewToObserve.value = PictureOfTheDayData.Loading(null)
        val apiKey: String = BuildConfig.NASA_API_KEY
        if (apiKey.isBlank()) {
            PictureOfTheDayData.Error(Throwable(YOU_NEED_API_KEY_ERROR_TEXT))
        } else {
            var dateStr: String? = null
            date?.let {
                dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(it)
            }
            println("dateStr " + dateStr)
            retrofitImpl.getRetrofitImpl().getPictureOfTheDay(apiKey, true, dateStr).enqueue(object :
                Callback<PODServerResponseData> {
                override fun onResponse(
                    call: Call<PODServerResponseData>,
                    response: Response<PODServerResponseData>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        response.body()?.let {
                            liveDataForViewToObserve.value = PictureOfTheDayData.Success(it)
                        }
                    } else {
                        val message = response.message()
                        if (message.isNullOrEmpty()) {
                            liveDataForViewToObserve.value =
                                PictureOfTheDayData.Error(Throwable(UNIDENTIFIED_ERROR_TEXT))
                        } else {
                            liveDataForViewToObserve.value =
                                PictureOfTheDayData.Error(Throwable(message))
                        }
                    }
                }

                override fun onFailure(call: Call<PODServerResponseData>, t: Throwable) {
                    liveDataForViewToObserve.value = PictureOfTheDayData.Error(t)
                }
            })
        }
    }
}
