package server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import common.InterfazDeServer;

public class RunServer {
    public static final int RMI_PORT = 1009; 
    public static final String RMI_NAME = "server_principal"; 
	
    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        try {
        	System.setProperty("java.rmi.server.hostname", "127.0.0.1");
            InterfazDeServer server = new ServerImpl();
            Registry registry = LocateRegistry.createRegistry(RMI_PORT);
            registry.rebind(RMI_NAME, server);
            System.out.println("✅ Servidor 1 (Principal) arriba en el puerto  " + RMI_PORT);
        } catch (Exception e) {
            System.err.println("Error al iniciar servidor principal: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
