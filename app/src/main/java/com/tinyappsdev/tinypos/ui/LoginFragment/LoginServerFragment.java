package com.tinyappsdev.tinypos.ui.LoginFragment;


import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.rest.ApiCallClient;
import com.tinyappsdev.tinypos.ui.BaseUI.BaseFragment;
import com.tinyappsdev.tinypos.ui.BaseUI.LoginActivityInterface;
import com.tinyappsdev.tinypos.ui.BaseUI.ReportActivityInterface;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class LoginServerFragment extends BaseFragment<LoginActivityInterface> {
    @BindView(R.id.serverAddress) EditText mServerAddress;
    @BindView(R.id.serverPassword) EditText mServerPassword;
    @BindView(R.id.login_server) Button mLoginServer;
    private Unbinder mUnbinder;

    public LoginServerFragment() {
    }

    public static LoginServerFragment newInstance() {
        LoginServerFragment fragment = new LoginServerFragment();
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
        View view = inflater.inflate(R.layout.fragment_login_server, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        mLoginServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String serverAddress = mServerAddress.getText().toString();
                String serverPassword = mServerPassword.getText().toString();
                mServerPassword.setText("");

                if(serverAddress.isEmpty())
                    serverAddress = getString(R.string.demo_server_address);

                mActivity.loginServer(serverAddress, serverPassword);
            }
        });

        if(savedInstanceState == null) {
            String serverAddress = mActivity.getSharedPreferences().getString("serverAddress", "");
            mServerAddress.setText(serverAddress == null ? "" : serverAddress);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

}
