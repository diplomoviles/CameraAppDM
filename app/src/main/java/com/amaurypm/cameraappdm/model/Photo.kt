package com.amaurypm.cameraappdm.model

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Creado por Amaury Perea Matsumura el 20/01/23
 */

@Parcelize
data class Photo(
    val bitmap: Bitmap
): Parcelable



