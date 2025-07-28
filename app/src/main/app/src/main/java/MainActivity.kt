package com.bobsfirstapp.whatsdownthere

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
import kotlin.math.*

class MainActivity : AppCompatActivity() {

    private lateinit var overlayLayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        overlayLayout = findViewById(R.id.overlay_container)

        AlertDialog.Builder(this)
            .setTitle("Disclaimer")
            .setMessage("This app is only for entertainment purposes, not for navigation or pilot directional use.")
            .setPositiveButton("I Understand") { _, _ -> requestPermissions() }
            .setCancelable(false)
            .show()
    }

    private fun requestPermissions() {
        val perms = arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
        val needs = perms.filter { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
        if (needs.isNotEmpty()) ActivityCompat.requestPermissions(this, needs.toTypedArray(), 100)
        else startApp()
    }

    private fun startApp() {
        startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5f) { loc -> updateOverlays(loc) }
        }
    }

    private fun updateOverlays(location: Location) {
        runOnUiThread {
            overlayLayout.removeAllViews()
            val cities = listOf(
                "Anchorage" to 61.2181 to -149.9003,
                "Fairbanks" to 64.8378 to -147.7164,
                "Juneau" to 58.3019 to -134.4197,
                "Honolulu" to 21.3069 to -157.8583
            )
            for ((name, lat, lon) in cities) {
                val d = haversine(location.latitude, location.longitude, lat, lon)
                if (d < 60) {
                    val tv = TextView(this).apply {
                        text = name
                        setTextColor(android.graphics.Color.YELLOW)
                        textSize = 18f
                        x = 400f + (Math.random() * 400).toFloat()
                        y = 700f + (Math.random() * 400).toFloat()
                    }
                    overlayLayout.addView(tv)
                }
            }
        }
    }

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 100 && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            startApp()
        } else {
            Toast.makeText(this, "App needs camera and location.", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
