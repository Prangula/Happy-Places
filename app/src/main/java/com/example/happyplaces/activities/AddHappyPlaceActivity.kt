package com.example.happyplaces.activities




import android.app.AlertDialog
import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.location.LocationManager
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.room.Dao
import com.example.happyplaces.R
import com.example.happyplaces.database.HappyApp
import com.example.happyplaces.database.HappyDao
import com.example.happyplaces.database.HappyEntity
import com.example.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.example.happyplaces.databinding.DialogBinding
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_add_happy_place.*
import kotlinx.android.synthetic.main.happy_recycle.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {

    var binding: ActivityAddHappyPlaceBinding? = null
    private var saveImageToInternalStorage:Uri? = null
    private var mLatitude : Double = 0.00
    private var mLongitude : Double = 0.00

    private val cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        setSupportActionBar(toolbar_add_place)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toolbar_add_place.setNavigationOnClickListener {

            onBackPressed()

        }

        dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

            cal.set(Calendar.YEAR,year)
            cal.set(Calendar.MONTH,month)
            cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateDateInView()
        }

        et_date.setOnClickListener(this)
        tv_add_image.setOnClickListener(this)
        et_location.setOnClickListener(this)
        btn_save.setOnClickListener(this)
        btnCurrentLocation.setOnClickListener(this)


     if(!Places.isInitialized()){

         Places.initialize(this@AddHappyPlaceActivity,resources.getString(R.string.google_maps_api_key))

     }



    }



    override fun onClick(v: View?) {
        when(v!!.id){

            R.id.et_date ->{

                DatePickerDialog(this@AddHappyPlaceActivity,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()

            }

            R.id.tv_add_image -> {

                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems = arrayOf("Select photo from Gallery",
                "Capture Photo from Camera")

                pictureDialog.setItems(pictureDialogItems){
                        _,which ->
                    when(which){

                      0 -> choosePhotoFromGallery()

                    1 -> takePhotoFromCamera()

                    }
                }
                pictureDialog.show()

            }
            R.id.btn_save -> {
                val dao = (application as HappyApp).db.happyDao()
                saveRecord(dao)

            }
            R.id.et_location -> {

                try {
                    val fields = listOf(Place.Field.ID,Place.Field.NAME,
                    Place.Field.ADDRESS,Place.Field.LAT_LNG)
                 val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,fields)
                     .build(this@AddHappyPlaceActivity)
                    startActivityForResult(intent, PLACE)

                }catch (e:Exception){
                    e.printStackTrace()
                }

            }


        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateDateInView(){

        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat,Locale.getDefault())
        et_date.setText(sdf.format(cal.time).toString())


    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){

            if(requestCode == GALLERY){

                if(data!=null){

                    val contentURI = data.data

                    try{

                        val selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,contentURI)
                        saveImageToInternalStorage = saveImageToInternalStorage(selectedImageBitmap)
                        Log.e("Saved image:", "path :: $saveImageToInternalStorage")
                        iv_place_image.setImageBitmap(selectedImageBitmap)
                    }catch (e: IOException){

                        e.printStackTrace()


                    }

                }
            }

            else if(requestCode == CAMERA){

                val thumbnail : Bitmap = data!!.extras!!.get("data") as Bitmap
                saveImageToInternalStorage = saveImageToInternalStorage(thumbnail)
                Log.e("Saved image:", "path :: $saveImageToInternalStorage")
                iv_place_image.setImageBitmap(thumbnail)
            }
          else if (requestCode == PLACE){

              val place : Place = Autocomplete.getPlaceFromIntent(data!!)
                mLatitude = place.latLng!!.latitude
                mLongitude = place.latLng!!.longitude
                et_location.setText(place.address)

            }
        }
    }

    private fun takePhotoFromCamera(){


        Dexter.withActivity(this).withPermissions(

            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA)
            .withListener(object: MultiplePermissionsListener {
                override fun  onPermissionsChecked
                            (report : MultiplePermissionsReport? )
                {if(report!!.areAllPermissionsGranted()){

                    val galleryIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(galleryIntent, CAMERA)

                }
                }
                override fun  onPermissionRationaleShouldBeShown(permissions:MutableList<PermissionRequest>, token: PermissionToken)
                {showRationaleDialogForPermissions()}
            }).onSameThread().check()

    }

    private fun choosePhotoFromGallery(){

        Dexter.withActivity(this).withPermissions(

             Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object: MultiplePermissionsListener {
                override fun  onPermissionsChecked
                            (report : MultiplePermissionsReport? )
                {if(report!!.areAllPermissionsGranted()){

                    val galleryIntent = Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, GALLERY)

                }
                            }
                override fun  onPermissionRationaleShouldBeShown(permissions:MutableList<PermissionRequest>, token: PermissionToken)
                {showRationaleDialogForPermissions()}
            }).onSameThread().check()

    }

    private fun showRationaleDialogForPermissions(){

        AlertDialog.Builder(this).setMessage("turn off")
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

    private fun saveImageToInternalStorage(bitmap: Bitmap):Uri{

        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")

        try{

            val stream : OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()

        }catch (e: IOException){
            e.printStackTrace()

        }
        return  Uri.parse(file.absolutePath)
    }

    companion object {

        private const val GALLERY = 1
        private const val CAMERA = 2
        const val IMAGE_DIRECTORY = "HappyPlacesImages"
        const val PLACE = 3


    }

    fun saveRecord(happyDao: HappyDao){

        val title = et_title.text.toString()
        val description = et_description.text.toString()
        val date = et_date.text.toString()
        val location = et_location.text.toString()





        if(title.isNotEmpty()&&description.isNotEmpty()
            &&date.isNotEmpty()&&location.isNotEmpty()&&saveImageToInternalStorage!=null){

            lifecycleScope.launch {

                happyDao.insert(HappyEntity(title=title, description = description, date = date,
                location = location, longitude = mLongitude, latitude = mLatitude,image =saveImageToInternalStorage.toString(),))

                Toast.makeText(applicationContext,"Record Saved",Toast.LENGTH_LONG).show()
                et_title.text!!.clear()
                et_description!!.text!!.clear()
                et_date.text!!.clear()
                et_location.text!!.clear()


            }

        }else{

            Toast.makeText(applicationContext,
                "None of them cannot be blank",Toast.LENGTH_LONG).show()

        }


    }








}