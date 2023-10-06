package com.example.plantlets.activities

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.plantlets.R


open class BaseActivity : AppCompatActivity() {

    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        hideStatusbar()

    }

    public val dismissListener = object : DialogInterface.OnDismissListener {
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
        onDismissListener: DialogInterface.OnDismissListener? = null,
    ) {
        val dialog = AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)

            onDismissListener?.let{
                setOnDismissListener(onDismissListener)

            }
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
    fun showProgressBar(context:Context) {
        if (progressBar == null) {
            progressBar = ProgressBar(context)
            progressBar!!.indeterminateTintList = getColorStateList(R.color.light_green)
            progressBar!!.layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = android.view.Gravity.CENTER
            }
            progressBar!!.isIndeterminate = true
        }

        val contentView = findViewById<ViewGroup>(android.R.id.content)
        progressBar!!.layoutParams.width = contentView.width
        progressBar!!.layoutParams.height = contentView.height

        contentView.addView(progressBar)
    }

    fun hideProgressBar() {
        if (progressBar != null) {
            val contentView = findViewById<ViewGroup>(android.R.id.content)
            contentView.removeView(progressBar)
        }
    }

}