package com.example.daniel.aroundme.activities_and_fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.daniel.aroundme.R;
import com.example.daniel.aroundme.database.Place;
import com.example.daniel.aroundme.database.PointsOfInterestList;

import java.util.ArrayList;

/**
 * Created by Daniel on 3/19/2016.
 */
public class PointOfIntrests extends Fragment{
    private View v;
    private RecyclerView recyclerView;
    private ArrayList<PointsOfInterestList> itemsData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.points, container, false);
        recyclerView = (RecyclerView)v.findViewById(R.id.points_recycler);


        itemsData = new ArrayList<>();
        itemsData.add(new PointsOfInterestList("daniel",R.drawable.add_to_fav));
        itemsData.add(new PointsOfInterestList("daniel",R.drawable.rsz_waze_icon));
        itemsData.add(new PointsOfInterestList("daniel",R.drawable.show_on_map));
        itemsData.add(new PointsOfInterestList("daniel",R.drawable.add_to_fav));
        itemsData.add(new PointsOfInterestList("daniel",R.drawable.rsz_waze_icon));
        itemsData.add(new PointsOfInterestList("daniel",R.drawable.show_on_map));
        itemsData.add(new PointsOfInterestList("daniel",R.drawable.add_to_fav));
        itemsData.add(new PointsOfInterestList("daniel",R.drawable.rsz_waze_icon));
        itemsData.add(new PointsOfInterestList("daniel",R.drawable.show_on_map));
        itemsData.add(new PointsOfInterestList("daniel",R.drawable.add_to_fav));
        itemsData.add(new PointsOfInterestList("daniel",R.drawable.rsz_waze_icon));
        itemsData.add(new PointsOfInterestList("daniel",R.drawable.show_on_map));
        itemsData.add(new PointsOfInterestList("daniel",R.drawable.add_to_fav));
        itemsData.add(new PointsOfInterestList("daniel",R.drawable.rsz_waze_icon));
        itemsData.add(new PointsOfInterestList("daniel",R.drawable.show_on_map));




        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 3. create an adapter
        ListAdapter mAdapter = new ListAdapter(itemsData);
        // 4. set adapter
        recyclerView.setAdapter(mAdapter);
        // 5. set item animator to DefaultAnimator
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Snackbar.make(v, itemsData.get(position).getPoint(), Snackbar.LENGTH_LONG).show();
                    }


                }));

        return v;
    }
}
