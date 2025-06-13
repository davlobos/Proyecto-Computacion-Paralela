package client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.ArrayList;
import common.InterfazDeServer;
import common.Juego;
import common.Pais;

public class Client {

    private InterfazDeServer server;
    private final String host = "localhost";

    // Configuración para los 5 servidores
    private final String[] SERVER_NAMES = {
        "server_principal", "server_respaldo_1", "server_respaldo_2", "server_respaldo_3", "server_respaldo_4"
    };
    private final int[] SERVER_PORTS = {
        1009, 1010, 1011, 1012, 1013
    };
    
    private int currentServerIndex = -1;
    private volatile boolean running = true;

    public Client() {
        buscarServidorActivo(); 
        startHeartbeat();
    }
    
    private void buscarServidorActivo() {
        System.out.println("Buscando un servidor activo en la red...");
        for (int i = 0; i < SERVER_NAMES.length; i++) {
            System.out.println("Intentando conectar con " + SERVER_NAMES[i] + "...");
            this.server = establecerConexion(host, SERVER_PORTS[i], SERVER_NAMES[i]);
            if (this.server != null) {
                this.currentServerIndex = i;
                System.out.println("✅ Conexión establecida con: " + SERVER_NAMES[i]);
                return; // Sale del método en cuanto encuentra un servidor
            }
        }
        
        // Si el bucle termina y no encontró ningún servidor
        System.err.println("💥 No se encontró ningún servidor activo en la red.");
        terminarEjecucion();
    }
    
    

    private void startHeartbeat() {
        new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(3000);
                    if (server != null) {
                        server.heartbeat();
                    }
                } catch (RemoteException e) {
                    System.err.println("\n⚠️ Heartbeat fallido con " + SERVER_NAMES[currentServerIndex] + ". Iniciando protocolo de failover.");
                    this.server = null; // Marca la conexión como perdida
                    buscarServidorActivoEnFailover();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }



    private void buscarServidorActivoEnFailover() {
        int intentos = 0;
        while (intentos < SERVER_NAMES.length) {
            currentServerIndex = (currentServerIndex + 1) % SERVER_NAMES.length;
            System.out.println("Intentando reconectar con el siguiente en la lista: " + SERVER_NAMES[currentServerIndex] + "...");
            
            this.server = establecerConexion(host, SERVER_PORTS[currentServerIndex], SERVER_NAMES[currentServerIndex]);
            
            if (this.server != null) {
                System.out.println("✅ Conexión de respaldo establecida con: " + SERVER_NAMES[currentServerIndex]);
                System.out.println("--> Presiona Enter para refrescar el menú <--");
                return; // Sale del bucle y del método si encuentra un servidor
            }
            intentos++;
        }
        
        // Si el bucle termina, es que todos los servidores han fallado.
        System.err.println("💥 Todos los servidores están caídos.");
        terminarEjecucion();
    }
    
