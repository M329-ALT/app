package com.example.restaurants_3;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LocationHelper {
    private static final String TAG = "LocationHelper";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private final Context context;
    private final FusedLocationProviderClient fusedLocationProviderClient;
    private final Executor executor;

    public interface LocationResultCallback {
        void onLocationReceived(double latitude, double longitude);
        void onError(String errorMessage);
    }

    public LocationHelper(Context context) {
        this.context = context.getApplicationContext(); // استخدم الـ Application Context
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.context);
        executor = Executors.newSingleThreadExecutor(); // استخدم Executor لتشغيل العمليات في الخلفية
    }

    public void getLocationOrRequestPermission(Activity activity, LocationResultCallback callback) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation(callback);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void getCurrentLocation(LocationResultCallback callback) {
        if (!hasLocationPermissions()) {
            callback.onError("Location permissions not granted.");
            return;
        }

        Task<Location> lastLocationTask = fusedLocationProviderClient.getLastLocation();
        lastLocationTask.addOnSuccessListener(executor, location -> {
            if (location != null) {
                callback.onLocationReceived(location.getLatitude(), location.getLongitude());
            } else {
                requestNewLocation(callback);
            }
        }).addOnFailureListener(executor, e -> {
            Log.e(TAG, "Failed to get last location", e);
            callback.onError("Failed to get location: " + e.getMessage());
        });
    }

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    private void requestNewLocation(LocationResultCallback callback) {
        if (!hasLocationPermissions()) {
            callback.onError("Location permissions not granted.");
            return;
        }

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(2000)
                .setNumUpdates(1);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    callback.onLocationReceived(location.getLatitude(), location.getLongitude());
                    fusedLocationProviderClient.removeLocationUpdates(this); // إيقاف التحديثات بعد الحصول على الموقع
                } else {
                    Log.e(TAG, "Received null location");
                    callback.onError("Unable to fetch location.");
                }
            }

            @Override
            public void onLocationAvailability(@NonNull com.google.android.gms.location.LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                if (!locationAvailability.isLocationAvailable()) {
                    Log.e(TAG, "Location is not available");
                    callback.onError("Location is not available.");
                }
            }
        };

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private boolean hasLocationPermissions() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}