package com.example.peacemind;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import org.tensorflow.lite.Interpreter;


import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TFLiteEmotionClassifier {

    private Interpreter tflite;
    private static final String MODEL_NAME = "emotiondetector.tflite";
    private static final int IMAGE_SIZE = 48;
    private static final int NUM_CLASSES = 7;

    public TFLiteEmotionClassifier(AssetManager assetManager) {
        try {
            tflite = new Interpreter(loadModelFile(assetManager));
        } catch (IOException e) {
            Log.e("TFLiteModel", "Error loading model", e);
        }
    }

    private MappedByteBuffer loadModelFile(AssetManager assetManager) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(MODEL_NAME);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private ByteBuffer preprocessImage(Bitmap bitmap) {
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, true);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(IMAGE_SIZE * IMAGE_SIZE * 4);
        byteBuffer.order(ByteOrder.nativeOrder());

        for (int y = 0; y < IMAGE_SIZE; y++) {
            for (int x = 0; x < IMAGE_SIZE; x++) {
                int pixel = resized.getPixel(x, y);
                int r = Color.red(pixel);
                int g = Color.green(pixel);
                int b = Color.blue(pixel);
                float gray = (r + g + b) / 3.0f / 255.0f;
                byteBuffer.putFloat(gray);
            }
        }
        return byteBuffer;
    }

    public String classify(Bitmap faceBitmap) {
        ByteBuffer input = preprocessImage(faceBitmap);
        float[][] output = new float[1][NUM_CLASSES];
        tflite.run(input, output);

        int maxIndex = 0;
        float maxProb = 0;
        for (int i = 0; i < NUM_CLASSES; i++) {
            if (output[0][i] > maxProb) {
                maxProb = output[0][i];
                maxIndex = i;
            }
        }

        String[] labels = {"Angry", "Disgust", "Fear", "Happy", "Sad", "Surprise", "Neutral"};
        return labels[maxIndex] + " (" + String.format("%.1f", maxProb * 100) + "%)";
    }
}