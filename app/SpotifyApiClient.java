package com.example.music_player;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import android.util.Base64;


public class SpotifyApiClient {
    private static final String CLIENT_ID = "YOUR_CLIENT_ID";
    private static final String CLIENT_SECRET = "YOUR_CLIENT_SECRET";
    // ...

    private static <OkHttpClient> String getAccessToken() {
        try {
            OkHttpClient client = new OkHttpClient();
            String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
            String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

            RequestBody requestBody = new FormBody.Builder()
                    .add("grant_type", "client_credentials")
                    .build();

            Request request = new Request.Builder()
                    .url("https://accounts.spotify.com/api/token")
                    .addHeader("Authorization", "Basic " + base64EncodedCredentials)
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();
            String responseString = response.body().string();

            JSONObject jsonObject = new JSONObject(responseString);
            String accessToken = jsonObject.getString("access_token");

            return accessToken;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void searchTrack(String accessToken, String query) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spotify.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SpotifyApiService service = retrofit.create(SpotifyApiService.class);
        Call<SearchResult> call = service.searchTrack("Bearer " + accessToken, query);
        call.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                if (response.isSuccessful()) {
                    SearchResult searchResult = response.body();
                    // Lakukan sesuatu dengan hasil pencarian track
                } else {
                    // Tangani jika respons tidak berhasil
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                // Tangani jika terjadi kesalahan jaringan atau lainnya
            }
        });
    }

    public interface SpotifyApiService {
        @GET("v1/search?type=track")
        Call<SearchResult> searchTrack(@Header("Authorization") String authorization, @Query("q") String query);
    }
}
