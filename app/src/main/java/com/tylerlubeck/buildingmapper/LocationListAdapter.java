package com.tylerlubeck.buildingmapper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Tyler on 10/11/2014.
 */
public class LocationListAdapter extends ArrayAdapter<Location> {
    private final Context ctx;
    private final ArrayList<Location> locations;

    LocationListAdapter(Context ctx, ArrayList<Location> locations) {
        super(ctx, R.layout.floor_list_item, locations);
        this.ctx = ctx;
        this.locations = locations;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        TextView nameView = (TextView) rowView.findViewById(android.R.id.text1);
        nameView.setText(this.locations.get(position).toString());
        return rowView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        TextView nameView = (TextView) rowView.findViewById(android.R.id.text1);
        nameView.setText(this.locations.get(position).toString());
        return rowView;
    }

    @Override
    public void clear() {
        this.locations.clear();
    }

    public void addAll(ArrayList<Location> newLocations) {
        for (Location l: newLocations) {
            this.locations.add(l);
        }
    }
}
