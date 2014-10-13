package com.jasonrobinson.racer.ui.ladder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.ui.base.BaseDialogFragment;
import com.jasonrobinson.racer.util.RawTypeface;
import com.metova.slim.Slim;
import com.metova.slim.annotation.Extra;

import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class WatchCharacterDialogFragment extends BaseDialogFragment {

    public static final String EXTRA_NAME = "name";

    private static final Pattern PATTERN_CHARACTER = Pattern.compile("[a-zA-Z_]*");

    @InjectView(R.id.name_EditText)
    EditText mNameEditText;

    @Extra(value = EXTRA_NAME, optional = true)
    String mName;

    WatchCharacterDialogListener mListener;

    public static WatchCharacterDialogFragment newInstance(String prepopulatedCharacter) {
        WatchCharacterDialogFragment fragment = new WatchCharacterDialogFragment();

        Bundle args = new Bundle();
        args.putString(EXTRA_NAME, prepopulatedCharacter);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (mListener == null) {
            throw new IllegalStateException(WatchCharacterDialogListener.class.getSimpleName() + " has not been set");
        }

        Slim.injectExtras(getArguments(), this);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.ic_action_search);
        builder.setTitle(R.string.watch_character);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.watch_character_edittext, null);
        builder.setView(v);

        ButterKnife.inject(this, v);

        mNameEditText.setTypeface(RawTypeface.obtain(getActivity(), R.raw.fontin_regular));
        mNameEditText.setText(mName);

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
        final AlertDialog dialog = (AlertDialog) getDialog();

        // This allows us to do validation before accepting the input
        Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        b.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String character = mNameEditText.getText().toString();
                if (PATTERN_CHARACTER.matcher(character).matches()) {
                    mListener.onCharacterSelected(character);
                    dialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), R.string.watcher_input_error, Toast.LENGTH_LONG).show();
                }
            }
        });
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
