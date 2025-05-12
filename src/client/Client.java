package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.ArrayList;
import common.InterfazDeServer;
import common.Juego;

public class Client {

    private InterfazDeServer server;

    public void startClient() {
        try {
            // Conexión con el servidor RMI
            Registry registry = LocateRegistry.getRegistry("localhost", 1009);
            server = (InterfazDeServer) registry.lookup("server");

            Scanner sc = new Scanner(System.in);
            int opcion = -1;

            // Menú de opciones
            while (opcion != 0) {
                System.out.println("\n======= CLIENTE RMI =======");
                System.out.println("[1] Ver lista de juegos");
                System.out.println("[2] Añadir nuevo juego");
                System.out.println("[0] Finalizar programa");
                System.out.print("Ingrese una opción: ");

                if (sc.hasNextInt()) {
                    opcion = sc.nextInt();
                    sc.nextLine(); 
                } else {
                    System.out.println("Debes ingresar un número válido.");
                    sc.nextLine(); 
                    continue;
                }

                // Ejecutar según la opción seleccionada
                switch (opcion) {
                    case 1:
                        listarJuegos();
                        break;
                    case 2:
                        agregarJuego(sc);
                        break;
                    case 0:
                        System.out.println("Cerrando cliente. ¡Hasta luego!");
                        break;
                    default:
                        System.out.println("Opción no reconocida. Intenta nuevamente.");
                }
            }

            sc.close();
        } catch (Exception e) {
            System.err.println("💥 Error al iniciar cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para listar los juegos registrados
    private void listarJuegos() {
        try {
            ArrayList<Juego> juegos = server.obtenerJuegos(); // Llamamos al método obtenerJuegos()
            System.out.println("\n--- Juegos Registrados ---");
            if (juegos.isEmpty()) {
                System.out.println("No hay juegos registrados.");
            } else {
                for (Juego j : juegos) {
                    System.out.println("- " + j.getNombre() + " (ID: " + j.getId() + ")");
                }
            }
        } catch (Exception e) {
            System.err.println("💥 Error al obtener lista de juegos: " + e.getMessage());
        }
    }

    // Método para agregar un nuevo juego
    private void agregarJuego(Scanner sc) {
        try {
            System.out.print("Ingrese nombre del juego: ");
            String nombre = sc.nextLine();

            // Creamos un objeto Juego con el nombre ingresado
            Juego nuevoJuego = new Juego(nombre, 0);

            // Llamamos al método del servidor para agregar el juego
            server.agregarJuego(nuevoJuego);
            System.out.println("Juego añadido exitosamente.");
        } catch (Exception e) {
            System.err.println("Error al agregar juego: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Client cliente = new Client();
        cliente.startClient(); // Iniciar el cliente
    }
}
