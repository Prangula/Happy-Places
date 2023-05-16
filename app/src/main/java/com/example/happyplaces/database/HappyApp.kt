package com.example.happyplaces.database

import android.app.Application

class HappyApp:Application() {

    val db by lazy {

        HappyDatabase.getInstance(this)
    }
}