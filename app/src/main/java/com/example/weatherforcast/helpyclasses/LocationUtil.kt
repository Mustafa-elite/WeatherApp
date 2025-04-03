package com.example.weatherforcast.helpyclasses

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale

class LocationUtil {
    companion object{
        @SuppressLint("MissingPermission")
        fun getLonLatLocation(context: Context,
                              onSuccess:(Location)->Unit,
                              onFailure:(Throwable)->Unit)
        {
            val fusedLocation= LocationServices.getFusedLocationProviderClient(context)
            val locationRequest=LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,5000)
                .setWaitForAccurateLocation(true).build()


            val locationCallback=object:LocationCallback(){
                override fun onLocationResult(result: LocationResult) {
                    val location=result.lastLocation
                    if(location!=null){
                        onSuccess(location)
                        fusedLocation.removeLocationUpdates(this)
                    }
                    else{
                        fetchLastCachedLocation(fusedLocation,onSuccess,onFailure)
                    }
                }
            }
            fusedLocation.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())

        }

        @SuppressLint("MissingPermission")
        private fun fetchLastCachedLocation(fusedLocation: FusedLocationProviderClient,
                                            onSuccess: (Location) -> Unit,
                                            onFailure: (Throwable) -> Unit) {

            fusedLocation.lastLocation.addOnSuccessListener {location->
                if(location!=null){
                    onSuccess(location)
                }
                else{
                    onFailure(NullPointerException("Cant get the Location"))
                }

            }.addOnFailureListener{
                onFailure(it)
            }


        }

        fun getCityAndCountry(lat: Double, lon: Double, context: Context): Pair<String, String> {
            val geocoder = Geocoder(context, Locale.getDefault())
            return try {
                val addresses = geocoder.getFromLocation(lat, lon, 1)
                if (!addresses.isNullOrEmpty()) {
                    val city = addresses[0].locality ?: "Unknown City"
                    val country = addresses[0].countryName ?: "Unknown Country"
                    city to country
                } else {
                    "Unknown City" to "Unknown Country"
                }
            } catch (e: Exception) {
                "Unknown City" to "Unknown Country"
            }
        }
    }

}