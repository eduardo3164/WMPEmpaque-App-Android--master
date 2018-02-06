package com.ns.empaque.wmpempaque.PopUp;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.ns.empaque.wmpempaque.R;

/**
 * Created by jcalderon on 31/05/2016.
 */
public class PopUp {

    public static int POPUP_INCORRECT = 0;
    public static int POPUP_OK = 1;
    public static int POPUP_INFORMATION = 2;
    private int ACTION;
    private String msg, title="";
    private Activity nContext;

    public PopUp(Activity nContext, String mensage, int ACTION){
        this.msg = mensage;
        this.ACTION = ACTION;
        this.nContext = nContext;
    }

    public PopUp(Activity nContext, String mensage, String title, int ACTION){
        this.msg = mensage;
        this.ACTION = ACTION;
        this.nContext = nContext;
        this.title = title;
    }

    public PopUp showPopUp(){

        showDialog();

        return null;
    }

    private void showDialog() {
        //seleccionamos vista
        LayoutInflater inflater = nContext.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.viewforpopup, null);

        ImageView Img = (ImageView) dialoglayout.findViewById(R.id.img1);
        TextView tvAlert = (TextView) dialoglayout.findViewById(R.id.textAlert);
        TextView txtTitle = (TextView) dialoglayout.findViewById(R.id.textTitle);

        if(ACTION == POPUP_OK) {
            Img.setImageResource(R.drawable.oktransp);
            title+=" - Informacion";
        }

        if(ACTION == POPUP_INCORRECT) {
            Img.setImageResource(R.drawable.incorrect);
            title+=" - Error";
        }

        if(ACTION == POPUP_INFORMATION) {
            Img.setImageResource(R.drawable.alerton);
            title+=" - Informacion";
        }

        tvAlert.setText(msg);
        txtTitle.setText(title);

        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(nContext);
        alertDialog2
                .setView(dialoglayout)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        //alertDialog2.setIcon(R.drawable.naturesweet);
        // alertDialog2.setTitle();
        alertDialog2.setCancelable(false);
        final AlertDialog ad2 = alertDialog2.create();
        ad2.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ad2.show();

        ad2.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        ad2.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

}
