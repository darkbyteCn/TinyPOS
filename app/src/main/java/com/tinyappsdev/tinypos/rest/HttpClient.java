package com.tinyappsdev.tinypos.rest;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpClient {
    private final static String TAG = HttpClient.class.getSimpleName();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient mOkHttpClient;

    public static class HttpRequest {
        private okhttp3.Call mCall;
        public void cancel() {
            if(mCall != null) {
                mCall.cancel();
                mCall = null;
            }
        }
    }

    public HttpClient() {
        mOkHttpClient = new OkHttpClient().newBuilder()
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        if(cookies == null) return;
                        Map<String, String> cookieMap = new HashMap();
                        for(Cookie cookie : cookies)
                            cookieMap.put(cookie.name(), cookie.value());
                        saveCookies(url.toString(), cookieMap);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        Map<String, String> cookieMap = loadCookies(url.toString());
                        List<Cookie> cookieList = new ArrayList();

                        if(cookieMap != null) {
                            for(Map.Entry<String, String> cookie: cookieMap.entrySet()) {
                                cookieList.add(
                                        new Cookie.Builder()
                                                .domain(url.host())
                                                .name(cookie.getKey())
                                                .value(cookie.getValue())
                                                .build()
                                );
                            }
                        }

                        return cookieList;
                    }
                })
                .build();
    }

    public Map<String, String> loadCookies(String uri) {
        return null;
    }

    public void saveCookies(String uri, Map<String, String> cookies) {
    }

    public Request buildRequest(String uri, String body) {
        HttpUrl.Builder builder = HttpUrl.parse(uri).newBuilder();

        Request.Builder requestBuilder = new Request.Builder().url(builder.build());
        if(body != null) requestBuilder.post(RequestBody.create(JSON, body));
        return requestBuilder.build();
    }

    public String makeRequestSync(String uri, String body) throws IOException {
        Response response = null;
        try {
            response = mOkHttpClient.newCall(buildRequest(uri, body)).execute();
            if(response.isSuccessful())
                return response.body().string();
            else
                return null;
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

                onResultListener.onResult(String.valueOf(e.getMessage()), null);
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
