package com.linder.find_bank.respository;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.linder.find_bank.R;
import com.linder.find_bank.activities.AgenteAllActivity;
import com.linder.find_bank.activities.HomeActivity;
import com.linder.find_bank.model.Agente;
import com.linder.find_bank.activities.FavoriteActivity;
import com.linder.find_bank.network.ApiService;
import com.linder.find_bank.network.ApiServiceGenerator;
import com.linder.find_bank.network.ResponseMessage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by linderhassinger on 10/30/17.
 */
public class AgenteAdapter extends RecyclerView.Adapter<AgenteAdapter.ViewHolder> {

    private static final String TAG = AgenteAdapter.class.getSimpleName();

    private List<Agente> agentes;
    private SharedPreferences sharedPreferences;

    public AgenteAdapter(FavoriteActivity favoriteActivity) {
        this.agentes = new ArrayList<>();
    }

    public void setAgentes(List<Agente> agentes) {
        this.agentes = agentes;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView fotoimage;
        public TextView nombreAgente, direccion, sistema;
        final LikeButton favorito;

        public ViewHolder(View itemView) {
            super(itemView);

            fotoimage = (ImageView) itemView.findViewById(R.id.foto_image);
            nombreAgente = (TextView) itemView.findViewById(R.id.nombre_text);
            direccion = (TextView) itemView.findViewById(R.id.direccionFavo);
            sistema = (TextView) itemView.findViewById(R.id.sistemaFavor);
            favorito = (LikeButton) itemView.findViewById(R.id.btn_favorite);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_agente, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AgenteAdapter.ViewHolder holder, final int position) {

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

        holder.favorito.setLiked(true);

        holder.favorito.setOnLikeListener(new OnLikeListener(){
            @Override
            public void liked(LikeButton likeButton) {

            }

            @Override
            public void unLiked(final LikeButton likeButton) {

                //SharedPreferences sharedPreferences1 = LikeButton.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(likeButton.getContext());

                final int user_id = sharedPreferences.getInt("user_id", 35);
                Log.d(TAG, "user_id: " + user_id);

                ApiService service = ApiServiceGenerator.createService(ApiService.class);
                Call<ResponseMessage> call;

                int agente_id = agente.getId();

                call = service.eliminarFavorito(user_id, agente_id);
                call.enqueue(new Callback<ResponseMessage>() {
                    @Override
                    public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                        try {
                            int statusCode = response.code();
                            Log.d(TAG, "HTTP STATUS CODE" + statusCode);
                            if (response.isSuccessful()) {
                                ResponseMessage responseMessage = response.body();
                                Log.d(TAG, "response message" + responseMessage);
                                //Toast.makeText(FavoriteActivity.this,  "Eliminado de favoritos", Toast.LENGTH_SHORT).show();

                                // Eliminar item del recyclerView y notificar cambios
                                agentes.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, agentes.size());

                            } else {
                                Log.e(TAG, "onError: " + response.errorBody().string());
                            }

                        } catch (Throwable t) {
                            try {
                                Log.e(TAG, "onThrowable: " + t.toString(), t);
                                Toast.makeText(likeButton.getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                            } catch (Throwable x) {
                                Toast.makeText(likeButton.getContext(), "Error", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseMessage> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.toString());
                        //Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) ;
            }

        });
    }

    @Override
    public int getItemCount() {
        return this.agentes.size();
    }

}
