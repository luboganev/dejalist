/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.luboganev.dejalist.ui;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.InjectView;
import butterknife.Views;

import com.luboganev.dejalist.R;
import com.luboganev.dejalist.data.DejalistContract;
import com.luboganev.dejalist.data.DejalistContract.Categories;
import com.luboganev.dejalist.data.DejalistContract.Products;
import com.luboganev.dejalist.data.SelectionBuilder;
import com.luboganev.dejalist.data.entities.Category;
import com.luboganev.dejalist.data.entities.Product;
import com.luboganev.dejalist.ui.CategoryDialogFragment.CategoryEditorCallback;
import com.luboganev.dejalist.ui.SetProductsCategoryDialogFragment.SetProductsCategoryCallback;

/**
 * This example illustrates a common usage of the DrawerLayout widget
 * in the Android support library.
 * <p/>
 * <p>When a navigation (left) drawer is present, the host activity should detect presses of
 * the action bar's Up affordance as a signal to open and close the navigation drawer. The
 * ActionBarDrawerToggle facilitates this behavior.
 * Items within the drawer should fall into one of two categories:</p>
 * <p/>
 * <ul>
 * <li><strong>View switches</strong>. A view switch follows the same basic policies as
 * list or tab navigation in that a view switch does not create navigation history.
 * This pattern should only be used at the root activity of a task, leaving some form
 * of Up navigation active for activities further down the navigation hierarchy.</li>
 * <li><strong>Selective Up</strong>. The drawer allows the user to choose an alternate
 * parent for Up navigation. This allows a user to jump across an app's navigation
 * hierarchy at will. The application should treat this as it treats Up navigation from
 * a different task, replacing the current task stack using TaskStackBuilder or similar.
 * This is the only form of navigation drawer that should be used outside of the root
 * activity of a task.</li>
 * </ul>
 * <p/>
 * <p>Right side drawers should be used for actions, not navigation. This follows the pattern
 * established by the Action Bar that navigation should be to the left and actions to the right.
 * An action should be an operation performed on the current contents of the window,
 * for example enabling or disabling a data overlay on top of the current content.</p>
 */
public class MainActivity extends FragmentActivity implements LoaderCallbacks<Cursor>, 
	ProductsGalleryController, CategoryEditorCallback, UndoBarController.UndoListener,
	SetProductsCategoryCallback, ChecklistController {
	
	@InjectView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
	@InjectView(R.id.left_drawer) ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    
    private NavigationCursorAdapter mAdapter;
    
    private UndoBarController mUndoBarController;
	
	private static final int LOADER_NAVIGATION_ID = 1;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
//    private String[] mPlanetTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Views.inject(this);
        
        mTitle = mDrawerTitle = getTitle();

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
        mAdapter = new NavigationCursorAdapter(getApplicationContext(), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, onAddCategoryListener);
        mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mDrawerList.setAdapter(mAdapter);
        
        if (savedInstanceState == null) {
        	selectItem(0);
        }
        else {
        	mStateSelectedNavigationPosition = savedInstanceState.getInt(STATE_SELECTED_NAVIGATION, -1);
        }
        
        if(getSupportLoaderManager().getLoader(LOADER_NAVIGATION_ID) != null) {
        	getSupportLoaderManager().restartLoader(LOADER_NAVIGATION_ID, null, this);
        }
        else {
        	getSupportLoaderManager().initLoader(LOADER_NAVIGATION_ID, null, this);
        }
        
        // set up the drawer's list view with items and click listener
//        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
//                R.layout.drawer_list_item, mPlanetTitles));
        
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                if(mProductsGalleryActionTaker != null) mProductsGalleryActionTaker.setOptionMenuItemsVisible(true);
                if(mChecklistActionTaker != null) mChecklistActionTaker.setOptionMenuItemsVisible(true);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                if(mProductsGalleryActionTaker != null) mProductsGalleryActionTaker.setOptionMenuItemsVisible(false);
                if(mChecklistActionTaker != null) mChecklistActionTaker.setOptionMenuItemsVisible(false);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mUndoBarController = new UndoBarController(findViewById(R.id.undobar), this);
    }
    
    private static final String STATE_SELECTED_NAVIGATION = "selected_navigation";
    private int mStateSelectedNavigationPosition = -1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putInt(STATE_SELECTED_NAVIGATION, mDrawerList.getCheckedItemPosition());
    	mUndoBarController.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mUndoBarController.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
        case R.id.menu_main_settings:
        	// TODO: implement the settings, help and about
