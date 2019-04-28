package com.viame.epgapplication.http;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viame.epgapplication.model.RegistrationResponseModel;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class RestClient {

    private static RestClient instance;
    private APIInterface gitHubService;

    private RestClient() {
        final Gson gson =
                new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API.EPG_SANDBOX_BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(setSSL())
                .build();
        gitHubService = retrofit.create(APIInterface.class);
    }

    public static RestClient getInstance() {
        if (instance == null) {
            // 雙重檢查
            synchronized (RestClient.class) {
                if (instance == null) {
                    instance = new RestClient();
                }
            }
        }
        return instance;
    }

    public Observable<RegistrationResponseModel> getStarredRepos(@NonNull String userName) {
        return null;//gitHubService.getStarredRepositories(userName);
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



//      SSL Settings from TWO Side
//    public RestClient(Context context, boolean twoWaySsl) {
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
//        builder.addInterceptor(interceptor);
//        //builder.addNetworkInterceptor(new StethoInterceptor());
//        if (twoWaySsl) {
//            SSLContext sslContext = getSSLConfig(context);
//            if (sslContext != null) {
//                builder.sslSocketFactory(sslContext.getSocketFactory());
//            }
//        }
//        OkHttpClient client = builder.build();
//
//        Retrofit retrofit = new Retrofit.Builder().baseUrl(API.EPG_SANDBOX_BASE_URL)
//                .client(client)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        gitHubService = retrofit.create(APIInterface.class);
//    }
//
//    private static class SingletonHolder {
//        private static RestClient INSTANCE = null;
//
//        private static RestClient getINSTANCE(Context context, boolean twoWaySsl) {
//            INSTANCE = new RestClient(context, twoWaySsl);
//            return INSTANCE;
//        }
//    }
//
//    private SSLContext getSSLConfig(Context context) {
//        SSLContext sslContext = null;
//        String pfxPassword = "";
//        InputStream keyStoreStream = null;
//        CertificateFactory cf = null;
//        try {
//            KeyStore keyStore = KeyStore.getInstance("PKCS12");
////            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509");
//            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//             // keyStoreStream = context.getResources().openRawResource(R.raw.sample_cert);
//             // pfxPassword = context.getResources().getString(R.string.certPassword);
//            keyStore.load(keyStoreStream, pfxPassword.toCharArray());
//            keyManagerFactory.init(keyStore, pfxPassword.toCharArray());
//
//            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm);
//
//            if (trustManagerFactory != null) {
//                trustManagerFactory.init(keyStore);
//            }
//            sslContext = SSLContext.getInstance("TLS");
//            if (sslContext != null && trustManagerFactory != null && trustManagerFactory.getTrustManagers() != null) {
//                sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
//            }
//        } catch (CertificateException ce) {
//            ce.printStackTrace();
//        } catch (KeyStoreException ke) {
//            ke.printStackTrace();
//        } catch (NoSuchAlgorithmException ne) {
//            ne.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (KeyManagementException me) {
//            me.printStackTrace();
//        } catch (UnrecoverableKeyException ue) {
//            ue.printStackTrace();
//        }
//        return sslContext;
//    }
//
//    public static RestClient getInstance(Context context, boolean twoWaySsl) {
//        return SingletonHolder.getINSTANCE(context, twoWaySsl);
//    }
}
