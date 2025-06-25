package com.example.peacemind.utils;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.YuvImage;
import android.media.Image;
import android.util.Log;

import androidx.camera.core.ImageProxy;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class ImageUtils {

    public static Bitmap imageProxyToBitmap(ImageProxy image) {
        ImageProxy.PlaneProxy[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];

        // U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        YuvImage yuvImage = new YuvImage(
                nv21,
                ImageFormat.NV21,
                image.getWidth(),
                image.getHeight(),
                null
        );

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(
                new android.graphics.Rect(0, 0, image.getWidth(), image.getHeight()),
                90,
                out
        );

        byte[] imageBytes = out.toByteArray();
        return android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
}
