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
            Registry registry = LocateRegistry.getRegistry("localhost", 1009);
            server = (InterfazDeServer) registry.lookup("server");

            Scanner sc = new Scanner(System.in);
            int opcion = -1;

            
            
            while (opcion != 0) {
                System.out.println("\n======= CLIENTE RMI =======");
                System.out.println("[1] Ver lista de juegos");
                System.out.println("[2] Añadir nuevo juego");
                System.out.println("[3] Buscar juego por nombre");
                System.out.println("[4] Comparar precio de juego en otro país distinto al local");
                System.out.println("[5] Comparar precio de juego en 10 países");
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

            
            server.cerrarConexion();
            sc.close();
        } catch (Exception e) {
            System.err.println("💥 Error al iniciar cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
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

    private void agregarJuego(Scanner sc) {
        try {
            System.out.print("Ingrese nombre del juego: ");
            String nombre = sc.nextLine();

            System.out.print("Ingrese id del juego: ");
            int id = Integer.parseInt(sc.nextLine());


            Juego newGame = new Juego(nombre, id);

            newGame = server.agregarJuego(newGame);
            
            if (newGame != null) {
                System.out.println("✅ Juego añadido exitosamente con ID: " + newGame.getId());
            } else {
                System.out.println("⚠ El juego no pudo ser añadido.");
            }
        } catch (Exception e) {
            System.err.println("Error al agregar juego: " + e.getMessage() + ". ID NO VÁLIDA.");
        }
    }
    
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
            
            
            double precioLocal = server.getPriceFromApiSteam(juego.getId(), "cl");
            double precioComparativa = server.getPriceFromApiSteam(juego.getId(), pais.getId());

            System.out.println("Precio Local (Chile): $" + precioLocal + " USD");
            System.out.println("Precio en " + pais.getNombre() + ": $" + precioComparativa + " USD");            
            
            
            
        } catch (Exception e) {
            System.err.println("Error al buscar juego: " + e.getMessage());
        }
    }
        

    private void compararPrecioEnRegiones(Scanner sc){
        try {
            System.out.print("Ingrese el nombre del juego a comparar: ");
            String nombre = sc.nextLine();

            Juego juego = server.buscarJuego(nombre);
            if (juego == null) {
                System.out.println("No se encontró el juego: " + nombre);
                return;
            }
            
           
            
            double precioLocal = server.getPriceFromApiSteam(juego.getId(), "cl");
            double precio1 = server.getPriceFromApiSteam(juego.getId(), "br");
            double precio2 = server.getPriceFromApiSteam(juego.getId(), "ca");
            double precio3 = server.getPriceFromApiSteam(juego.getId(), "es");
            double precio4 = server.getPriceFromApiSteam(juego.getId(), "in");
            double precio5 = server.getPriceFromApiSteam(juego.getId(), "cn");
            double precio6 = server.getPriceFromApiSteam(juego.getId(), "mx");
            double precio7 = server.getPriceFromApiSteam(juego.getId(), "tr");
            double precio8 = server.getPriceFromApiSteam(juego.getId(), "au");
            double precio9 = server.getPriceFromApiSteam(juego.getId(), "us");
            
            
            
            System.out.println("Comparativa de Precios del juego: " + juego.getNombre());
            System.out.println("Precio Local (Chile): $" + precioLocal + " USD");
            System.out.println("Precio en Brasil: $" + precio1 + " USD");
            System.out.println("Precio en Canada: $" + precio2 + " USD");
            System.out.println("Precio en España: $" + precio3 + " USD");
            System.out.println("Precio en Inglaterra: $" + precio4 + " USD");
            System.out.println("Precio en China: $" + precio5 + " USD");
            System.out.println("Precio en Mexico: $" + precio6 + " USD");
            System.out.println("Precio en Turquía: $" + precio7 + " USD");
            System.out.println("Precio en Australia: $" + precio8 + " USD");
            System.out.println("Precio en Estados Unidos $: " + precio9 + " USD");
            
        } catch (Exception e) {
            System.err.println("Error al buscar juego: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        Client cliente = new Client();
        cliente.startClient();
    }
}


