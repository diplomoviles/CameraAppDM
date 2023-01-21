package com.amaurypm.cameraappdm.view.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.amaurypm.cameraappdm.databinding.ActivityMainBinding
import com.amaurypm.cameraappdm.model.Photo
import com.amaurypm.cameraappdm.util.ImageUtils
import com.amaurypm.cameraappdm.view.adapters.PhotosAdapter
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //Para el permiso de la cámara y el resultado del intent de la cámara
    private var cameraPermissionGranted = false
    private var isCameraActive = false
    private var currentPhotoPath: String? = null //el path o la ruta temporal de la foto tomada
    private lateinit var resultLauncher: ActivityResultLauncher<Intent> //para procesar el resultado de la cámara

    var photos = ArrayList<Photo>()
    var photosAdapter: PhotosAdapter? = null

    companion object {
        const val PERMISO_CAMARA = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivCamera.setOnClickListener {
            updateOrRequestPermissions()
        }

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == Activity.RESULT_OK){
                currentPhotoPath?.let{ path ->
                    photos.add(Photo(ImageUtils.decodeSampledBitmapFromPath(path, 500,900)))
                    photosAdapter?.notifyItemInserted(photos.size-1)
                }
            }
            isCameraActive = false
        }

        photosAdapter = PhotosAdapter(this, photos)
        binding.rvPhotos.layoutManager = GridLayoutManager(this, 3)
        binding.rvPhotos.adapter = photosAdapter

    }

    fun updateOrRequestPermissions() {

        cameraPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val permissionsToRequest = mutableListOf<String>()

        if (!cameraPermissionGranted)
            permissionsToRequest.add(Manifest.permission.CAMERA)

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISO_CAMARA
            )
        } else {
            //Tenemos el permiso
            if(!isCameraActive) startIntentCamera()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //Se obtuvo el permiso
            Toast.makeText(this, "Se obtuvo el permiso de la cámara", Toast.LENGTH_SHORT).show()
        }else{
            if(shouldShowRequestPermissionRationale(permissions[0])){
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Permiso requerido")
                    .setMessage("Se necesita el permiso para poder tomar las fotos")
                    .setPositiveButton("Entendido", DialogInterface.OnClickListener { _, _ ->
                        updateOrRequestPermissions()
                    })
                    .setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialog, _ ->
                        dialog.dismiss()
                        finish()
                    })
                    .create()
                    .show()
            }else{
                Toast.makeText(this, "El permiso al acceso de la cámara se ha negado permanentemente", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        updateOrRequestPermissions()
    }

    fun selectedPhoto(photo: Photo, position: Int){

    }

    fun startIntentCamera(){
        val filename = "photo"
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        try{
            val imageFile = File.createTempFile(filename, ".jpg", storageDirectory)

            currentPhotoPath = imageFile.absolutePath

            val imageUri = FileProvider.getUriForFile(
                this,
                "com.amaurypm.cameraappdm.fileprovider",
                imageFile
            )

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            }

            resultLauncher.launch(intent)
            isCameraActive = true


        }catch(e: IOException){
            e.printStackTrace()
        }
    }
}