package com.luboganev.dejalist.ui;

import butterknife.InjectView;
import butterknife.Views;

import com.luboganev.dejalist.R;
import com.luboganev.dejalist.data.DejalistContract;
import com.luboganev.dejalist.data.DejalistContract.Products;
import com.luboganev.dejalist.data.entities.Category;

import android.app.Activity;
import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class ProductsGalleryFragment extends Fragment implements ProductsGalleryActionTaker, LoaderCallbacks<Cursor>, OnItemClickListener, MultiChoiceModeListener {
    public static final String ARG_CATEGORY = "category";
    
    @InjectView(R.id.v_category_colorheader) View categoryColorHeader;
    @InjectView(R.id.grdv_products) GridView mProducts;
    
    private Category mSelectedCategory;
    
    private ProductsGalleryCursorAdapter mAdapter;
    
    private static final int LOADER_PRODUCTS_ID = 2;
    
    private static final String STATE_OPTIONMENUITEMSVISIBLE = "state_optionmenuitemsvisible"; 
    private static final String STATE_CHECKEDPRODUCTPOS = "state_checkedproductpos"; 
    private static final String STATE_SORTBY = "state_sortby"; 
    private boolean mOptionMenuItemsVisible; 
    private int mSortBy;
    private static final int SORT_AZ = 0;
    private static final int SORT_RECENT = 1;
    private static final int SORT_MOST = 2;
    private int[] mCheckedItemPos;
    
    public ProductsGalleryFragment() {
        // Empty constructor required for fragment subclasses
    }
    
    ProductsGalleryController mProductsGalleryController;
    
    public static ProductsGalleryFragment getInstance() {
    	ProductsGalleryFragment fragment = new ProductsGalleryFragment();
    	fragment.setArguments(new Bundle());
    	return fragment;
    }
    
    public static ProductsGalleryFragment getInstance(Category category) {
    	ProductsGalleryFragment fragment = new ProductsGalleryFragment();
    	Bundle bundle = new Bundle();
    	bundle.putParcelable(ARG_CATEGORY, category);
    	fragment.setArguments(bundle);
    	return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	if(savedInstanceState != null) {
    		mOptionMenuItemsVisible = savedInstanceState.getBoolean(STATE_OPTIONMENUITEMSVISIBLE, true);
    		mSortBy = savedInstanceState.getInt(STATE_SORTBY, SORT_AZ);
    		mCheckedItemPos = savedInstanceState.getIntArray(STATE_CHECKEDPRODUCTPOS);
    	}
    	else {
    		mOptionMenuItemsVisible = true;	
    		mSortBy = SORT_AZ;
    		mCheckedItemPos = null;
    	}
    	setHasOptionsMenu(true);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putBoolean(STATE_OPTIONMENUITEMSVISIBLE, mOptionMenuItemsVisible);
    	outState.putInt(STATE_SORTBY, mSortBy);
    	if(mProducts.getCheckedItemCount() > 0) {
    		int[] checkedItemsPositions = new int[mProducts.getCheckedItemCount()];
    		SparseBooleanArray array = mProducts.getCheckedItemPositions();
    		int j=0;
			for (int i = 0; i < array.size(); i++) {
				if(array.valueAt(i)) {
					checkedItemsPositions[j] = array.keyAt(i);
					j++;
				}
			}   		
    		outState.putIntArray(STATE_CHECKEDPRODUCTPOS, checkedItemsPositions);
    	}
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	if(mProductsGalleryController != null) {
    		mProductsGalleryController.unregisterProductsGalleryActionTaker();
    	}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_productsgallery, container, false);
        Views.inject(this, rootView);
        
        if(getArguments().containsKey(ARG_CATEGORY)) {
        	mSelectedCategory = getArguments().getParcelable(ARG_CATEGORY);
        	categoryColorHeader.setBackgroundColor(mSelectedCategory.color);
        	getActivity().setTitle(mSelectedCategory.name);
        } else {
        	mSelectedCategory = null;
        	categoryColorHeader.setVisibility(View.GONE);
        	getActivity().setTitle(R.string.nav_my_products);
        }
        
        return rootView;
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	inflater.inflate( 
    			(getArguments().containsKey(ARG_CATEGORY) ? 
    					R.menu.menu_category : 
    						R.menu.menu_all_products), menu);
    	super.onCreateOptionsMenu(menu, inflater);
    }
    
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
    	super.onPrepareOptionsMenu(menu);
		menu.findItem(R.id.menu_products_sort).setVisible(mOptionMenuItemsVisible);
		menu.findItem(R.id.menu_products_sort).setEnabled(mOptionMenuItemsVisible);
		
		if(mOptionMenuItemsVisible) {
			switch (mSortBy) {
			default:
			case SORT_AZ:
				menu.findItem(R.id.menu_products_sort_az).setChecked(true);
				break;
			case SORT_RECENT:
				menu.findItem(R.id.menu_products_sort_recent).setChecked(true);
				break;
			case SORT_MOST:
				menu.findItem(R.id.menu_products_sort_usage).setChecked(true);
				break;
			}
		}
		
		menu.findItem(R.id.menu_new_product).setVisible(mOptionMenuItemsVisible);
		menu.findItem(R.id.menu_new_product).setEnabled(mOptionMenuItemsVisible);
		
		if(getArguments().containsKey(ARG_CATEGORY)) {
			menu.findItem(R.id.menu_categories).setVisible(mOptionMenuItemsVisible);
			menu.findItem(R.id.menu_categories).setEnabled(mOptionMenuItemsVisible);
		}
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_products_sort_az:
        	if (!item.isChecked()) {
        		item.setChecked(true);
        		mSortBy = SORT_AZ;
        		reloadProducts();
        	}
            return true;
        case R.id.menu_products_sort_recent:
        	if (!item.isChecked()) {
        		item.setChecked(true);
        		mSortBy = SORT_RECENT;
        		reloadProducts();
        	}
            return true;   
        case R.id.menu_products_sort_usage:
            if (!item.isChecked()) {
            	item.setChecked(true);
            	mSortBy = SORT_MOST;
            	reloadProducts();
            }
            return true;
        case R.id.menu_new_product:
            if(mProductsGalleryController != null) mProductsGalleryController.newProduct(mSelectedCategory);
            return true;  
        case R.id.menu_categories_edit:
        	if(mProductsGalleryController != null) mProductsGalleryController.editCategory(mSelectedCategory);
            return true;   
        case R.id.menu_categories_delete:
        	if(mProductsGalleryController != null) mProductsGalleryController.deleteCategory(mSelectedCategory);
            return true;  
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
        	mProductsGalleryController = (ProductsGalleryController) activity;
        	mProductsGalleryController.registerProductsGalleryActionTaker(this);
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ProductsGalleryController");
        }
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	
        mAdapter = new ProductsGalleryCursorAdapter(getActivity().getApplicationContext(), 
        		CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, mSelectedCategory == null);
        mProducts.setAdapter(mAdapter);
        mProducts.setOnItemClickListener(this);
        mProducts.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        mProducts.setMultiChoiceModeListener(this);
    	
        if(getActivity().getSupportLoaderManager().getLoader(LOADER_PRODUCTS_ID) != null) {
        	getActivity().getSupportLoaderManager().restartLoader(LOADER_PRODUCTS_ID, null, this);
        }
        else getActivity().getSupportLoaderManager().initLoader(LOADER_PRODUCTS_ID, null, this);
    }
    
    private void reloadProducts() {
    	if(getActivity().getSupportLoaderManager().getLoader(LOADER_PRODUCTS_ID) != null) {
        	getActivity().getSupportLoaderManager().restartLoader(LOADER_PRODUCTS_ID, null, this);
        }
        else getActivity().getSupportLoaderManager().initLoader(LOADER_PRODUCTS_ID, null, this);
    }

	@Override
	public void updateShownCategory(Category category) {
		if(mSelectedCategory == null) return;
		if(mSelectedCategory._id == category._id) {
			mSelectedCategory = category;
			categoryColorHeader.setBackgroundColor(mSelectedCategory.color);
			getActivity().setTitle(mSelectedCategory.name);
		}
	}

	@Override
	public void setOptionMenuItemsVisible(boolean visible) {
		mOptionMenuItemsVisible = visible;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String sortOrder = null;
		//TODO
//		switch(mSortBy) {
//		case SORT_AZ:
//			sortOrder = ;
//			break;
//		case SORT_MOST:
//			sortOrder = ;
//			break;
//		case SORT_RECENT:
//			sortOrder = ;
//			break;
//		}
		if(mSelectedCategory != null) {
			return new CursorLoader(getActivity().getApplicationContext(), 
					DejalistContract.Products.buildCategoryProductsUri(mSelectedCategory._id), null, null, null, sortOrder);
		}
		else {
			return new CursorLoader(getActivity().getApplicationContext(), 
					DejalistContract.Products.CONTENT_URI, null, null, null, sortOrder);
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		data.setNotificationUri(getActivity().getContentResolver(), DejalistContract.Products.CONTENT_URI);
		mAdapter.changeCursor(data);
		if(mCheckedItemPos != null) {
			for (int itemPos : mCheckedItemPos) {
				mProducts.setItemChecked(itemPos, true);
			}
			mCheckedItemPos = null;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.changeCursor(null);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ProductsGalleryCursorAdapter.ViewHolder holder = (ProductsGalleryCursorAdapter.ViewHolder)view.getTag();
		ContentValues values = new ContentValues();
		values.put(Products.PRODUCT_INLIST, holder.inList.getVisibility() == View.VISIBLE ? 0 : 1);
		getActivity().getContentResolver().update(Products.buildProductUri(id), values, null, null);
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		// Respond to clicks on the actions in the CAB
        switch (item.getItemId()) {
//            case R.id.menu_cab_products_edit:
//                deleteSelectedItems();
//                mode.finish(); // Action picked, so close the CAB
//                return true;
//            case R.id.menu_cab_products_set_category:
//                deleteSelectedItems();
//                mode.finish(); // Action picked, so close the CAB
//                return true;
//            case R.id.menu_cab_products_delete:
//                deleteSelectedItems();
//                mode.finish(); // Action picked, so close the CAB
//                return true;
            default:
                return false;
        }
	}
	
	private ActionMode mActionMode;

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		mActionMode = mode;
		// Inflate the menu for the CAB
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.menu_cab_products, menu);
        return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		mActionMode = null;
		 // Here you can make any necessary updates to the activity when
        // the CAB is removed. By default, selected items are deselected/unchecked.
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		// Here you can perform updates to the CAB due to
        // an invalidate() request
		MenuItem editItem = mode.getMenu().findItem(R.id.menu_cab_products_edit);
		MenuItem setCategoryItem = mode.getMenu().findItem(R.id.menu_cab_products_set_category);
    	if(mProducts.getCheckedItemCount() == 1) {
    		editItem.setVisible(true);
    		editItem.setEnabled(true);
    		setCategoryItem.setVisible(false);
    		setCategoryItem.setEnabled(false);
    	}
    	else {
    		editItem.setVisible(false);
    		editItem.setEnabled(false);
    		setCategoryItem.setVisible(true);
    		setCategoryItem.setEnabled(true);
    	}
        return true;
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position,
			long id, boolean checked) {
		// Here you can do something when items are selected/de-selected,
        // such as update the title in the CAB
		int count = mProducts.getCheckedItemCount();
		if(checked && count == 2) mode.invalidate();
		else if (count == 1) mode.invalidate();
		Resources res = getResources();
    	String text = String.format(res.getString(R.string.menu_cab_products_title), mProducts.getCheckedItemCount());
    	mode.setTitle(text);
	}

	@Override
	public void closeActionMode() {
		if(mActionMode != null) mActionMode.finish();
	}
}
