package com.example.plantlets.activities

import android.content.DialogInterface
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.plantlets.R


open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusbar()

    }

    private val dismissListener = object : DialogInterface.OnDismissListener {
        override fun onDismiss(dialog: DialogInterface?) {
            finish()
        }
    }
    private val onAlertBoxClickListener = object : DialogInterface.OnClickListener {
        override fun onClick(dialog: DialogInterface?, which: Int) {
            finish()
        }
    }


    // Function to show an alert dialog
    fun showAlert(
        title: String,
        message: String,
        positiveButtonText: String? = null,
        positiveButtonClickListener: DialogInterface.OnClickListener? = onAlertBoxClickListener,
        negativeButtonText: String? = null,
        negativeButtonClickListener: DialogInterface.OnClickListener? = null,
        onDismissListener: DialogInterface.OnDismissListener? = dismissListener,
    ) {
        val dialog = AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setOnDismissListener(onDismissListener)


            if (negativeButtonText != null && negativeButtonClickListener != null) {
                setNegativeButton(negativeButtonText, negativeButtonClickListener)
            }
            if (positiveButtonText != null) {
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