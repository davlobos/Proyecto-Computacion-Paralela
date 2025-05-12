package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.ArrayList;
import common.InterfazDeServer;
import common.Persona;

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
                System.out.println("[1] Ver lista de personas");
                System.out.println("[2] Añadir persona nueva");
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
                        listarPersonas();
                        break;
                    case 2:
                        agregarPersona(sc);
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

    private void listarPersonas() {
        try {
            ArrayList<Persona> personas = server.getPersona();
            System.out.println("\n--- Personas Registradas ---");
            if (personas.isEmpty()) {
                System.out.println("No hay personas registradas.");
            } else {
                for (Persona p : personas) {
                    System.out.println("- " + p.getNombre() + " (" + p.getEdad() + " años)");
                }
            }
        } catch (Exception e) {
            System.err.println("💥 Error al obtener lista de personas: " + e.getMessage());
        }
    }

    private void agregarPersona(Scanner sc) {
        try {
            System.out.print("Ingrese nombre: ");
            String nombre = sc.nextLine();

            int edad = -1;
            boolean edadValida = false;

            while (!edadValida) {
                System.out.print("Ingrese edad: ");
                if (sc.hasNextInt()) {
                    edad = sc.nextInt();
                    sc.nextLine();
                    if (edad >= 0) {
                        edadValida = true;
                    } else {
                        System.out.println("La edad no puede ser negativa.");
                    }
                } else {
                    System.out.println("Debes ingresar un número válido para la edad.");
                    sc.nextLine(); 
                }
            }

            Persona nuevaPersona = new Persona(nombre, edad);
            server.agregarPersona(nuevaPersona);
            System.out.println("Persona añadida exitosamente.");
        } catch (Exception e) {
            System.err.println("Error al agregar persona: " + e.getMessage());
        }
    }
}
