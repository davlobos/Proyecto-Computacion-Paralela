package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface InterfazDeServer extends Remote {
	ArrayList<Persona> getPersona() throws RemoteException;
	void agregarPersona(Persona p) throws RemoteException;;
}
