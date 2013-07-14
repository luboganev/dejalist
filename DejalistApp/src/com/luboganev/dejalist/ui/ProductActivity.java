package com.luboganev.dejalist.ui;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

import java.io.File;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.Views;

import com.luboganev.dejalist.R;
import com.luboganev.dejalist.crop.CropActivity;
import com.luboganev.dejalist.data.DejalistContract;
import com.luboganev.dejalist.data.DejalistContract.Categories;
import com.luboganev.dejalist.data.DejalistContract.Products;
import com.luboganev.dejalist.data.ProductImageFileHelper;
import com.luboganev.dejalist.data.entities.Category;
import com.luboganev.dejalist.data.entities.Product;
import com.luboganev.dejalist.ui.CategoryDialogFragment.CategoryEditorCallback;
import com.squareup.picasso.Picasso;

public class ProductActivity extends FragmentActivity implements CategoryEditorCallback, OnItemSelectedListener {
	private static final int REQUEST_CODE = 1;
	
	public static final String RESULT_EXTRA_PRODUCT_CATEGORY_ID = "product_category_id";

	@InjectView(R.id.iv_product_picture)
	ImageView mImage;
	@InjectView(R.id.et_product_name)
	EditText mName;
	@InjectView(R.id.sp_product_category)
	Spinner mCategory;
	@InjectView(R.id.ib_product_change_camera)
	ImageButton mChangeCamera;
	@InjectView(R.id.ib_product_change_image)
	ImageButton mChangeImage;
	@InjectView(R.id.ib_new_category)
	ImageButton mNewCategory;
	@InjectView(R.id.tv_product_picture_hint)
	TextView mProductImageHint;
	
	private static final String STATE_NEW_PICTURE_URI = "state_new_picture_uri";
	private Uri mNewPictureUri = null;
	private static final String STATE_NEW_CATEGORY_ID = "state_new_category_id";
	private long mNewCategoryId;
	
	public static final String EXTRA_PRODUCT = "extra_product";
	private Product mOriginalProduct = null;
	public static final String EXTRA_CATEGORY_ID = "extra_category_id";

	private CategoriesListCursorAdapter mAdapter;
	
