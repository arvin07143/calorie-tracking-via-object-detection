package com.example.fyp.dependency

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.fyp.AppExecutors
import com.example.fyp.data.local.GoalDAO
import com.example.fyp.data.local.MealDAO
import com.example.fyp.data.local.MealDatabase
import com.example.fyp.data.remote.MealService
import com.example.fyp.data.remote.WebAPI
import com.example.fyp.data.repository.MealRepository
import com.example.fyp.objectdetection.DetectedObjectConverter
import com.example.fyp.utils.AuthTokenProvider
import com.example.fyp.utils.LiveDataCallAdapterFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    fun provideOKHTTP(): OkHttpClient = OkHttpClient.Builder()
        .readTimeout(60, TimeUnit.SECONDS)
        .callTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(AuthTokenProvider())
        .build()

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://arvin07143-finalyearproject.herokuapp.com/")
        .client(okHttpClient)
        .addConverterFactory(
            MoshiConverterFactory.create(
                Moshi.Builder()
                    .add(Date::class.java, Rfc3339DateJsonAdapter())
                    .add(DetectedObjectConverter())
                    .add(KotlinJsonAdapterFactory())
                    .build()
            )
        )
        .addCallAdapterFactory(LiveDataCallAdapterFactory())
        .build()

    @Singleton
    @Provides
    fun provideMealService(retrofit: Retrofit): MealService {
        return retrofit.create(MealService::class.java)
    }

    @Singleton
    @Provides
    fun provideWebAPI(retrofit: Retrofit): WebAPI {
        return retrofit.create(WebAPI::class.java)
    }

    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): MealDatabase {
        return Room.databaseBuilder(context, MealDatabase::class.java, "my_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideMealDAO(db: MealDatabase): MealDAO {
        return db.mealDao()
    }

    @Singleton
    @Provides
    fun provideGoalDAO(db: MealDatabase): GoalDAO {
        return db.goalDao()
    }

    @Singleton
    @Provides
    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideRepository(
        appExecutors: AppExecutors,
        remoteDataSource: MealService,
        webAPI: WebAPI,
        localDataSource: MealDAO,
        goalSource: GoalDAO,
        sharedPreferences: SharedPreferences,
    ): MealRepository {
        return MealRepository(appExecutors,
            remoteDataSource,
            webAPI,
            localDataSource,
            goalSource,
            sharedPreferences)
    }
}