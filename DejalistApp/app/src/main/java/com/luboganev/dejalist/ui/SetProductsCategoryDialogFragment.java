package com.luboganev.dejalist.ui;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.CursorAdapter;
import com.luboganev.dejalist.R;
import com.luboganev.dejalist.data.DejalistContract.Categories;
import com.luboganev.dejalist.data.entities.Category;

public class SetProductsCategoryDialogFragment extends DialogFragment {
	public static final String ARG_PRODUCT_IDS = "product_ids";
	
	public SetProductsCategoryDialogFragment() {}
	
	public static interface SetProductsCategoryCallback {
		public void onSetProductsCategory(long[] productIds, long categoryId);
	}
	
	private SetProductsCategoryCallback mCategoryEditorCallback;
	
	public static SetProductsCategoryDialogFragment getInstance(long[] productIds) {
		SetProductsCategoryDialogFragment fragment = new SetProductsCategoryDialogFragment();
		Bundle arguments = new Bundle();
		arguments.putLongArray(ARG_PRODUCT_IDS, productIds);
		fragment.setArguments(arguments);
		return fragment;
	}
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
        	mCategoryEditorCallback = (SetProductsCategoryCallback) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement SetProductsCategoryCallback");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCategoryEditorCallback = null;
    }
    
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setTitle(R.string.dialogfragment_setproductcategory_title);
	    
	    Cursor c = cupboard().withContext(getActivity().getApplicationContext()).query(Categories.CONTENT_URI, Category.class).getCursor();
	    final CategoriesListCursorAdapter adapter = new CategoriesListCursorAdapter(getActivity().getApplicationContext(),
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
	    adapter.changeCursor(c);
	    
	    builder.setAdapter(adapter, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(mCategoryEditorCallback != null) {
					mCategoryEditorCallback.onSetProductsCategory(getArguments().getLongArray(ARG_PRODUCT_IDS), adapter.getItemId(which));
				}
			}
		});
	    return builder.create();
	}
}
