package com.tylerlubeck.buildingmapper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Tyler on 10/11/2014.
 */
public class AccessPointListAdapter extends ArrayAdapter<AccessPoint> {
    private final Context ctx;
    private final ArrayList<AccessPoint> accessPoints;

    AccessPointListAdapter(Context ctx, ArrayList<AccessPoint> accessPoints) {
        super(ctx, R.layout.access_point_list_item, accessPoints);
        this.ctx = ctx;
        this.accessPoints = accessPoints;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.access_point_list_item, parent, false);
        TextView bssidView = (TextView) rowView.findViewById(R.id.bssid_view);
        TextView rssView = (TextView) rowView.findViewById(R.id.rss_view);
        TextView dateView = (TextView) rowView.findViewById(R.id.date_view);

        bssidView.setText(this.accessPoints.get(position).getBSSID());
        rssView.setText(String.valueOf(this.accessPoints.get(position).getRSS()));
        dateView.setText("Some Date");

        return rowView;
    }
}
