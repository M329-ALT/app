package com.example.restaurants_3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements RestaurantAdapter.OnRestaurantClickListener {
    private static final String TAG = "MainActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final String ERROR_FETCHING_RESTAURANTS = "Error fetching restaurants";
    private static final String LOCATION_PERMISSION_REQUIRED = "Location permission is required";
    private static final String LOCATION_RETRIEVAL_FAILED = "Failed to retrieve location. Please try again.";
    private static final String INTERNET_CONNECTION_REQUIRED = "Internet connection is required";
    private static final String SERVER_CONNECTION_FAILED = "Cannot connect to server. Please check your connection and try again.";

    // Server configuration
    private static final String SERVER_HOST = "192.168.1.4";
    private static final int SERVER_PORT = 80;
    private static final int MAX_RETRY_COUNT = 3;

    private TextView locationText;
    private Button getLocationBtn;
    private Button addNewBusinessBtn;
    private Spinner distanceSpinner;
    private List<String> distanceList;
    private int selectedDistance;
    private RecyclerView recyclerView;
    private RestaurantAdapter restaurantAdapter;
    private LocationHelper locationHelper;
    private NetworkHelper networkHelper;
    private List<Restaurant> restaurantsList;
    private Users user;
    private String LOCATION_DEFINED_FAILED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        initializeLocation();
        setupSpinner();
        setupButtonClick();
        // Example usage of FileUtils.checkFileExists
        String filePath = "/path/to/your/file"; // Replace with your file path
        FileUtils.checkFileExists(filePath, new FileUtils.FileCheckCallback() {
            @Override
            public void onFileExists(boolean exists) {
                // This code runs on the main thread!
                if (exists) {
                    Log.d(TAG, "File exists: " + filePath);
                    // Update UI or take other actions
                } else {
                    Log.d(TAG, "File does not exist: " + filePath);
                    // Update UI or take other actions
                }
            }
        });
    }


    private void initializeViews() {
        locationText = findViewById(R.id.locationText);
        getLocationBtn = findViewById(R.id.getLocationBtn);
        addNewBusinessBtn = findViewById(R.id.addNewBusiness);
        distanceSpinner = findViewById(R.id.numberSpinner);
        recyclerView = findViewById(R.id.restaurantRecyclerView);
        locationHelper = new LocationHelper(this);
        networkHelper = new NetworkHelper(this);


         // Add this for GPU monitoring

    }

    private void initializeLocation() {
        locationHelper.getLocationOrRequestPermission(this, new LocationHelper.LocationResultCallback() {
            @Override
            public void onLocationReceived(double latitude, double longitude) {
                onLocationSuccess(latitude, longitude);
            }

            @Override
            public void onError(String errorMessage) {
                onLocationFailure(errorMessage);
            }
        });

        recyclerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {

            @Override
            public void onViewAttachedToWindow(@NonNull View v) {
                Log.d("focusDebug","RecyclerView attached");
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull View v) {
                Log.d("focusDebug","RecyclerView detached");
            }
        });
        getLocationBtn.setOnFocusChangeListener((v, hasFocus) -> {
            Log.d("focusDebug","Location Button focus:"+hasFocus);
        });


    }


    private void establishConnection() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        restaurantsList = new ArrayList<>();
        recyclerView.setAdapter(restaurantAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
        restaurantAdapter = new RestaurantAdapter(this, restaurantsList, restaurant -> {
            // Handle restaurant click
            Intent intent = new Intent(MainActivity.this, MealsActivity.class);
            intent.putExtra("restaurant_id", restaurant.getId());
            startActivity(intent);
        });


    }


    private void setupButtonClick() {
        getLocationBtn.setOnClickListener(v -> {
            if (user != null) {
                if (networkHelper.isNetworkAvailable()) {
                    checkServerAndFetchRestaurants();
                } else {
                    showErrorMessage(INTERNET_CONNECTION_REQUIRED);
                }
            } else {
                showErrorMessage(LOCATION_DEFINED_FAILED);
            }
        });

        // Setup click listener for addNewBusinessBtn if needed
        // addNewBusinessBtn.setOnClickListener(v -> {
        //     // Handle add new business button click
        // });
    }

    private void checkServerAndFetchRestaurants() {
        showLoadingMessage("Checking server connection...");

        networkHelper.connectToServerWithRetry(SERVER_HOST, SERVER_PORT, MAX_RETRY_COUNT,
                new NetworkHelper.ServerAvailabilityCallback() {
                    @Override
                    public void onResult(boolean isAvailable, String message) {
                        if (isAvailable) {
                            establishConnection();
                            fetchNearbyRestaurants();
                        } else {
                            showErrorMessage(SERVER_CONNECTION_FAILED);
                        }
                    }
                });
    }


    private void fetchNearbyRestaurants() {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        RestaurantFinder finder = new RestaurantFinder(apiService, locationHelper, selectedDistance,
                new RestaurantFinder.RestaurantFetchListener() {
                    @Override
                    public void onRestaurantsFetched(List<Restaurant> restaurants) {
                        runOnUiThread(() -> {
                            restaurantsList.clear();

                            // Get the user's location
                            double userLat = user.getUserLat();
                            double userLon = user.getUserLon();

                            // Filter restaurants based on distance
                            for (Restaurant restaurant : restaurants) {
                                double restaurantLat = restaurant.getLatitude();
                                double restaurantLon = restaurant.getLongitude();

                                // Calculate the distance between user and restaurant
                                double distance = LocationUtils.calculateDistance(userLat, userLon, restaurantLat, restaurantLon);

                                // Check if the restaurant is within the selected distance
                                if (distance <= selectedDistance) {
                                    restaurantsList.add(restaurant);
                                }
                            }

                            restaurantAdapter.notifyDataSetChanged();

                            if (restaurantsList.isEmpty()) {
                                Toast.makeText(MainActivity.this, "No_restaurants found within the selected distance.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        runOnUiThread(() ->
                                Toast.makeText(MainActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show());
                    }
                });

        finder.fetchRestaurants(user.getUserLat(), user.getUserLon(), selectedDistance);
    }


    @Override
    public void onRestaurantClick(Restaurant restaurant) {
        Intent intent = new Intent(this, MealsActivity.class);
        intent.putExtra("restaurant_id", restaurant.getId());
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeLocation();
            } else {
                showErrorMessage(LOCATION_PERMISSION_REQUIRED);
            }
        }
    }



    private void onLocationSuccess(double latitude, double longitude) {
        runOnUiThread(() -> {
            if (user == null) {
                user = new Users(0, 0, "", "", "");
            }
            user.setUserLat(latitude);
            user.setUserLon(longitude);
            locationText.setText("Latitude: " + user.getUserLat() + ", Longitude: " + user.getUserLon());
            getLocationBtn.setText("Click to get nearby restaurants");
            distanceSpinner.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        });
    }

    private void onLocationFailure(String errorMessage) {
        runOnUiThread(() -> {
            showErrorMessage(LOCATION_RETRIEVAL_FAILED);
            getLocationBtn.setText("Retry getting location");
            distanceSpinner.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        });
    }

    private void setupSpinner() {
        distanceList = new ArrayList<>(Arrays.asList("5 KM", "10 KM", "15 KM", "20 KM", "30 KM", "50 KM"));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, distanceList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distanceSpinner.setAdapter(adapter);

        distanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = distanceList.get(position);
                selectedDistance = Integer.parseInt(selected.split(" ")[0]); // استخراج الرقم فقط من "5 KM"
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // لا شيء
            }
        });
    }


    // Utility methods
    private void showLoadingMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void showErrorMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        Log.e(TAG, message);
    }
}