package com.tinyappsdev.tinypos.rest;

import android.os.Handler;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tinyappsdev.tinypos.data.Customer;
import com.tinyappsdev.tinypos.data.ModelHelper;

import java.io.IOException;
import java.util.Map;


public class ApiCallClient {
    public final static String TAG = ApiCallClient.class.getSimpleName();

    private static final ApiCallClient sUiApiCallClient = new ApiCallClient();
    private static final ApiCallClient sBgApiCallClient = new ApiCallClient();
    public static ApiCallClient getUiInstance() { return sUiApiCallClient; }
    public static ApiCallClient getBgInstance() { return sBgApiCallClient; }

    private HttpClient mHttpClient = new HttpClient();


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

        Log.i(TAG, String.format("ApiCallClient -> %s", uri));
        String bodyJson = null;
        if(body != null) {
            try {
                bodyJson = ModelHelper.getObjectMapper().writeValueAsString(body);
                Log.i(TAG, String.format("ApiCallClient -> %s", bodyJson));
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
                result.data = ModelHelper.getObjectMapper().readValue(
                        mHttpClient.makeRequestSync(uri, bodyJson),
                        resultType
                );
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
        return makeCall("/DocEvent/getDocs?fromId=" + fromId, null, Map.class, onResultListener);
    }

}
