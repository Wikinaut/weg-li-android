package com.github.weg_li_android

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.weg_li_android.data.model.Report
import com.github.weg_li_android.ui.main.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class MainActivity : AppCompatActivity(), PhotoRecyclerViewAdapter.ItemClickListener {

    private lateinit var photoAdapter: PhotoRecyclerViewAdapter
    private lateinit var mainViewModel: MainViewModel
    private val report = Report()
    private val pickImage = 1
    private val takeImage = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        setContentView(R.layout.activity_main)
        setupPhotoRecyclerView()
        setupCarTypeSpinner()
        setupViolationSpinner()
        durationText.setOnClickListener {
        }

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        sendButton.setOnClickListener { mainViewModel.sendReport(report) }
    }

    private fun setupPhotoRecyclerView() {
        val data = mutableListOf<Bitmap>()

        val recyclerView = findViewById<RecyclerView>(R.id.photos_grid)
        val numberOfColumns = 3
        recyclerView.layoutManager = GridLayoutManager(this, numberOfColumns)
        photoAdapter = PhotoRecyclerViewAdapter(this, data)
        photoAdapter.setClickListener(this)
        recyclerView.adapter = photoAdapter

        take_picture_button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN ->
                    dispatchTakePictureIntent()
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                    v.performClick()
            }
            false
        }

        add_picture_button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN ->
                    dispatchPickPictureIntent()
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                    v.performClick()
            }
            false
        }

    }

    private fun dispatchPickPictureIntent() {
        val pickPhoto = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickPhoto.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(pickPhoto, pickImage)
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, takeImage)
            }
        }
    }

    private fun setupCarTypeSpinner() {
        ArrayAdapter.createFromResource(
            this,
            R.array.car_type_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            carTypeSpinner.adapter = adapter
        }
        carTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                report.type = (view as AppCompatTextView).text.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupViolationSpinner() {
        ArrayAdapter.createFromResource(
            this,
            R.array.violation_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            violationSpinner.adapter = adapter
        }
    }

    override fun onItemClick(view: View?, position: Int) {
        Timber.e("You clicked number %s", position.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && data != null) {
            when(requestCode) {
                pickImage -> {
                    if (data.clipData != null) {
                        val count = data.clipData!!.itemCount
                        for(i in 0 until count) {
                            val imageUri : Uri? = data.clipData!!.getItemAt(i).uri
                            if(Build.VERSION.SDK_INT > 28) {
                                val source =
                                    imageUri?.let {
                                        ImageDecoder.createSource(this.contentResolver,
                                            it
                                        )
                                    }
                                val bitmap = source?.let { ImageDecoder.decodeBitmap(it) }
                                if (bitmap != null) {
                                    val insertIndex = photoAdapter.addItem(bitmap)
                                    photoAdapter.notifyItemInserted(insertIndex)
                                }
                            }

                            /* we should add this for SDK_INT <= 28
                            val bitmap = MediaStore.Images.Media.getBitmap(
                                this.contentResolver,
                                imageUri
                            )*/
                        }
                    }
                    else if (data.data != null) {
                        val mImageUri: Uri = data.data!! // TODO: Add something useful here.
                    }

                    }
                takeImage ->
                    data.hashCode() // TODO: Replace this with something useful.
            }

        }
    }
}