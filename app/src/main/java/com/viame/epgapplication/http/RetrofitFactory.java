package com.viame.epgapplication.http;
import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {
    private volatile static Retrofit epaRetrofit;
    private volatile static Retrofit cwbRetrofit;

    // 單例 Retrofit
    public static Retrofit getEPAInstance() {
        if (epaRetrofit == null) {
            // 雙重檢查
            synchronized (RetrofitFactory.class) {
                if (epaRetrofit == null) {
                    epaRetrofit = new Retrofit.Builder()
                            .baseUrl("https://opendata.epa.gov.tw/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(setSSL())
                            .build();
                }
            }
        }
        return epaRetrofit;
    }

    // 單例 Retrofit
    public static Retrofit getCWBInstance() {
        if (cwbRetrofit == null) {
            // 雙重檢查
            synchronized (RetrofitFactory.class) {
                if (cwbRetrofit == null) {
                    cwbRetrofit = new Retrofit.Builder()
                            .baseUrl("https://opendata.cwb.gov.tw/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(setSSL())
                            .build();
                }
            }
        }
        return cwbRetrofit;
    }

    // SSL 檢查
    private static OkHttpClient setSSL() {
        OkHttpClient sClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .callTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }}, new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }

        HostnameVerifier hv1 = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        try {
            Class workerClass = Class.forName("okhttp3.OkHttpClient");
            Field hostnameVerifier = workerClass.getDeclaredField("hostnameVerifier");
            hostnameVerifier.setAccessible(true);
            hostnameVerifier.set(sClient, hv1);

            Field sslSocketFactory = workerClass.getDeclaredField("sslSocketFactory");
            sslSocketFactory.setAccessible(true);
            sslSocketFactory.set(sClient, sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sClient;
    }
}
