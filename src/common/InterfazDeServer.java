package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface InterfazDeServer extends Remote {
	ArrayList<Juego> obtenerJuegos() throws RemoteException;
    void agregarJuego(Juego juego) throws RemoteException;
    void eliminarJuego(String nombre) throws RemoteException;
    public Juego buscarJuego(String nombre) throws RemoteException;

}
