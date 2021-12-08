package com.udacity.moonstore.storeItems

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import com.udacity.moonstore.BuildConfig
import com.udacity.moonstore.R
import com.udacity.moonstore.storeItems.models.Store
import com.udacity.moonstore.data.StockNotificationStatus
import com.udacity.moonstore.geofence.GeofenceBroadcastReceiver
import kotlinx.android.synthetic.main.activity_store.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class StoreActivity : AppCompatActivity() {

    private val storeViewModel: StoreViewModel by viewModel()

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var resolutionForResult: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private lateinit var geofencingClient: GeofencingClient
    private lateinit var stores: List<Store>

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE_EVENT
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        geofencingClient = LocationServices.getGeofencingClient(this)
        locationCallback = LocationCallback()
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        requestPermissionLauncher = registerForActivityResult()

        resolutionForResult =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
                if (activityResult.resultCode == RESULT_OK) {
                    checkDeviceLocationAndCreateGeofence()
                } else {
                    warningLocationPermission()
                }
            }

        storeViewModel.availableStores.observeForever { storeList ->
            stores = storeList
        }

        storeViewModel.stockNotificationStatus.observeForever { status ->
            when (status) {
                StockNotificationStatus.NOTIF_ON -> {
                    checkPermissionsAndCreateGeofence()
                }
                StockNotificationStatus.NOTIF_OFF -> {
                    //remove geofences
                }
            }
        }
        setContentView(R.layout.activity_store)
    }

    private fun registerForActivityResult() = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        var accepted = true
        permissions.forEach { permission ->
            if (!permission.value) {
                accepted = false
            }
        }
        if (accepted) {
            checkPermissionsAndCreateGeofence()
        } else {
            warningLocationPermission()
        }
    }

    private fun warningLocationPermission() {
        Snackbar.make(
            findViewById(R.id.nav_host_fragment),
            R.string.location_required_error, Snackbar.LENGTH_LONG
        )
            .setAction(R.string.settings) {
                startActivity(Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }
            .show()
        storeViewModel.updateStockNotificationStatus(this, StockNotificationStatus.NOTIF_OFF)
    }

    private fun checkPermissionsAndCreateGeofence() {
        if (locationPermissionApproved()) {
            checkDeviceLocationAndCreateGeofence()
        } else {
            requestLocationPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun createGeofence() {
        trackLocation()
        if (::stores.isInitialized) {

            val geofences = stores.map { store ->
                Geofence.Builder()
                    .setRequestId(store.id.toString())
                    .setCircularRegion(
                        store.lat,
                        store.lng,
                        GEOFENCE_RADIUS_IN_METERS
                    )
                    .setExpirationDuration(GEOFENCE_EXPIRATION)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .build()
            }

            val geofencingRequest = GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences(geofences)
                .build()

            geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)?.run {
                addOnSuccessListener {
                    geofencingRequest.geofences.forEach { geofence ->
                        Toast.makeText(
                            this@StoreActivity, "geofence ${geofence.requestId} added",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
                addOnFailureListener {
                    Toast.makeText(
                        this@StoreActivity, R.string.geofence_not_added,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }

    private fun checkDeviceLocationAndCreateGeofence() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(this)
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution).build()
                    resolutionForResult.launch(intentSenderRequest)

                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d("POI_APP", "Error geting location settings resolution: " + sendEx.message)
                }
            } else {
                Snackbar.make(
                    findViewById(R.id.nav_host_fragment),
                    R.string.error_happened, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                }.show()
            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                createGeofence()
            }
        }
    }

    private fun requestLocationPermissions() {
        if (locationPermissionApproved()) {
            return
        }
        val permissions = if (storeViewModel.runningQOrLater) {
            storeViewModel.qPermissionList
        } else {
            storeViewModel.beforeQPermissionList
        }
        requestPermissionLauncher.launch(permissions.toTypedArray())
    }

    @TargetApi(29)
    private fun locationPermissionApproved(): Boolean {
        val foregroundLocationApproved =
            checkPermission(FINE_LOCATION_PERMISSION)
        val backgroundPermissionApproved =
            if (storeViewModel.runningQOrLater) {
                checkPermission(BACKGROUND_LOCATION_PERMISSION)
            } else {
                true
            }
        return foregroundLocationApproved and backgroundPermissionApproved
    }

    private fun checkPermission(
        permission: String
    ): Boolean {
        val permissionGranted = PackageManager.PERMISSION_GRANTED

        return permissionGranted ==
                ActivityCompat.checkSelfPermission(
                    this,
                    permission
                )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                (nav_host_fragment as NavHostFragment).navController.popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("MissingPermission")
    private fun trackLocation() {
        val locationRequest = LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(2000)

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    companion object {
        const val GEOFENCE_RADIUS_IN_METERS = 200f

        //Geofence expires after 1 month
        private val GEOFENCE_EXPIRATION = TimeUnit.DAYS.toMillis(30)

        @TargetApi(29)
        private const val BACKGROUND_LOCATION_PERMISSION =
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        private const val FINE_LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
        private const val ACTION_GEOFENCE_EVENT =
            "moonstore.ACTION_GEOFENCE_EVENT"
    }
}
