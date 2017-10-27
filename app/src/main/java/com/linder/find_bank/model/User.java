package com.linder.find_bank.model;

/**
 * Created by linderhassinger on 10/13/17.
 */

public class User {
    private Integer id;
    private String nombre;
    private String email;
    private String password;

    public Integer getCod() {
        return id;
    }

    public void setCod(Integer cod) {
        this.id = cod;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return email;
    }

    public void setCorreo(String correo) {
        this.email = correo;
    }

    public String getPass() {
        return password;
    }

    public void setPass(String pass) {
        this.password = pass;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "cod=" + id +
                ", nombre='" + nombre + '\'' +
                ", correo='" + email + '\'' +
                ", pass='" + password + '\'' +
                '}';
    }
}
