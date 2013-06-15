package com.luboganev.dejalist;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

/**
 * This class contains methods that return the device's display resolution 
 * in pixels with or without the decorations (navigation bar)
 * in its natural or current orientation.
 */
public class DisplayCompat {
	/**
	 * The natural orientation of the device
	 */
	public static final byte ORIENTATION_0 = 1;
	
	/**
	 * The device is rotated 90 degrees counter clockwise
	 */
	public static final byte ORIENTATION_90 = 2;
	
	/**
	 * The opposite of the natural rotation, i.e. device is rotated 180 degrees
	 */
	public static final byte ORIENTATION_180 = 3;
	
	/**
	 * The device is rotated 90 degrees clockwise
	 */
	public static final byte ORIENTATION_270 = 4;
	
	/** Since the raw resolution does not ever change we read it once and cache it */
	private static Point mFullNaturalResolution = null;
	
	/**
	 * Returns the physical pixels that 
	 * corresponds to the input DIPs
	 * 
	 * @param context
	 * 		Use application context and not some activity's context to prevent memory leaks
	 */
	public static int getPixelsFromDips(Context context, int dips) {
		float result = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dips, context.getResources().getDisplayMetrics());
		return (int)result;
	}
	
	/**
	 * Gets the screen resolution for the current orientation excluding decorations
	 * 
	 * @param context
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static Point getRotatedResolution(Context context) {
		Display disp = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
			return new Point(disp.getWidth(), disp.getHeight());
		}
		else {
			Point p = new Point();
			disp.getSize(p);
			return p;
		}
	}
	
	/**
	 * Gets the screen resolution for the natural orientation excluding decorations
	 * 
	 * @param context
	 * @return
	 */
	public static Point getNaturalResolution(Context context) {
		Point p = getRotatedResolution(context);
		byte orientation = getOrientation(context);
		switch (orientation) {
		case ORIENTATION_0:
		case ORIENTATION_180:
			return p;
		case ORIENTATION_90:
		case ORIENTATION_270:
			return new Point(p.y, p.x);
		default:
			return p;
		}
	}
	
	/**
	 * Return the orientation of the current previewing screen
	 * 
	 * @param context
	 * 		We need the context to get the orientation
	 * @return
	 * 		One of the following orientation constants:
	 * 		<ul>
	 * 			<li>{@link #ORIENTATION_0}</li>
	 * 			<li>{@link #ORIENTATION_90}</li>
	 * 			<li>{@link #ORIENTATION_180}</li>
	 * 			<li>{@link #ORIENTATION_270}</li>
	 * 		</ul>
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static byte getOrientation(Context context)  {
		WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display disp = wm.getDefaultDisplay();
		
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
			int orientation = disp.getOrientation();
			// Sometimes you may get undefined orientation Value is 0
			// simple logic solves the problem compare the screen
			// X,Y Coordinates and determine the Orientation in such cases
			if(orientation==Configuration.ORIENTATION_UNDEFINED) {
				Configuration config = context.getResources().getConfiguration();
				orientation = config.orientation;
	
				if(orientation==Configuration.ORIENTATION_UNDEFINED){
					//if height and width of screen are equal then
					// it is square orientation
					if(disp.getWidth()==disp.getHeight()){
						orientation = Configuration.ORIENTATION_SQUARE;
					}else{ //if width is less than height than it is portrait
						if(disp.getWidth() < disp.getHeight()){
							orientation = Configuration.ORIENTATION_PORTRAIT;
						}else{ // if it is not any of the above it will definitely be landscape
							orientation = Configuration.ORIENTATION_LANDSCAPE;
						}
					}
				}
			}
			return orientation == 1 ? ORIENTATION_0 : ORIENTATION_180; // 1 for portrait, 2 for landscape
		}
		else {
			int rot = disp.getRotation();
			switch (rot) {
				case Surface.ROTATION_0:	return ORIENTATION_0;
				case Surface.ROTATION_90:	return ORIENTATION_90;
				case Surface.ROTATION_180:	return ORIENTATION_180;
				case Surface.ROTATION_270:	return ORIENTATION_270;
				default:					return ORIENTATION_0;
			}
		}
	}
	
	/**
	 * Returns the full device resolution depending on the current orientation.
	 * @return
	 * 		A {@link Point} containing the width and height in pixels as x and y.
	 */
	public static Point getRotatedFullResolution(Context context) {
		Point p = getNaturalFullResolution(context);
		switch(getOrientation(context)) {
		case ORIENTATION_0:
		case ORIENTATION_180:
			return p;
		case ORIENTATION_90:
		case ORIENTATION_270:
			return new Point(p.y,p.x);
		default:
			return p;
		}
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private static Point getJBFullRotatedResolution(Display display) {
		Point p = new Point();
		display.getRealSize(p);
		return p;
	}

	/**
	 * Tries to get the natural full resolution of the current device. This is the whole resolution of the
	 * screen including of system bars for notification and navigation in the natural
	 * device orientation, i.e. {@link #ORIENTATION_0}. If the natural method fails this
	 * method returns the size of the visible part without these bars.
	 * 
	 * @param context
	 * @return
	 * 		A {@link Point} containing the width and height in pixels as x and y.
	 */
	public static Point getNaturalFullResolution(Context context) {
		if(mFullNaturalResolution == null) {
			Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			int rawWidth, rawHeight, width, height;
			
			if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
				// Try with the undocumented methods first
				Method mGetRawH;
				Method mGetRawW;
				try {
				    mGetRawH = Display.class.getMethod("getRawWidth");
				    mGetRawW = Display.class.getMethod("getRawHeight");
				    rawWidth = (Integer) mGetRawW.invoke(display);
				    rawHeight = (Integer) mGetRawH.invoke(display);
				} catch (NoSuchMethodException e) {
					rawWidth = -1; rawHeight = -1;
				} catch (IllegalArgumentException e) {
					rawWidth = -1; rawHeight = -1;
				} catch (IllegalAccessException e) {
					rawWidth = -1; rawHeight = -1;
				} catch (InvocationTargetException e) {
					rawWidth = -1; rawHeight = -1;
				}
			}
			else {
				Point p = getJBFullRotatedResolution(display);
				rawWidth = p.x;
				rawHeight = p.y;
			}
			
			// Get the documented ones
			DisplayMetrics dm = new DisplayMetrics();
			display.getMetrics(dm);
			width = dm.widthPixels;
			height = dm.heightPixels;
			
			// adjust the width and height to the raw ones if necessary
			if(rawWidth != -1 && rawHeight != -1) {
				if(width>=height) {
					width = Math.max(rawWidth, rawHeight);
					height = Math.min(rawWidth, rawHeight);
				}
				else {
					width = Math.min(rawWidth, rawHeight);
					height = Math.max(rawWidth, rawHeight);
				}
			}
			
			// take care of orientation
			byte orientation = getOrientation(context);
			if(orientation == ORIENTATION_0 || orientation == ORIENTATION_180) {
				mFullNaturalResolution = new Point(width,height);
			}
			else {
				mFullNaturalResolution = new Point(height,width);
			}
		}
		return mFullNaturalResolution;
	}
}
