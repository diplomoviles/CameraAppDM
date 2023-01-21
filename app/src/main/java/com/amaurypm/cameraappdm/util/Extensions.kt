package com.amaurypm.cameraappdm.util

import android.app.Activity
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast

/**
 * Creado por Amaury Perea Matsumura el 21/01/23
 */


inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

fun Activity.mitoast(msg: String){
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun logs(msg: String){
    Log.d("LOGS", msg)
}