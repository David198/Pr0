package com.pr0gramm.app.services

import com.google.gson.Gson
import com.pr0gramm.app.Settings
import com.pr0gramm.app.util.doInBackground
import okhttp3.OkHttpClient
import org.slf4j.LoggerFactory
import proguard.annotation.KeepPublicClassMemberNames
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST


class SettingsTrackerService(httpClient: OkHttpClient, gson: Gson) {
    private val logger = LoggerFactory.getLogger("SettingsTrackerService")

    private val settings: Settings = Settings.get()

    private val httpInterface: HttpInterface = Retrofit.Builder()
            .baseUrl("https://pr0.wibbly-wobbly.de/")
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .validateEagerly(true)
            .build()
            .create(HttpInterface::class.java)

    fun track() {
        val values = settings.raw().all.filterKeys { it.startsWith("pref_") }

        doInBackground {
            httpInterface.track(mapOf("settings" to values)).execute()
            logger.info("Tracked settings successfully.")
        }
    }

    @KeepPublicClassMemberNames
    private interface HttpInterface {
        @POST("track-settings")
        fun track(@Body values: Any): Call<Void>
    }
}
