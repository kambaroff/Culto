package kz.iitu.culto;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import kz.iitu.culto.Loader.PlacesLoader;
import kz.iitu.culto.Model.Result;
import kz.iitu.culto.adapter.PlacesAdapter;

public class MainActivity extends AppCompatActivity implements PlacesLoader.PlaceLoadCallback, View.OnClickListener {

    private RecyclerView mPlacesView;
    private PlacesAdapter mPlacesAdapter;

    private PlacesLoader mPlacesLoader;

    public static final String API_KEY = "AIzaSyAEDSvNzfptBzsQDnlJHByyPikKtEBZfqI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        mPlacesLoader = new PlacesLoader(this);
    }

    private void initUI(){
        mPlacesView = findViewById(R.id.places_list);
        mPlacesView.setLayoutManager(new LinearLayoutManager(this));

        mPlacesAdapter = new PlacesAdapter();
        mPlacesView.setAdapter(mPlacesAdapter);




        findViewById(R.id.btn_artgal).setOnClickListener(this);
        findViewById(R.id.btn_museum).setOnClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onPlacesLoaded(List<Result> places) {
        findViewById(R.id.progress_bar).setVisibility(View.GONE);
        mPlacesAdapter.setPlaces(places);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_artgal:
                findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
                mPlacesLoader.loadArtGalleries();
            case R.id.btn_museum:
                findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
                mPlacesLoader.loadMuseums();
        }

    }

    public boolean onOptionsItemSelected(MenuItem item){
        startActivity(new Intent(this, SideMenuActivity.class));
        return true;
    }
}
