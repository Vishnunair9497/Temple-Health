package com.example.templepocforground.di

import android.content.Context
import android.content.SharedPreferences
import com.example.templepocforground.data.ApiService
import com.example.templepocforground.repository.AuthRepository
import com.example.templepocforground.utils.SharedPrefsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        //.baseUrl("https://func-alert-notification-app.azurewebsites.net/api/")
        .baseUrl("http://endpoint-temple-er-demo-epaugjcsfzbdhqhd.a02.azurefd.net/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideAuthRepository(apiService: ApiService): AuthRepository =
        AuthRepository(apiService)

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("TempleAppPrefs", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideSharedPrefsManager(sharedPreferences: SharedPreferences): SharedPrefsManager =
        SharedPrefsManager(sharedPreferences)
}
