package com.tinyappsdev.tinypos.ui.BaseUI;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.tinyappsdev.tinypos.R;


public class NumberPickerDialog<AI extends ActivityInterface> extends BaseDialog<AI> {
    protected NumberPicker mNumberPicker;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        LayoutInflater inflater = activity.getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        Bundle bundle = getArguments();

        View view = inflater.inflate(R.layout.dialog_number_picker, null);
        mNumberPicker = (NumberPicker)view.findViewById(R.id.numberPicker);
        mNumberPicker.setMinValue(bundle.getInt("min"));
        mNumberPicker.setMaxValue(bundle.getInt("max"));
        mNumberPicker.setValue(bundle.getInt("val"));

        builder.setView(view).setMessage(bundle.getString("msg"));
        builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                onConfirm(mNumberPicker.getValue());
            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                onCancel();
            }
        });

        return  builder.create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mNumberPicker = null;
    }

    public void onConfirm(int number) { dismiss(); }
    public void onCancel() { dismiss(); }
}
