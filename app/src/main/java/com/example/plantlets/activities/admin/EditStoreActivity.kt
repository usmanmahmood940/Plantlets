package com.example.plantlets.activities.admin

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.plantlets.R
import com.example.plantlets.databinding.ActivityEditStoreBinding
import com.example.plantlets.models.Store
import com.example.plantlets.repositories.StoreRepository
import com.example.plantlets.utils.Constants
import javax.inject.Inject

class EditStoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditStoreBinding
    private lateinit var store: Store
    private var status : String? = null

    @Inject
    lateinit var storeRepository:StoreRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUI()
        setOnClick()
    }

    private fun setUI() {

        status = intent.getStringExtra("storeStatus")
        binding.apply {
            etItemName.setText(intent.getStringExtra("storeName"))
            etStoreLoc.setText(intent.getStringExtra("storeAddress"))

        }
        if(status == Constants.ACTIVE) setActive(binding.activeBtn)
        if(status == Constants.Non_ACTIVE) setActive(binding.nActiveBtn)
        if(status == Constants.IN_PROCESS) setActive(binding.progressBtn)

    }

    private fun setOnClick() {
        binding.apply {
            activeBtn.setOnClickListener {
                setUnActive(nActiveBtn)
                setUnActive(progressBtn)
                setActive(activeBtn)
                status = Constants.ACTIVE
            }
            nActiveBtn.setOnClickListener {
                setUnActive(progressBtn)
                setUnActive(activeBtn)
                setActive(nActiveBtn)
                status = Constants.Non_ACTIVE
            }
            progressBtn.setOnClickListener {
                setUnActive(activeBtn)
                setUnActive(nActiveBtn)
                setActive(progressBtn)
                status = Constants.IN_PROCESS
            }
            btnAction.setOnClickListener{
                storeRepository.upsertStore(intent.getStringExtra("storeEmail"),status)
            }
        }
    }

    private fun setActive(view : TextView)
    {
        view.apply {
            background = resources.getDrawable(R.drawable.button_bg_2)
            setTextColor(resources.getColor(R.color.white))
        }
    }
    private fun setUnActive(view : TextView)
    {
        view.apply {
            background = resources.getDrawable(R.drawable.button_bg)
            setTextColor(resources.getColor(R.color.light_green))
        }
    }
}