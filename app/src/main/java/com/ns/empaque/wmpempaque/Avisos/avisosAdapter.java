package com.ns.empaque.wmpempaque.Avisos;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ns.empaque.wmpempaque.R;

import java.util.ArrayList;

/**
 * Created by jcalderon on 25/08/2016.
 */
public class avisosAdapter extends BaseAdapter {


    private Activity nContext;
    private ArrayList<Avisos> avisos;
    private LayoutInflater layoutInflater;

    public avisosAdapter(Activity context, ArrayList<Avisos> avisos) {
        this.nContext = context;
        this.avisos = avisos;
        this.layoutInflater = nContext.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return avisos.size();
    }

    @Override
    public Object getItem(int position) {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.vw_avisos, null);
            holder = new ViewHolder();

            holder.txtAviso = (TextView) convertView.findViewById(R.id.txtAviso);
            holder.imgClose = (ImageView) convertView.findViewById(R.id.imgClose);
            holder.txtTitleAviso = (TextView) convertView.findViewById(R.id.titleAviso);
      //      holder.wvAvisos = (WebView) convertView.findViewById(R.id.webViewAviso);

            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        Avisos av = avisos.get(position);

        holder.txtTitleAviso.setText(av.getTitleSpanish());
        holder.txtAviso.setText(av.getDescriptionSpanish());

    //    holder.wvAvisos.loadData(av.getDescriptionSpanish(), "text/html", "utf-8");

        holder.imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avisos.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView txtAviso;
        TextView txtTitleAviso;
        ImageView imgClose;
    //    WebView wvAvisos;
    }

}
