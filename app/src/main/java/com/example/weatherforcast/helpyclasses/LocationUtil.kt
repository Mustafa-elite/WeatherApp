package com.example.weatherforcast.helpyclasses

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import com.example.weatherforcast.R
import com.google.android.gms.location.FusedLocationProviderClient
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
                        fetchLastCachedLocation(context,fusedLocation,onSuccess,onFailure)
                    }
                }
            }
            fusedLocation.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())

        }

        @SuppressLint("MissingPermission")
        private fun fetchLastCachedLocation(
            context: Context,
            fusedLocation: FusedLocationProviderClient,
            onSuccess: (Location) -> Unit,
            onFailure: (Throwable) -> Unit
        ) {

            fusedLocation.lastLocation.addOnSuccessListener {location->
                if(location!=null){
                    onSuccess(location)
                }
                else{
                    onFailure(NullPointerException(context.getString(R.string.cant_get_location)))
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
                    val city = addresses[0].locality ?: context.getString(R.string.unknown_city)
                    val country = addresses[0].countryName ?: context.getString(R.string.unknown_country)
                    city to country
                } else {
                    context.getString(R.string.unknown_city) to context.getString(R.string.unknown_country)
                }
            } catch (e: Exception) {
                context.getString(R.string.unknown_city) to context.getString(R.string.unknown_country)
            }
        }
    }

}