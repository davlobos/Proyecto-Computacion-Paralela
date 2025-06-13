
package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import common.InterfazDeServer;

public class RunServerRespaldo2 {
    public static final int RMI_PORT = 1011;
    public static final String RMI_NAME = "server_respaldo_2"; 

    public static void main(String[] args) {
        try {
            InterfazDeServer server = new ServerImpl();
            Registry registry = LocateRegistry.createRegistry(RMI_PORT);
            registry.rebind(RMI_NAME, server);
            System.out.println("✅ Servidor 3 (Respaldo) arriba en el puerto  " + RMI_PORT);
        } catch (Exception e) {
            System.err.println("💥 Error al iniciar servidor de respaldo 3: " + e.getMessage());
            e.printStackTrace();
        }
    }
}