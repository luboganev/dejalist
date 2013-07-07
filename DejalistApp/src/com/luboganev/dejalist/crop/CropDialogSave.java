package com.luboganev.dejalist.crop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import junit.framework.Assert;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.luboganev.dejalist.R;

public class CropDialogSave extends DialogFragment {
	private final static String KEY_CROP_LEFT = "crop_left";
	private final static String KEY_CROP_RIGHT = "crop_right";
	private final static String KEY_CROP_TOP = "crop_top";
	private final static String KEY_CROP_BOTTOM = "crop_bottom";
	private final static String KEY_SOURCE_URI = "source_uri";
	private final static String KEY_DESTINATION_FILE = "destination_file";
	private final static String KEY_ROTATION = "rotation";
	
	private int rotation;
	private Rect crop = null;
	private static ThreadSave mThread = null;
	private Uri mSourceUri;
	private File mDestinationFile;

	public static CropDialogSave newInstance(Uri sourceUri, File destination, int rotation, Rect crop) {
		CropDialogSave fragment = new CropDialogSave();
		Bundle args = CropDialogSave.storeArgs(sourceUri, destination);
		args.putInt(KEY_ROTATION, rotation);
		if (crop != null) {
			args.putInt(KEY_CROP_LEFT,   crop.left);
			args.putInt(KEY_CROP_RIGHT,  crop.right);
			args.putInt(KEY_CROP_TOP,    crop.top);
			args.putInt(KEY_CROP_BOTTOM, crop.bottom);
		}
		fragment.setArguments(args);
		return fragment;
	}
	
	private String getTitle() {
		return getString(R.string.dialog_crop_title);
	}

	private String getMessage() {
		return getString(R.string.dialog_crop_message);
	}	
	private String getCancelMessage() {
		return getString(R.string.dialog_crop_cancel);
	}
	
	private void setProgress(int progress) {}

	private ThreadSave makeThread(Uri source, File destination) {
		String path = source.getPath();
		if (path == null) return null;
		File sourceFile = new File(path);
		return new ThreadSave(sourceFile, destination, rotation, crop);
	}

	private void initProgresDialog(ProgressDialog dialog) {
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setTitle(getTitle());
		dialog.setMessage(getMessage());
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);		
	}
	
	private static Bundle storeArgs(Uri uri, File file) {
		Bundle bundle = new Bundle();
		bundle.putString(KEY_SOURCE_URI, uri.toString());
		bundle.putString(KEY_DESTINATION_FILE, file.getAbsolutePath());
		return bundle;
	}
	
	private void loadArgs(Bundle bundle) {
		Assert.assertNotNull(bundle);
		mSourceUri = Uri.parse(bundle.getString(KEY_SOURCE_URI));
		mDestinationFile = new File(bundle.getString(KEY_DESTINATION_FILE));
		this.rotation = bundle.getInt(KEY_ROTATION);
		if (bundle.containsKey(KEY_CROP_LEFT)) {
			int left   = bundle.getInt(KEY_CROP_LEFT); 
			int right  = bundle.getInt(KEY_CROP_RIGHT); 
			int top    = bundle.getInt(KEY_CROP_TOP);
			int	bottom = bundle.getInt(KEY_CROP_BOTTOM);
			this.crop = new Rect(left, top, right, bottom);
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadArgs(getArguments());
	}
	
	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		final ProgressDialog dialog = new ProgressDialog(getActivity());
		initProgresDialog(dialog);
		return dialog;
	}

	private ProgressDialog getProgressDialog() {
		return (ProgressDialog) getDialog();
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		if (mThread != null)
			mThread.interrupt();
		mThread = null;
		toast(getCancelMessage());
		super.onCancel(dialog);
	}
	
	@Override
	public void onPause() {
		handler.removeCallbacks(mUpdateProgress);
		super.onPause();
	}
	
	@Override
	public void onResume() {
		handler.post(mUpdateProgress);
		super.onResume();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		ProgressDialog dlg = getProgressDialog();
		if (mThread == null) {
			dlg.setProgress(0);
			mThread = makeThread(mSourceUri, mDestinationFile);
			mThread.start();
		}
	}

	private final static int MESSAGE_TOAST = 1;
	private final static int MESSAGE_PROGRESS = 2;
	
	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			Activity activity = getActivity();
			if (activity == null) return true;
			Context context = activity.getApplicationContext();
			switch (msg.what) {
			case MESSAGE_TOAST:
				String s = (String) msg.obj;
				Toast.makeText(context, s, Toast.LENGTH_LONG).show();
				return false;
			case MESSAGE_PROGRESS:
				Integer progress = (Integer) msg.obj;
				setProgress(progress);				
				return false;
			}
			return true;
		}
	});

	/**
	 * Show a toast from a background thread
	 * @param s
	 */
	private void toast(String s) {
		Message msg = handler.obtainMessage(MESSAGE_TOAST, s);
		handler.sendMessage(msg);
	}

	private Runnable mUpdateProgress = new Runnable() {
		public void run() {
			if (mThread == null) return;
			ProgressDialog dlg = getProgressDialog();
			if (dlg == null) return;
			if (mThread.isFinished()) {
				CropActivity activity = (CropActivity) getActivity();
				activity.onSaveFinished(mDestinationFile);
				dismiss();
				mThread = null;
			} else {
				Message msg = handler.obtainMessage(MESSAGE_PROGRESS, Integer.valueOf(mThread.getProgress()));
				handler.sendMessage(msg);
				handler.postDelayed(mUpdateProgress, 100);
			}
		}
	};
	
	private class ThreadSave extends Thread {
		private final File input, output;
		private final Rect crop;
		private final int rotation;
		private int progress = 0;
		private boolean finished = false;
		
		public ThreadSave(File input, File output, int rotation, Rect crop) {
			this.input = input;
			this.output = output;
			this.crop = crop;
			this.rotation = rotation;
		}
		
		public int getProgress() {
			return progress;
		}

		public void run() {
			
			
			if (input.equals(output) && rotation == 0 && crop == null) return;
			BitmapFactory.Options o2 = null;
			try {
				FileInputStream fis = new FileInputStream(input);
				BitmapFactory.Options o = new BitmapFactory.Options();
				o.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(fis, null, o);
				fis.close();
		        int scale = CropUtils.scalePow2(o.outHeight, o.outWidth);
		        o2 = new BitmapFactory.Options();
		        o2.inSampleSize = scale;
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			if(o2 != null) {
				Bitmap bitmap = BitmapFactory.decodeFile(input.getPath(), o2);
				if (isInterrupted()) {
					bitmap.recycle();
					return;
				}

				bitmap = CropUtils.rotateAndCrop(bitmap, rotation, crop);
				if (isInterrupted()) {
					bitmap.recycle();
					return;
				}
				
				int maxDimen = ((int)getActivity().getResources().getDimension(R.dimen.product_picture_cropped_max));
				if(bitmap.getWidth() > maxDimen || bitmap.getHeight() > maxDimen) {
					bitmap = CropUtils.resize(bitmap, maxDimen, maxDimen);
				}
				
				OutputStream out = openOutput();
		        try {
		            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
		        } catch (Exception e) {
		        }
		        bitmap.recycle();
		        closeOutput(out);
			}
			finished = true;
		}

		public boolean isFinished() {
			return finished;
		}
		
		private OutputStream openOutput() {
	        try {
				return new FileOutputStream(output);
			} catch (FileNotFoundException e) {
			}
	        return null;
	    }
	    
	    private void closeOutput(OutputStream outputStream) {
	    	try {
	    		outputStream.close();
	    	} catch (IOException e) {
	    	}	
	    }

	}

}
