package com.amaurypm.cameraappdm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amaurypm.cameraappdm.data.local.model.Photo
import com.amaurypm.cameraappdm.databinding.PhotoItemBinding

class PhotosAdapter(
    private val photos: MutableList<Photo>,
    private val onImageClick: (photo: Photo, position: Int) -> Unit
): RecyclerView.Adapter<PhotosAdapter.ViewHolder>() {

    class ViewHolder(private val binding: PhotoItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(photo: Photo){
            binding.ivPhoto.setImageBitmap(photo.bitmap)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = photos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(photos[position])

        //Click a cada elemento
        holder.itemView.setOnClickListener {
            onImageClick(photos[position], position)
        }

    }

}