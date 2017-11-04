package com.exemple.eac3_2017s1;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * Created by BlueStorm on 28/10/2017.
 */

public class Adaptador extends RecyclerView.Adapter<Adaptador.ElMeuViewHolder> {
    private List<Media> lista;
    private Context context;
    private DBInterface db;

    //Creem el constructor
    public Adaptador(Context context) {
        this.context = context;
    }

    //Crea noves files (l'invoca el layout manager). Aquí fem referència al layout fila.xml
    @Override
    public Adaptador.ElMeuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fila, null);
        // create ViewHolder
        ElMeuViewHolder viewHolder = new ElMeuViewHolder(itemLayoutView);
        return viewHolder;
    }

    //Retorna la quantitat de les dades
    @Override
    public int getItemCount() {
        if (lista == null) return 0;
        else return lista.size();
    }

    //Carreguem els widgets amb les dades (l'invoca el layout manager)
    @Override
    public void onBindViewHolder(ElMeuViewHolder viewHolder, int position) {
        /* *
         * position conté la posició de l'element actual a la llista. També l'utilitzarem
         * com a índex per a recòrrer les dades
         * */
        //Drawable icon = Drawable.createFromPath()
        int photoOrVideo = lista.get(position).getPhotoOrVideo();
        if (photoOrVideo == 1) {
            viewHolder.imageView.setImageResource(R.drawable.ic_play_circle_outline_white_24dp);
        } else {
            viewHolder.imageView.setImageResource(R.drawable.ic_photo_white_24dp);
        }
        viewHolder.vTitle.setText(lista.get(position).getName());
    }

    public void setList(List lista) {
        this.lista = lista;
    }

    public void setDB(DBInterface db) {
        this.db = db;
    }

    private void removeFile(int posicion) {
        File file = new File(lista.get(posicion).getFile() + File.separator + lista.get(posicion).getName());
        file.delete();
    }

    //Definim el nostre ViewHolder, és a dir, un element de la llista en qüestió
    public class ElMeuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        protected ImageView imageView;
        protected TextView vTitle;

        public ElMeuViewHolder(View v) {
            super(v);
            //referenciem widgets al layout
            imageView = v.findViewById(R.id.imageView);
            vTitle = v.findViewById(R.id.title);
            //Establecemos listner
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, VisorGPS.class);
            intent.putExtra("media", lista.get(getAdapterPosition()));

            context.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            int posicion = getAdapterPosition();
            db.open();
            db.delete(lista.get(posicion).getId());
            db.close();
            removeFile(posicion);
            lista.remove(posicion);
            notifyItemRemoved(posicion);
            return true;
        }

    }
}

