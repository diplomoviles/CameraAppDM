package com.amaurypm.cameraappdm

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.amaurypm.cameraappdm.data.PhotoRepository
import com.amaurypm.cameraappdm.databinding.ActivityMainBinding
import com.amaurypm.cameraappdm.ui.adapters.PhotosAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //Para el permiso y resultado del intent de la cámara
    private var cameraPermissionGranted = false  //Bandera para el permiso
    private var isCameraActive = false  //Bandera para saber si la cámara se está usando
    private var currentPhotoPath: String? = null //Ruta de la foto actual
    private lateinit var resultLauncher: ActivityResultLauncher<Intent> //launcher para recibir la foto tomada

    private var photoPaths = mutableListOf<String>()
    private var photosAdapter: PhotosAdapter? = null

    private val mainViewModel: MainViewModel by viewModels{
        MainViewModelFactory(PhotoRepository(this))
    }

    companion object{
        const val CAMERA_PERMISSION = 1  //Request id del permiso
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mainViewModel.loadSavedPhotoPaths()

        mainViewModel.photoPaths.observe(this){ paths ->
            photoPaths.addAll(paths)

            photosAdapter = PhotosAdapter(photoPaths){ photoPath, position ->
                //Manejo del click de cada foto
            }

            binding.rvPhotos.apply {
                layoutManager = GridLayoutManager(this@MainActivity, 3)
                adapter = photosAdapter
            }

        }


        binding.ivCamera.setOnClickListener {
            updateOrRequestPermission()
        }
    }

    private fun actionPermissionGranted(){
        Toast.makeText(
            this,
            "El permiso a la cámara se ha concedido",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun updateOrRequestPermission(){
        //Revisamos si tenemos el permiso
        cameraPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val permissionsToRequest = mutableListOf<String>()

        if(!cameraPermissionGranted)
            permissionsToRequest.add(Manifest.permission.CAMERA)

        if(permissionsToRequest.isNotEmpty()){
            //Tenemos que pedir el permiso
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                CAMERA_PERMISSION
            )
        }else{
            //Tenemos el permiso!!!
            actionPermissionGranted()
        }
    }

    override fun onRestart() {
        super.onRestart()
        updateOrRequestPermission()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //El usuario nos concedió el permiso
            actionPermissionGranted()
        }else{
            if(shouldShowRequestPermissionRationale(permissions[0].toString())){
                //Puedo mostrar el rationale
                AlertDialog.Builder(this)
                    .setTitle("Permiso requerido")
                    .setMessage("Se requiere el permiso solamente para tomar fotos, no video")
                    .setPositiveButton("Entendido"){ _, _ ->
                        updateOrRequestPermission()
                    }
                    .setNegativeButton("Cancelar"){ dialog, _ ->
                        dialog.dismiss()
                    }.create().show()
            }else{
                AlertDialog.Builder(this)
                    .setTitle("Permiso negado")
                    .setMessage("El permiso se ha negado permanentemente, " +
                            "por favor actívalo desde la configuración de la app")
                    .setNeutralButton("Ir a configuración"){ _, _ ->
                        startActivity(Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts(
                                "package",
                                packageName,
                                null
                            )
                        ))
                    }
                    .setOnDismissListener { dialog ->
                        dialog.dismiss()
                    }
                    .create().show()
            }
        }
    }
}