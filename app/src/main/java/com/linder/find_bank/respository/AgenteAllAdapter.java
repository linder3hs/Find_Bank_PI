package com.linder.find_bank.respository;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.linder.find_bank.R;
import com.linder.find_bank.activities.AgenteAllActivity;
import com.linder.find_bank.activities.FavoriteActivity;
import com.linder.find_bank.model.Agente;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linderhassinger on 11/26/17.
 */

public class AgenteAllAdapter  extends RecyclerView.Adapter<AgenteAllAdapter.ViewHolder> {

    private List<Agente> agentes;

    public AgenteAllAdapter(AgenteAllActivity agenteAllActivity) {
        this.agentes = new ArrayList<>();

    }

    public void setAgentes(List<Agente> agentes) {
        this.agentes = agentes;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView fotoimage;
        public TextView nombreAgente, direccion, sistema;

        public ViewHolder(View itemView) {
            super(itemView);

            //fotoimage = (ImageView) itemView.findViewById(R.id.foto_image);
            nombreAgente = (TextView) itemView.findViewById(R.id.nombre_textall);
            direccion = (TextView) itemView.findViewById(R.id.direccionFavoall);
            sistema = (TextView) itemView.findViewById(R.id.sistemaFavorall);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_agente, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Agente agente = this.agentes.get(position);
        String siste;
        if (agente.getSistema().equals("1")) {
            siste = "Si tiene sistema";
        } else {
            siste = "No tienes sistema";
        }
        holder.nombreAgente.setText(agente.getNombre());
        holder.direccion.setText(agente.getDireccion());
        holder.sistema.setText(siste);

    }

    @Override
    public int getItemCount() {
        return this.agentes.size();
    }


}
