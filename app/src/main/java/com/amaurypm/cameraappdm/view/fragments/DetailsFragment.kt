package com.amaurypm.cameraappdm.view.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.amaurypm.cameraappdm.R
import com.amaurypm.cameraappdm.databinding.FragmentDetailsBinding
import com.amaurypm.cameraappdm.model.Photo
import com.amaurypm.cameraappdm.util.parcelable
import com.amaurypm.cameraappdm.view.activities.MainActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailsFragment : DialogFragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //Con View Binding
        _binding = FragmentDetailsBinding.inflate(LayoutInflater.from(context))
        return createPhotoDetailsDialog()
    }

    private fun createPhotoDetailsDialog(): Dialog {
        val photoDetailsDialog = AlertDialog.Builder(requireActivity())
        photoDetailsDialog.setView(binding.root)

        val bundle: Bundle? = arguments

        var position = 0
        var photo: Photo? = null

        if(bundle != null){

            position = bundle.getInt("position", 0)

            /*photo = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                bundle.getParcelable("photo", Photo::class.java)
                else
                bundle.getParcelable<Photo>("photo")*/

            photo = bundle.parcelable("photo")
        }

        binding.ivPhotoDetails.setImageBitmap(photo?.bitmap)

        binding.ivDelete.setOnClickListener {
            AlertDialog.Builder(requireActivity())
                .setTitle("Confirmación")
                .setMessage("¿Realmente deseas eliminar la foto?")
                .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, which ->
                    val size = (activity as MainActivity).photosAdapter?.itemCount
                    if(size!=null){
                        (activity as MainActivity).photos.removeAt(position)
                        (activity as MainActivity).photosAdapter?.notifyItemRemoved(position)
                        (activity as MainActivity).photosAdapter?.notifyItemRangeChanged(
                            position,
                            size
                        )
                    }
                    dialog.dismiss()
                    dismiss()
                })
                .setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
                .create()
                .show()


        }

        binding.ivClose.setOnClickListener {
            dismiss()
        }

        return photoDetailsDialog.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}