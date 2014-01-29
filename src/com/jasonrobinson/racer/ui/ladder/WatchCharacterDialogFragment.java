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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.ui.base.BaseDialogFragment;
import com.jasonrobinson.racer.util.RawTypeface;

public class WatchCharacterDialogFragment extends BaseDialogFragment {

	public static final String ARG_NAME = "name";

	private static final Pattern PATTERN_CHARACTER = Pattern.compile("[a-zA-Z_]*");

	private EditText mNameEditText;

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

		mNameEditText = (EditText) LayoutInflater.from(getActivity()).inflate(R.layout.watch_character_edittext, null);
		// nameEditText.setFilters(new InputFilter[] { getCharacterInputFilter()
		// });
		mNameEditText.setTypeface(RawTypeface.obtain(getActivity(), R.raw.fontin_regular));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mNameEditText.setTextColor(Color.WHITE);
		}
		mNameEditText.setText(name);
		builder.setView(mNameEditText);

		builder.setPositiveButton(R.string.watch, null); // Overwritten later
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

	@Override
	public void onStart() {

		super.onStart();
		final AlertDialog d = (AlertDialog) getDialog();

		// This allows us to do validation before accepting the input
		Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String character = mNameEditText.getText().toString();
				if (PATTERN_CHARACTER.matcher(character).matches()) {
					mListener.onCharacterSelected(character);
					d.dismiss();
				}
				else {
					Toast.makeText(getActivity(), R.string.watcher_input_error, Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	// This is causing problems on some phones
	// http://www.reddit.com/r/pathofexile/comments/1wft3z/mobile_app_path_of_exile_racer_11_released_now/cf1pkk0
	private InputFilter getCharacterInputFilter() {

		return new InputFilter() {

			@Override
			public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

				StringBuilder sb = new StringBuilder();
				for (int i = start; i < end; i++) {
					char c = source.charAt(i);
					if (PATTERN_CHARACTER.matcher(Character.toString(c)).matches()) {
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
