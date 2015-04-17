package com.tylerlubeck.maraudersmapmultiuser.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tylerlubeck.maraudersmapmultiuser.Models.FacebookFriend;

import java.util.ArrayList;

/**
 * Created by Tyler on 2/23/2015.
 */
public class SelectFriendsArrayAdapter extends ArrayAdapter<FacebookFriend> {
    private final Context context;
    private final ArrayList<FacebookFriend> friends;

    public SelectFriendsArrayAdapter(Context context, ArrayList<FacebookFriend> friends ) {
        super(context, android.R.layout.simple_list_item_1, friends);

        this.context = context;
        this.friends = friends;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        TextView nameView = (TextView) rowView.findViewById(android.R.id.text1);
        nameView.setText(this.friends.get(position).getName());
        return rowView;
    }

}
