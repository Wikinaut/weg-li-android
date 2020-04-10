package com.github.weg_li_android

import PhotoRecyclerViewAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
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


class MainActivity : AppCompatActivity(), PhotoRecyclerViewAdapter.ItemClickListener {

    private lateinit var photoAdapter: PhotoRecyclerViewAdapter
    private lateinit var mainViewModel: MainViewModel
    private val report = Report()
    private val PICK_IMAGE = 1
    private val TAKE_IMAGE = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val data = arrayOf(
            "1",
            "2"
        )

        val recyclerView = findViewById<RecyclerView>(R.id.photos_grid)
        val numberOfColumns = 3
        recyclerView.layoutManager = GridLayoutManager(this, numberOfColumns)
        photoAdapter = PhotoRecyclerViewAdapter(this, data)
        photoAdapter.setClickListener(this)
        recyclerView.adapter = photoAdapter

        take_picture_button.setOnTouchListener(OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN ->
                    dispatchTakePictureIntent()
                    //return@OnTouchListener true // if you want to handle the touch event
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                    v.performClick()
                   // return@OnTouchListener true // if you want to handle the touch event
            }
            false
        })

        add_picture_button.setOnTouchListener(OnTouchListener {v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN ->
                    dispatchPickPictureIntent()
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                    v.performClick()
            }
            false
        })

    }

    private fun dispatchPickPictureIntent() {
        val pickPhoto = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(pickPhoto, PICK_IMAGE)

    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, TAKE_IMAGE)
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
        Log.i("TAG", "You clicked number " + photoAdapter.getItem(position) + ", which is at cell position " + position);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_CANCELED && data != null) {
            when(resultCode) {
                PICK_IMAGE ->
                    data.hashCode()
                TAKE_IMAGE ->
                    data.hashCode()
            }

        }
    }
}