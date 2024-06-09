package com.example.man.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    public static byte[] readFileBytesFromUri(Context context, Uri uri) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();
        InputStream inputStream = contentResolver.openInputStream(uri);
        if (inputStream != null) {
            byte[] bytes = new byte[inputStream.available()];
            int bytesRead = inputStream.read(bytes);
            if (bytesRead != -1) {
                inputStream.close();
                return bytes;
            } else {
                inputStream.close();
                throw new IOException("Failed to read bytes from InputStream");
            }
        } else {
            throw new IOException("Failed to open input stream for URI: " + uri);
        }
    }
}