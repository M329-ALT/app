package com.example.restaurants_3;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * مساعد للتحقق من اتصال الشبكة والخادم
 */
public class NetworkHelper {
    private static final String TAG = "NetworkHelper";
    private static final int CONNECTION_TIMEOUT = 5000; // 5 seconds timeout
    private static final int MAX_RETRY_COUNT = 3;

    private final Context context;
    private final Handler mainHandler;

    public NetworkHelper(Context context) {
        this.context = context;
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * التحقق من توفر اتصال الإنترنت
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * التحقق من إمكانية الوصول إلى الخادم
     */
    public void checkServerAvailability(String host, int port, ServerAvailabilityCallback callback) {
        if (!isNetworkAvailable()) {
            mainHandler.post(() -> callback.onResult(false, "لا يوجد اتصال بالإنترنت"));
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            boolean isAvailable = isServerReachable(host, port);
            mainHandler.post(() -> {
                if (isAvailable) {
                    callback.onResult(true, "الخادم متاح");
                } else {
                    callback.onResult(false, "لا يمكن الوصول إلى الخادم. تأكد من أن الخادم يعمل على العنوان " + host + ":" + port);
                }
            });
        });
    }

    /**
     * محاولة الاتصال بالخادم مع إعادة المحاولة
     */
    public void connectToServerWithRetry(String host, int port, int retryCount, ServerAvailabilityCallback callback) {
        if (!isNetworkAvailable()) {
            mainHandler.post(() -> callback.onResult(false, "لا يوجد اتصال بالإنترنت"));
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            boolean isConnected = false;
            String errorMessage = "";

            for (int attempt = 0; attempt < retryCount && !isConnected; attempt++) {
                try {
                    Log.d(TAG, "محاولة الاتصال بالخادم " + host + ":" + port + " (محاولة " + (attempt + 1) + ")");

                    if (isServerReachable(host, port)) {
                        isConnected = true;
                    } else {
                        // انتظر قبل إعادة المحاولة
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, "تمت مقاطعة محاولة الاتصال", e);
                    errorMessage = "تمت مقاطعة محاولة الاتصال: " + e.getMessage();
                }
            }

            final boolean finalIsConnected = isConnected;
            final String finalErrorMessage = errorMessage;

            mainHandler.post(() -> {
                if (finalIsConnected) {
                    callback.onResult(true, "تم الاتصال بالخادم بنجاح");
                } else {
                    String message = "فشل الاتصال بالخادم بعد " + retryCount + " محاولات";
                    if (!finalErrorMessage.isEmpty()) {
                        message += ": " + finalErrorMessage;
                    }
                    callback.onResult(false, message);
                }
            });
        });
    }

    /**
     * التحقق من إمكانية الوصول إلى الخادم
     */
    private boolean isServerReachable(String host, int port) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port), CONNECTION_TIMEOUT);
            socket.close();
            return true;
        } catch (IOException e) {
            Log.e(TAG, "فشل الاتصال بالخادم: " + e.getMessage());
            try {
                socket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "فشل إغلاق المقبس", closeException);
            }
            return false;
        }
    }

    /**
     * واجهة لاستقبال نتيجة التحقق من توفر الخادم
     */
    public interface ServerAvailabilityCallback {
        void onResult(boolean isAvailable, String message);
    }

    /**
     * عرض رسالة خطأ للمستخدم
     */
    public void showNetworkError(String message) {
        mainHandler.post(() -> {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        });
    }
}
