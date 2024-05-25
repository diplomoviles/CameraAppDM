package com.amaurypm.cameraappdm.ui.fragments

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.amaurypm.cameraappdm.R
import com.amaurypm.cameraappdm.data.local.model.Photo
import com.amaurypm.cameraappdm.databinding.FragmentDetailsBinding
import com.amaurypm.cameraappdm.ui.MainViewModel
import com.amaurypm.cameraappdm.utils.retrieveParcelable

class DetailsFragment : DialogFragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentDetailsBinding.inflate(LayoutInflater.from(context))
        return createPhotoDetailsDialog()
    }

    private fun createPhotoDetailsDialog(): Dialog {

        val photoDetailsDialog = AlertDialog.Builder(requireActivity())
        photoDetailsDialog.setView(binding.root)

        //Instanciamos el viewModel
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        val bundle: Bundle? = arguments
        var position = 0
        var photo: Photo? = null

        if(bundle != null){
            position = bundle.getInt("position", 0)
            /*photo = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        bundle.getParcelable("photo", Photo::class.java)
                    else
                        bundle.getParcelable<Photo>("photo")*/
            photo = bundle.retrieveParcelable("photo")
        }

        binding.ivPhotoDetails.setImageBitmap(photo?.bitmap)

        binding.ivDelete.setOnClickListener {

            val context = requireContext() //Guardamos el contexto

            AlertDialog.Builder(requireContext())
                .setTitle(context.getString(R.string.confirmation))
                .setMessage("¿Realmente desea eliminar la foto?")
                .setPositiveButton("Aceptar"){ dialog, _ ->
                    mainViewModel.notifyItemRemoved(position)
                    dialog.dismiss() //este es el dismiss del diálogo de confirmación
                    dismiss() //es el dismiss para el DialogFragment
                }
                .setNegativeButton("Cancelar"){ dialog, _ ->
                    dismiss()
                }
                .create()
                .show()
        }

        binding.ivClose.setOnClickListener {
            dismiss()
        }
        return photoDetailsDialog.create()

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }



}