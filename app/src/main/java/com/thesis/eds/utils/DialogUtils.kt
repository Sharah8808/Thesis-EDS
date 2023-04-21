package com.thesis.eds.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog

object DialogUtils {

    fun showExitAlertDialog(context: Context, onPositiveButtonClick: () -> Unit) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Confirmation")
        alertDialogBuilder.setMessage("Data akan hilang. Yakin ingin kembali?")
            .setCancelable(false)
            .setPositiveButton("Iya") { _, _ ->
                onPositiveButtonClick.invoke()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}
