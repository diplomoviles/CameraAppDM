package com.amaurypm.cameraappdm.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class PhotosAdapter(
    private val photoPaths: MutableList<String>,
    private val onPhotoClick: (String, Int) -> Unit
): RecyclerView.Adapter<PhotoViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PhotoViewHolder =
        PhotoViewHolder.create(
            parent,
            onPhotoClick
        )

    override fun onBindViewHolder(
        holder: PhotoViewHolder,
        position: Int
    ) {
        holder.bind(photoPaths[position], position)
    }

    override fun getItemCount(): Int = photoPaths.size
}