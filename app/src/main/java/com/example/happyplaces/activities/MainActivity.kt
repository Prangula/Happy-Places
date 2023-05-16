package com.example.happyplaces.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import com.example.happyplaces.R
import com.example.happyplaces.activities.AddHappyPlaceActivity.Companion.IMAGE_DIRECTORY
import com.example.happyplaces.activities.AddHappyPlaceActivity.Companion.PLACE
import com.example.happyplaces.database.HappyAdapter
import com.example.happyplaces.database.HappyApp
import com.example.happyplaces.database.HappyDao
import com.example.happyplaces.database.HappyEntity
import com.example.happyplaces.databinding.ActivityMainBinding
import com.example.happyplaces.databinding.DialogBinding
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.activity_add_happy_place.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog.*
import kotlinx.android.synthetic.main.happy_recycle.*
import kotlinx.coroutines.launch
import java.io.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

   private var binding :ActivityMainBinding? = null
   private var binding1: DialogBinding? = null
    private var saveImageToInternalStorage: Uri? = null


    private val cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        fabAddHappyPlace.setOnClickListener {

            val intent = Intent(this, AddHappyPlaceActivity::class.java)
            startActivity(intent)
        }





        val dao = (application as HappyApp).db.happyDao()

        lifecycleScope.launch {
            dao.fetchAllData().collect{

                val list = ArrayList(it)
                recycle(list,dao)
            }
        }



    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun recycle(list:ArrayList<HappyEntity>, happyDao: HappyDao) {

       if(list.isNotEmpty()){

           val happyAdapter = HappyAdapter(list,
               {

                   deleteId ->

                   delete(deleteId,happyDao)

               })

           rvHappy.layoutManager=LinearLayoutManager(this)
           rvHappy.adapter = happyAdapter
           rvHappy.visibility = View.VISIBLE
           noText.visibility = View.GONE

       }else{
           rvHappy.visibility = View.GONE
           noText.visibility = View.VISIBLE

       }


    }


    private fun delete(id:Int,happyDao: HappyDao){


        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Record")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes"){dialogInterface,_ ->

            lifecycleScope.launch{

                happyDao.delete(HappyEntity(id))
                Toast.makeText(
                    applicationContext,
                    "Record deleted successfully",
                    Toast.LENGTH_LONG
                ).show()
                dialogInterface.dismiss()
            }
        }

        builder.setNegativeButton("No"){dialogInterface,_ ->

            dialogInterface.dismiss()

        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

    }



    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateDateInView(editText: EditText){

        binding1 = DialogBinding.inflate(layoutInflater)


        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat,Locale.getDefault())
        editText.setText(sdf.format(cal.time).toString())


    }

    private fun showRationaleDialogForPermissions(){

        android.app.AlertDialog.Builder(this).setMessage("turn off")
            .setPositiveButton("GO TO SETTINGS"){
                    _,_ ->
                try {

                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package",packageName,null)
                    intent.data = uri
                    startActivity(intent)
                }catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                }

            }.setNegativeButton("Cancel"){dialog,which ->
                dialog.dismiss()

            }.show()
    }




    @RequiresApi(Build.VERSION_CODES.N)
    private fun update(id: Int, happyDao: HappyDao){


        val updateDialog = Dialog(this, R.style.Theme_Dialog)
        updateDialog.setCancelable(false)
        val binding1 = DialogBinding.inflate(layoutInflater)
        updateDialog.setContentView(binding1!!.root)



        dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

            cal.set(Calendar.YEAR,year)
            cal.set(Calendar.MONTH,month)
            cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateDateInView(binding1!!.etDate1)
        }




        binding1!!.etDate1!!.setOnClickListener {



            DatePickerDialog(this@MainActivity,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }





        lifecycleScope.launch {

            happyDao.fetchAllId(id).collect{

                if(it!=null){

                    binding1!!.etTitle1!!.setText(it.title)
                    binding1!!.etDescription1!!.setText(it.description)
                    binding1!!.etDate1!!.setText(it.date)
                    binding1!!.etLocation1!!.setText(it.location)






                }

            }
        }











}

}