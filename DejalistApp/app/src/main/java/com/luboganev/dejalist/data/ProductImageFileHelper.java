package com.luboganev.dejalist.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.net.Uri;
import com.luboganev.dejalist.Utils;

public class ProductImageFileHelper {
	private static final String TAG = "ProductPicturesFileHelper";
	private static final String PRODUCT_IMAGES_DIR = "product_images";
	private static final String PRODUCT_FILE_PREFIX = "product_";
	private static final String PRODUCT_FILE_SUFFIX = ".jpg";
	
	private static final File getFreshFile(Context context) {
		File imagesDir = new File(context.getFilesDir(), PRODUCT_IMAGES_DIR);
		if(!imagesDir.exists()) imagesDir.mkdirs();
		Utils.currentTimestampInMillis();
		return new File(imagesDir, PRODUCT_FILE_PREFIX+Utils.currentTimestampInMillis()+PRODUCT_FILE_SUFFIX);
	}
	
	/**
	 * This is a function which deletes the wrong folder with product images
	 * which used to be created in a bad way. The Android SDK is so genius that 
	 * it provides two methods: getDir and getFilesDir. However, It turns out that the 
	 * getDir is completely unusable by any of the goodies like FileProvider. 
	 * Therefore, we have to move all files to a new folder that is created by 
	 * calling getFilesDir, and not getDir as it used to be :( This method should be
	 * called once the whole move and the necessary DB changes are performed
	 */
	public static final void deleteWrongImagesFolder(Context context) {
		File imagesDir = context.getDir(PRODUCT_IMAGES_DIR, Context.MODE_PRIVATE);
		File[] allProductPhotosFiles = imagesDir.listFiles();
		for (File file : allProductPhotosFiles) {
			file.delete();
		}
		imagesDir.delete();
	}
	
	public static final File getFile(Context context, String filename) {
		File imagesDir = new File(context.getFilesDir(), PRODUCT_IMAGES_DIR);
//		File imagesDir = context.getDir(PRODUCT_IMAGES_DIR, Context.MODE_PRIVATE);
		if(!imagesDir.exists()) imagesDir.mkdirs();
		return new File(imagesDir, filename);
	}
	
	public static Uri copyToANewProductImageFile(Context context, Uri uri) {
		File inputFile = new File(uri.getPath());
		if(inputFile.exists()) {
			File freshFile = getFreshFile(context);
			if(freshFile.exists()) {
				Utils.e(TAG, "moveToANewProductImageFile(Context, Uri) - Fresh file already exists!");
				return null;
			}
			else {
				try {
					freshFile.createNewFile();
				} catch (IOException e) {
					Utils.e(TAG, "moveToANewProductImageFile(Context, Uri) - Could not create a new fresh file!");
					return null;
				}
				if(!copy(inputFile, freshFile)) {
					Utils.e(TAG, "moveToANewProductImageFile(Context, Uri) - Could not copy to a new fresh file!");
					return null;
				}
				return Uri.fromFile(freshFile);
			}
		}
		else {
			Utils.e(TAG, "moveToANewProductImageFile(Context, Uri) - File with the given input Uri does not exist!");
			return null;
		}
	}
	
	public static boolean deleteProductImageFile(Uri uri) {
		File file = new File(uri.getPath());
		if(file.exists() && file.isFile()) {
			file.delete();
			return true;
		}
		else {
			Utils.e(TAG, "deleteProductImageFile(Uri) - File with the given input Uri does not exist!");
			return false;
		}
	}
	
	public static void deleteAllProductImageFiles(Context context) {
		File imagesDir = new File(context.getFilesDir(), PRODUCT_IMAGES_DIR);
		if(imagesDir.exists()) {
			File[] allProductPhotosFiles = imagesDir.listFiles();
			for (File file : allProductPhotosFiles) {
				file.delete();
			}
		}
	}
	
	public static boolean copy(File src, File dst) {
		try {
			InputStream in = new FileInputStream(src);
			return copy(in, dst);
		} catch (FileNotFoundException e) {
			return false;
		}
	}
	
	public static boolean copy(InputStream src, File dst) {
		InputStream in = null;
		OutputStream out = null;
		boolean success = false;
		try {
		    in = src;
		    out = new FileOutputStream(dst);
	
		    // Transfer bytes from in to out
		    byte[] buf = new byte[1024];
		    int len;
		    while ((len = in.read(buf)) > 0) {
		        out.write(buf, 0, len);
		    }
		    in.close();
		    out.close();
		    success = true;
		}
		catch (IOException e){
			 Utils.e(TAG, "copy(File, File) IOException - " + e.getMessage());
		}
		finally {
			try{
				if(in != null) in.close();
			    if(out != null) out.close();
			}
			catch (IOException ex) { Utils.e(TAG, "copy(File, File) IOException - " + ex.getMessage()); }
		}
		return success;
	}
}
