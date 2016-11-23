package com.tinyappsdev.tinypos.rest;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tinyappsdev.tinypos.AppConst;
import com.tinyappsdev.tinypos.AppGlobal;
import com.tinyappsdev.tinypos.R;
import com.tinyappsdev.tinypos.data.Customer;
import com.tinyappsdev.tinypos.data.ModelHelper;
import com.tinyappsdev.tinypos.helper.ConfigCache;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ApiCallClient {
    public final static String TAG = ApiCallClient.class.getSimpleName();

    public class ApiResponse {
        public boolean success;
        public boolean authFailed;
    }

    private AppGlobal mAppGlobal;
    private String mServerUri;
    private SharedPreferences mSharedPreferences;
    private HttpClient mHttpClient = new HttpClient() {
        @Override
        public Map<String, String> loadCookies(String uri) {
            Map<String, String> map = new HashMap();
            String serverAuth = mSharedPreferences.getString("serverAuth", "");
            int employeeCode = mSharedPreferences.getInt("employeeCode", 0);

            if(serverAuth != null) map.put("serverAuth", serverAuth);
            if(employeeCode != 0) map.put("employeeCode", employeeCode + "");
            return map;
        }

        @Override
        public void saveCookies(String uri, Map<String, String> cookies) {
            if(cookies.containsKey("serverAuth"))
                mSharedPreferences.edit().putString("serverAuth", cookies.get("serverAuth")).apply();
        }
    };

    public ApiCallClient(AppGlobal appGlobal) {
        mAppGlobal = appGlobal;
        mSharedPreferences = mAppGlobal.getSharedPreferences();
        setServerAddress(mSharedPreferences.getString("serverAddress", ""));
    }

    public void setServerAddress(String serverAddress) {
        if(serverAddress != null && !serverAddress.isEmpty()) {
            if (serverAddress.indexOf(':') < 0) serverAddress += ":" + AppConst.DEFAULT_SERVER_PORT;

            mServerUri = String.format("%s://%s",
                    mSharedPreferences.getBoolean("serverSecure", false) ? "https" : "http",
                    serverAddress
            );
        } else
            mServerUri = null;
    }

    public static class Result<T> {
        public String error;
        public T data;

        private HttpClient.HttpRequest mHttpRequest;
        private Handler mHandler;
        public void cancel() {
            if(mHttpRequest != null) {
                mHttpRequest.cancel();
                mHttpRequest = null;
            }
        }
    }

    public interface OnResultListener<T> {
        void onResult(Result<T> result);
    }

    public <T> Result<T> makeCall(String uri, Object body, Class<T> resultType) {
        return makeCall(uri, body, resultType, null);
    }

    public <T> Result<T> makeCall(String uri, Object body,
                                  final Class<T> resultType,
                                  final OnResultListener<T> onResultListener) {

        final Result<T> result = new Result();
        if(onResultListener != null) result.mHandler = new Handler();



        if(mServerUri == null) {
            result.error = "No Server Address";
            if(onResultListener != null) {
                result.mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onResultListener.onResult(result);
                    }
                });
            }
            return result;
        }

        uri = mServerUri + "/Api" + uri;
        Log.i(TAG, String.format("ApiCallClient -> %s", uri));

        String bodyJson = null;
        if(body != null) {
            try {
                bodyJson = ModelHelper.getObjectMapper().writeValueAsString(body);
                //Log.i(TAG, String.format("ApiCallClient -> %s", bodyJson));
            } catch (JsonProcessingException e) {
                result.error = e.getMessage();
                if(onResultListener != null) {
                    result.mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onResultListener.onResult(result);
                        }
                    });
                }
                return result;
            }
        }

        if(onResultListener == null) {
            try {
                String response = mHttpClient.makeRequestSync(uri, bodyJson);
                if(resultType == null)
                    result.data = (T)response;
                else if(response != null)
                    result.data = ModelHelper.getObjectMapper().readValue(response, resultType);
            } catch (IOException e) {
                result.error = e.getMessage();
            }

        } else {
            result.mHttpRequest = mHttpClient.makeRequestAsync(uri, bodyJson, new HttpClient.OnResultListener() {
                @Override
                public void onResult(String error, String body) {
                    //not in UI thread
                    if(error != null) {
                        result.error = error;
                    } else {
                        try {
                            if(resultType == null)
                                result.data = (T)body;
                            else if(body != null)
                                result.data = ModelHelper.getObjectMapper().readValue(body, resultType);
                        } catch (IOException e) {
                            result.error = e.getMessage();
                        }
                    }
                    result.mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(result.mHttpRequest == null) return;
                            result.mHttpRequest = null;
                            onResultListener.onResult(result);
                        }
                    });
                }
            });
        }

        return result;
    }

    public Result<Map> saveCustomer(Customer customer, Class resultType, OnResultListener onResultListener) {
        return makeCall(
                customer.getId() > 0 ? "/Customer/updateDoc" : "/Customer/newDoc",
                customer,
                resultType,
                onResultListener
        );
    }

    public Result<Map> getDocEventLastId(OnResultListener onResultListener) {
        return makeCall("/DocEvent/getLastId", null, Map.class, onResultListener);
    }

    public Result<Map> getDocEventDocs(long fromId, OnResultListener onResultListener) {
        return makeCall(
                "/DocEvent/getDocs?sync=" + ModelHelper.SYNCABLE_TABLES_QUERY + "&fromId=" + fromId,
                null,
                Map.class,
                onResultListener);
    }

}
