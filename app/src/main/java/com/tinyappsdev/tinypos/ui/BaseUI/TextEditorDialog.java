package com.tinyappsdev.tinypos.ui.BaseUI;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.tinyappsdev.tinypos.R;


public class TextEditorDialog<AI extends ActivityInterface> extends BaseDialog<AI> {
    protected EditText mEditText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        LayoutInflater inflater = activity.getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        Bundle bundle = getArguments();

        View view = inflater.inflate(R.layout.dialog_text_editor, null);
        mEditText = (EditText)view.findViewById(R.id.editText);
        mEditText.setText(bundle.getString("val"));

        builder.setView(view).setMessage(bundle.getString("msg"));
        builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                onConfirm(mEditText.getText().toString());
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
        mEditText = null;
    }

    public void onConfirm(String text) { dismiss(); }
    public void onCancel() { dismiss(); }
}
