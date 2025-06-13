
package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import common.InterfazDeServer;

public class RunServerRespaldo4 {
    public static final int RMI_PORT = 1013;
    public static final String RMI_NAME = "server_respaldo_4"; 

    public static void main(String[] args) {
        try {
        	System.setProperty("java.rmi.server.hostname", "127.0.0.1");
            InterfazDeServer server = new ServerImpl();
            Registry registry = LocateRegistry.createRegistry(RMI_PORT);
            registry.rebind(RMI_NAME, server);
            System.out.println("✅ Servidor 5 (Respaldo) arriba en el puerto " + RMI_PORT);
        } catch (Exception e) {
            System.err.println("💥 Error al iniciar servidor de respaldo 5: " + e.getMessage());
            e.printStackTrace();
        }
    }
}