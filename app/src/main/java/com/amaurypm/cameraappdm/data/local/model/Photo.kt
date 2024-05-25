package com.amaurypm.cameraappdm.data.local.model

import android.graphics.Bitmap

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Photo(
    val bitmap: Bitmap
): Parcelable
