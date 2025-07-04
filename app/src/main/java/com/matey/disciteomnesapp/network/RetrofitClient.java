package com.matey.disciteomnesapp.network;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

/**
 * Singleton class for providing a Retrofit client instance.
 *
 * ✅ This version uses an OkHttp interceptor to mock an API response with a static JSON payload.
 * ✅ Useful for development/testing without relying on a live backend.
 *
 * ⚠️ AI-assisted suggestion: The mocked interceptor was generated based on GPT recommendations.
 * ✅ Manual adjustments were made to ensure Retrofit and OkHttp integration with the mock response.
 */
public class RetrofitClient {

    // Retrofit instance (singleton)
    private static Retrofit retrofit = null;

    /**
     * Returns a singleton Retrofit client with a mocked API response.
     *
     * @return Configured Retrofit instance with mock interceptor
     */
    public static Retrofit getClient() {
        if (retrofit == null) {

            // Interceptor to mock every API call with a hardcoded JSON response
            Interceptor mockInterceptor = chain -> {
                Request request = chain.request();

                // This static response simulates a successful JSON message
                String fakeJson = "{ \"message\": \"Hello from mocked API!\" }";

                return new Response.Builder()
                        .code(200)  // HTTP OK
                        .message("OK")
                        .request(request)
                        .protocol(Protocol.HTTP_1_1)
                        .body(ResponseBody.create(
                                MediaType.parse("application/json"),
                                fakeJson
                        ))
                        .build();
            };

            // OkHttpClient with the mock interceptor injected
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(mockInterceptor)
                    .build();

            // Retrofit instance with dummy base URL and Gson converter
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://mock.local/") // Dummy URL for development
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }

        return retrofit;
    }
}
