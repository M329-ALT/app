package com.example.restaurants_3;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private List<Restaurant> restaurantList;
    private Context context;
    private OnRestaurantClickListener listener;

    public Context getContext() {
        return context;
    }

    public interface OnRestaurantClickListener {
        void onRestaurantClick(Restaurant restaurant);
    }

    public RestaurantAdapter(Context context, List<Restaurant> restaurantList, OnRestaurantClickListener listener) {
        this.context = context;
        this.restaurantList = restaurantList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);
        holder.name.setText(restaurant.getName());
        holder.address.setText(restaurant.getAddress());
        holder.distance.setText(String.format("يبعد %.2f كم", restaurant.getDistance()));
        holder.phone.setText(restaurant.getPhone());
        holder.rating.setText(String.valueOf(restaurant.getRating()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRestaurantClick(restaurant);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateList(List<Restaurant> newList) {
        // ترتيب حسب المسافة
        Collections.sort(newList, Comparator.comparingDouble(Restaurant::getDistance));
        this.restaurantList.clear();
        this.restaurantList.addAll(newList);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, address, distance, phone, rating;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.restaurantName);
            address = itemView.findViewById(R.id.restaurantAddress);
            distance = itemView.findViewById(R.id.restaurantDistance);
            phone = itemView.findViewById(R.id.restaurantPhone);
            rating = itemView.findViewById(R.id.restaurantRating);
        }
    }
}
