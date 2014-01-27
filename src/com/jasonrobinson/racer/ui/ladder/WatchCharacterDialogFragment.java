package com.jasonrobinson.racer.ui.ladder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.ui.base.BaseDialogFragment;
import com.jasonrobinson.racer.util.RawTypeface;

public class WatchCharacterDialogFragment extends BaseDialogFragment {

	public static final String ARG_NAME = "name";

	private WatchCharacterDialogListener mListener;

	public static WatchCharacterDialogFragment newInstance(String prepopulatedCharacter) {

		WatchCharacterDialogFragment fragment = new WatchCharacterDialogFragment();

		Bundle args = new Bundle();
		args.putString(ARG_NAME, prepopulatedCharacter);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		if (mListener == null) {
			throw new IllegalStateException(WatchCharacterDialogListener.class.getSimpleName() + " has not been set");
		}

		String name = getArguments().getString(ARG_NAME);

		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.Theme_AppCompat));
		builder.setIcon(R.drawable.ic_action_search);
		builder.setTitle(R.string.watch_character);

		final EditText nameEditText = (EditText) LayoutInflater.from(getActivity()).inflate(R.layout.watch_character_edittext, null);
		nameEditText.setTypeface(RawTypeface.obtain(getActivity(), R.raw.fontin_regular));
		nameEditText.setTextColor(Color.WHITE);
		nameEditText.setText(name);
		builder.setView(nameEditText);

		builder.setPositiveButton(R.string.watch, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				String character = nameEditText.getText().toString();
				mListener.onCharacterSelected(character);
			}
		});
		builder.setNeutralButton(R.string.remove, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				mListener.onRemove();
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				mListener.onCancel();
			}
		});

		return builder.create();
	}

	public void setWatchCharacterDialogListener(WatchCharacterDialogListener l) {

		mListener = l;
	}

	public interface WatchCharacterDialogListener {

		public void onCharacterSelected(String character);

		public void onRemove();

		public void onCancel();
	}
}
