package com.bobsfirstapp.whatsdowntthere

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var overlayLayout: FrameLayout
    private val mapHelper = MapDataHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        overlayLayout = findViewById(R.id.overlayContainer)

        // Show disclaimer first
        AlertDialog.Builder(this)
            .setTitle("Disclaimer")
            .setMessage("This app is only for entertainment purposes, not for navigation or pilot directional use.")
            .setPositiveButton("I Understand") { _, _ -> requestPermissions() }
            .setCancelable(false)
            .show()
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val needs = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (needs.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, needs.toTypedArray(), 100)
        } else {
            startApp()
        }
    }

    private fun startApp() {
        startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5f) { location ->
                updateOverlays(location)
            }
        }
    }

    private fun updateOverlays(location: Location) {
        runOnUiThread {
            overlayLayout.removeAllViews()
            val features = mapHelper.getNearby(
                location.latitude,
                location.longitude,
                60.0
            )
            for (f in features) {
                val tv = TextView(this).apply {
                    text = f.name
                    setTextColor(f.color)
                    textSize = 16f
                    x = f.x
                    y = f.y
                }
                overlayLayout.addView(tv)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 100 && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            startApp()
        } else {
            Toast.makeText(this, "App needs camera and location to work.", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
