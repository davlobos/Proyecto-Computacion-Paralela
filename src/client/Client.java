package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.ArrayList;
import common.InterfazDeServer;
import common.Juego;
import common.Pais;

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
                System.out.println("[3] Buscar juego por nombre");
                System.out.println("[4] Comparar precio de juego en otra región distinta a la local");
                System.out.println("[5] Comparar precio de juego en 10 regiones");
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
                    case 3:
                    	buscarJuego(sc);
                    	break;
                    case 4:
                    	compararPrecioEnRegion(sc);
                    	break;
                    case 5:
                    	compararPrecioEnRegiones(sc);
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
            ArrayList<Juego> games = server.obtenerJuegos(); // Llamamos al método obtenerJuegos()
            System.out.println("\n--- Juegos Registrados ---");
            if (games.isEmpty()) {
                System.out.println("No hay juegos registrados.");
            } else {
                for (Juego j : games) {
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
            Juego newGames = new Juego(nombre, 0);

            // Llamamos al método del servidor para agregar el juego
            server.agregarJuego(newGames);
            System.out.println("Juego añadido exitosamente.");
        } catch (Exception e) {
            System.err.println("Error al agregar juego: " + e.getMessage());
        }
    }
    
    //Metodo para buscar juego
    private void buscarJuego(Scanner sc) {
        try {
            System.out.print("Ingrese el nombre del juego a buscar: ");
            String nombre = sc.nextLine();

            Juego juego = server.buscarJuego(nombre);
            if (juego != null) {
                System.out.println("Juego encontrado: " + juego.getNombre() + " (ID: " + juego.getId() + ")");
            } else {
                System.out.println("No se encontró el juego: " + nombre);
            }
        } catch (Exception e) {
            System.err.println("Error al buscar juego: " + e.getMessage());
        }
    }
    
    
    //Metodo para buscar juego
    private void compararPrecioEnRegion(Scanner sc) {
        try {
            System.out.print("Ingrese el nombre del juego a comparar: ");
            String nombre = sc.nextLine();

            Juego juego = server.buscarJuego(nombre);
            if (juego == null) {
                System.out.println("No se encontró el juego: " + nombre);
                return;
            }
            
            System.out.print("Ingrese el nombre del país a realizar la comparativa: ");
            String nombre_pais = sc.nextLine();
            
            
            Pais pais = server.buscarPais(nombre_pais);
            
            
            double precioLocal = server.getDataFromApiSteam(juego.getId(), "cl");
            double precioComparativa = server.getDataFromApiSteam(juego.getId(), pais.getId());

            System.out.println("Precio Local: " + precioLocal);
            System.out.println("Precio en "+ pais.getNombre() + ": " + precioComparativa);            
            
            
            
        } catch (Exception e) {
            System.err.println("Error al buscar juego: " + e.getMessage());
        }
    }
    
    
    
    //Metodo para buscar juego
    private void compararPrecioEnRegiones(Scanner sc){
        try {
            System.out.print("Ingrese el nombre del juego a comparar: ");
            String nombre = sc.nextLine();

            Juego juego = server.buscarJuego(nombre);
            if (juego == null) {
                System.out.println("No se encontró el juego: " + nombre);
                return;
            }
            
            
            System.out.println("Comparativa");
            
            double precioLocal = server.getDataFromApiSteam(juego.getId(), "cl");
            double precio1 = server.getDataFromApiSteam(juego.getId(), "br");
            double precio2 = server.getDataFromApiSteam(juego.getId(), "ca");
            double precio3 = server.getDataFromApiSteam(juego.getId(), "es");
            double precio4 = server.getDataFromApiSteam(juego.getId(), "in");
            double precio5 = server.getDataFromApiSteam(juego.getId(), "cn");
            double precio6 = server.getDataFromApiSteam(juego.getId(), "mx");
            double precio7 = server.getDataFromApiSteam(juego.getId(), "tr");
            double precio8 = server.getDataFromApiSteam(juego.getId(), "au");
            double precio9 = server.getDataFromApiSteam(juego.getId(), "us");
            
            
            System.out.println("Precio Local: " + precioLocal);
            System.out.println("Precio en Brasil: " + precio1);
            System.out.println("Precio en Canada: " + precio2);
            System.out.println("Precio en España: " + precio3);
            System.out.println("Precio en Inglaterra: " + precio4);
            System.out.println("Precio en China: " + precio5);
            System.out.println("Precio en Mexico: " + precio6);
            System.out.println("Precio en Turquía: " + precio7);
            System.out.println("Precio en Australia: " + precio8);
            System.out.println("Precio en Estados Unidos : " + precio9);
            
        } catch (Exception e) {
            System.err.println("Error al buscar juego: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        Client cliente = new Client();
        cliente.startClient(); // Iniciar el cliente
    }
}


