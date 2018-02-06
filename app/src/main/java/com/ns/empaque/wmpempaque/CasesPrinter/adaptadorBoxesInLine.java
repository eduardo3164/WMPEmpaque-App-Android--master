package com.ns.empaque.wmpempaque.CasesPrinter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ns.empaque.wmpempaque.AsignarPrepallets.Linea;
import com.ns.empaque.wmpempaque.Desgrane.BoxesFolioInLine;
import com.ns.empaque.wmpempaque.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Héctor on 02/11/2016.
 */
public class adaptadorBoxesInLine extends BaseAdapter {

    public Activity nContext;
    public ArrayList<BoxesFolioInLine> bfl;
    private LayoutInflater layoutInflater;
    private DecimalFormat myFormatter;

    public adaptadorBoxesInLine(Activity context, ArrayList<BoxesFolioInLine> bfl){
        this.nContext = context;
        this.bfl = bfl;
        myFormatter = new DecimalFormat("0.00");
    }


    @Override
    public int getCount() {
        return bfl.size();
    }

    @Override
    public Object getItem(int position) {
        return bfl.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View v;
        layoutInflater = nContext.getLayoutInflater();
        v = layoutInflater.inflate(R.layout.vw_item_folios, null, true);


       // final TextView folio,lineName,plantName,cajasInLine,boxesInPallet,avblBoxes,EnterLineTime;

        LinearLayout lytItemFolio = (LinearLayout) v.findViewById(R.id.lytItemFolio);
        LinearLayout lytSeparador1 = (LinearLayout) v.findViewById(R.id.lytSeparador1);
        LinearLayout lytSeparador2 = (LinearLayout) v.findViewById(R.id.lytSeparador2);
        TextView folio = (TextView) v.findViewById(R.id.vFormaA);
        TextView packPlant =(TextView) v.findViewById(R.id.packPlant);
        TextView lineName = (TextView) v.findViewById(R.id.lineName);
        TextView sku =(TextView) v.findViewById(R.id.sku);
        TextView GreenHouse =(TextView) v.findViewById(R.id.gh);
        TextView Lbs =(TextView) v.findViewById(R.id.libras);
        TextView BoxInLine =(TextView) v.findViewById(R.id.BoxInLine);
        TextView dInLine = (TextView) v.findViewById(R.id.dInLine);

        BoxesFolioInLine b = bfl.get(position);
        //double librasDisponibles = b.getLbsXBox() * b.getBoxesAvailable();
        //double librasCasesGenerados = b.getCasesGenerados() * b.getLbsPorSKU();

        folio.setText(b.getvFolio()+" - "+b.getTotalBoxes()+" Cajas pesadas"+" - "+ myFormatter.format(b.getLbsXBox() * b.getTotalBoxes())+" Lbs");
        lineName.setText(b.getLineName());
        packPlant.setText(b.getFarmName());
        sku.setText(b.getSKU());
        GreenHouse.setText(b.getGH());
        Lbs.setText(myFormatter.format(b.getLbsXBox() * b.getBoxesAvailable())+"");
        BoxInLine.setText(b.getBoxesAvailable()+"");
        dInLine.setText(b.getFechaEnterLine()+"");

        if(!b.getEstado()){
            lytItemFolio.setBackgroundResource(R.drawable.esquinas_redondas_gris);
            lytSeparador1.setBackgroundColor(Color.parseColor("#333333"));
            lytSeparador2.setBackgroundColor(Color.parseColor("#333333"));
        }

        return v;
    }
}