    private InterfazDeServer establecerConexion(String host, int port, String bindingName) {
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            return (InterfazDeServer) registry.lookup(bindingName);
        } catch (Exception e) {
            // ESTAS LÍNEAS NOS DIRÁN EL ERROR EXACTO
            System.err.println("Falló el lookup a '" + bindingName + "' en el puerto " + port + ".");
            System.err.println("Error específico: " + e.getMessage());
            // Descomenta la siguiente línea para ver la traza completa del error si es necesario
            // e.printStackTrace(); 
            return null;
        }
    }

    private void terminarEjecucion() {
        System.err.println("Terminando ejecución por falta de servidores disponibles.");
        this.running = false;
        System.exit(1);
    }
    
    public void startClient() {
        try (Scanner sc = new Scanner(System.in)) {
            int opcion = -1;
            while (running && opcion != 0) {
                String serverState = (server != null) ? SERVER_NAMES[currentServerIndex] : "Ninguno";
                System.out.println("\n======= CLIENTE RMI (" + serverState + ") =======");
                System.out.println("[1] Ver lista de juegos");
                System.out.println("[2] Añadir nuevo juego");
                System.out.println("[3] Buscar juego por nombre");
                System.out.println("[4] Comparar precio de juego en otro país");
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
                    case 1: listarJuegos(); break;
                    case 2: agregarJuego(sc); break;
                    case 3: buscarJuego(sc); break;
                    case 4: compararPrecioEnRegion(sc); break;
                    case 5: compararPrecioEnRegiones(sc); break;
                    case 0:
                        System.out.println("Cerrando cliente.");
                        running = false;
                        break;
                    default: System.out.println("Opción no reconocida."); break;
                }
            }
        }
    }

    private void listarJuegos() {
        if (server == null) {
            System.err.println("No hay conexión a ningún servidor. Esperando reconexión automática...");
            return;
        }
        try {
            ArrayList<Juego> games = server.obtenerJuegos();
            System.out.println("\n--- Juegos Registrados ---");
            if (games.isEmpty()) {
                System.out.println("No hay juegos registrados.");
            } else {
                int cont = 0;
                for (Juego j : games) {
                    String entrada = String.format(" ● %s (ID: %d)", j.getNombre(), j.getId());
                    System.out.printf(" || %-70s", entrada);
                    if (++cont % 2 == 0) System.out.println();
                }
                if (cont % 2 != 0) System.out.println();
            }
        } catch (RemoteException e) {
            System.err.println("Error de conexión durante la operación. El heartbeat intentará reconectar.");
            server = null;
        } catch (Exception e) {
            System.err.println("Error en la operación de listar juegos: " + e.getMessage());
        }
    }
    
    private void agregarJuego(Scanner sc) {
        if (server == null) {
            System.err.println("No hay conexión a ningún servidor. No se puede agregar juego.");
            return;
        }
        try {
            System.out.print("Ingrese nombre del juego: ");
            String nombre = sc.nextLine();
            System.out.print("Ingrese id del juego: ");
            int id = Integer.parseInt(sc.nextLine());
            Juego newGame = new Juego(nombre, id);
            
            Juego addedGame = server.agregarJuego(newGame);
            if (addedGame != null) {
                System.out.println("✅ Juego añadido exitosamente con ID: " + addedGame.getId());
            } else {
                System.out.println("⚠ El juego no pudo ser añadido.");
            }
        } catch (RemoteException e) {
            System.err.println("Error de conexión durante la operación. El heartbeat intentará reconectar.");
            server = null;
        } catch (NumberFormatException e) {
            System.err.println("Error: El ID debe ser un número válido.");
        } catch (Exception e) {
            System.err.println("Error en la operación de agregar juego: " + e.getMessage());
        }
    }
    
    private void buscarJuego(Scanner sc) {
        if (server == null) {
            System.err.println("No hay conexión a ningún servidor.");
            return;
        }
        try {
            System.out.print("Ingrese el nombre del juego a buscar: ");
            String nombre = sc.nextLine();
            Juego juego = server.buscarJuego(nombre);
            if (juego != null) {
                System.out.printf("\n🎮 Juego encontrado: %s (ID: %d)%n\n", juego.getNombre(), juego.getId());
            } else {
                System.out.println("No se encontró el juego: " + nombre);
            }
        } catch (RemoteException e) {
            System.err.println("Error de conexión durante la operación. El heartbeat intentará reconectar.");
            server = null;
        } catch (Exception e) {
            System.err.println("Error en la operación de buscar juego: " + e.getMessage());
        }
    }
    
    private void compararPrecioEnRegion(Scanner sc) {
        if (server == null) {
            System.err.println("No hay conexión a ningún servidor.");
            return;
        }
        try {
            System.out.print("Ingrese el nombre del juego a comparar: ");
            String nombre = sc.nextLine();
            Juego juego = server.buscarJuego(nombre);
            if (juego == null) {
                System.out.println("No se encontró el juego: " + nombre);
                return;
            }
            
            System.out.print("Ingrese el nombre del país para la comparativa: ");
            String nombre_pais = sc.nextLine();
            Pais pais = server.buscarPais(nombre_pais);
            if (pais == null) {
                System.out.println("No se encontró el país: " + nombre_pais);
                return;
            }

            double precioLocal = server.getPriceFromApiSteam(juego.getId(), "cl");
            double precioComparativa = server.getPriceFromApiSteam(juego.getId(), pais.getId());
            
            String texto1 = "💰 Precio Local: $" + precioLocal + " USD";
            String texto2 = "🌍 Precio en " + pais.getNombre() + ": $" + precioComparativa + " USD";

            int maxAncho = Math.max(texto1.length(), texto2.length());
            texto1 = String.format("%-" + maxAncho + "s", texto1);
            texto2 = String.format("%-" + maxAncho + "s", texto2);

            System.out.println("\n💱 Comparativa en USD de precios entre el \nPrecio Local (Chile) y " + pais.getNombre() + ":");
            System.out.println("|| " + texto1 + " ||");
            System.out.println("|| " + texto2 + " ||\n");

        } catch (RemoteException e) {
            System.err.println("Error de conexión durante la operación. El heartbeat intentará reconectar.");
            server = null;
        } catch (Exception e) {
            System.err.println("Error en la operación de comparar precios: " + e.getMessage());
        }
    }

    private void compararPrecioEnRegiones(Scanner sc) {
        if (server == null) {
            System.err.println("No hay conexión a ningún servidor.");
            return;
        }
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
            
            System.out.println("\n 🌍 Comparativa en USD de Precios del juego: " + juego.getNombre() + ":");
            String[] lineas = {
                "💰 Precio Local (Chile): $" + precioLocal + " USD",
                "💰 Precio en Brasil: $" + precio1 + " USD",
                "💰 Precio en Canada: $" + precio2 + " USD",
                "💰 Precio en España: $" + precio3 + " USD",
                "💰 Precio en Inglaterra: $" + precio4 + " USD",
                "💰 Precio en China: $" + precio5 + " USD",
                "💰 Precio en Mexico: $" + precio6 + " USD",
                "💰 Precio en Turquía: $" + precio7 + " USD",
                "💰 Precio en Australia: $" + precio8 + " USD",
                "💰 Precio en Estados Unidos $: " + precio9 + " USD"
            };

            int maxLength = 0;
            for (String linea : lineas) {
                maxLength = Math.max(maxLength, linea.length());
            }

            for (String linea : lineas) {
                System.out.println("|| " + String.format("%-" + maxLength + "s", linea) + " ||");
            }
        } catch (RemoteException e) {
            System.err.println("Error de conexión durante la operación. El heartbeat intentará reconectar.");
            server = null;
        } catch (Exception e) {
            System.err.println("Error al comparar precios en múltiples regiones: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Client cliente = new Client();
        cliente.startClient();
    }
}