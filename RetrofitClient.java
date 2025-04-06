
// RetrofitClient.java
package com.example.restaurants_3;
// RetrofitClient.java package com.example.restaurants_3;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL ="http://192.168.1.4:80/";
    private static Retrofit retrofitInstance = null;

    public RetrofitClient() {
        // Private constructor to prevent instantiation
    }

    public static synchronized Retrofit getInstance() {
        if (retrofitInstance == null) {
            retrofitInstance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitInstance;
    }

}