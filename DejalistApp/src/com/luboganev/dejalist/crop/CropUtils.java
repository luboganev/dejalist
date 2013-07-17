package com.luboganev.dejalist.crop;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.luboganev.dejalist.Utils;

import junit.framework.Assert;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.util.Log;

public class CropUtils {
	// Let's not be memory greedy at all
	// The bitmap can be at most 512x512x4 = 1MB
	public final static int DEFAULT_IMAGE_MAX_DIMENSION_RES = 512;
	
	private static int mMaxImageDimenstionRes = DEFAULT_IMAGE_MAX_DIMENSION_RES;
	
	public static int recalculateMaxImagesize(Context context) {
		final ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		Utils.d("CropUtils", "Memory class:"+am.getMemoryClass()+"MB");
		// 1/16 of the total memory in bytes for a whole quadratic image ( Image size = X * X * 4 Bytes per pixel)
		mMaxImageDimenstionRes = (int)Math.sqrt(am.getMemoryClass() * 1024 * 16);
		return mMaxImageDimenstionRes;
	}
	
	/**
	 * Calculates a inSampleSize depending on the input width 
	 * and height and ({@link #IMAGE_MAX_SIZE} = {@value #IMAGE_MAX_SIZE})
	 * 
	 * @param height
	 * 		The height of the image
	 * @param width
	 * 		The width of the image
	 * @return
	 */
	public static int scalePow2(int height, int width) {
        int scale = 1;
        int size = Math.max(height, width);
        if (size > mMaxImageDimenstionRes) {
            scale = (int)Math.pow(2, (int) Math.round(Math.log(mMaxImageDimenstionRes / 
            			(double) size) / Math.log(0.5)));
        }
        return scale;
	}
	
	/**
	 * Decodes a bitmap by using the default decode options
	 * 
	 * @param contentResolver
	 * 		The content resolver
	 * @param uri
	 * 		The uri of the bitmap file
	 * @return
	 */
	public static Bitmap getBitmap(ContentResolver contentResolver, Uri uri) {
		//Decode image size
        BitmapFactory.Options o = getBitmapOptions(contentResolver, uri);
        o.inSampleSize = CropUtils.scalePow2(o.outHeight, o.outWidth);
		return getBitmap(o, contentResolver, uri);
	}
	
	/**
	 * Decodes a bitmap by using the provided decode options
	 * 
	 * @param decodeOptions
	 * 		The decode options
	 * @param contentResolver
	 * 		The content resolver
	 * @param uri
	 * 		The uri of the bitmap file
	 * @return
	 */
	public static Bitmap getBitmap(BitmapFactory.Options decodeOptions, ContentResolver contentResolver, Uri uri) {
		InputStream in = null;
		try {
	        in = contentResolver.openInputStream(uri);
	        Bitmap b = BitmapFactory.decodeStream(in, null, decodeOptions);
	        in.close();
			return b;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return null;
	}
	
	/**
	 * Decodes the bitmap options only without actually loading bitmap in memory
	 * 
	 * @param contentResolver
	 * 		The content resolver
	 * @param uri
	 * 		The uri of the bitmap
	 * @return
	 * 		The size of the bitmap file
	 */
	public static BitmapFactory.Options getBitmapOptions(ContentResolver contentResolver, Uri uri) {
		InputStream in = null;
		try {
			in = contentResolver.openInputStream(uri);
			//Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(in, null, o);
			in.close();
			return o;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return null;
	}
	
	// Rotates the bitmap by the specified degree.
    // If a new bitmap is created, the original bitmap is recycled.
    public static Bitmap rotateAndCrop(Bitmap b, int degrees, Rect crop) {
        if (b == null) return b;
        Bitmap b2 = null;
        int scale = CropUtils.scalePow2(b.getHeight(), b.getWidth());
        if (scale != 1 && crop != null) {
                crop.left *= scale;
                crop.right*= scale;
                crop.bottom *= scale;
                crop.top *= scale;
        }
        try {
                if (degrees != 0) {
                        Matrix m = new Matrix();
                        m.setRotate(degrees, 0, 0);
                                RectF r_rot = new RectF(0,0,b.getWidth(),b.getHeight());
                                m.mapRect(r_rot);
                                m.postTranslate(-r_rot.left, -r_rot.top);

//                              r_rot.set(0,0,b.getWidth(),b.getHeight());
//                              m.mapRect(r_rot);
//                              Utils.d(TAG, "rotated bitmap = "+r_rot.toString());

                                if (crop == null)
                                b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                        else {
                                Matrix minv = new Matrix();
                                m.invert(minv);
//                              minv.postScale(scale, scale);
                                RectF r = new RectF();
                                r.set(crop);
                                minv.mapRect(r);
                                Utils.d("CropUtils", "crop = "+crop.toString());
                                r.round(crop);
                                Utils.d("CropUtils", "bitmap "+b.getDensity() + " " + b.getWidth() + " x "+b.getHeight());
                                Utils.d("CropUtils", "inv rotated crop = "+crop.toString());
                                b2 = Bitmap.createBitmap(b, crop.left, crop.top, crop.width(), crop.height(), m, true);
                        }
                } else {
                        if (crop != null) {
                        	Utils.d("CropUtils", "crop = "+crop.toString());
                        	Utils.d("CropUtils", "bitmap "+b.getDensity() + " " + b.getWidth() + " x "+b.getHeight());
                            b2 = Bitmap.createBitmap(b, crop.left, crop.top, crop.width(), crop.height());
                        } else
                            b2 = b;
                }
        } catch (OutOfMemoryError ex) {
                // We have no memory to rotate. Return the original bitmap.
                b2 = b;
        }
        Assert.assertNotNull(b2);
        if (b == b2) {
                return b;
        } else {
        	Utils.d("CropUtils", "b != b2, recycling b");
            b.recycle();
            return b2;
        }
    }

    public static Bitmap rotate(Bitmap b, int degrees) {
        return rotateAndCrop(b, degrees, null);
    }
    
	public static Bitmap resize(Bitmap b, int width, int height) {
		if (b == null)
			return b;
		if(b.getWidth() == width && b.getHeight() == height) return b;
		
		Bitmap b2 = null;
		try {
			b2 = Bitmap.createScaledBitmap(b, width, height, false);
		} catch (OutOfMemoryError ex) {
			// We have no memory to rotate. Return the original bitmap.
			b2 = b;
		}
		Assert.assertNotNull(b2);
        if (b == b2) {
                return b;
        } else {
        	Utils.d("CropUtils", "b != b2, recycling b");
            b.recycle();
            return b2;
        }
	}
}
