package com.jasonrobinson.racer.ui.ladder;

import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
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
		nameEditText.setFilters(new InputFilter[] { getCharacterInputFilter() });
		nameEditText.setTypeface(RawTypeface.obtain(getActivity(), R.raw.fontin_regular));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			nameEditText.setTextColor(Color.WHITE);
		}
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

	private InputFilter getCharacterInputFilter() {

		return new InputFilter() {

			private final Pattern p = Pattern.compile("[a-zA-Z_]*");

			@Override
			public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

				StringBuilder sb = new StringBuilder();
				for (int i = start; i < end; i++) {
					char c = source.charAt(i);
					if (p.matcher(Character.toString(c)).matches()) {
						sb.append(c);
					}
				}

				return sb.toString();
			}
		};
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
