package com.luboganev.dejalist.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.luboganev.dejalist.R;
import com.luboganev.dejalist.data.BackupIntentService;
import com.luboganev.dejalist.data.BackupIntentService.Action;

public class ConfirmBackResDialogFragment extends DialogFragment {
	public static final String ARG_MODE = "mode";
	
	public ConfirmBackResDialogFragment() {}
	
	public static interface ConfirmBackResCallback {
		public void onConfirmBackup();
		public void onConfirmRestore();
	}
	
	private ConfirmBackResCallback mConfirmBackResCallback;
	
	public static ConfirmBackResDialogFragment getBackupInstance() {
		ConfirmBackResDialogFragment fragment = new ConfirmBackResDialogFragment();
		Bundle arguments = new Bundle();
		arguments.putSerializable(ARG_MODE, BackupIntentService.Action.BACKUP);
		fragment.setArguments(arguments);
		return fragment;
	}
	
	public static ConfirmBackResDialogFragment getRestoreInstance() {
		ConfirmBackResDialogFragment fragment = new ConfirmBackResDialogFragment();
		Bundle arguments = new Bundle();
		arguments.putSerializable(ARG_MODE, BackupIntentService.Action.RESTORE);
		fragment.setArguments(arguments);
		return fragment;
	}
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
        	mConfirmBackResCallback = (ConfirmBackResCallback) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement ConfirmBackResCallback");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mConfirmBackResCallback = null;
    }
    
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		BackupIntentService.Action action = (BackupIntentService.Action)getArguments().getSerializable(ARG_MODE);
		if(action == Action.BACKUP) {
			builder.setTitle(R.string.backup_confirm_title)
				.setMessage(R.string.backup_confirm_message)
				.setPositiveButton(R.string.backup_confirm_button_positive, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                	if(mConfirmBackResCallback != null) mConfirmBackResCallback.onConfirmBackup();
                }
            })
            .setNegativeButton(R.string.backup_confirm_button_negative, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                	// do nothing
                }
            });
		}
		else if(action == Action.RESTORE) {
			builder.setTitle(R.string.restore_confirm_title)
			.setMessage(R.string.restore_confirm_message)
			.setPositiveButton(R.string.restore_confirm_button_positive, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	            	if(mConfirmBackResCallback != null) mConfirmBackResCallback.onConfirmRestore();
	            }
	        })
	        .setNegativeButton(R.string.restore_confirm_button_negative, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	            	// do nothing
	            }
	        });
		}
	    return builder.create();
	}
}
