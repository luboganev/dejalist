package com.luboganev.dejalist;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.util.Log;

public class Utils {
	public static final String TAG = "Dejalist";
	
	/**
	 * 	Returns the Android API version running on that device
	 */
	public static int getApiLevel() {
		return android.os.Build.VERSION.SDK_INT;
	}
	
	/**
	 * 	Returns the current timestamp in milliseconds since January 1, 1970 00:00:00 UTC
	 */
	public static long currentTimestampInMillis() {
		return System.currentTimeMillis();
	}
	
	/**
	 * 	Returns the current timestamp in seconds since January 1, 1970 00:00:00 UTC
	 */
	public static long currentTimestampInSeconds() {
		return System.currentTimeMillis() / 1000;
	}
	
	/**
	 * 	This variable defines the current mode of the application. 
	 * 	When it is being released, the debug mode will be set to false.
	 */
	public static final boolean DEBUG = true;
	
	/**
	 * 	Prints a debug message in the system log
	 * 
	 * @param sender
	 * 		A string with custom name or a class
	 * @param message
	 * 		The message to be logged
	 */
	public static void d(Object sender, String message) {
		if(DEBUG) Log.d(TAG,  getSenderString(sender) + ": " + message);
	}
	
	/**
	 * 	Prints an error message in the system log
	 * 
	 * @param sender
	 * 		A string with custom name or a class
	 * @param message
	 * 		The message to be logged
	 */
	public static void e(Object sender, String message) {
		if(DEBUG) Log.e(TAG,  getSenderString(sender) + ": " + message);
	}
	
	/**
	 * Gets the name of the Class of the input object
	 * 
	 * @param sender
	 * 		The input object 
	 * @return
	 */
	private static String getSenderString(Object sender) {
		if(sender instanceof String) {
			return (String)sender;
		}
		else return sender.getClass().getSimpleName();
	}
	
	/**
	 * Calculates a sample size and dimensions of an
	 * image with the input width and height so that it can
	 * fit inside the required input width and height without
	 * changing its aspect ratio
	 * 
	 * @param width
	 * 		The width of the image
	 * @param height
	 * 		The height of the image
	 * @param reqWidth
	 * 		The required width of the image
	 * @param reqHeight
	 * 		The required height of the image
	 * @return
	 * 		The calculated sample size and dimensions in the form
	 * 		of {@link Options}
	 */
	public static BitmapFactory.Options calculateSampleSizeAndDimensions(int width, int height, int reqWidth, int reqHeight) {
		BitmapFactory.Options res = new Options();
	    
		res.inSampleSize = 1;
		res.outWidth = height;
		res.outHeight = width;
	    
	    if(height >= reqHeight) {
	    	float ratio = ((float)height) / ((float)reqHeight);
	    	int newWidth = (int)(((float)width) / ratio);
	    	res.inSampleSize = Math.round(ratio);
	    	if(newWidth > reqWidth) {
	    		ratio = ((float)width) / ((float)reqWidth);
	    		int newHeight = (int)(((float)height) / ratio);
	    		res.inSampleSize = Math.round(ratio);
	    		res.outWidth = reqWidth;
	    		res.outHeight = newHeight;
	    	}
	    	else {
	    		res.outWidth = newWidth;
	    		res.outHeight = reqHeight;
	    	}
	    }
	    else if(width > reqWidth) {
	    	float ratio = ((float)width) / ((float)reqWidth);
	    	int newHeight = (int)(((float)height) / ratio);
	    	res.inSampleSize = Math.round(ratio);
	    	if(newHeight > reqHeight) {
	    		ratio = ((float)height) / ((float)reqHeight);
	    		res.inSampleSize = Math.round(ratio);
	    		int newWidth = (int)(((float)width) / ratio);
	    		res.outWidth = newWidth;
	    		res.outHeight = reqHeight;
	    	}
	    	else {
	    		res.outWidth = reqWidth;
	    		res.outHeight = newHeight;
	    	}
	    }
	    return res;
	}
	
	/**
	 * Reads the dimensions of a particular drawable 
	 * resource without loading the whole thing into memory
	 * 
	 * @param res
	 * 		A reference to the resources object
	 * @param resId
	 * 		The id of the resource
	 * @return
	 * 		A {@link Point} containing the width and height as x and y
	 */
	public static Point getResourceBitmapDimensions(Resources res, int resId) {
		// First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(res, resId, options);
	    return new Point(options.outWidth,options.outHeight);
	}
	
	/**
	 * Decodes a particular drawable resource using the input sample size
	 * 
	 * @param res
	 * 		A reference to the resources object
	 * @param resId
	 * 		The id of the resource
	 * @param sampleSize
	 * 		The sample size to be used when decoding the drawable into a bitmap
	 * @return
	 * 		The decoded Bitmap
	 */
	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int sampleSize) {    
	    // Calculate required inSampleSize and new dimensions
	    BitmapFactory.Options options =  new BitmapFactory.Options();
	    // Decode bitmap with inSampleSize set
	    options.inSampleSize = sampleSize;
	    return BitmapFactory.decodeResource(res, resId, options);
	}
	
	/**
	 * Helper method to convert Long[] to long[]
	 * 
	 * @param input
	 * @return
	 */
	public static long[] objectArrayToPrimitiveLongArray(Object[] input) {
		long[] result = new long[input.length];
		for (int i = 0; i < input.length; i++) {
			result[i] = (Long)input[i];
		}
		return result;
	}
	
	/**
     * Simple extension that uses {@link Bitmap} instances as keys, using their
     * memory footprint in bytes for sizing.
     */
    public static class ThumbnailCache extends android.support.v4.util.LruCache<Long, Bitmap> {
        public ThumbnailCache(int maxSizeBytes) {
            super(maxSizeBytes);
        }
        
        @Override
        protected int sizeOf(Long key, Bitmap value) {
            return value.getRowBytes() * value.getHeight();
        }
    }
}


