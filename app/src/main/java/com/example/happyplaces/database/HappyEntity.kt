package com.example.happyplaces.database

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "happy-table")
data class HappyEntity(

    @PrimaryKey(autoGenerate = true)

    val id:Int = 0,
    val title: String ="",
    val image: String = "",
    val description:String="",
    val date: String ="" ,
    val location:String ="",
    val latitude: Double = 0.00,
    val longitude: Double = 0.00

): Serializable
