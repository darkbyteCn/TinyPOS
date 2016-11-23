package com.tinyappsdev.tinypos.ui.CustomerFragment;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.data.Customer;
import com.tinyappsdev.tinypos.data.ModelHelper;
import com.tinyappsdev.tinypos.helper.TinyUtils;
import com.tinyappsdev.tinypos.rest.ApiCallClient;
import com.tinyappsdev.tinypos.ui.BaseUI.AutoCompleteAdapter;
import com.tinyappsdev.tinypos.ui.BaseUI.BaseFragment;
import com.tinyappsdev.tinypos.ui.BaseUI.CustomerActivityInterface;
import com.tinyappsdev.tinypos.ui.TicketFragment.TicketSearchFragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CustomerInfoFragment extends BaseFragment<CustomerActivityInterface> implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = CustomerInfoFragment.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private AutoCompleteAdapter mAutoCompleteAdapter;

    @BindView(R.id.editName) EditText mEditName;
    @BindView(R.id.editAddress) AutoCompleteTextView mEditAddress;
    @BindView(R.id.editAddress2) EditText mEditAddress2;
    @BindView(R.id.editPhone) EditText mEditPhone;
    @BindView(R.id.editCity) EditText mEditCity;
    @BindView(R.id.editState) EditText mEditState;
    private Unbinder mUnbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleApiAvailability gaa = GoogleApiAvailability.getInstance();
        if(gaa.isGooglePlayServicesAvailable(this.getContext()) == ConnectionResult.SUCCESS) {
            mGoogleApiClient = new GoogleApiClient.Builder(this.getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
        } else {
            Toast.makeText(
                    this.getActivity(),
                    getString(R.string.google_service_not_available),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    public static CustomerInfoFragment newInstance() {
        Bundle args = new Bundle();
        
        CustomerInfoFragment fragment = new CustomerInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mGoogleApiClient != null) mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mGoogleApiClient != null) mGoogleApiClient.disconnect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_info, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        view.findViewById(R.id.editSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCustomer();
            }
        });

        mAutoCompleteAdapter = new MyAutoCompleteAdapter();
        mEditAddress.setAdapter(mAutoCompleteAdapter);
        mEditAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Address address = (Address)mAutoCompleteAdapter.getItem(i);
                mEditAddress.setText(address.primary);

                String[] s = TextUtils.split(address.secondary, ",");
                if(s != null && s.length >= 2) {
                    mEditCity.setText(s[0]);
                    mEditState.setText(s[1]);
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    protected void onMessage(Message msg) {
        switch(msg.what) {
            case R.id.customerActivityOnCustomerUpdate: {
                updateUI();
                break;
            }
        }
    }

    protected void updateUI() {
        Customer customer = mActivity.getCustomer();

        mEditName.setText(customer.getName() != null ? customer.getName() : "");
        mEditPhone.setText(customer.getPhone() != null ? customer.getPhone() : "");
        mEditAddress.setText(customer.getAddress() != null ? customer.getAddress() : "");
        mEditAddress2.setText(customer.getAddress2() != null ? customer.getAddress2() : "");
        mEditCity.setText(customer.getCity() != null ? customer.getCity() : "");
        mEditState.setText(customer.getState() != null ? customer.getState() : "");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Google Service onConnectionFailed: " + connectionResult.toString());
    }

    static class Address {
        String full;
        String primary;
        String secondary;
        Address(String full, String primary, String secondary) {
            this.full = full;
            this.primary = primary;
            this.secondary = secondary;
        }
    }

    class MyAutoCompleteAdapter extends AutoCompleteAdapter {

        @Override
        public List doFilter(String query) {
            if(mGoogleApiClient == null) return null;

            AutocompletePredictionBuffer result = Places.GeoDataApi
                    .getAutocompletePredictions(mGoogleApiClient, query, null, null)
                    .await();
            try {
                if(!result.getStatus().isSuccess()) return null;

                List<Address> addresses = new ArrayList<Address>();
                Iterator<AutocompletePrediction> iterator = result.iterator();
                while (iterator.hasNext()) {
                    AutocompletePrediction prediction = iterator.next();
                    addresses.add(
                            new Address(
                                    prediction.getFullText(null).toString(),
                                    prediction.getPrimaryText(null).toString(),
                                    prediction.getSecondaryText(null).toString()
                            )
                    );
                }
                return addresses;

            } finally {
                if(result != null) result.release();
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.dropdown_item_1line, parent, false);

            Address address = (Address)getItem(position);
            ((TextView)convertView).setText(address.full);
            return convertView;
        }
    }

    public void saveCustomer() {
        Customer customer = new Customer();

        customer.setName(mEditName.getText().toString().trim());
        if(customer.getName().isEmpty())  {
            TinyUtils.showMsgBox(getContext(), R.string.save_ticket_name_empty);
            return;
        }

        customer.setPhone(mEditPhone.getText().toString().trim());
        customer.setAddress(mEditAddress.getText().toString().trim());
        customer.setAddress2(mEditAddress2.getText().toString().trim());
        customer.setCity(mEditCity.getText().toString().trim());
        customer.setState(mEditState.getText().toString().trim());

        mActivity.saveCustomer(customer);
    }

}
