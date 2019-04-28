package kz.iitu.culto.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlacesService {
    public static final String ENDPOINT = "https://maps.googleapis.com/maps/api/place/";
    public static final String IMAGE_ENDPOINT = "https://maps.googleapis.com/maps/api/place/photo?key=maxwidth=400&photoreference=";

    public static PlacesApi getApi(){return getRetrofit().create(PlacesApi.class);}

    public static Retrofit getRetrofit(){
        return new Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
