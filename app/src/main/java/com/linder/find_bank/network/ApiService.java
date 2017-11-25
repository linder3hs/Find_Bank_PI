package com.linder.find_bank.network;

import com.linder.find_bank.model.Agente;
import com.linder.find_bank.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by ROQUE on 22/10/2017.
 */

public interface ApiService {

    String API_BASE_URL = "https://findbank-api-roque363.c9users.io/";

    @GET("api/v1/usuarios")
    Call<List<User>> getUsuarios();

    @FormUrlEncoded
    @POST("api/v1/login")
    Call<ResponseMessage> loginUsuario(
            @Field("email") String email,
            @Field("password") String password,
            @Field("tipo") String tipo);

    @FormUrlEncoded
    @POST("api/v1/usuarios")
    Call<ResponseMessage> registrarUsuario(
            @Field("nombre") String nombre,
            @Field("email") String email,
            @Field("password") String password,
            @Field("tipo") String tipo);

    @GET("api/v1/usuarios/{email}")
    Call<User> showUsuario(@Path("email") String email);

    @GET("api/v1/agentes")
    Call<List<Agente>> getAgentes();

}
