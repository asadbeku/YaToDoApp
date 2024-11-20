package uz.foursquare.todoapp.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Network {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Authorization", "Bearer Thranduil")
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit: Retrofit = try {
        Retrofit.Builder()
            .baseUrl("https://hive.mrdekk.ru/todo/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    } catch (e: Exception) {
        throw RuntimeException("Retrofit initialization failed", e)
    }

    fun <T> buildService(service: Class<T>): T {
        return retrofit.create(service)
    }

}