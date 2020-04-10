package com.github.weg_li_android

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProviders
import com.github.weg_li_android.data.model.Report
import com.github.weg_li_android.ui.main.viewmodel.MainViewModel
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private val report = Report()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializePhotoList(findViewById(R.id.photos_grid))
        setupCarTypeSpinner()
        setupViolationSpinner()
        durationText.setOnClickListener {
        }

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        sendButton.setOnClickListener { mainViewModel.sendReport(report) }
    }

    private fun initializePhotoList(view: Any) {
        if (view is RecyclerView) {
             val images = arrayOf(
                R.drawable.ic_baseline_add_a_photo_24,
                R.drawable.ic_baseline_add_to_photos_24
            )
            val photoAdapter = PhotoAdapter(this, images)
            // view.adapter = photoAdapter
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
}