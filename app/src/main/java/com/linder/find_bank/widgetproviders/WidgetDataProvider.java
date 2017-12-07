package com.linder.find_bank.widgetproviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.linder.find_bank.activities.FavoriteActivity;
import com.linder.find_bank.model.Agente;
import com.linder.find_bank.network.ApiService;
import com.linder.find_bank.network.ApiServiceGenerator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = "WidgetDataProvider";
    int id;
    List<String> mCollection = new ArrayList<>();
    Context mContext = null;

    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        initialize();
    }

    @Override
    public void onDataSetChanged() {
        initialize();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mCollection.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews view = new RemoteViews(mContext.getPackageName(),
                android.R.layout.simple_list_item_1);
        view.setTextViewText(android.R.id.text1, mCollection.get(position));
        view.setTextColor(android.R.id.text1, Color.BLACK);
        final Intent fillInIntent = new Intent();
        fillInIntent.setAction(MyWidgetProvider.ACTION_TOAST);
        final Bundle bundle = new Bundle();
        bundle.putString(MyWidgetProvider.EXTRA_STRING,
                mCollection.get(position));
        fillInIntent.putExtras(bundle);
        view.setOnClickFillInIntent(android.R.id.text1, fillInIntent);
        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    private void initialize() {
        try {
            ApiService service = ApiServiceGenerator.createService(ApiService.class);
            Call<List<Agente>> call = service.getAgentes();
            List<Agente> agentes = call.execute().body();
            Log.d(TAG, "eventos: " + agentes);
            for (Agente agente : agentes){
                String nombre = agente.getNombre();
                String s = agente.getSistema();
                String sp;
                if (s.equals("1")){
                    sp = " Tiene Sistema";
                }else if (s.equals("0")){
                    sp = " No tiene sistema";
                } else {
                    sp = "error";
                }
                id = agente.getId();
                Log.d(TAG, "nombre: " + nombre);
                mCollection.add(nombre + sp);
            }
        }catch(Exception e){
            Log.e(TAG, e.toString());
        }
    }

}
