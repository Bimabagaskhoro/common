package com.bimabk.common.extension

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import okhttp3.OkHttpClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiProvider : KoinComponent {
    inline fun <reified T> provideApi(baseUrl: String): T {
        val context: Context = get()
        val chuckerCollector =
            ChuckerCollector(context, true, RetentionManager.Period.ONE_HOUR)

        val chuckerInterceptor = ChuckerInterceptor
            .Builder(context)
            .collector(chuckerCollector)
            .build()

        val okHttpClient = OkHttpClient
            .Builder()
            .addInterceptor(chuckerInterceptor)
            .build()

        val retrofit = Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(T::class.java)
    }
}