	private boolean deviceRotated;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product);
		Views.inject(this);
		
		// Inflate a "Done/Discard" custom action bar view.
        LayoutInflater inflater = (LayoutInflater) getActionBar().getThemedContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        final View customActionBarView = inflater.inflate(
                R.layout.actionbar_product_done_discard, null);
        customActionBarView.findViewById(R.id.product_done).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    	if(saveProduct()) {
                    		Intent data = new Intent();
                    		data.putExtra(RESULT_EXTRA_PRODUCT_CATEGORY_ID, mNewCategoryId);
            				setResult(Activity.RESULT_OK, data);
            				finish();
            			}
                    }
                });
        customActionBarView.findViewById(R.id.product_discard).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    	setResult(Activity.RESULT_CANCELED);
            			finish();
                    }
                });

        // Show the custom action bar view and hide the normal Home icon and title.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(
                ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
                        | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(customActionBarView, new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
		
		
		// Load just category sent to this activity
		mNewCategoryId = getIntent().getLongExtra(EXTRA_CATEGORY_ID, Products.PRODUCT_CATEGORY_NONE_ID);
		
		// Load the original Product sent to this activity
		if(getIntent().hasExtra(EXTRA_PRODUCT)) {
			mOriginalProduct = getIntent().getParcelableExtra(EXTRA_PRODUCT);
		}
		
		if(savedInstanceState != null) {
			if(savedInstanceState.containsKey(STATE_NEW_CATEGORY_ID)) mNewCategoryId = savedInstanceState.getLong(STATE_NEW_CATEGORY_ID);
			if(savedInstanceState.containsKey(STATE_NEW_PICTURE_URI)) mNewPictureUri = Uri.parse(savedInstanceState.getString(STATE_NEW_PICTURE_URI));
		}
		else {
			// no saved instance, if there is a whole product, load its properties
			if(mOriginalProduct != null) {
				mNewCategoryId = mOriginalProduct.categoryId;
				mName.setText(mOriginalProduct.name);
			}
		}
		
		// Load all categories
		

		mAdapter = new CategoriesListCursorAdapter(getApplicationContext(),
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		mCategory.setAdapter(mAdapter);
		
		reloadSpinnerCategories();
		
		mChangeImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						CropActivity.class);
				intent.putExtra(CropActivity.EXTRA_SOURCE,
						CropActivity.EXTRA_SOURCE_IMAGE);
				startActivityForResult(intent, REQUEST_CODE);
			}
		});

		mChangeCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						CropActivity.class);
				intent.putExtra(CropActivity.EXTRA_SOURCE,
						CropActivity.EXTRA_SOURCE_CAMERA);
				startActivityForResult(intent, REQUEST_CODE);
			}
		});
		
		mNewCategory.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CategoryDialogFragment dialog = CategoryDialogFragment.getInstance();
		        dialog.show(getSupportFragmentManager(), "CategoryDialogFragment");
			}
		});
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		deviceRotated = true;
		if(mNewPictureUri != null) outState.putString(STATE_NEW_PICTURE_URI, mNewPictureUri.toString());
		outState.putLong(STATE_NEW_CATEGORY_ID, mNewCategoryId);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(!deviceRotated) {
			if(mNewPictureUri != null) {
				File tempFile = new File(mNewPictureUri.getPath());
				if(tempFile.exists()) tempFile.delete();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// for some reason the onSaveState is called when we open the other activity, 
		// so we need to reset it to false once we come back
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			if(mNewPictureUri != null) {
				File tempFile = new File(mNewPictureUri.getPath());
				if(tempFile.exists()) tempFile.delete();
			}
			mNewPictureUri = data.getData();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		loadPicture();
		deviceRotated = false; 
	}
	
	private void loadPicture() {
		if(mNewPictureUri != null) {
			Picasso.with(getApplicationContext()).load(mNewPictureUri)
			.resizeDimen(R.dimen.product_picture_cropped_max, R.dimen.product_picture_cropped_max)
			.error(R.drawable.product_no_pic_big).into(mImage);
			mProductImageHint.setVisibility(View.INVISIBLE);
		}
		else if(mOriginalProduct != null) {
			if(mOriginalProduct.uri != null) {
				Picasso.with(getApplicationContext()).load(mOriginalProduct.uri)
				.resizeDimen(R.dimen.product_picture_cropped_max, R.dimen.product_picture_cropped_max)
				.error(R.drawable.product_no_pic_big).into(mImage);
				mProductImageHint.setVisibility(View.INVISIBLE);
			}
			else mProductImageHint.setVisibility(View.VISIBLE);
		}
		else {
			mProductImageHint.setVisibility(View.VISIBLE);
		}
				
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view,
			int position, long id) {
		mNewCategoryId = id;				
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// do nothing
	}
	
	private void reloadSpinnerCategories() {
		mCategory.setOnItemSelectedListener(null);
		Cursor c = cupboard().withContext(getApplicationContext()).query(Categories.CONTENT_URI, Category.class).getCursor();
		mAdapter.changeCursor(c);
		mCategory.setSelection(getCategoryPositionById(mNewCategoryId));
		mCategory.setOnItemSelectedListener(this);
	}
	
	private int getCategoryPositionById(long categoryId) {
		for (int i = 0; i < mCategory.getCount(); i++) {
			if(mCategory.getItemIdAtPosition(i) == categoryId) return i;
		}
		return -1;
	}
	
	private boolean saveProduct() {
		if(mOriginalProduct != null) {
			// we're editing a product
			
			//validate name
			if(mName.getText().length() <=0) {
				Toast.makeText(getApplicationContext(), R.string.toast_product_no_name, Toast.LENGTH_SHORT).show();
				return false;
			}
			else mOriginalProduct.name = mName.getText().toString();
			
			// set the category id
			mOriginalProduct.categoryId = mNewCategoryId;
			
			// if the image was changed
			if(mNewPictureUri != null) {
				// image was changed
				Uri productFileUri = ProductImageFileHelper.copyToANewProductImageFile(getApplicationContext(), mNewPictureUri);
				if(productFileUri != null) {
					if(mOriginalProduct.uri != null) {
						// delete old image if any
						ProductImageFileHelper.deleteProductImageFile(Uri.parse(mOriginalProduct.uri));
					}
					// update image
					mOriginalProduct.uri = productFileUri.toString();
				}
				else {
					Toast.makeText(getApplicationContext(), R.string.toast_product_failed_save, Toast.LENGTH_SHORT).show();
					return false;
				}
			}
			
			ContentValues values = cupboard().withEntity(Product.class).toContentValues(mOriginalProduct);
			int updated = getContentResolver().update(DejalistContract.Products.buildProductUri(mOriginalProduct._id), values, null, null);
			// check if it was successfully updated
			if(updated > 0) return true;
			else {
				Toast.makeText(getApplicationContext(), R.string.toast_product_failed_save, Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		else {
			Product product = new Product();
			// we're creating a new product
			
			//validate name
			if(mName.getText().length() <=0) {
				Toast.makeText(getApplicationContext(), R.string.toast_product_no_name, Toast.LENGTH_SHORT).show();
				return false;
			}
			else product.name = mName.getText().toString();
			
			// set the category id
			product.categoryId = mNewCategoryId;
			
			//validate image
			if(mNewPictureUri != null) {
				// try to store the image file in the folder
				Uri productFileUri = ProductImageFileHelper.copyToANewProductImageFile(getApplicationContext(), mNewPictureUri);
				if(productFileUri != null) {
					product.uri = productFileUri.toString();
				}
				else {
					Toast.makeText(getApplicationContext(), R.string.toast_product_failed_save, Toast.LENGTH_SHORT).show();
					return false;
				}
			}
			else product.uri = null;
			
			Uri insertedProductUri = cupboard().withContext(getApplicationContext()).put(DejalistContract.Products.CONTENT_URI, product);
			// check if it was successfully inserted
			try {
				DejalistContract.Products.getProductId(insertedProductUri);
				return true;
			}
			catch (NumberFormatException e) {
				Toast.makeText(getApplicationContext(), R.string.toast_product_failed_save, Toast.LENGTH_SHORT).show();
				return false;
			}
		}
	}

	@Override
	public void onCategoryEdited(Category category) {
		// do nothing, cause it cannot come from anywhere
	}

	@Override
	public void onCategoryCreated(Category category) {
		mNewCategoryId = category._id;
		reloadSpinnerCategories();
	}
}
