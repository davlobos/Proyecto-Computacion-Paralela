
package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import common.InterfazDeServer;

public class RunServerRespaldo3 {
    public static final int RMI_PORT = 1012;
    public static final String RMI_NAME = "server_respaldo_3"; 

    public static void main(String[] args) {
        try {
        	System.setProperty("java.rmi.server.hostname", "127.0.0.1");
            InterfazDeServer server = new ServerImpl();
            Registry registry = LocateRegistry.createRegistry(RMI_PORT);
            registry.rebind(RMI_NAME, server);
            System.out.println("✅ Servidor 4 (Respaldo) arriba en el puerto " + RMI_PORT);
        } catch (Exception e) {
            System.err.println("💥 Error al iniciar servidor de respaldo 4: " + e.getMessage());
            e.printStackTrace();
        }
    }
}