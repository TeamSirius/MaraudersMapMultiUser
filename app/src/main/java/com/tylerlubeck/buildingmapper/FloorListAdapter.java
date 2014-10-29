package com.tylerlubeck.buildingmapper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Tyler on 10/11/2014.
 */
public class FloorListAdapter extends ArrayAdapter<Floor> {
    private final Context ctx;
    private final ArrayList<Floor> floors;

    FloorListAdapter(Context ctx, ArrayList<Floor> floors) {
        super(ctx, R.layout.floor_list_item, floors);
        this.ctx = ctx;
        this.floors = floors;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        TextView nameView = (TextView) rowView.findViewById(android.R.id.text1);
        nameView.setText(this.floors.get(position).toString());
        return rowView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        TextView nameView = (TextView) rowView.findViewById(android.R.id.text1);
        nameView.setText(this.floors.get(position).toString());
        return rowView;
    }

    @Override
    public void clear() {
        this.floors.clear();
    }

    public void addAll(ArrayList<Floor> newFloors) {
        for (Floor f: newFloors) {
            this.floors.add(f);
        }
    }
}
