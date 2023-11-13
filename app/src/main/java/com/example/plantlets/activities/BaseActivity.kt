package com.example.plantlets.activities

import android.content.DialogInterface
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.example.plantlets.R
import com.example.plantlets.databinding.ActivityBaseBinding
import com.example.plantlets.models.User
import com.example.plantlets.repositories.LocalRepository
import com.example.plantlets.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint

open class BaseActivity : AppCompatActivity() {

    private var progressBar: ProgressBar? = null
    lateinit var baseBinding: ActivityBaseBinding

    @Inject
    lateinit var localRepository: LocalRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseBinding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(baseBinding.root)
        hideStatusbar()


    }

    fun setChildView(childView: View) {
        baseBinding.layoutContainer.addView(childView)
    }

    val dismissListener = object : DialogInterface.OnDismissListener {
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
        message: String?,
        positiveButtonText: String? = null,
        positiveButtonClickListener: DialogInterface.OnClickListener? = onAlertBoxClickListener,
        negativeButtonText: String? = null,
        negativeButtonClickListener: DialogInterface.OnClickListener? = null,
        onDismissListener: DialogInterface.OnDismissListener? = null,
    ) {
        val dialog = AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)

            onDismissListener?.let {
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
                setTextColor(ContextCompat.getColor(context, R.color.light_green))
            }
            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).apply {
                setTextColor(ContextCompat.getColor(context, R.color.black))
            }
        })
        dialog.show()

    }

    fun hideStatusbar() {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    fun showProgressBar() {
        baseBinding.baseProgressBar.visibility = View.VISIBLE

    }

    fun hideProgressBar() {
        baseBinding.baseProgressBar.visibility = View.GONE
    }

    fun getNavigation(): Class<out BaseActivity> {
        var destinationClass: Class<out BaseActivity> = LoginActivity::class.java
        val user: User? = localRepository.getCurrentUserData()

        user?.let {
            destinationClass = when (user.type) {
                Constants.VENDOR_TYPE -> {
                    SellerHomeActivity::class.java
                }

                Constants.USER_TYPE -> {
                    UserHomeActivity::class.java
                }

                Constants.ADMIN_TYPE -> {
                    AdminHomeActivity::class.java
                }
                else -> {
                    UserHomeActivity::class.java
                }
            }
        }
        return destinationClass
    }


    fun setTitleBar(title: String = getString(R.string.app_name), backVisibility: Boolean? = null,activity:AppCompatActivity) {

        activity.supportActionBar?.apply {
            setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
            setCustomView(R.layout.custom_toolbar)
            customView.findViewById<TextView>(R.id.tvTitle).text = title
            backVisibility?.let {
                customView.findViewById<ImageView>(R.id.ivBack).apply {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }
}