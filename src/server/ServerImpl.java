package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import common.InterfazDeServer;
import common.Juego;

public class ServerImpl implements InterfazDeServer {
    private ArrayList<Juego> BD_juegos = new ArrayList<>();
    private Connection connection = null;

    public ServerImpl() throws RemoteException {
        conectarBD();
        UnicastRemoteObject.exportObject(this, 0);
    }
    
    
    @Override
    public String getDataFromApi(int id) {
        String output = null;
         
        try {
            // URL de la API REST, el listado de APIs públicas está en: 
            // https://github.com/juanbrujo/listado-apis-publicas-en-chile
            URL apiUrl = new URL("https://store.steampowered.com/api/appdetails?appids={id}&cc=cl&l=es");

            // Abre la conexión HTTP
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();

            // Configura la solicitud (método GET en este ejemplo)
            connection.setRequestMethod("GET");

            // Obtiene el código de respuesta
            int responseCode = connection.getResponseCode();

            // Procesa la respuesta si es exitosa
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Lee la respuesta del servidor
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                // Cierra la conexión y muestra la respuesta
                in.close();
                output = response.toString();
            } else {
                System.out.println("Error al conectar a la API. Código de respuesta: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Como resultado tenemos un String output que contiene el JSON de la respuesta de la API
        return output;
    }
    


    public void conectarBD() {
        try {
            if (connection == null || connection.isClosed()) {
                String url = "jdbc:mysql://localhost:3306/project_db";
                String username = "root";
                String password_BD = "";
                connection = DriverManager.getConnection(url, username, password_BD);
                System.out.println("Conexión con la BD exitosa!");
            }

            // Cargar los juegos desde la BD
            Statement query = connection.createStatement();
            String sql = "SELECT * FROM games";
            ResultSet resultados = query.executeQuery(sql);
            BD_juegos.clear();

            while (resultados.next()) {
                int id = resultados.getInt("id_api_game");
                String nombre = resultados.getString("nombre");
                BD_juegos.add(new Juego(nombre, id));
                System.out.println("cargado: " + id + "-" + nombre);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("No se pudo conectar a la BD");
        }
    }

    public void agregarJuego(Juego j) throws RemoteException {
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO juegos (nombre, id_api_game) VALUES (?)";
            ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, j.getNombre());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int idGenerado = rs.getInt(1);
                j = new Juego(j.getNombre(), idGenerado);
                BD_juegos.add(j);
                System.out.println("Juego agregado: " + idGenerado + " - " + j.getNombre());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al agregar juego.");
        }
    }

    @Override
    public ArrayList<Juego> obtenerJuegos() throws RemoteException {
        return BD_juegos;
    }

    @Override
    public void eliminarJuego(String nombre) throws RemoteException {
        Juego juegoAEliminar = null;
        for (Juego j : BD_juegos) {
            if (j.getNombre().equals(nombre)) {
                juegoAEliminar = j;
                break;
            }
        }  

        if (juegoAEliminar != null) {
            BD_juegos.remove(juegoAEliminar);

            PreparedStatement ps = null;
            try {
                String sql = "DELETE FROM games WHERE nombre = ?";
                ps = connection.prepareStatement(sql);
                ps.setString(1, nombre);
                ps.executeUpdate();

                System.out.println("Juego eliminado: " + nombre);
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error al eliminar juego.");
            }
        } else {
            System.out.println("Juego no encontrado: " + nombre);
        }
    }
    
    @Override
    public Juego buscarJuego(String nombre) throws RemoteException {
        try {
            String sql = "SELECT * FROM games WHERE nombre = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id_api_game");
                return new Juego(nombre, id);
            } else {
                System.out.println("Juego no encontrado en la BD: " + nombre);
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al buscar el juego.");
            return null;
        }
    }


    public void cerrarConexion() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al cerrar la conexión.");
        }
    }
}
