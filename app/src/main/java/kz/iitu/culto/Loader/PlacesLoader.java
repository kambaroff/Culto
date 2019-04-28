package kz.iitu.culto.Loader;

import java.util.List;

import kz.iitu.culto.Api.PlacesService;
import kz.iitu.culto.Model.Photo;
import kz.iitu.culto.Model.PlacesResponse;
import kz.iitu.culto.Model.Result;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PlacesLoader {

    private PlaceLoadCallback mCallback;

    public PlacesLoader(PlaceLoadCallback callback){mCallback = callback;}

    public void loadArtGalleries(){
        PlacesService.getApi()
                .getArtGallery()
                .enqueue(new Callback<PlacesResponse>() {
                    @Override
                    public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {
                        mCallback.onPlacesLoaded(response.body().getResults());
                    }

                    @Override
                    public void onFailure(Call<PlacesResponse> call, Throwable t) {

                    }
                });
    }

    public void loadMuseums(){
        PlacesService.getApi()
                .getMuseum()
                .enqueue(new Callback<PlacesResponse>() {
                    @Override
                    public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {
                        mCallback.onPlacesLoaded(response.body().getResults());
                    }

                    @Override
                    public void onFailure(Call<PlacesResponse> call, Throwable t) {

                    }
                });
    }




    public interface PlaceLoadCallback {
        void onPlacesLoaded(List<Result> places);
    }


}
