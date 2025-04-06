package com.example.restaurants_3;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealsActivity extends AppCompatActivity {
    private static final String TAG = "MealsActivity";
    private MealAdapter mealAdapter;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals);

        RecyclerView recyclerView = findViewById(R.id.viewPager);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mealAdapter = new MealAdapter(this, new ArrayList<>(), new MealAdapter.OnMealClickListener() {
            @Override
            public void onMealClick(Meal meal) {
                Log.d(TAG, "Meal clicked: " + meal.getName());
            }
        });
        recyclerView.setAdapter(mealAdapter);

        int restaurantId = getIntent().getIntExtra("restaurant_id", -1);
        if (restaurantId != -1) {
            loadMeals(restaurantId);
        }
    }

    private void loadMeals(int restaurantId) {
        apiService.getMeals(restaurantId).enqueue(new Callback<List<Meal>>() {
            @Override
            public void onResponse(@NonNull Call<List<Meal>> call, @NonNull Response<List<Meal>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mealAdapter.updateList(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Meal>> call, @NonNull Throwable t) {
                Log.e(TAG, "خطأ في جلب الوجبات", t);
            }
        });
    }
}