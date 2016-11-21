package com.tinyappsdev.tinypos.ui.LoginFragment;


import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import com.tinyappsdev.tinypos.AppGlobal;
import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.rest.ApiCallClient;
import com.tinyappsdev.tinypos.ui.BaseUI.BaseFragment;
import com.tinyappsdev.tinypos.ui.BaseUI.LoginActivityInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class LoginEmployeeFragment extends BaseFragment<LoginActivityInterface> {
    @BindView(R.id.keypad) GridLayout mKeypad;
    @BindView(R.id.passcode) EditText mPasscode;
    private Unbinder mUnbinder;

    public LoginEmployeeFragment() {
    }

    public static LoginEmployeeFragment newInstance() {
        LoginEmployeeFragment fragment = new LoginEmployeeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_customer, container, false);
        mUnbinder = ButterKnife.bind(this, view);


        mPasscode.setText("");
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String label = ((Button)view).getText().toString();
                String passcode = mPasscode.getText().toString();

                if(view.getId() == R.id.backspace) {
                    if(passcode.length() > 0)
                        mPasscode.setText(passcode.substring(0, passcode.length() - 1));

                } else {
                    String newLabel = passcode.toString() + label;
                    if(newLabel.length() >= 4) {
                        try {
                            mPasscode.setText("");
                            loginCustomer(Integer.parseInt(newLabel));
                        } catch(NumberFormatException e) {
                        }
                    } else
                        mPasscode.setText(newLabel);
                }
            }
        };

        for(int i = 0; i < mKeypad.getChildCount(); i++) {
            View btn = mKeypad.getChildAt(i);
            if(btn instanceof Button)
                btn.setOnClickListener(onClickListener);
        }

        return view;
    }

    public void loginCustomer(int code) {
        mActivity.loginCustomer(code);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

}
