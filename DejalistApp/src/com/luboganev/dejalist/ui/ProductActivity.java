package com.luboganev.dejalist.ui;

import butterknife.InjectView;
import butterknife.Views;

import com.luboganev.dejalist.R;
import com.luboganev.dejalist.crop.CropActivity;
import com.luboganev.dejalist.data.DejalistContract;
import com.luboganev.dejalist.data.entities.Category;
import com.luboganev.dejalist.ui.MainActivity.NavigationCursorAdapter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

public class ProductActivity extends FragmentActivity implements
		LoaderCallbacks<Cursor>, OnItemSelectedListener, CategoriesController {
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

	private CategoriesListCursorAdapter mAdapter;

	private static final int LOADER_NAVIGATION_ID = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product);
		Views.inject(this);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		if (getSupportLoaderManager().getLoader(LOADER_NAVIGATION_ID) != null) {
			getSupportLoaderManager().restartLoader(LOADER_NAVIGATION_ID, null,
					this);
		} else
			getSupportLoaderManager().initLoader(LOADER_NAVIGATION_ID, null,
					this);

		mAdapter = new CategoriesListCursorAdapter(getApplicationContext(),
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		mCategory.setAdapter(mAdapter);
		mCategory.setOnItemSelectedListener(this);

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

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			mImage.setImageURI(data.getData());
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader cursorLoader = new CursorLoader(getApplicationContext(),
				DejalistContract.Categories.CONTENT_URI, null, null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		data.setNotificationUri(getContentResolver(),
				DejalistContract.Categories.CONTENT_URI);
		mAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
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
	public void onCategoryNewProduct() {
		// do nothing, cause it cannot come from anywhere
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id)	 {
		if (id == CategoriesListCursorAdapter.CATEGORY_ADD_ITEM_ID) {
			CategoryDialogFragment dialog = CategoryDialogFragment.getInstance();
	        dialog.show(getSupportFragmentManager(), "CategoryDialogFragment");
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
	}
}
