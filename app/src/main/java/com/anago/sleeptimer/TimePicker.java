package com.anago.sleeptimer;

import static android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class TimePicker extends DialogFragment {

    interface CallBack {
        void onClick(int hour, int min);
    }

    private Button negativeButton;
    private TimePicker.CallBack callBack;

    public void setClick(CallBack _callback) {
        callBack = _callback;
    }

    @NonNull
    @SuppressLint("DefaultLocale")
    @Override

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = requireContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setPadding(0, 50, 0, 0);

        NumberPicker hour = new NumberPicker(context);
        hour.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        hour.setMinValue(0);
        hour.setMaxValue(23);
        linearLayout.addView(hour);

        TextView textView = new TextView(context);
        textView.setLayoutParams(layoutParams);
        textView.setText(" : ");
        linearLayout.addView(textView);

        NumberPicker min = new NumberPicker(context);
        min.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        min.setFormatter(i -> String.format("%02d", i));
        min.setMinValue(0);
        min.setMaxValue(59);
        linearLayout.addView(min);

        hour.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if (negativeButton != null) {
                negativeButton.setEnabled(!(hour.getValue() == 0 && min.getValue() == 0));
            }
        });

        min.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if (negativeButton != null) {
                negativeButton.setEnabled(!(hour.getValue() == 0 && min.getValue() == 0));
            }
        });

        builder.setNegativeButton("OK", (dialog, which) -> {
            callBack.onClick(hour.getValue(), min.getValue());
        });

        builder.setView(linearLayout);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        negativeButton = ((AlertDialog) Objects.requireNonNull(getDialog())).getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setEnabled(false);
    }
}
