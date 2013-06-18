package com.luboganev.dejalist.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.luboganev.dejalist.R;

/**
 * Fragment that appears in the "content_frame", shows a planet
 */
public class PlanetFragment extends Fragment {
    public static final String ARG_CATEGORY = "category";
    public static final String ARG_CATEGORY_ID = "category_id";

    public PlanetFragment() {
        // Empty constructor required for fragment subclasses
    }
    
    public interface ShowDialogListener {
        public void onShowDialogClick(long categoryId);
    }
    
    ShowDialogListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_planet, container, false);
        String planet = getArguments().getString(ARG_CATEGORY);
        ((TextView) rootView.findViewById(R.id.text)).setText(planet);
        ((Button)rootView.findViewById(R.id.btn_showDialog)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onShowDialogClick(getArguments().getLong(ARG_CATEGORY_ID));
			}
		});
        getActivity().setTitle(planet);
        return rootView;
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ShowDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ShowDialogListener");
        }
    }
}
