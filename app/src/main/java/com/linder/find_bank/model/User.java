package com.linder.find_bank.model;

/**
 * Created by linderhassinger on 10/13/17.
 */

public class User {
    private Integer id;
    private String nombre;
    private String email;
    private String password;
    private String imagen;

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

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", imagen='" + imagen + '\'' +
                '}';
    }

}
