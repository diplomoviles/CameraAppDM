package com.amaurypm.cameraappdm.ui.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.amaurypm.cameraappdm.MainViewModel
import com.amaurypm.cameraappdm.databinding.FragmentDetailsBinding
import com.amaurypm.cameraappdm.utils.decodeSampledBitmapPath
import com.amaurypm.cameraappdm.utils.rotateImageIfRequired
import java.io.File

class DetailsFragment : DialogFragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //Inflamos la vista
        _binding = FragmentDetailsBinding.inflate(layoutInflater)
        val dialog = createPhotoDetailsDialog()
        return dialog
    }

    private fun createPhotoDetailsDialog(): Dialog {
        val photoDetailsDialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)

        //Recuperando los parámetros que llegan
        val bundle: Bundle? = arguments

        var position: Int
        var photoPath: String

        if(bundle !=null){
            position = bundle.getInt("position", 0)
            photoPath = bundle.getString("photo_path", "")

            val bitmap = decodeSampledBitmapPath(photoPath, 500, 900)
            val rotatedBitmap = rotateImageIfRequired(bitmap, photoPath)

            //Cargamos la imagen en el ImageView
            binding.ivPhotoDetails.setImageBitmap(rotatedBitmap)

            binding.ivShare.setOnClickListener {
                //Para función de compartir
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/jpeg"
                    putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                        requireContext(),
                        "com.amaurypm.cameraappdm.fileprovider",
                        File(photoPath)
                    ))
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                //Revisamos si hay apps que puedan abrir el tipo de contenido
                val packageManager = requireContext().packageManager
                if(shareIntent.resolveActivity(packageManager)!=null){
                    requireContext().startActivity(Intent.createChooser(shareIntent, "Compartir imagen"))
                }else{
                    //No hay apps compatibles para ese tipo de contenido o MIME Type
                    Toast.makeText(
                        requireContext(),
                        "No hay apps compatibles para compartir",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            }

            binding.ivDelete.setOnClickListener {
                //Para función de eliminar
                AlertDialog.Builder(requireContext())
                    .setTitle("Confirmación")
                    .setMessage("¿Realmente deseas eliminar la foto?")
                    .setPositiveButton("Aceptar"){ dialog, _ ->
                        mainViewModel.notifyItemRemoved(position)
                        dialog.dismiss() //del diálogo de confirmación
                        dismiss() //del diálogo de detalles
                    }
                    .setNegativeButton("Cancelar"){ dialog, _ ->
                        dialog.dismiss()
                    }
                    .create().show()
            }

            binding.ivClose.setOnClickListener {
                //Para función de cerrar el diálogo
                dismiss()
            }

        }

        return photoDetailsDialog.create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}