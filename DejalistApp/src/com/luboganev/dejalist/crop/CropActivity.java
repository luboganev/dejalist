package com.luboganev.dejalist.crop;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import com.luboganev.dejalist.R;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class CropActivity extends FragmentActivity {
	private final static int REQUEST_CODE_PICK_IMAGE = 1;
	private final static int REQUEST_CODE_TAKE_PHOTO = 2;
	
	private final static String STATE_FILE_URI = "state_file_uri";
	private final static String STATE_DESTINATION_FILE_URI = "state_destination_file_uri";
	private final static String STATE_PHOTO_TMP_FILE = "state_photo_tmp_file";
	private final static String STATE_LAST_PHOTO_TMP_FILE = "state_last_photo_tmp_file";
	private final static String STATE_SOURCE = "state_source";
	
	public final static String EXTRA_DESTINATION_FILE = "extra_destination_file";
	public final static String EXTRA_SOURCE = "extra_source_image";
	public final static String EXTRA_ROTATION = "extra_rotation";
	public final static int EXTRA_SOURCE_IMAGE = 1;
	public final static int EXTRA_SOURCE_CAMERA = 2;
	
	private Bitmap bitmap;	
	private CropView preview;
	private BitmapFactory.Options bitmapOptions;
	private File photoFile = null;
	private File lastPhotoFile = null;
	private Uri uri = null;
	private Uri destinationUri = null;
	private int mSource = -1;
	private TextView noImageText;
	private boolean deleteTempPicturesOnDestroy = true;
	private int rotation;
	
	private File generateTempPictureFile(final Context context) {
		return new File(Environment.getExternalStorageDirectory(), UUID.randomUUID().toString() + ".jpg"); 
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Setup the layout
		setContentView(R.layout.activity_crop);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowHomeEnabled(false);
		preview = (CropView) findViewById(R.id.image_editor_preview);
		noImageText = (TextView) findViewById(R.id.image_editor_no_image_text);
		
		// Try to restore state first
		restoreState(savedInstanceState);
		
		// Read intent data if not restored		
		if(destinationUri == null && getIntent() != null) {
			String s = getIntent().getStringExtra(EXTRA_DESTINATION_FILE);
			if(s != null) {
				destinationUri = Uri.parse(s);
			}
		}
		if(mSource == -1 && getIntent() != null){
			mSource = getIntent().getIntExtra(EXTRA_SOURCE, -1);
		}
		
		// load the custom actionbar
        getActionBar().setCustomView(R.layout.actionbar_crop_done_discard);
        getActionBar().setDisplayShowCustomEnabled(true);
        final View customActionBarView = getActionBar().getCustomView();
        customActionBarView.findViewById(R.id.actionbar_crop_done).setOnClickListener(
                new OnCustomActionBarItemClicked());
        customActionBarView.findViewById(R.id.actionbar_crop_discard).setOnClickListener(
        		new OnCustomActionBarItemClicked());
        customActionBarView.findViewById(R.id.actionbar_crop_rotate_ccw).setOnClickListener(
        		new OnCustomActionBarItemClicked());
        customActionBarView.findViewById(R.id.actionbar_crop_rotate_cw).setOnClickListener(
        		new OnCustomActionBarItemClicked());
        
        CropUtils.recalculateMaxImagesize(getApplicationContext());
        
     	// If we need to have a defined source of the image through the starting intent!
 		if(mSource == EXTRA_SOURCE_IMAGE) {
 			// if the activity starts for the first time go straight to image open
         	if(savedInstanceState == null) openImage(); 
         	else if(!savedInstanceState.containsKey(STATE_SOURCE)) openImage();
         }
 		else if(mSource == EXTRA_SOURCE_CAMERA) {
 			// if the activity starts for the first time go straight to take photo
         	if(savedInstanceState == null) takePhoto(); 
         	else if(!savedInstanceState.containsKey(STATE_SOURCE)) takePhoto();
         }
 		else finish();
	}
	
	private class OnCustomActionBarItemClicked implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.actionbar_crop_done:
				saveImage();
				break;
			case R.id.actionbar_crop_discard:
				setResult(Activity.RESULT_CANCELED);
                finish();
				break;
			case R.id.actionbar_crop_rotate_ccw:
				addToRotation(270);
				break;
			case R.id.actionbar_crop_rotate_cw:
				addToRotation(90);
				break;
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(deleteTempPicturesOnDestroy) {
			deleteLastPhotoFile();
			deletePhotoFile();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		showImageTools(uri != null);
		deleteTempPicturesOnDestroy = true;
	}
	
	private void openImage() {
		// Sends general image content request
    	Intent intent = new Intent();
    	intent.setType("image/*");
    	intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
	}
	
	private void takePhoto() {
		Intent intent = new Intent();
		intent.setAction("android.media.action.IMAGE_CAPTURE");
		if(photoFile != null) {
			deleteLastPhotoFile();
			lastPhotoFile = photoFile;
		}
		photoFile = generateTempPictureFile(getApplicationContext());
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
		startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
	}
	
	private void addToRotation(int degrees) {
		if(bitmap == null) {
			Toast.makeText(getApplicationContext(), R.string.toast_crop_no_image, Toast.LENGTH_SHORT).show();
			return;
		}
        rotation = (rotation + degrees) % 360;
        preview.setHighlight(null);             
        bitmap = CropUtils.rotate(bitmap, degrees);
        preview.setImageBitmapResetBase(bitmap, true);
        makeHighlight();
	}
	
	private void showImageTools(boolean show) {
		noImageText.setVisibility(show ? View.GONE : View.VISIBLE);
		if(mSource <= 0) getActionBar().setDisplayShowCustomEnabled(show);
	}
	
	private void restoreState(Bundle bundle) {
		if (bundle == null) return;
		if (bundle.containsKey(STATE_PHOTO_TMP_FILE))
			photoFile = new File(bundle.getString(STATE_PHOTO_TMP_FILE));
		if (bundle.containsKey(STATE_LAST_PHOTO_TMP_FILE))
			lastPhotoFile = new File(bundle.getString(STATE_LAST_PHOTO_TMP_FILE));
		rotation = bundle.getInt(EXTRA_ROTATION, 0);
		mSource = bundle.getInt(STATE_SOURCE, -1);
		String uriStr = bundle.getString(STATE_FILE_URI);
		if (uriStr != null) {
			uri = Uri.parse(uriStr);
			loadBitmap();
		}
		uriStr = bundle.getString(STATE_DESTINATION_FILE_URI);
		if (uriStr != null) {
			destinationUri = Uri.parse(uriStr);
		}
	}

	private Bundle saveState(Bundle bundle) {
		deleteTempPicturesOnDestroy = false;
		if (uri != null)
			bundle.putString(STATE_FILE_URI, uri.toString());
		if (destinationUri != null)
			bundle.putString(STATE_DESTINATION_FILE_URI, uri.toString());
		if (photoFile != null) {
			bundle.putString(STATE_PHOTO_TMP_FILE, photoFile.getAbsolutePath());
		}
		if (lastPhotoFile != null) {
			bundle.putString(STATE_LAST_PHOTO_TMP_FILE, lastPhotoFile.getAbsolutePath());
		}
		if (mSource != -1) {
			bundle.putInt(STATE_SOURCE, mSource);
		}
		bundle.putInt(EXTRA_ROTATION, rotation);
		return bundle;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState(outState);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		switch (requestCode) {
			case REQUEST_CODE_TAKE_PHOTO:
				if (resultCode != RESULT_OK) {
					// if no image was ever loaded, this means initial state, then close the activity with cancel
					if (uri == null) {
						setResult(Activity.RESULT_CANCELED);
						finish();
					}
					photoFile = lastPhotoFile;
					lastPhotoFile = null;
	
					break;
				}
				if (photoFile == null || !photoFile.exists()) {
					photoFile = null;
					Toast.makeText(this, R.string.toast_crop_img_load_failed,
							Toast.LENGTH_LONG).show();
					return;
				}
				loadBitmap(Uri.fromFile(photoFile), 0);
			break;
			case REQUEST_CODE_PICK_IMAGE:
				if (resultCode != RESULT_OK) {
					// if no image was ever loaded, this means initial state, then close the activity with cancel
					if (uri == null) {
						setResult(Activity.RESULT_CANCELED);
						finish();
					}
					break;
				}
				
				deleteLastPhotoFile();
				deletePhotoFile();
				
				Uri selectedImage = intent.getData();
				if (selectedImage == null) {
					return;
				}

				if (selectedImage.toString().startsWith(
						"content://com.android.gallery3d.provider")) {
					// some devices/OS versions return an URI of com.android instead
					// of com.google.android
					String str = selectedImage.toString()
							.replace("com.android.gallery3d", "com.google.android.gallery3d");
					selectedImage = Uri.parse(str);
				}
				boolean picasaImage = selectedImage.toString().startsWith(
						"content://com.google.android.gallery3d");
				
				final String[] filePathColumn = { MediaColumns.DATA,
						MediaColumns.DISPLAY_NAME };
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				if (cursor == null) {
					Toast.makeText(this, R.string.toast_crop_img_load_failed, Toast.LENGTH_LONG)
							.show();
					return;
				}
				
				cursor.moveToFirst();
				if (picasaImage) {
					int columnIndex = cursor.getColumnIndex(MediaColumns.DISPLAY_NAME);
					if (columnIndex == -1) {
						return;
					}
					Toast.makeText(getApplicationContext(), getString(R.string.toast_crop_not_local_image), Toast.LENGTH_SHORT).show();
					return;
				} else {
					int columnIndex = cursor.getColumnIndex(MediaColumns.DATA);
					if (columnIndex == -1) {
						return;
					}
					String name = cursor.getString(columnIndex);
					File file = new File(name);
					if (!file.exists() || !file.canRead()) {
						Toast.makeText(this, R.string.toast_crop_img_load_failed,
								Toast.LENGTH_LONG).show();
						return;
					}
					loadBitmap(Uri.fromFile(file), 0);
				}
				cursor.close();
			break;
		}
	}
	
	protected void loadBitmap(Uri sourceUri, int rotation) {
		this.uri = sourceUri;
		this.rotation = rotation;
		loadBitmap();
	}
	
	private void loadBitmap() {
		if (uri == null) return;
		// make some space for the new bitmap
		if(bitmapOptions != null) {
			preview.setImageBitmap(null);
			bitmapOptions = null;
		}
		BitmapFactory.Options o = CropUtils.getBitmapOptions(getContentResolver(), uri);
		bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = CropUtils.scalePow2(o.outHeight, o.outWidth);

		bitmap = CropUtils.getBitmap(bitmapOptions, getContentResolver(), uri);
		bitmap = CropUtils.rotate(bitmap, rotation);
		preview.setImageBitmapResetBase(bitmap, true);
		Log.d("CropActivity", "SampleSize " + bitmapOptions.inSampleSize + " -> ("+bitmapOptions.outWidth+":"+bitmapOptions.outHeight+")");
		makeHighlight();
	}
	
	private void makeHighlight() {
		if (bitmap == null) return;
		CropHighlightView hv = new CropHighlightView(preview);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Rect imageRect = new Rect(0, 0, width, height);

		// make the default size about 4/5 of the width or height
		int cropWidth = Math.min(width, height) * 4 / 5;
		int cropHeight = cropWidth;
		int x = (width - cropWidth) / 2;
		int y = (height - cropHeight) / 2;
		RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
		hv.setup(preview.getImageMatrix(), imageRect, cropRect, false, true, 160f);
		hv.setFocus(true);
		preview.setHighlight(hv);
	}
	
	/**
	 * Callback from the save progress dialog
	 */
	protected void onSaveFinished(File file) {
		Intent intent = new Intent();
		intent.setData(Uri.fromFile(file));
		setResult(RESULT_OK, intent);
		finish();
	}
	
	private void saveImage() {
		if(bitmap == null) {
			Toast.makeText(getApplicationContext(), R.string.toast_crop_no_image, Toast.LENGTH_SHORT).show();
			return;
		}
		File destinationFile;
		if(destinationUri != null) {
			destinationFile = new File(destinationUri.getPath());
		}
		else {
			destinationFile = generateTempPictureFile(getApplicationContext());
		}
		try {
			if(bitmapOptions != null) {
				preview.setImageBitmap(null);
				bitmapOptions = null;
			}
			destinationFile.createNewFile();
			DialogFragment newFragment = 
					CropDialogSave.newInstance(uri, destinationFile, rotation, 
							preview.getCropRect());
			newFragment.show(getSupportFragmentManager(), "saveImage");
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), R.string.toast_crop_save_failed, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void deleteLastPhotoFile() {
		if(lastPhotoFile != null) {
			lastPhotoFile.delete();
			lastPhotoFile = null;
		}
	}
	private void deletePhotoFile() {
		if(photoFile != null) {
			photoFile.delete();
			photoFile = null;
		}
	}
	
}
