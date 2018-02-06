package com.ns.empaque.wmpempaque.PrePallet;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ns.empaque.wmpempaque.R;

/**
 * Created by jcalderon on 31/03/2016.
 */
public class prePalletListAdapter  extends ArrayAdapter<String> {
    private final Activity context;
    private String[] idPrePallet, active, sync;

    public prePalletListAdapter(Activity context, String[] idPrePallet, String[] active, String[] sync) {
        super(context, R.layout.viewlistqrmanager, idPrePallet);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.idPrePallet= idPrePallet;
        this.active = active;
        this.sync = sync;
    }



    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.viewcolumnrepallet, null, true);

        TextView id = (TextView) rowView.findViewById(R.id.idPrePallet);
        TextView activo = (TextView) rowView.findViewById(R.id.active);
        TextView synctv = (TextView) rowView.findViewById(R.id.sync);

        id.setText(idPrePallet[position]);

        if(active[position].compareToIgnoreCase("0") == 0)
            activo.setText("No");
        else
            activo.setText("Si");

        if(sync[position].compareToIgnoreCase("0") == 0)
            synctv.setText("No");
        else
            synctv.setText("Si");

        return rowView;

    };


}
