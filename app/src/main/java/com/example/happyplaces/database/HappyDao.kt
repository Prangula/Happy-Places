package com.example.happyplaces.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface HappyDao {

    @Insert

    suspend fun insert(happyEntity: HappyEntity)

    @Delete
    suspend fun delete(happyEntity: HappyEntity)

    @Update

    suspend fun update(happyEntity: HappyEntity)

    @Query("SELECT * FROM 'happy-table'")

    fun fetchAllData(): Flow<List<HappyEntity>>

    @Query("SELECT * FROM 'happy-table' where id=:id")

    fun fetchAllId(id: Int):Flow<HappyEntity>

}