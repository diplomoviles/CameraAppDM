package com.amaurypm.cameraappdm.data

import android.content.Context
import android.os.Environment

class PhotoRepository(
    private val context: Context
) {
    fun loadSavedPhotoPaths(): List<String>{

        val storageDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: return emptyList()

        val files = storageDirectory.listFiles { file ->
            // Filtramos solo imágenes por extensión
            val name = file.name.lowercase()
            name.endsWith(".jpg")
        } ?: return emptyList()

        return files
            .sortedBy { it.lastModified() }   // más antiguos primero
            .map { file ->  file.absolutePath }
    }

    fun deletePhotoFile(position: Int){
        val storageDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        val files = storageDirectory?.listFiles { file ->
            // Filtramos solo imágenes por extensión
            val name = file.name.lowercase()
            name.endsWith(".jpg")
        }

        files?.let{ files ->
            files.sortedBy { it.lastModified() }   // más antiguos primero
            files[position].delete()
        }
    }
}