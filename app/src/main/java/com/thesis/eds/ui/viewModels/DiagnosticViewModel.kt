package com.thesis.eds.ui.viewModels

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thesis.eds.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DiagnosticViewModel : ViewModel() {

    fun savePhoto(uri: Uri, context : Context, activity : Activity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val buffer = ByteArray(inputStream!!.available())
                inputStream.read(buffer)

                // Save the photo to local storage using a file output stream
                val fileOutputStream = FileOutputStream(File(getOutputDirectory(activity), "photo.jpg"))
                fileOutputStream.write(buffer)
                fileOutputStream.close()

                Log.d(TAG, "Photo saved to local storage.")
            } catch (e: IOException) {
                Log.e(TAG, "Error saving photo to local storage: ${e.message}", e)
            }
        }
    }

    fun getOutputDirectory(activity: Activity): File {
        val mediaDir = activity.externalMediaDirs.firstOrNull()?.let {
            File(it, activity.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else activity.filesDir
    }
}

