package ru.gb.nasadata.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PictureOfTheDayAPI {

    @GET("planetary/apod")
    fun getPictureOfTheDay(@Query("api_key") apiKey: String, @Query("thumbs") thumbs: Boolean, @Query("date") dateStr: String?): Call<PODServerResponseData>
}
