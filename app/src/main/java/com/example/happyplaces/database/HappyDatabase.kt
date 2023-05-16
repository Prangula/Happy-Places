package com.example.happyplaces.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HappyEntity::class], version = 1)

abstract class HappyDatabase:RoomDatabase() {

    abstract fun happyDao():HappyDao

    companion object {

        @Volatile
        private var INSTANCE: HappyDatabase? = null

        fun getInstance(context: Context): HappyDatabase{

            synchronized(this){

                var instance = INSTANCE

                if(instance == null){

                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        HappyDatabase::class.java,
                        "happy-database")
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance

                }
                return instance
            }

        }



    }
}