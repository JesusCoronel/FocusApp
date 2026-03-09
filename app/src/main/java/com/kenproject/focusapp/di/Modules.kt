package com.kenproject.focusapp.di

import android.content.Context
import androidx.room.Room
import com.kenproject.focusapp.R
import com.kenproject.focusapp.data.local.FocusDatabase
import com.kenproject.focusapp.data.local.dao.DistractionEventDao
import com.kenproject.focusapp.data.local.dao.SessionDao
import com.kenproject.focusapp.data.remote.api.FocusApi
import com.kenproject.focusapp.data.repository.MovementDetector
import com.kenproject.focusapp.data.repository.NoiseDetector
import com.kenproject.focusapp.data.repository.SessionRepositoryImpl
import com.kenproject.focusapp.domain.model.DetectionThresholds
import com.kenproject.focusapp.domain.repository.DistractionDetector
import com.kenproject.focusapp.domain.repository.SessionRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FocusDatabase =
        Room.databaseBuilder(context, FocusDatabase::class.java, "focus_app.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideSessionDao(db: FocusDatabase): SessionDao = db.sessionDao()

    @Provides
    fun provideEventDao(db: FocusDatabase): DistractionEventDao = db.distractionEventDao()
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        )
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        @ApplicationContext context: Context
    ): Retrofit = Retrofit.Builder()
        .baseUrl(context.getString(R.string.api_base_url))
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideFocusApi(retrofit: Retrofit): FocusApi = retrofit.create(FocusApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository
}

@Module
@InstallIn(SingletonComponent::class)
object SensorModule {

    @Provides
    @Singleton
    @Named("noise")
    fun provideNoiseDetector(detector: NoiseDetector): DistractionDetector = detector

    @Provides
    @Singleton
    @Named("movement")
    fun provideMovementDetector(detector: MovementDetector): DistractionDetector = detector

    @Provides
    fun provideDetectionThresholds(): DetectionThresholds = DetectionThresholds()
}