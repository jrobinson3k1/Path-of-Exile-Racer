package com.jasonrobinson.racer.ui.race;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.ui.base.BaseDialogFragment;

public class NotificationPickerDialogFragment extends BaseDialogFragment {

    private OnTimeSelectedListener mOnTimeSelectedListener;

    public static NotificationPickerDialogFragment newInstance() {
        return new NotificationPickerDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.add_notification);

        View customView = LayoutInflater.from(getActivity()).inflate(R.layout.notification_dialog_view, null);
        final NumberPicker numberPicker = (NumberPicker) customView.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(60);
        numberPicker.setValue(10);
        numberPicker.setFocusable(true);
        numberPicker.setFocusableInTouchMode(true);

        builder.setView(customView);

        builder.setPositiveButton(R.string.add, (dialog, which) -> {
            mOnTimeSelectedListener.onTimeSelected(numberPicker.getValue() * 1000 * 60);
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            mOnTimeSelectedListener.onCancel();
        });

        return builder.create();
    }

    public void setOnTimeSelectedListener(OnTimeSelectedListener l) {
        mOnTimeSelectedListener = l;
    }

    public interface OnTimeSelectedListener {

        void onTimeSelected(long millis);

        void onCancel();
    }
}
