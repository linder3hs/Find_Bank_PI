package com.linder.find_bank.model;

/**
 * Created by linderhassinger on 10/30/17.
 */

public class Agente {

    private String nombre;
    private String direccion;
    private float lat;
    private float lng;
    private String tipo;
    private char sistema;
   // private boolean sistema;
    private int seguridad;
    private String horario;
    private String descripcion;


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

   // public boolean isSistema() {
    //    return sistema;
    //}

   // public void setSistema(boolean sistema) {
    //    this.sistema = sistema;
   // }

    public int getSeguridad() {
        return seguridad;
    }

    public void setSeguridad(int seguridad) {
        this.seguridad = seguridad;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Agente{" +
                "nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", tipo='" + tipo + '\'' +
               // ", sistema=" + sistema +
                ", seguridad=" + seguridad +
                ", horario='" + horario + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}