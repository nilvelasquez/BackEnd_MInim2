package edu.upc.dsa.models;

public class Mensaje {
    String mensaje;
    public Mensaje(String mensaje){
        setMensaje(mensaje);
    }
    public String getMensaje() {
        return mensaje;
    }
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
