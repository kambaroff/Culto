package kz.iitu.culto.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kz.iitu.culto.Api.PlacesService;
import kz.iitu.culto.Model.Result;
import kz.iitu.culto.R;

import static kz.iitu.culto.MainActivity.API_KEY;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder> {

    private List<Result> mPlaces;

    public PlacesAdapter(){mPlaces = new ArrayList<>();
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_item_place, viewGroup, false);

        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder placeViewHolder, int i) {
        placeViewHolder.bindPlace(mPlaces.get(i));
    }

    @Override
    public int getItemCount() { return mPlaces.size(); }

    public void setPlaces(List<Result> places){
        mPlaces.clear();
        mPlaces.addAll(places);

        notifyDataSetChanged();
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {

        private TextView mNameView;
        private TextView mRatingView;
        private TextView mTotalView;
        private TextView mAddressView;

        private ImageView mPlaceImage;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);

            mNameView = itemView.findViewById(R.id.place_name);
            mRatingView = itemView.findViewById(R.id.rating_view);
            mTotalView = itemView.findViewById(R.id.rating_total);
            mAddressView = itemView.findViewById(R.id.address_view);

            mPlaceImage = itemView.findViewById(R.id.place_image);
        }

        public void bindPlace(Result result){
            mNameView.setText(result.getName());
            mRatingView.setText(String.format("%.1f",result.getRating()));
            mTotalView.setText(String.format("%d", result.getUserRatingsTotal()));
            mAddressView.setText(result.getVicinity());

            Picasso.with(itemView.getContext())
                    .load(PlacesService.IMAGE_ENDPOINT + result.getPhotos() + "&key=" + API_KEY)
                    .into(mPlaceImage);

        }
    }
}
