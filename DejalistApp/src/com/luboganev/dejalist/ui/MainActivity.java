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
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.os.Bundle;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.Views;

import com.luboganev.dejalist.R;
import com.luboganev.dejalist.data.DejalistContract;
import com.luboganev.dejalist.data.DejalistContract.Categories;
import com.luboganev.dejalist.data.DejalistContract.Products;
import com.luboganev.dejalist.data.entities.Category;

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
public class MainActivity extends FragmentActivity implements LoaderCallbacks<Cursor>, CategoriesController {
	@InjectView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
	@InjectView(R.id.left_drawer) ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    
    private NavigationCursorAdapter mAdapter;
	
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
        
        if(getSupportLoaderManager().getLoader(LOADER_NAVIGATION_ID) != null) {
        	getSupportLoaderManager().restartLoader(LOADER_NAVIGATION_ID, null, this);
        }
        else getSupportLoaderManager().initLoader(LOADER_NAVIGATION_ID, null, this);
        
        // set up the drawer's list view with items and click listener
//        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
//                R.layout.drawer_list_item, mPlanetTitles));
        
        mAdapter = new NavigationCursorAdapter(getApplicationContext(), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        mDrawerList.setAdapter(mAdapter);
        
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
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // TODO: hide menu items of fragments on opened navigation
//    /* Called whenever we call invalidateOptionsMenu() */
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        // If the nav drawer is open, hide action items related to the content view
//        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
//        return super.onPrepareOptionsMenu(menu);
//    }

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
        	// TODO:
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
        case R.id.menu_main_help:
            // TODO:
        	return true;
        case R.id.menu_main_about:
        	// TODO:
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
        }
    }

    private void selectItem(int position) {
    	Category selectedCategory = cupboard().withCursor((Cursor)mAdapter.getItem(position)).get(Category.class);
    	if(selectedCategory._id == NavigationCursorAdapter.NAV_CHECKLIST_ITEM_ID) {
    		selectedCategory.name = getString(R.string.nav_checklist);
    		
            // update the main content by replacing fragments
            Fragment fragment = new ChecklistFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    	}
    	else if(selectedCategory._id == NavigationCursorAdapter.NAV_ALL_ITEMS_ITEM_ID) {
    		selectedCategory.name = getString(R.string.nav_all_products);
    		
            // update the main content by replacing fragments
            getSupportFragmentManager().beginTransaction().replace(
            		R.id.content_frame, 
            		ProductsGalleryFragment.getInstance()).commit();
    	}
    	else {
    		
            // update the main content by replacing fragments
            getSupportFragmentManager().beginTransaction().replace(
            		R.id.content_frame, 
            		ProductsGalleryFragment.getInstance(selectedCategory)).commit();
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
        // Pass any configuration change to the drawer toggls
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
		mAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}
    
    public static class NavigationCursorAdapter extends CursorAdapter {
    	public static final int VIEW_TYPE_NAVIGATION = 0; 
    	public static final int VIEW_TYPE_HEADER = 1; 
    	
		public static final long NAV_CHECKLIST_ITEM_ID = -101;
		public static final long NAV_ALL_ITEMS_ITEM_ID = -102;
		
	    private Context mContext;
		
		private static Cursor addMainNavigationItems(Cursor categories) {
			MatrixCursor mainNavigation = new MatrixCursor(new String[] {Categories._ID, Categories.CATEGORY_NAME, Categories.CATEGORY_COLOR});
			mainNavigation.addRow(new Object[]{NAV_CHECKLIST_ITEM_ID, "", 0});
			mainNavigation.addRow(new Object[]{NAV_ALL_ITEMS_ITEM_ID, "", 0});
			if(categories != null) return new MergeCursor(new Cursor[]{mainNavigation, categories});
			else return mainNavigation;
		}
		
		@Override
		public int getCount() {
			return super.getCount() + 1;
		}
		
		@Override
		public int getItemViewType(int position) {
			return position == 2 ? VIEW_TYPE_HEADER : VIEW_TYPE_NAVIGATION;
		}
		
		@Override
		public Object getItem(int position) {
			if(position == 2) {
				return null;
			}
			else {
				if(position > 2) return super.getItem(position - 1);
				else return super.getItem(position);
			}
		}
		
		@Override
		public long getItemId(int position) {
			if(position == 2) {
				return -1;
			}
			else {
				if(position > 2) return super.getItemId(position - 1);
				else return super.getItemId(position);
			}
		}
		
		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}
		
		@Override
		public boolean isEnabled(int position) {
			return position != 2;
		}

		public NavigationCursorAdapter(Context context, int flags) {
			super(context, addMainNavigationItems(null), flags);
			mContext = context;
		}
		
		@Override
		public Cursor swapCursor(Cursor categories) {
			return super.swapCursor(addMainNavigationItems(categories));
		}
		
		
		/**
		 * @see android.widget.ListAdapter#getView(int, View, ViewGroup)
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			switch (getItemViewType(position)) {
			case VIEW_TYPE_HEADER: {
				if (convertView == null) {
					convertView = LayoutInflater.from(mContext)
							.inflate(R.layout.list_item_categories_header,
									parent, false);
				}
				convertView.setEnabled(isEnabled(position));
				return convertView;
			}
			case VIEW_TYPE_NAVIGATION: {
				if (position > 2)
					position--;
				return super.getView(position, convertView, parent);
			}
			}

			return null;
		}
	    	
		

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ViewHolder holder = (ViewHolder) view.getTag();
			Category category = cupboard().withCursor(cursor).get(Category.class);
			
			if(category._id == NAV_CHECKLIST_ITEM_ID) {
				holder.name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_nav_list, 0, 0, 0);
				holder.catColor.setVisibility(View.GONE);
				holder.name.setText(R.string.nav_checklist);
			}
			else if(category._id == NAV_ALL_ITEMS_ITEM_ID){
				holder.name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_nav_items, 0, 0, 0);
				holder.catColor.setVisibility(View.GONE);
				holder.name.setText(R.string.nav_all_products);
			}
			else {
				holder.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				holder.catColor.setVisibility(View.VISIBLE);
				holder.catColor.setBackgroundColor(category.color);
				holder.name.setText(category.name);
			}
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
		    View view = LayoutInflater.from(context).inflate(R.layout.list_item_navigation, parent, false);
		    ViewHolder holder = new ViewHolder(view);
		    view.setTag(holder);
		    return view;
		}
		
		static class ViewHolder {
			@InjectView(R.id.tv_nav_name) TextView name;
			@InjectView(R.id.v_nav_cat_color) View catColor;

			public ViewHolder(View view) {
				Views.inject(this, view);
				name.setCompoundDrawablePadding(8);
			}
		}
	}

	@Override
	public void onCategoryEdited(Category category) {
		categoryFragment.updateShownCategory(category);
	}
	
	private CategoriesActionTaker categoryFragment;

	@Override
	public void registerCategories(CategoriesActionTaker categoriesDelegate) {
		categoryFragment = categoriesDelegate;
	}

	@Override
	public void unregisterCategories() {
		categoryFragment = null;
	}

	@Override
	public void onCategoryNewAction() {
		CategoryDialogFragment dialog = CategoryDialogFragment.getInstance();
        dialog.show(getSupportFragmentManager(), "CategoryDialogFragment");
	}

	@Override
	public void onCategoryEditAction(Category category) {
		CategoryDialogFragment dialog = CategoryDialogFragment.getInstance(category);
        dialog.show(getSupportFragmentManager(), "CategoryDialogFragment");
	}

	@Override
	public void onCategoryDeleteAction(Category category) {
		cupboard().withContext(getApplicationContext()).delete(Categories.CONTENT_URI, category);
		ContentValues values = new ContentValues();
		values.put(Products.PRODUCT_CATEGORY_ID, -1L);
		getContentResolver().update(Products.buildCategoryProductsUri(category._id), values, null, null);
		// go back to all categories
		selectItem(1);
	}
}