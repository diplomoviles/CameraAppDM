package com.amaurypm.cameraappdm.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.amaurypm.cameraappdm.data.local.model.Photo
import com.amaurypm.cameraappdm.databinding.ActivityMainBinding
import com.amaurypm.cameraappdm.ui.adapters.PhotosAdapter
import com.amaurypm.cameraappdm.ui.fragments.DetailsFragment
import com.amaurypm.cameraappdm.utils.decodeSampledBitmapPath
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //Para el permiso y el resultado de la cámara
    private var cameraPermissionGranted = false
    private var isCameraActive = false
    private var currentPhotoPath: String? = null //ruta temporal de cada foto

    //Para procesar el resultado que nos devuelva la cámara
    private lateinit var resultLaucher: ActivityResultLauncher<Intent>

    private var photos = mutableListOf<Photo>()
    private var photosAdapter: PhotosAdapter? = null

    private val mainViewModel: MainViewModel by viewModels()

    companion object {
        const val CAMERA_PERMISSION = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resultLaucher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){ result ->
            //Verificando que se obtuvo una foto
            if(result.resultCode == Activity.RESULT_OK){
                currentPhotoPath?.let { path ->

                    val bitmap = decodeSampledBitmapPath(path, 500, 900)
                    val rotatedBitmap = rotateImageIfRequired(bitmap, path)

                    //photos.add(Photo(decodeSampledBitmapPath(path, 500, 900)))
                    photos.add(Photo(rotatedBitmap))
                    photosAdapter?.notifyItemInserted(photos.size-1)
                }
            }
            isCameraActive = false
        }

        photosAdapter = PhotosAdapter(photos){ photo, position ->
            //Manejamos el click a cada foto

            val bundle = bundleOf(
                "photo" to photo,
                "position" to position
            )

            /*val bundle2 = Bundle()
            bundle2.putInt("position", position)
            bundle.putParcelable("photo", photo)*/

            val detailsDialog = DetailsFragment()
            detailsDialog.arguments = bundle
            detailsDialog.show(supportFragmentManager, "Detalles")

        }

        binding.rvPhotos.layoutManager = GridLayoutManager(this, 3)
        binding.rvPhotos.adapter = photosAdapter

        mainViewModel.itemRemoved.observe(this){ position ->
            val size = photosAdapter?.itemCount
            if(size != null){
                photos.removeAt(position)
                photosAdapter?.notifyItemRemoved(position)
                photosAdapter?.notifyItemRangeChanged(
                    position,
                    size
                )
                Toast.makeText(
                    this,
                    "Foto eliminada exitosamente",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.ivCamera.setOnClickListener {
            updateOrRequestPermissions()
        }



    }

    private fun updateOrRequestPermissions() {

        //Verificamos si tenemos el permiso de la cámara
        cameraPermissionGranted =
            ContextCompat.checkSelfPermission(
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
                CAMERA_PERMISSION
            )
        } else {
            //Tenemos el permiso de la cámara
            actionPermissionGranted()
        }

    }

    override fun onRestart() {
        super.onRestart()
        updateOrRequestPermissions()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //El usuario concedió el permiso
            actionPermissionGranted()
        } else {
            if(shouldShowRequestPermissionRationale(permissions[0])){
                //Le damos las razones al usuario
                AlertDialog.Builder(this)
                    .setTitle("Permiso requerido")
                    .setMessage("Se necesita el permiso para poder tomar las fotos en la app")
                    .setPositiveButton("Aceptar"){ _, _ ->
                        updateOrRequestPermissions()
                    }
                    .setNegativeButton("Salir"){ _, _ ->
                        finish()
                    }
                    .create()
                    .show()

            }else {
                Toast.makeText(
                    this,
                    "El acceso a la cámara ha sido negado permanentemente",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun actionPermissionGranted(){
        /*Toast.makeText(
            this,
            "Permiso a la cámara concedido",
            Toast.LENGTH_SHORT
        ).show()*/

        if(!isCameraActive)
            startIntentCamera()
    }

    private fun startIntentCamera(){

        val filename = "photo"
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        try{

            val imageFile = File.createTempFile(filename, ".jpg", storageDirectory)

            currentPhotoPath = imageFile.absolutePath

            //Usando el File provider declarado en el manifest con sus rutas (paths)
            val imageUri = FileProvider.getUriForFile(
                this,
                "com.amaurypm.cameraappdm.fileprovider",
                imageFile
            )

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            }

            resultLaucher.launch(intent)
            isCameraActive = true

        }catch (e: IOException){
            //Manejo de la excepción
            e.printStackTrace()
        }

    }

    private fun rotateImageIfRequired(img: Bitmap, selectedImage: String): Bitmap {
        val ei = ExifInterface(selectedImage)
        val orientation: Int = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270f)
            else -> img
        }
    }

    private fun rotateImage(img: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
    }

}