package com.example.plantlets.module

import android.content.Context
import android.content.SharedPreferences
import com.example.plantlets.Manager.CartManager
import com.example.plantlets.repositories.LocalRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.firestore.ktx.memoryCacheSettings
import com.google.firebase.firestore.ktx.persistentCacheSettings
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
//        val settings = firestoreSettings {
//            // Use memory cache
//            setLocalCacheSettings(memoryCacheSettings {})
//            // Use persistent disk cache (default)
//            setLocalCacheSettings(persistentCacheSettings {})
//        }
//        val firestore = Firebase.firestore
//        firestore.firestoreSettings
        return Firebase.firestore
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideCartManager(sharedPreferences: SharedPreferences,localRepository:LocalRepository): CartManager {
        return CartManager(sharedPreferences, localRepository )
    }

}