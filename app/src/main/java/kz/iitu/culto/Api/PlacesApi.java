package kz.iitu.culto.Api;

import retrofit2.Call;
import retrofit2.http.GET;
import kz.iitu.culto.Model.PlacesResponse;

import static kz.iitu.culto.MainActivity.API_KEY;

public interface PlacesApi {
    @GET("nearbysearch/json?location=43.238949, 76.889709&radius=10000&type=art_gallery&key=" + API_KEY)
    Call<PlacesResponse> getArtGallery();

    @GET("nearbysearch/json?location=43.238949, 76.889709&radius=10000&type=museum&key=" + API_KEY)
    Call<PlacesResponse> getMuseum();

    @GET("")
    Call<PlacesResponse> getPhoto();
}
