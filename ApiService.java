package com.example.restaurants_3;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("get_restaurants.php")
    Call<List<Restaurant>> getNearbyRestaurants(@Query("latitude") double latitude, @Query("longitude") double longitude,@Query("selectedDistance") int selectedDistance);

    @GET("get_meals.php")
    Call<List<Meal>> getMeals(@Query("restaurant_id") int restaurantId);
}