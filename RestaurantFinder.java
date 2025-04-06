package com.example.restaurants_3;
import com.example.restaurants_3.ApiService;
import com.example.restaurants_3.LocationHelper;
import com.example.restaurants_3.Restaurant;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantFinder {

    private ApiService apiService;
    private LocationHelper locationHelper;
    private double selectedDistance;
    private RestaurantFetchListener listener;

    public RestaurantFinder(ApiService apiService, LocationHelper locationHelper, double selectedDistance, RestaurantFetchListener listener) {
        this.apiService = apiService;
        this.locationHelper = locationHelper;
        this.selectedDistance = selectedDistance;
        this.listener = listener;
    }

    public interface RestaurantFetchListener {
        void onRestaurantsFetched(List<Restaurant> restaurants);
        void onError(String errorMessage);
    }

    public void fetchRestaurants(double userLat, double userLon, int selectedDistance) {
        // هنا بتنادي API وتستخدم listener:
        Call<List<Restaurant>> call = apiService.getNearbyRestaurants(userLat, userLon, selectedDistance);
        call.enqueue(new Callback<List<Restaurant>>() {
            @Override
            public void onResponse(Call<List<Restaurant>> call, Response<List<Restaurant>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onRestaurantsFetched(response.body());
                } else {
                    listener.onError("No restaurants found.");
                }
            }

            @Override
            public void onFailure(Call<List<Restaurant>> call, Throwable t) {
                listener.onError("Failed to fetch restaurants: " + t.getMessage());
            }
        });
    }
}