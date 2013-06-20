package com.luboganev.dejalist.ui;

import java.io.File;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import butterknife.InjectView;
import butterknife.Views;

import com.luboganev.dejalist.R;
import com.luboganev.dejalist.crop.CropActivity;
import com.luboganev.dejalist.data.DejalistContract.Categories;
import com.luboganev.dejalist.data.entities.Category;
import com.luboganev.dejalist.data.entities.Product;
import com.squareup.picasso.Picasso;

public class ProductActivity extends FragmentActivity implements CategoriesController, OnItemSelectedListener {
	private static final int REQUEST_CODE = 1;

	@InjectView(R.id.iv_product_picture)
	ImageView mImage;
	@InjectView(R.id.et_category_name)
	EditText mName;
	@InjectView(R.id.sp_product_category)
	Spinner mCategory;
	@InjectView(R.id.ib_product_change_camera)
	ImageButton mChangeCamera;
	@InjectView(R.id.ib_product_change_image)
	ImageButton mChangeImage;
	@InjectView(R.id.ib_new_category)
	ImageButton mNewCategory;
	
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
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Load just category sent to this activity
		mNewCategoryId = getIntent().getLongExtra(EXTRA_CATEGORY_ID, CategoriesListCursorAdapter.CATEGORY_NONE_ITEM_ID);
		
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
		if(mNewPictureUri != null) 
			Picasso.with(getApplicationContext()).load(mNewPictureUri)
				.resizeDimen(R.dimen.product_picture_big, R.dimen.product_picture_big)
				.error(R.drawable.product_no_pic_big).into(mImage);
		else if(mOriginalProduct != null) Picasso.with(getApplicationContext()).load(mOriginalProduct.uri)
				.resizeDimen(R.dimen.product_picture_big, R.dimen.product_picture_big)
				.error(R.drawable.product_no_pic_big).into(mImage);
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

	@Override
	public void onCategoryEdited(Category category) {
		// do nothing, cause it cannot come from anywhere
	}

	@Override
	public void onCategoryNewAction() {
		// do nothing, cause it cannot come from anywhere
	}

	@Override
	public void onCategoryEditAction(Category category) {
		// do nothing, cause it cannot come from anywhere
	}

	@Override
	public void onCategoryDeleteAction(Category category) {
		// do nothing, cause it cannot come from anywhere
	}

	@Override
	public void registerCategories(CategoriesActionTaker categoriesDelegate) {
		// do nothing, cause it cannot come from anywhere
	}

	@Override
	public void unregisterCategories() {
		// do nothing, cause it cannot come from anywhere
	}

	@Override
	public void onCategoryNewProduct(Category category) {
		// do nothing, cause it cannot come from anywhere
	}

	@Override
	public void onCategoryCreated(Category category) {
		mNewCategoryId = category._id;
		reloadSpinnerCategories();
	}
}
