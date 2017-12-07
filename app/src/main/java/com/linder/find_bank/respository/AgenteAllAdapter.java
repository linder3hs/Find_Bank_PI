package com.linder.find_bank.respository;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.linder.find_bank.R;
import com.linder.find_bank.activities.ShowAgenteActivity;
import com.linder.find_bank.activities.AgenteAllActivity;
import com.linder.find_bank.model.Agente;
import com.linder.find_bank.network.ApiService;
import com.linder.find_bank.network.ResponseMessage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by linderhassinger on 11/26/17.
 */

public class AgenteAllAdapter  extends RecyclerView.Adapter<AgenteAllAdapter.ViewHolder> {

    private static final String TAG = AgenteAdapter.class.getSimpleName();

    private List<Agente> agentes;

    private Activity activity2;

    public AgenteAllAdapter(AgenteAllActivity agenteAllActivity) {
        this.agentes = new ArrayList<>();
        this.activity2 = agenteAllActivity;


    }

    public void setAgentes(List<Agente> agentes) {
        this.agentes = agentes;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView fotoimage;
        public TextView nombreAgente, direccion, sistema;

        public ViewHolder(View itemView) {
            super(itemView);

            fotoimage = (ImageView) itemView.findViewById(R.id.foto_imageall);
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
    public void onBindViewHolder(AgenteAllAdapter.ViewHolder holder, final int position) {

        final Agente agente = this.agentes.get(position);

        if (agente.getSistema().equals("1")) {
            holder.sistema.setText(R.string.si_tiene_sistema);
        } else {
            holder.sistema.setText(R.string.no_tiene_sistema);
        }

        holder.nombreAgente.setText(agente.getNombre());
        holder.direccion.setText(agente.getDireccion());

        String url = ApiService.API_BASE_URL + "/images/" + agente.getImagen();
        Picasso.with(holder.itemView.getContext()).load(url).into(holder.fotoimage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity2, ShowAgenteActivity.class);
                intent.putExtra("ID", agente.getId());
                activity2.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.agentes.size();
    }

}
