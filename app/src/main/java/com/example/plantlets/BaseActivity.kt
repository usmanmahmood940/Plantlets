package com.example.plantlets

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusbar()

    }

    // Function to show an alert dialog
    fun showAlert(
        title: String,
        message: String,
        positiveButtonText: String = "OK",
        positiveButtonClickListener: DialogInterface.OnClickListener? = null,
        negativeButtonText: String? = null,
        negativeButtonClickListener: DialogInterface.OnClickListener? = null,
        onDismissListener: DialogInterface.OnDismissListener? = null,
    ) {
        val dialog = AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setOnDismissListener(onDismissListener)
            setPositiveButton(positiveButtonText, positiveButtonClickListener)

            if (negativeButtonText != null && negativeButtonClickListener != null) {
                setNegativeButton(negativeButtonText, negativeButtonClickListener)
            }
            if (positiveButtonClickListener != null) {
                setPositiveButton(positiveButtonText, positiveButtonClickListener)
            }
        }.create()
        dialog.setOnShowListener(DialogInterface.OnShowListener {
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).apply {
                setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        })
        dialog.show()

    }

    fun hideStatusbar(){

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

}