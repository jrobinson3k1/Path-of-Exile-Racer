package com.jasonrobinson.racer.ui.ladder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.model.WatchType;
import com.jasonrobinson.racer.ui.base.BaseDialogFragment;
import com.jasonrobinson.racer.util.RawTypeface;
import com.metova.slim.Slim;
import com.metova.slim.annotation.Extra;

import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class WatchCharacterDialogFragment extends BaseDialogFragment {

    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_TYPE = "type";

    private static final Pattern PATTERN_CHARACTER = Pattern.compile("[a-zA-Z_]{3,}");
    private static final Pattern PATTERN_ACCOUNT = Pattern.compile("[a-zA-Z_\\d]{3,}");

    @InjectView(R.id.name_EditText)
    EditText mNameEditText;
    @InjectView(R.id.character_RadioButton)
    RadioButton mCharacterRadioButton;
    @InjectView(R.id.account_RadioButton)
    RadioButton mAccountRadioButton;

    @Extra(value = EXTRA_NAME, optional = true)
    String mName;
    @Extra(value = EXTRA_TYPE, optional = true)
    WatchType mType;

    WatchCharacterDialogListener mListener;

    public static WatchCharacterDialogFragment newInstance(String prepopulatedName, WatchType prepopulatedType) {
        WatchCharacterDialogFragment fragment = new WatchCharacterDialogFragment();

        Bundle args = new Bundle();
        args.putString(EXTRA_NAME, prepopulatedName);
        args.putSerializable(EXTRA_TYPE, prepopulatedType);
        fragment.setArguments(args);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (mListener == null) {
            throw new IllegalStateException(WatchCharacterDialogListener.class.getSimpleName() + " has not been set");
        }

        Slim.injectExtras(getArguments(), this);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.ic_action_search_dark);
        builder.setTitle(R.string.watch_character);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.watch_character_dialog, null);
        builder.setView(v);

        ButterKnife.inject(this, v);

        mNameEditText.setTypeface(RawTypeface.obtain(getActivity(), R.raw.fontin_regular));
        mNameEditText.setText(mName);

        if (mType != null) {
            switch (mType) {
                case ACCOUNT:
                    mAccountRadioButton.setChecked(true);
                    break;
                case CHARACTER:
                    mCharacterRadioButton.setChecked(true);
                    break;
            }
        }

        builder.setPositiveButton(R.string.watch, null); // Overwritten later
        builder.setNeutralButton(R.string.remove, (dialog, which) -> {
            mListener.onRemove();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            mListener.onCancel();
        });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        final AlertDialog dialog = (AlertDialog) getDialog();

        // This allows us to do validation before accepting the input
        Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        b.setOnClickListener(v -> {
            String name = mNameEditText.getText().toString();
            WatchType type = mCharacterRadioButton.isChecked() ? WatchType.CHARACTER : WatchType.ACCOUNT;

            Pattern pattern = null;
            switch (type) {
                case CHARACTER:
                    pattern = PATTERN_CHARACTER;
                    break;
                case ACCOUNT:
                    pattern = PATTERN_ACCOUNT;
                    break;
            }

            if (pattern.matcher(name).matches()) {
                mListener.onNameSelected(name, type);
                dialog.dismiss();
            } else {
                Toast.makeText(getActivity(), R.string.watcher_input_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setWatchCharacterDialogListener(WatchCharacterDialogListener l) {
        mListener = l;
    }

    public interface WatchCharacterDialogListener {

        public void onNameSelected(String name, WatchType type);

        public void onRemove();

        public void onCancel();
    }
}
