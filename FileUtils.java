package com.example.restaurants_3;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileUtils {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler handler = new Handler(Looper.getMainLooper());

    public interface FileCheckCallback {
        void onFileExists(boolean exists);
    }

    public static void checkFileExists(String filePath, FileCheckCallback callback) {
        executor.execute(() -> {
            File file = new File(filePath);
            boolean exists = file.exists();
            handler.post(() -> callback.onFileExists(exists));
        });
    }
}