package com.tinyappsdev.tinypos.rest;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpClient {
    private final static String TAG = HttpClient.class.getSimpleName();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient mOkHttpClient = new OkHttpClient();

    public static class HttpRequest {
        private okhttp3.Call mCall;
        public void cancel() {
            if(mCall != null) {
                mCall.cancel();
                mCall = null;
            }
        }
    }

    public Request buildRequest(String uri, String body) {
        HttpUrl.Builder builder = HttpUrl.parse("http://192.168.1.109:8888" + uri).newBuilder();

        Request.Builder requestBuilder = new Request.Builder().url(builder.build());
        if(body != null) requestBuilder.post(RequestBody.create(JSON, body));
        return requestBuilder.build();
    }

    public String makeRequestSync(String uri, String body) throws IOException {
        Response response = null;
        try {
            response = mOkHttpClient.newCall(buildRequest(uri, body)).execute();
            return response.body().string();
        } finally {
            if(response != null) response.close();
        }
    }

    public HttpRequest makeRequestAsync(String uri, String json, final OnResultListener onResultListener) {

        final HttpRequest httpRequest = new HttpRequest();
        httpRequest.mCall = mOkHttpClient.newCall(buildRequest(uri, json));
        httpRequest.mCall.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(httpRequest.mCall == null) return;

                onResultListener.onResult(e.getMessage(), null);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if(httpRequest.mCall == null) return;

                String body = null;
                String error = null;
                try {
                    if (!response.isSuccessful())
                        error = response.toString();
                    else
                        body = response.body().string();

                } catch(IOException e) {
                    error = e.getMessage();
                    body = null;

                } finally {
                    response.close();
                }

                onResultListener.onResult(error, body);
            }
        });

        return httpRequest;
    }

    public interface OnResultListener {
        void onResult(String error, String body);
    }
}
