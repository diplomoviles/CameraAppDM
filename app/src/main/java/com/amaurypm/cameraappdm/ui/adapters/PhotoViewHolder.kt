package com.amaurypm.cameraappdm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amaurypm.cameraappdm.databinding.PhotoItemBinding
import com.amaurypm.cameraappdm.utils.decodeSampledBitmapPath
import com.amaurypm.cameraappdm.utils.rotateImageIfRequired

class PhotoViewHolder(
    private val binding: PhotoItemBinding,
    private val onPhotoClick: (String, Int) -> Unit
): RecyclerView.ViewHolder(binding.root) {

    private var currentPhotoPath: String? = null
    private var currentPhotoPosition: Int? = null

    init{
        //Manejando el click de cada foto
        binding.root.setOnClickListener {
            currentPhotoPath?.let{ photoPath ->
                currentPhotoPosition?.let{ position ->
                    onPhotoClick(photoPath, position)
                }
            }
        }
    }

    fun bind(photoItemPath: String, position: Int){
        currentPhotoPath = photoItemPath
        currentPhotoPosition = position

        //Cargamos una versión reducida de la foto
        val bitmap = decodeSampledBitmapPath(photoItemPath, 500, 900)
        //Rotamos la imagen si los metadatos Exif así lo indican
        val rotatedBitmap = rotateImageIfRequired(bitmap, photoItemPath)

        binding.ivPhoto.setImageBitmap(rotatedBitmap)
    }

    companion object{
        fun create(
            parent: ViewGroup,
            onPhotoClick: (String, Int) -> Unit
        ): PhotoViewHolder{
            val binding = PhotoItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return PhotoViewHolder(binding, onPhotoClick)
        }
    }
}