
package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import common.InterfazDeServer;

public class RunServerRespaldo {
    public static final int RMI_PORT = 1010;
    public static final String RMI_NAME = "server_respaldo"; 

    public static void main(String[] args) {
        try {
            InterfazDeServer server = new ServerImpl();
            Registry registry = LocateRegistry.createRegistry(RMI_PORT);
            registry.bind(RMI_NAME, server);
            System.out.println("✅ Servidor de RESPALDO arriba en el puerto " + RMI_PORT);
        } catch (Exception e) {
            System.err.println("💥 Error al iniciar servidor de respaldo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}