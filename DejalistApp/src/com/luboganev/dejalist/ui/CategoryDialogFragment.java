package com.luboganev.dejalist.ui;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import butterknife.InjectView;
import butterknife.Views;

import com.larswerkman.colorpicker.ColorPicker;
import com.larswerkman.colorpicker.SVBar;
import com.luboganev.dejalist.R;
import com.luboganev.dejalist.data.DejalistContract;
import com.luboganev.dejalist.data.entities.Category;

public class CategoryDialogFragment extends DialogFragment {
	@InjectView(R.id.category_picker) ColorPicker picker;
	@InjectView(R.id.category_svbar) SVBar svBar;
	@InjectView(R.id.et_category_name) EditText name;
	
	public static final String ARG_CATEGORY = "category";
	
	public CategoryDialogFragment() {}
	
	public static interface CategoryEditorCallback {
		public void onCategoryCreated(Category category);
		public void onCategoryEdited(Category category);
	}
	
	private CategoryEditorCallback mCategoryEditorCallback;
	
	public static CategoryDialogFragment getInstance(Category category) {
		CategoryDialogFragment fragment = new CategoryDialogFragment();
		Bundle arguments = new Bundle();
		arguments.putParcelable(ARG_CATEGORY, category);
		fragment.setArguments(arguments);
		return fragment;
	}
	
	public static CategoryDialogFragment getInstance() {
		CategoryDialogFragment fragment = new CategoryDialogFragment();
		fragment.setArguments(new Bundle());
		return fragment;
	}
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
        	mCategoryEditorCallback = (CategoryEditorCallback) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement CategoryEditorCallback");
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
		
		// Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    View v = inflater.inflate(R.layout.dialogfragment_category, null);
	    Views.inject(this, v);
	    picker.addSVBar(svBar);
	    
    	if(getArguments().containsKey(ARG_CATEGORY)) {
    		Category category = getArguments().getParcelable(ARG_CATEGORY);
    		name.setText(category.name);
    		picker.setColor(category.color);
    		picker.setOldCenterColor(category.color);
    	}
    	else {
    		//inflate controls with default
    	}
	    
	    builder.setView(v)
	    .setPositiveButton(R.string.dialogfragment_category_save, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				saveChanges();
				CategoryDialogFragment.this.getDialog().dismiss();
			}
		}).setNegativeButton(R.string.dialogfragment_category_cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				CategoryDialogFragment.this.getDialog().cancel();
			}
		});
	    
	    return builder.create();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Views.reset(this);
	}
	
	private void saveChanges() {
		Category category = new Category();
		category.name = name.getText().length() <= 0 ? 
				getString(R.string.dialogfragment_category_utitled) 
				: name.getText().toString();
		category.color = picker.getColor();
		ContentValues categoryValues = cupboard().withEntity(Category.class).toContentValues(category);
		
		if(getArguments().containsKey(ARG_CATEGORY)) {
			Category originalCategory = getArguments().getParcelable(ARG_CATEGORY);
			// if we edit an existing category
			getActivity().getContentResolver().update(
					DejalistContract.Categories.buildCategoryUri(originalCategory._id),
					categoryValues, null, null);
			category._id = originalCategory._id;
			if(mCategoryEditorCallback != null) {
				mCategoryEditorCallback.onCategoryEdited(category);
			}
		}
		else {
			// if we create a new category
			category._id = DejalistContract.Categories.getCategoryId(getActivity().getContentResolver().insert(
					DejalistContract.Categories.CONTENT_URI, categoryValues));
			if(mCategoryEditorCallback != null) {
				mCategoryEditorCallback.onCategoryCreated(category);
			}
		}
	}
}
