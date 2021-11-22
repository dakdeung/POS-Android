package com.example.skripsi_kamal.apihelper;

import com.example.skripsi_kamal.config.UserConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.BuildConfig;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestApi {
    private static final Object OBJ_SYNC = new Object();

    private Map<String, String> requestHeaders = new HashMap<>();

    private String bearerToken;

    private String coreBearerToken;

    private Retrofit.Builder builder = null;

    private static volatile RestApi Instance =
            new RestApi();

    private static final String API_URL = "https://cikkan.com/project/pos/";

    public static RestApi get() {
        RestApi localInstance = Instance;
        if (localInstance == null) {
            synchronized (RestApi.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new RestApi();
                }
            }
        }
        return localInstance;
    }
    public void setBearerToken(String bearerToken) {
        synchronized (OBJ_SYNC) {
            this.bearerToken = bearerToken;
        }
    }

    /**
     * <pre>{@code
     *
     * OkHttpClient client = this.defaultRetrofit(withCache);
     * Gson gson = new GsonBuilder().setLenient().serializeNulls().create();
     * return new Retrofit.Builder()
     *     .client(client)
     *     .baseUrl(BASE_URL)
     *     .addConverterFactory(new RetrofitToStringConverter())
     *     .addConverterFactory(GsonConverterFactory.create(gson))
     *     .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
     * }</pre>
     */
    @SuppressWarnings("unused,WeakerAccess")
    public OkHttpClient defaultRetrofit(boolean... withCache) {
        synchronized (OBJ_SYNC) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(10, TimeUnit.MINUTES);
            builder.readTimeout(10, TimeUnit.MINUTES);
            builder.writeTimeout(10, TimeUnit.MINUTES);
            builder.addInterceptor(headerInterceptor());
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                builder.addInterceptor(interceptor);
            }
            return builder.build();
        }
    }

    private Retrofit.Builder getDefaultBuilder() {
        synchronized (OBJ_SYNC) {
            return this.defaultBuilder();
        }
    }

    public void setBuilder(Retrofit.Builder builder) {
        synchronized (OBJ_SYNC) {
            this.builder = builder;
        }
    }

    @SuppressWarnings("WeakerAccess")
    public Retrofit.Builder defaultBuilder(boolean... withCache) {
        synchronized (OBJ_SYNC) {
            OkHttpClient client = this.defaultRetrofit(withCache);
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .serializeNulls()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
            this.builder = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson));
            return this.builder;
        }
    }

    /**
     * This method is used when calling http request.
     * <p>
     * This method has added a timeout for connectTimeout, readTimeout and writeTimeout,
     * you can setup the all timeout at above variables.
     * <p>
     * This method also has configured for debugging, but for security purpose
     * the debugging just added for BuildConfig.DEBUG mode.
     * <p>
     * For parser / converter is using GSON Converter.
     **/
    public BaseApiService api(Retrofit.Builder builder) {
        synchronized (OBJ_SYNC) {
            Retrofit retrofit = builder.build();
            return retrofit.create(BaseApiService.class);
        }
    }


    /**
     * <pre>{@code
     *
     * RestApi.setBaseUrl(Constanta.BASE_URL);
     * Observable<ApiResponse> requestData = new RestApi()
     *     .setApiService(GetApiEndpoint.class)
     *     .getPopularMovie(BuildConfig.API_KEY, maps);
     * }</pre>
     */
    public <T> T api(Class<T> clazz) {
        synchronized (OBJ_SYNC) {
            Retrofit retrofit = this.getDefaultBuilder().build();
            return retrofit.create(clazz);
        }
    }

    public <T> T api(Class<T> clazz, Retrofit.Builder builder) {
        synchronized (OBJ_SYNC) {
            Retrofit retrofit = builder.build();
            return retrofit.create(clazz);
        }
    }

    public BaseApiService api(boolean... withCache) {
        synchronized (OBJ_SYNC) {
            Retrofit.Builder builder = this.defaultBuilder(withCache);
            Retrofit retrofit = builder.build();
            return retrofit.create(BaseApiService.class);
        }
    }

    /**
     * Set request headers.
     *
     * @param requestHeaders data header yang akan dipassing.
     */
    @SuppressWarnings("unused")
    public void setRequestHeaders(Map<String, String> requestHeaders) {
        synchronized (OBJ_SYNC) {
            this.requestHeaders = requestHeaders;
        }
    }

    /**
     * Intercept request, Disini berfungsi untuk input data header atau lainnya.
     *
     * @return Interceptor
     */
    private Interceptor headerInterceptor() {
        synchronized (OBJ_SYNC) {
            return chain -> {
                Request original = chain.request();
                Request.Builder builder = original.newBuilder();
                if (StringUtils.isNotEmpty(UserConfig.getInstance().getUserToken())) {
                    final String bearer = "Bearer " + UserConfig.getInstance().getUserToken();
                    builder.addHeader("Authorization", bearer);
                }
                builder.addHeader("Cache-Control", "no-cache");
                builder.addHeader("Accept", "application/json");
                for (final String headerName : this.requestHeaders.keySet()) {
                    final String headerValue = this.requestHeaders.get(headerName);
                    assert headerValue != null;
                    builder.header(headerName, headerValue);
                }
                return chain.proceed(builder.build());
            };
        }
    }


}
