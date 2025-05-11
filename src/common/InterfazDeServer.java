package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface InterfazDeServer extends Remote {
	ArrayList<Juego> getJuego() throws RemoteException;
	void agregarJuego(Juego p) throws RemoteException;;
	
	// funciones del video
	
	String getDataFromApi() throws RemoteException;
	Object[] getUF() throws RemoteException;
}
