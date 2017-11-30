package com.linder.find_bank.network;

import com.linder.find_bank.model.Agente;
import com.linder.find_bank.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by ROQUE on 22/10/2017.
 */

public interface ApiService {

    String API_BASE_URL = "https://findbank-api-roque363.c9users.io/";

    // Listar usuarios
    @GET("api/v1/usuarios")
    Call<List<User>> getUsuarios();

    // Login de usuario
    @FormUrlEncoded
    @POST("api/v1/login")
    Call<ResponseMessage> loginUsuario(
            @Field("email") String email,
            @Field("password") String password,
            @Field("tipo") String tipo);

    // Registrar usuario
    @FormUrlEncoded
    @POST("api/v1/usuarios")
    Call<ResponseMessage> registrarUsuario(
            @Field("nombre") String nombre,
            @Field("email") String email,
            @Field("password") String password,
            @Field("tipo") String tipo);

    // Mostrar datos del usuario
    @GET("api/v1/usuarios/{email}")
    Call<User> showUsuario(
            @Path("email") String email);

    // Editar datos del usuario
    @FormUrlEncoded
    @PUT("api/v1/usuarios/{id}")
    Call<ResponseMessage> updateUsuario(
            @Path("id") int id,
            @Field("nombre") String nombre,
            @Field("email") String email);

    // Mostrar datos del Agente
    @GET("api/v1/agentes/{id}")
    Call<Agente> showAgente(
            @Path("id") int id);

    // Editar Agente
    @FormUrlEncoded
    @PUT("api/v1/agentes/{id}")
    Call<ResponseMessage> updateAgente(
            @Path("id") int id,
            @Field("sistema") String sistema,
            @Field("seguridad") String seguridad);

    // Lista de agentes
    @GET("api/v1/agentes")
    Call<List<Agente>> getAgentes();

    // Validacion de favoritos
    @FormUrlEncoded
    @POST("api/v1/favoritos/validacion")
    Call<ResponseMessage> validarFavorito(
            @Field("user_id") int user_id,
            @Field("agente_id") int agente_id);

    // Mostrar agentes favoritos de un usuario
    @GET("api/v1/favoritos/{id}")
    Call<List<Agente>> showFavoritos(
            @Path("id") int id);

    // Guardar favorito
    @FormUrlEncoded
    @POST("api/v1/favoritos")
    Call<ResponseMessage> registrarFavorito(
            @Field("user_id") int user_id,
            @Field("agente_id") int agente_id);

}
