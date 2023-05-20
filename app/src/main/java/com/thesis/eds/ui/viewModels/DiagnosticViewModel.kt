package com.thesis.eds.ui.viewModels

import android.app.Activity
import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thesis.eds.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DiagnosticViewModel : ViewModel() {

    private val _outputFilePath = MutableLiveData<String>()
    private val outputFilePath: LiveData<String>
        get() = _outputFilePath

    private val _uri = MutableLiveData<String>()
    val uri: LiveData<String>
        get() = _uri

    private var isSavingPhoto = false

    fun savePhoto(activity: Activity, resizedBitmap: Bitmap) {
        if (isSavingPhoto) {
            return
        }
        isSavingPhoto = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Convert the resized bitmap to a byte array
                val outputStream = ByteArrayOutputStream()
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                val buffer = outputStream.toByteArray()

                // Save the photo to local storage using a file output stream
                val outputFile = File(getOutputDirectory(activity), "photo.jpg")
                val fileOutputStream = FileOutputStream(outputFile)
                fileOutputStream.write(buffer)
                fileOutputStream.close()

                val uriTmp = Uri.fromFile(outputFile)
                // Post the file path to the outputFilePath LiveData
                _outputFilePath.postValue(outputFile.absolutePath)
                _uri.postValue(uriTmp.toString())

                Log.d("EDSThesis_DiagnosticVM", "Variables check --> Output filepath == $outputFilePath | Output file == $outputFile | Uri == $uriTmp ----------")
                Log.d(TAG, "Photo saved to local storage.")
            } catch (e: IOException) {
                Log.e(TAG, "Error saving photo to local storage: ${e.message}", e)
            } finally {
                // Reset the flag to allow another coroutine to run
                isSavingPhoto = false
            }
        }
    }

    private fun getOutputDirectory(activity: Activity): File {
        val mediaDir = activity.externalMediaDirs.firstOrNull()?.let {
            File(it, activity.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else activity.filesDir
    }
}

