package com.amaurypm.cameraappdm.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amaurypm.cameraappdm.databinding.PhotoItemBinding
import com.amaurypm.cameraappdm.model.Photo
import com.amaurypm.cameraappdm.view.activities.MainActivity

/**
 * Creado por Amaury Perea Matsumura el 20/01/23
 */
class PhotosAdapter(private val context: Context, val photos: ArrayList<Photo>): RecyclerView.Adapter<PhotosAdapter.ViewHolder>(){

    class ViewHolder(view: PhotoItemBinding): RecyclerView.ViewHolder(view.root){
        val ivPhoto = view.ivPhoto
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PhotoItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = photos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ivPhoto.setImageBitmap(photos[position].bitmap)

        //Para los clicks
        holder.itemView.setOnClickListener {
            if(context is MainActivity) context.selectedPhoto(photos[position], position)
        }
    }


}