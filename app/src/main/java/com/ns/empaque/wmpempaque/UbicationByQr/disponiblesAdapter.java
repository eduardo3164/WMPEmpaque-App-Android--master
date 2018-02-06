package com.ns.empaque.wmpempaque.UbicationByQr;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ns.empaque.wmpempaque.R;

/**
 * Created by jcalderon on 05/04/2016.
 */
public class disponiblesAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private String[] x, y, z, nameLocation, totalBoxes;

    public disponiblesAdapter(Activity context, String[] x, String[] y, String[] z, String[] nameLocation, String[] totalBoxes) {
        super(context, R.layout.viewdisponibles, x);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.x= x;
        this.y = y;
        this.z = z;
        this.nameLocation = nameLocation;
        this.totalBoxes = totalBoxes;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.viewdisponibles, null, true);

        TextView xyz = (TextView) rowView.findViewById(R.id.xyz);
        TextView nLocation = (TextView) rowView.findViewById(R.id.nLocation);
        TextView NoBoxes = (TextView) rowView.findViewById(R.id.nBoxes);

        xyz.setText("( "+x[position]+" ,"+y[position]+" ,"+z[position]+" )");
        nLocation.setText(nameLocation[position]);
        NoBoxes.setText(totalBoxes[position]);

        return rowView;

    };
}
