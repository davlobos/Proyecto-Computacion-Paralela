package common;

import java.io.Serializable;

public class Juego implements Serializable {
	private int id;
	private String nombre;

    public Juego(int id, String nombre) {
    	this.id = id;
    	this.nombre = nombre;
        
    }

    public String getNombre() {
        return nombre;
    }

    public int getId() {
        return id;
    }
}
