package com.tylerlubeck.maraudersmapmultiuser.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tylerlubeck.maraudersmapmultiuser.Models.Floor;
import com.tylerlubeck.maraudersmapmultiuser.R;

import java.util.ArrayList;

/**
 * Created by Tyler Lubeck on 10/11/2014.
 */

/**
 * @author Tyler Lubeck
 *  A class to model how a ListAdapter should display a Floor
 */
class FloorListAdapter extends ArrayAdapter<Floor> {
    private final Context context;
    private final ArrayList<Floor> floors;

    /**
     *
     * @param context           The context this will apply in
     * @param floors  An initial list of floors to display
     */
    FloorListAdapter(Context context, ArrayList<Floor> floors) {
        super(context, R.layout.floor_list_item, floors);
        this.context = context;
        this.floors = floors;
    }

    /**
     * Defines the view for how a Floor should be displayed in the Spinner when selected
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        TextView nameView = (TextView) rowView.findViewById(android.R.id.text1);
        nameView.setText(this.floors.get(position).toString());
        return rowView;
    }

    /**
     * Defines the view for how a Floor should be displayed in a dropdown
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        TextView nameView = (TextView) rowView.findViewById(android.R.id.text1);
        nameView.setText(this.floors.get(position).toString());
        return rowView;
    }

    /**
     * Empties the internal list of floors
     */
    @Override
    public void clear() {
        this.floors.clear();
    }

}