//            // create intent to perform web search for this planet
//            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//            intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
//            // catch event that there's no activity to handle intent
//            if (intent.resolveActivity(getPackageManager()) != null) {
//                startActivity(intent);
//            } else {
//                Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
//            }
            return true;
        case R.id.menu_main_about:
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    		selectItem(position);
    		if(mProductsGalleryActionTaker != null) mProductsGalleryActionTaker.closeActionMode();
    		if(mChecklistActionTaker != null) mChecklistActionTaker.closeActionMode();
        }
    }
    
    private OnClickListener onAddCategoryListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			CategoryDialogFragment dialog = CategoryDialogFragment.getInstance();
	        dialog.show(getSupportFragmentManager(), "CategoryDialogFragment");
		}
	};

    private void selectItem(int position) {
    	Category selectedCategory = cupboard().withCursor((Cursor)mAdapter.getItem(position)).get(Category.class);
    	if(selectedCategory._id == NavigationCursorAdapter.NAV_CHECKLIST_ITEM_ID) {
    		selectedCategory.name = getString(R.string.nav_checklist);
    		
            // update the main content by replacing fragments
            Fragment fragment = new ChecklistFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    	}
    	else if(selectedCategory._id == NavigationCursorAdapter.NAV_ALL_PRODUCTS_ITEM_ID) {
    		selectedCategory.name = getString(R.string.nav_all_products);
    		
            // update the main content by replacing fragments
            getSupportFragmentManager().beginTransaction().replace(
            		R.id.content_frame, 
            		ProductsGalleryFragment.getInstanceAllProducts()).commit();
    	}
    	else if(selectedCategory._id == NavigationCursorAdapter.NAV_NO_CATEGORY_ITEM_ID) {
    		selectedCategory.name = getString(R.string.nav_products_no_category);
    		
    		// update the main content by replacing fragments
    		getSupportFragmentManager().beginTransaction().replace(
    				R.id.content_frame, 
    				ProductsGalleryFragment.getInstanceNoCategoryProducts()).commit();
    	}
    	else {
    		
            // update the main content by replacing fragments
            getSupportFragmentManager().beginTransaction().replace(
            		R.id.content_frame, 
            		ProductsGalleryFragment.getInstanceCategoryProducts(selectedCategory)).commit();
    	}

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
    	setTitle(selectedCategory.name);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggle
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    
    
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
	    CursorLoader cursorLoader = new CursorLoader(getApplicationContext(), DejalistContract.Categories.CONTENT_URI, null, null, null, null);
	    return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		data.setNotificationUri(getContentResolver(), DejalistContract.Categories.CONTENT_URI);
		mAdapter.changeCursor(data);
		if(mStateSelectedNavigationPosition >= 0) {
			mDrawerList.setItemChecked(mStateSelectedNavigationPosition, true);
			mStateSelectedNavigationPosition = -1;
		}
	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.changeCursor(null);
	}
    
	@Override
	public void onCategoryEdited(Category category) {
		if(mProductsGalleryActionTaker != null) mProductsGalleryActionTaker.updateShownCategory(category);
	}

	@Override
	public void onCategoryCreated(Category category) {
		//FIXME: cannot switch to it because of the loaders. See if you can do sth.
	}
	
	public static final int REQUEST_CODE_NEW_PRODUCT = 1;
	public static final int REQUEST_CODE_EDIT_PRODUCT = 2;

	@Override
	public void newProduct(Category category) {
		Intent intent = new Intent(this, ProductActivity.class);
		if(category != null) intent.putExtra(ProductActivity.EXTRA_CATEGORY_ID, category._id);
		startActivityForResult(intent, REQUEST_CODE_NEW_PRODUCT);
	}
	
	@Override
	public void editProduct(Product product) {
		Intent intent = new Intent(this, ProductActivity.class);
		if(product != null) intent.putExtra(ProductActivity.EXTRA_PRODUCT, product);
		startActivityForResult(intent, REQUEST_CODE_EDIT_PRODUCT);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_CODE_NEW_PRODUCT || requestCode == REQUEST_CODE_EDIT_PRODUCT) {
			if(resultCode == Activity.RESULT_OK) {
				if(mProductsGalleryActionTaker != null) mProductsGalleryActionTaker.reloadProducts();
				if(mChecklistActionTaker != null) mChecklistActionTaker.reloadProducts();
			}
		}
	}

	@Override
	public void setProductsCategory(long[] productIds) {
		SetProductsCategoryDialogFragment dialog = SetProductsCategoryDialogFragment.getInstance(productIds);
        dialog.show(getSupportFragmentManager(), "SetProductsCategoryDialogFragment");
	}
	
	@Override
	public void onSetProductsCategory(long[] productIds, long categoryId) {
		ContentValues values = new ContentValues();
		values.put(DejalistContract.Products.PRODUCT_CATEGORY_ID, categoryId);
		getContentResolver().update(DejalistContract.Products.CONTENT_URI, 
				values, DejalistContract.Products.buildSelectionIdIn(productIds), null);
		if(mProductsGalleryActionTaker != null) mProductsGalleryActionTaker.reloadProducts();
	}
	
	private static final String UNDO_EXTRA_DELETED_PRODUCTS = "deleted_products";
	
	@Override
	public void deleteProducts(long[] productIds) {
		if(mUndoBarController.isShown()) {
			getContentResolver().delete(Products.CONTENT_URI, Products.SELECTION_DELETED, null);
		}
		ContentValues values = new ContentValues();
		values.put(Products.PRODUCT_DELETED, 1);
		getContentResolver().update(Products.CONTENT_URI, values, Products.buildSelectionIdIn(productIds), null);
		if(mProductsGalleryActionTaker != null) mProductsGalleryActionTaker.reloadProducts();
		Bundle undoExtras = new Bundle();
		undoExtras.putLongArray(UNDO_EXTRA_DELETED_PRODUCTS, productIds);
		mUndoBarController.showUndoBar(
                false,
                getString(R.string.undobar_product_deleted_message),
                undoExtras);
	}
	
	@Override
    public void onUndo(Parcelable token) {
		ContentValues values = new ContentValues();
		values.put(Products.PRODUCT_DELETED, 0);
		getContentResolver().update(Products.CONTENT_URI, values, Products.SELECTION_DELETED, null);
		if(mProductsGalleryActionTaker != null) mProductsGalleryActionTaker.reloadProducts();
    }
	
	@Override
	public void onUndoExpired(Parcelable token) {
		getContentResolver().delete(Products.CONTENT_URI, Products.SELECTION_DELETED, null);
	}

	private ProductsGalleryActionTaker mProductsGalleryActionTaker;

	@Override
	public void editCategory(Category category) {
		CategoryDialogFragment dialog = CategoryDialogFragment.getInstance(category);
        dialog.show(getSupportFragmentManager(), "CategoryDialogFragment");
	}

	@Override
	public void deleteCategory(Category category) {
		getContentResolver().delete(Categories.buildCategoryUri(category._id), null, null);
		// go back to all categories
		selectItem(2);
	}

	@Override
	public void registerProductsGalleryActionTaker(ProductsGalleryActionTaker actionTaker) {
		mProductsGalleryActionTaker = actionTaker;
	}

	@Override
	public void unregisterProductsGalleryActionTaker() {
		mProductsGalleryActionTaker = null;
	}
	
	private ChecklistActionTaker mChecklistActionTaker;

	@Override
	public void removeProducts(long[] productIds) {
		ContentValues values = new ContentValues();
		values.put(Products.PRODUCT_INLIST, 0);
		values.put(Products.PRODUCT_CHECKED, 0);
		getContentResolver().update(Products.CONTENT_URI, values, Products.buildSelectionIdIn(productIds), null);
		if(mChecklistActionTaker != null) mChecklistActionTaker.reloadProducts();
	}

	@Override
	public void clearCheckList(boolean onlyChecked) {
		ContentValues values = new ContentValues();
		values.put(Products.PRODUCT_INLIST, 0);
		values.put(Products.PRODUCT_CHECKED, 0);
		if(onlyChecked) {
			getContentResolver().update(Products.CONTENT_URI, values, new SelectionBuilder()
			.where(Products.SELECTION_IN_LIST, (String[]) null)
			.where(Products.SELECTION_CHECKED, (String[]) null)
			.getSelection(), null);
		}
		else getContentResolver().update(Products.CONTENT_URI, values, null, null);
		if(mChecklistActionTaker != null) mChecklistActionTaker.reloadProducts();
	}
	
	@Override
	public void addCheckListProductsClicked() {
		if(!mDrawerLayout.isDrawerOpen(mDrawerList)) {
			mDrawerLayout.openDrawer(mDrawerList);
		}
	}

	@Override
	public void registerChecklistActionTaker(ChecklistActionTaker actionTaker) {
		mChecklistActionTaker = actionTaker;
	}

	@Override
	public void unregisterChecklistActionTaker() {
		mChecklistActionTaker = null;
	}
}