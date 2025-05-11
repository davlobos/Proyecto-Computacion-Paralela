package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import common.InterfazDeServer;
import common.Persona;

public class ServerImpl implements InterfazDeServer {
    private ArrayList<Persona> BD_personas = new ArrayList<>();

    public ServerImpl() throws RemoteException {
        UnicastRemoteObject.exportObject(this, 0);
        crearBD();
    }

    private void crearBD() {
        BD_personas.add(new Persona("matias", 27));
        BD_personas.add(new Persona("maria eugenia", 31));
    }

    public ArrayList<Persona> getPersona() {
        return BD_personas;
    }

    public void agregarPersona(Persona p) throws RemoteException {
        BD_personas.add(p);
        System.out.println("Nueva persona agregada: " + p.getNombre());
    }
}
