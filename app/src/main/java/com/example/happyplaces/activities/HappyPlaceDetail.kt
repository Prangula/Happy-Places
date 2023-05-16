package com.example.happyplaces.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.happyplaces.R
import com.example.happyplaces.database.HappyEntity
import kotlinx.android.synthetic.main.activity_happy_place_detail2.*

class HappyPlaceDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_happy_place_detail2)


        setSupportActionBar(toolbar_happy_place_detail)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toolbar_happy_place_detail.setNavigationOnClickListener {

            onBackPressed()

        }




        val happyPlace = intent.getSerializableExtra("happy_place") as HappyEntity

        iv_place_image.setImageURI(Uri.parse(happyPlace.image))
        tv_description.text = happyPlace.description
        tv_location.text = happyPlace.location

        btn_view_on_map.setOnClickListener {

            val intent = Intent(this, MapActivity1::class.java)
            intent.putExtra("MAP",happyPlace)
            startActivity(intent)
        }

    }
}