package com.matey.disciteomnesapp.network;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Interface representing API endpoints for the DisciteOmnes application.
 * Used by Retrofit to automatically generate HTTP request implementations.
 *
 * ✅ This mock implementation is used to simulate a welcome message.
 * ✅ The endpoint is hosted on Mocky.io for testing purposes only.
 *
 * ⚠️ This design and endpoint usage was suggested via AI, then customized to match
 * the structure of the app and integration flow.
 */
public interface DisciteOmnesApi {

    /**
     * Simulated GET endpoint to retrieve a welcome message.
     * Hosted at Mocky.io and returns a JSON object like:
     * { "message": "Hello from mocked API!" }
     *
     * @return Retrofit Call wrapping a MessageResponse object
     */
    @GET("v3/215c5460-1a75-4fef-ae5b-2e8a30c54c79") // Static mock endpoint for testing
    Call<MessageResponse> getWelcomeMessage();
}
