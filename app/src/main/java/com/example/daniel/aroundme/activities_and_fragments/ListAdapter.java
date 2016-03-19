package com.example.daniel.aroundme.activities_and_fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.daniel.aroundme.R;
import com.example.daniel.aroundme.database.PointsOfInterestList;

import java.util.ArrayList;

/**
 * Created by daniel.gotlieb on 09/03/2016.
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>  {
//    private Place[] itemsData;
    private ArrayList <PointsOfInterestList> itemsData;


    public ListAdapter(ArrayList itemsData) {
        this.itemsData = itemsData;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_card_view, null);

        // create ViewHolder
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData

        viewHolder.txtViewTitle.setText(itemsData.get(position).getPoint());
        viewHolder.imgViewIcon.setImageResource(itemsData.get(position).getImage());


    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtViewTitle;
        public ImageView imgViewIcon;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.titlem);
            imgViewIcon = (ImageView) itemLayoutView.findViewById(R.id.thumbnail);
        }


    }


    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itemsData.size();
    }
}
