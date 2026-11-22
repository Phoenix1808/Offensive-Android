package com.example.offensiveandroid

import android.Manifest
import android.R
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionUtils {
    companion object {
    const val PERMISSION_REQUEST_ALL = 100
    val CONTACTS_PERMISSIONS = arrayOf(
        Manifest.permission.READ_CONTACTS
    )

    //notifications permissions
    val NOTIFICATIONS_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        arrayOf()
    }

    val SMS_PERMISSIONS = arrayOf(
        Manifest.permission.READ_SMS
    )
    val CALL_LOG_PERMISSIONS = arrayOf(
        Manifest.permission.READ_CALL_LOG
    )

    //LOCATIONS PERMISSIONS WITH ANDROID 14 SUPPORT
    val FOREGROUND_LOCATION_PERMISSIONS =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.FOREGROUND_SERVICE_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }

    val BACKGROUND_LOCATION_PERMISSION = arrayOf(
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )

    val AUDIO_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.FOREGROUND_SERVICE_MICROPHONE
        )
    } else {
        arrayOf(
            Manifest.permission.RECORD_AUDIO
        )
    }

    val CAMERA_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )

    fun arePermissionGranted(activity: Activity, permissions: Array<String>): Boolean {
        if (permissions.isEmpty()) return true

        //filtering out permissions that doesn'texist on current Android Version
        val applicablePermissions = permissions.filter { permission ->
            try {
                ContextCompat.checkSelfPermission(activity, permission)
                true
            } catch (e: Exception) {
                Log.d("Permissions", "Permission $permission not available on this android version")
                false
            }
        }.toTypedArray()

        applicablePermissions.forEach { permission ->
            val isGranted = ContextCompat.checkSelfPermission(
                activity, permission
            ) == PackageManager.PERMISSION_GRANTED
            Log.d("Permissions", "Checking $permissions: $isGranted")
        }

        return applicablePermissions.all {
            ContextCompat.checkSelfPermission(
                activity, it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun shouldShowRequestPermissionsRationale(
        activity: Activity,
        permissions: Array<String>
    ): Boolean {
        //filter out permission that don't exist on this android version
        val applicablePermissions = permissions.filter { permission ->
            try {
                ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
                true
            } catch (e: Exception) {
                false
            }
        }
        return applicablePermissions.any { permission ->
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        }
    }

    fun checkContactPermission(activity: Activity) =
        arePermissionGranted(activity, CONTACTS_PERMISSIONS)

    fun checkContactsPermission(activity: Activity) =
        checkContactPermission(activity)

    fun checkSMSPermission(activity: Activity) =
        arePermissionGranted(activity, SMS_PERMISSIONS)

    fun checkCallLogPermission(activity: Activity) =
        arePermissionGranted(activity, CALL_LOG_PERMISSIONS)

    fun checkForegroundLocationPermission(activity: Activity) =
        arePermissionGranted(activity, FOREGROUND_LOCATION_PERMISSIONS)

    fun checkBackgroundLocationPermission(activity: Activity) =
        arePermissionGranted(activity, BACKGROUND_LOCATION_PERMISSION)

    fun checkAudioPermission(activity: Activity) =
        arePermissionGranted(activity, AUDIO_PERMISSIONS)

    fun checkCameraPermission(activity: Activity) =
        arePermissionGranted(activity, CAMERA_PERMISSIONS)

    fun checkNotificationPermission(activity: Activity) =
        arePermissionGranted(activity, NOTIFICATIONS_PERMISSIONS)
   }
}