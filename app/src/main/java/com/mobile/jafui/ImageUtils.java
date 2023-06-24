package com.mobile.jafui;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageUtils {
    private static final String TAG = "ImageUtils";
    private static final int MAX_IMAGE_WIDTH = 512;
    private static final int MAX_IMAGE_HEIGHT = 512;
    private static final int IMAGE_QUALITY = 80;

    public static List<String> compressImages(Context context, List<Uri> imageUris) {
        List<String> compressedImagePaths = new ArrayList<>();

        for (Uri imageUri : imageUris) {
            try {
                String compressedImagePath = compressImage(context, imageUri);
                compressedImagePaths.add(compressedImagePath);
            } catch (IOException e) {
                Log.e(TAG, "Failed to compress image: " + imageUri.toString(), e);
            }
        }

        return compressedImagePaths;
    }

    private static String compressImage(Context context, Uri imageUri) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();

        Bitmap bitmap;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, imageUri));
        } else {
            bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri));
        }

        if (bitmap == null) {
            throw new IOException("Failed to decode bitmap from Uri: " + imageUri.toString());
        }

        Bitmap compressedBitmap = compressBitmap(bitmap);
        String compressedImagePath = saveCompressedBitmap(context, compressedBitmap);

        return compressedImagePath;
    }

    private static Bitmap compressBitmap(Bitmap bitmap) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();

        float scaleRatio = Math.min((float) MAX_IMAGE_WIDTH / originalWidth, (float) MAX_IMAGE_HEIGHT / originalHeight);

        int scaledWidth = Math.round(originalWidth * scaleRatio);
        int scaledHeight = Math.round(originalHeight * scaleRatio);

        return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
    }

    private static String saveCompressedBitmap(Context context, Bitmap bitmap) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, outputStream);

        File tempDir = context.getCacheDir();
        String fileName = "compressed_" + System.currentTimeMillis() + ".jpg";
        File tempFile = new File(tempDir, fileName);

        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(outputStream.toByteArray());
        fos.flush();
        fos.close();

        return tempFile.getAbsolutePath();
    }

    public static List<String> convertToBase64(List<String> imagePaths) {
        List<String> base64Images = new ArrayList<>();

        for (String imagePath : imagePaths) {
            try {
                String base64Image = convertToBase64(imagePath);
                base64Images.add(base64Image);
            } catch (IOException e) {
                Log.e(TAG, "Failed to convert image to base64: " + imagePath, e);
            }
        }

        return base64Images;
    }

    private static String convertToBase64(String imagePath) throws IOException {
        byte[] imageBytes = readFile(imagePath);
        String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return base64Image;
    }

    private static byte[] readFile(String filePath) throws IOException {
        java.io.File file = new java.io.File(filePath);
        int length = (int) file.length();
        byte[] bytes = new byte[length];

        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(bytes);
        }

        return bytes;
    }
}






