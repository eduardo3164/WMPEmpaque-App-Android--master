package com.ns.empaque.wmpempaque.PrePallet;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.ns.empaque.wmpempaque.BaseDatos.BaseDatos;
import com.ns.empaque.wmpempaque.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jcalderon on 29/03/2016.
 */
public class casesAdapter extends BaseAdapter{

        private Activity nContext;
        private ArrayList<HashMap<String, String>> items;
        private boolean edit;

        public casesAdapter(Activity context, ArrayList<HashMap<String, String>> cases, boolean edit) {
            this.nContext = context;
            this.items = cases;
            this.edit = edit;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater layoutInflater = nContext.getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.viewtextview, null);

            TextView cases = (TextView) view.findViewById(R.id.txtEmbalaje);
            Button btnRemove = (Button) view.findViewById(R.id.btnRemove);

            final HashMap<String, String> item = items.get(position);
            cases.setText(item.get("case"));

            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    items.remove(position);
                    notifyDataSetChanged();

                    if (edit) {
                        BaseDatos bd = new BaseDatos(nContext);
                        bd.abrir();
                        String datos[][] = bd.cpdb.buscaCase(item.get("case"));

                        if(datos.length > 0)
                            bd.cpdb.eraseCase(item.get("case"));

                        bd.cerrar();
                    }
                }
            });

            return view;
        }


}
