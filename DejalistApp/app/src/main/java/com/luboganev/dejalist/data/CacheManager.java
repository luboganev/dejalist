package com.luboganev.dejalist.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

public class CacheManager {

    private static final long MAX_SIZE = 1000000L; // <1MB

    private CacheManager() {

    }

    public static File cacheData(Context context, File source) throws IOException {

        File cacheDir = context.getExternalCacheDir();
        long size = getDirSize(cacheDir);
        long newSize = source.length() + size;

        if (newSize > MAX_SIZE) {
            cleanDir(cacheDir, newSize - MAX_SIZE);
        }

        File file = new File(cacheDir, source.getName());
        if(file.getPath().equals(source.getPath())) {
        	return file;
        }
        else {
        	if(file.exists()) file.delete();
        	file.createNewFile();
        	if(ProductImageFileHelper.copy(source, file)) return file;
        	else return null;
        }
    }
    
    public static File cacheData(Context context, InputStream is, String cachedFileName) throws IOException {
    	File cacheDir = context.getExternalCacheDir();
    	if (getDirSize(cacheDir) > MAX_SIZE) {
    		cleanDir(cacheDir, MAX_SIZE);
    	}
    	
    	File file = new File(cacheDir, cachedFileName);
		if(file.exists()) file.delete();
   		file.createNewFile();
   		
		if(ProductImageFileHelper.copy(is, file)) return file;
		else return null;
    }

    public static File retrieveData(Context context, String name) throws IOException {

        File cacheDir = context.getExternalCacheDir();
        File file = new File(cacheDir, name);

        if (!file.exists()) {
            // File doesn't exist
            return null;
        }

        return file;
    }

    private static void cleanDir(File dir, long bytes) {

        long bytesDeleted = 0;
        File[] files = dir.listFiles();

        for (File file : files) {
            bytesDeleted += file.length();
            file.delete();

            if (bytesDeleted >= bytes) {
                break;
            }
        }
    }

    private static long getDirSize(File dir) {

        long size = 0;
        File[] files = dir.listFiles();

        for (File file : files) {
            if (file.isFile()) {
                size += file.length();
            }
        }

        return size;
    }
}
