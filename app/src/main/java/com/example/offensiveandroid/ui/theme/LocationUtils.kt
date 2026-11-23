package com.example.offensiveandroid.ui.theme

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.*

class LocationUtils(private val context: Context) {

    //this is used to request location updates and rtrieves last location

    private val fusedLocationClient: FusedLocationProviderClient=
        LocationServices.getFusedLocationProviderClient(context)

    //holds reference to callback that listens for location updates
    private var locationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(onLocationReceived:(Location?)->Unit){
        try{
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    onLocationReceived(location)
                }
                .addOnFailureListener { e ->
                    Log.e("LocationUtils","Error getting last known location",e)
                    onLocationReceived(null)
                }
        } catch (e:Exception){
            Log.e("LocationUtils","Location permit not granted")
            onLocationReceived(null)
        }
    }

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates(
        interval: Long = 10000L,
        fasterInterval: Long = 5000L,
        priority: Int = Priority.PRIORITY_HIGH_ACCURACY,
        onLocationReceived: (Location?) -> Unit
    ){
        if (!isLocationEnabled(context)){
            Log.e("LocationUtils","Location services are disabled")
            onLocationReceived(null)
            return
        }

        if (!hasLocationPermission(context)){
            Log.e("LocationUtils","Location permissions not granted")
            onLocationReceived(null)
            return
        }

        val locationRequest = LocationRequest.Builder(interval)
            .setPriority(priority)
            .setMinUpdateIntervalMillis(fasterInterval)
            .build()
        locationCallback = object: LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult){
                val location = locationResult.lastLocation
                if (location != null){
                    Log.d("LocationUtils","Location uopdates received : ${location.latitude}, ${location.longitude}")
                    onLocationReceived(location)
                } else {
                    Log.e("LocationUtils","No location updatse available")
                    onLocationReceived(null)
                }
            }
        }
        locationCallback?.let { callback ->
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                callback,
                Looper.getMainLooper()
            )
            Log.d("LocationUtils","Started requesting location updates")
        }
    }
    private fun hasLocationPermission(context:Context): Boolean{
        return android.Manifest.permission.ACCESS_FINE_LOCATION.let{
            androidx.core.content.ContextCompat.checkSelfPermission(context,it)
        } == android.content.pm.PackageManager.PERMISSION_GRANTED
    }
    private fun isLocationEnabled(context:Context):Boolean{
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
    }

}