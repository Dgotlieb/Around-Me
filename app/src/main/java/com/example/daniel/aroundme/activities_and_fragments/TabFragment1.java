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

import java.util.ArrayList;

/**
 * Created by daniel.gotlieb on 08/03/2016.
 */
public class TabFragment1 extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<Place> itemsData;
    private  View v;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         v = inflater.inflate(R.layout.tab_fragment1,container,false);

        recyclerView = (RecyclerView)v.findViewById(R.id.recycler);

        itemsData = new ArrayList<>();
        itemsData.add(new Place("daniel",String.valueOf(R.drawable.bs_ic_clear)));
        itemsData.add(new Place("daniel",String.valueOf(R.drawable.add_to_fav)));
        itemsData.add(new Place("daniel",String.valueOf(R.drawable.rsz_waze_icon)));
        itemsData.add(new Place("daniel",String.valueOf(R.drawable.show_on_map)));



        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 3. create an adapter
        MyAdapter mAdapter = new MyAdapter(itemsData);
        // 4. set adapter
        recyclerView.setAdapter(mAdapter);
        // 5. set item animator to DefaultAnimator
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Snackbar.make(v, itemsData.get(position).getName(), Snackbar.LENGTH_LONG).show();
                    }


                }));





        return v;
    }
}
