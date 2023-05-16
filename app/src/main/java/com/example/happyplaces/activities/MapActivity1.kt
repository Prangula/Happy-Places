package com.example.happyplaces.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.happyplaces.R
import com.example.happyplaces.database.HappyAdapter
import com.example.happyplaces.database.HappyEntity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_map1.*

class MapActivity1 : AppCompatActivity(),OnMapReadyCallback {

    private var mHappyPlaceDetail: HappyEntity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map1)




        mHappyPlaceDetail = intent.getSerializableExtra("MAP") as HappyEntity


        if (mHappyPlaceDetail != null) {


            setSupportActionBar(toolbar_map)

            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

            supportActionBar!!.title = mHappyPlaceDetail!!.title

            toolbar_map!!.setNavigationOnClickListener {

                onBackPressed()
            }

            val supportMapFragment: SupportMapFragment =
                supportFragmentManager.findFragmentById(R.id.map)
                        as SupportMapFragment
            supportMapFragment.getMapAsync(this)

        }






    }
    override fun onMapReady(googleMap: GoogleMap) {
        val position = LatLng(mHappyPlaceDetail!!.latitude, mHappyPlaceDetail!!.longitude)
        googleMap.addMarker(
            MarkerOptions().position(position).title(mHappyPlaceDetail!!.location)
        )
        val zoom = CameraUpdateFactory.newLatLngZoom(position, 10f)
        googleMap.animateCamera(zoom)
    }

}