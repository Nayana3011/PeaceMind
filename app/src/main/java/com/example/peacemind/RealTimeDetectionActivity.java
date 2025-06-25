package com.example.peacemind;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.camera.core.Preview;
import android.graphics.Color;

import com.example.peacemind.api.ApiClient;
import com.example.peacemind.api.EmotionApi;
import com.example.peacemind.model.EmotionResponse;
import com.example.peacemind.utils.ImageUtils;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RealTimeDetectionActivity extends AppCompatActivity {

    private PreviewView previewView;
    //private TextView emotionOutput;
    private TextView emotionOutput, detectedEmotionsView;
    private TFLiteEmotionClassifier classifier;
    private ExecutorService cameraExecutor;
    private EmotionApi emotionApi;

    private long lastApiUpdateTime = 0;
    private static final long UPDATE_INTERVAL_MS = 3000; // 3 seconds
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_detection);
//        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//            finish();
//            overridePendingTransition(R.anim.fade_out, R.anim.fade_out); // optional fade-out animation
//        }, 5000); // 5000 ms = 5 seconds

        View root = findViewById(android.R.id.content);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        root.startAnimation(fadeIn);

        previewView = findViewById(R.id.previewView);
        emotionOutput = findViewById(R.id.predictionResult);
       detectedEmotionsView = findViewById(R.id.detectedEmotions);


        classifier = new TFLiteEmotionClassifier(getAssets());
        cameraExecutor = Executors.newSingleThreadExecutor();
        emotionApi = ApiClient.getRetrofitInstance().create(EmotionApi.class);

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

//    private void startCamera() {
//        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
//
//        cameraProviderFuture.addListener(() -> {
//            try {
//                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
//
//                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
//                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                        .build();
//
//                imageAnalysis.setAnalyzer(cameraExecutor, image -> {
//                    Bitmap bitmap = ImageUtils.imageProxyToBitmap(image);
//                    image.close();
//
//                    if (bitmap != null) {
//                        String emotion = classifier.classify(bitmap);
//                        runOnUiThread(() -> emotion));
//                        sendEmotionToApi(bitmap);
//                    }
//                });
//
//                CameraSelector cameraSelector = new CameraSelector.Builder()
//                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
//                        .build();
//
//                cameraProvider.unbindAll();
//                cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis);
//
//            } catch (Exception e) {
//                Log.e("CameraX", "Camera start failed", e);
//            }
//        }, ContextCompat.getMainExecutor(this));
//    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Create Preview
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // Create ImageAnalysis for frame processing
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, image -> {
                    Bitmap bitmap = ImageUtils.imageProxyToBitmap(image);
                    image.close();

                    if (bitmap != null) {
                        String emotion = classifier.classify(bitmap);
                        runOnUiThread(() -> emotionOutput.setText(emotion));
                        sendEmotionToApi(bitmap);
                    }
                });

                // Use the front camera
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build();

                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                // Bind to lifecycle
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

            } catch (Exception e) {
                Log.e("CameraX", "Camera start failed", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void sendEmotionToApi(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
        MultipartBody.Part frame = MultipartBody.Part.createFormData("frame", "frame.jpg", requestFile);

        Call<EmotionResponse> call = emotionApi.uploadFrame(frame);
        call.enqueue(new Callback<EmotionResponse>() {
            @Override
            public void onResponse(Call<EmotionResponse> call, Response<EmotionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EmotionResponse result = response.body();
                    String status = result.getStatus(); // "stress_detected" or "ok"
                    List<String> emotions = result.getEmotions();

                    Log.d("API", "Detected: " + emotions + " | Status: " + status);

//                    runOnUiThread(() -> {
//                        String emotionsText = emotions.isEmpty() ? "None" : String.join(", ", emotions);
//                        detectedEmotionsView.setText("Detected: " + emotionsText);  // Line 2
//
//                        if ("stress_detected".equals(status)) {
//                            emotionOutput.setText("😰 Stressed");  // Line 1
//                            emotionOutput.setTextColor(Color.RED);
//                        } else {
//                            emotionOutput.setText("🙂 Not Stressed");  // Line 1
//                            emotionOutput.setTextColor(Color.GREEN);
//                        }
//                    });

                    runOnUiThread(() -> {
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - lastApiUpdateTime >= UPDATE_INTERVAL_MS) {
                            String emotionsText = emotions.isEmpty() ? "None" : String.join(", ", emotions);
                            detectedEmotionsView.setText("Detected: " + emotionsText);

                            if ("stress_detected".equals(status)) {
                                emotionOutput.setText("😰 Stressed");
                                emotionOutput.setTextColor(Color.RED);


                            } else {
                                emotionOutput.setText("🙂 Not Stressed");
                                emotionOutput.setTextColor(Color.GREEN);
                            }

                            lastApiUpdateTime = currentTime;

                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                Intent intent = new Intent(RealTimeDetectionActivity.this, StressAdviceActivity.class);
                                String dominantEmotion = emotions.isEmpty() ? "Unknown" : emotions.get(0);
                                intent.putExtra("status", status);
                                intent.putExtra("emotion", dominantEmotion);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }, 3000); // 3-second delay
                        }
                    });
                } else {
                    Log.e("API", "Unsuccessful response");
                }
            }

            @Override
            public void onFailure(Call<EmotionResponse> call, Throwable t) {
                Log.e("API", "API failure", t);
            }
        });

//        call.enqueue(new Callback<EmotionResponse>() {
//            @Override
//            public void onResponse(Call<EmotionResponse> call, Response<EmotionResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    EmotionResponse result = response.body();
//                    String status = result.getStatus();
//                    List<String> emotions = result.getEmotions();
//                    Log.d("API", "Detected: " + emotions + " | Status: " + status);
//                } else {
//                    Log.e("API", "Unsuccessful response");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<EmotionResponse> call, Throwable t) {
//                Log.e("API", "API failure", t);
//            }
//        });
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}
