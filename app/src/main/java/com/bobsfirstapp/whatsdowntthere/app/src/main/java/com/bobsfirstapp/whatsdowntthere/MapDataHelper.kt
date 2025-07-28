package com.bobsfirstapp.whatsdowntthere

import android.graphics.Color
import kotlin.math.*

data class FeatureView(val name: String, val x: Float, val y: Float, val color: Int)

class MapDataHelper {

    private val features = listOf(
        // Alaska
        Feature("Anchorage", 61.2181, -149.9003, "city", Color.YELLOW),
        Feature("Fairbanks", 64.8378, -147.7164, "city", Color.YELLOW),
        Feature("Juneau", 58.3019, -134.4197, "city", Color.YELLOW),
        Feature("Yukon River", 64.0, -145.0, "water", Color.BLUE),
        Feature("Kenai River", 60.5, -151.0, "water", Color.BLUE),
        Feature("Glenn Highway", 61.5, -149.0, "road", Color.WHITE),
        Feature("Richardson Highway", 64.0, -147.0, "road", Color.WHITE),
        // Hawaii
        Feature("Honolulu", 21.3069, -157.8583, "city", Color.YELLOW),
        Feature("Hilo", 19.7050, -155.0850, "city", Color.YELLOW),
        Feature("Maui", 20.7500, -156.2500, "city", Color.YELLOW),
        Feature("Kauai", 22.0000, -159.5000, "city", Color.YELLOW),
        // Lower 48
        Feature("Seattle", 47.6062, -122.3321, "city", Color.YELLOW),
        Feature("Denver", 39.7392, -104.9903, "city", Color.YELLOW),
        Feature("Mississippi River", 45.0, -95.0, "water", Color.BLUE),
        Feature("I-5", 46.0, -122.0, "road", Color.WHITE)
        // In real app: 10,000+ entries loaded from file
    )

    fun getNearby(lat: Double, lon: Double, radiusKm: Double): List<FeatureView> {
        return features.filter { haversine(lat, lon, it.lat, it.lon) <= radiusKm }
            .map {
                // Fake screen position (real version uses camera FOV + orientation)
                FeatureView(it.name, 500f + Math.random().toFloat() * 200, 800f + Math.random().toFloat() * 200, it.color)
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
}

data class Feature(
    val name: String,
    val lat: Double,
    val lon: Double,
    val type: String,
    val color: Int
)
