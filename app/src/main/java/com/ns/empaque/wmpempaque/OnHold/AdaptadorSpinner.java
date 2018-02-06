package com.ns.empaque.wmpempaque.OnHold;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ns.empaque.wmpempaque.R;

/**
 * Created by Christopher BA on 14/03/2017.
 */

public class AdaptadorSpinner extends ArrayAdapter<String> {

    private final Activity contexto;
    private final String itemsSpinner[];

    public AdaptadorSpinner(Activity context, String[] items) {
        super(context, R.layout.adaptador_spinner, new String[items.length]);
        contexto = context;

        itemsSpinner = items;
    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = contexto.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.adaptador_spinner, null, true);

        TextView txtItem = (TextView) rowView.findViewById(R.id.lblItemSpinner);
        txtItem.setText(itemsSpinner[position]);

        return rowView;
    }
}
