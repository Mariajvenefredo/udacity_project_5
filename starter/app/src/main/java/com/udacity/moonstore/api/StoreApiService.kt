package com.udacity.moonstore.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.moonstore.api.models.Store
import com.udacity.moonstore.storeItems.StoreDataItem
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface StoreApiService {
    @GET("stores")
    suspend fun getStores(@Query("key") key: String): List<Store>

    @GET("pieces")
    suspend fun getPieces(@Query("key") key: String): List<StoreDataItem>
}

enum class StoreItemFilter { ALL, FAVORITES }

object StoreApi {
    val retrofitService: StoreApiService by lazy { retrofit.create(StoreApiService::class.java) }
}