package com.example.fianca.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        CategoryEntity::class,
        FreelancerCategoryEntity::class,
        FreelancerProfileEntity::class,
        ServiceRequestEntity::class,
        ServiceInterestEntity::class,
        ChatMessageEntity::class,
        RatingEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class FiancaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun freelancerDao(): FreelancerDao
    abstract fun requestsDao(): RequestsDao
    abstract fun chatDao(): ChatDao
    abstract fun ratingDao(): RatingDao

    companion object {
        @Volatile
        private var INSTANCE: FiancaDatabase? = null

        fun getInstance(context: Context): FiancaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FiancaDatabase::class.java,
                    "fianca.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
