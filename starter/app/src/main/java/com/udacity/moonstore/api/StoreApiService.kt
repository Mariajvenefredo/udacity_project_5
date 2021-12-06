package com.udacity.moonstore.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.moonstore.api.models.Store
import com.udacity.moonstore.storeItems.StoreItem
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(buildClient())
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .addConverterFactory(GsonConverterFactory.create())
    .build()

fun buildClient(): OkHttpClient {

    return OkHttpClient.Builder().apply {
        readTimeout(50, TimeUnit.SECONDS)
        writeTimeout(50, TimeUnit.SECONDS)
        connectTimeout(50, TimeUnit.SECONDS)
    }
        .build()
}

interface StoreApiService {
    @GET("stores")
    suspend fun getStores(@Query("key") key: String): List<Store>

    @GET("pieces")
    suspend fun getPieces(@Query("key") key: String): List<StoreItem>

    @GET("stores/stock/{piece_id}")
    suspend fun getPieceStock(
        @Path("piece_id") pieceID: String,
        @Query("key") key: String
    ): List<Store>

    @GET("stores/{store_id}/stock/{piece_id}")
    suspend fun getStoreStockForPiece(
        @Path("store_id") storeID: String,
        @Path("piece_id") pieceID: String,
        @Query("key") key: String
    ): String
}

enum class StoreItemFilter { ALL, FAVORITES }

object StoreApi {
    val retrofitService: StoreApiService by lazy { retrofit.create(StoreApiService::class.java) }
}