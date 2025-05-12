package server;

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

    public ServerImpl() throws RemoteException {
    	conectarBD();
        UnicastRemoteObject.exportObject(this, 0);
        crearBD();
    }

    private void crearBD() {
        //ACÁ EL EDU PONE COMO LO HIZO Y ADAPTA EL RESTO :)
    }
    
//del video del profe
    
    public void conectarBD() {
    	
    	Connection connection = null;
    	Statement query = null;
    	ResultSet resultados = (null);
    		
    	try {
        	String url = "jdbc:mysql://localhost:3306/conversion_moneda_steam";
        	String username = "root";
        	String password_BD = "";
        	
        	connection = DriverManager.getConnection(url, username, password_BD);
        	
        	//TODO Metodos con la BD
        	query = connection.createStatement(0, 0);
    		String sql = "Select * FROM juegos";
    		//INSERT para agregar datos a la BD, PreparedStatement
    		//DELETE
    		//UPDATE
    		
    		resultados = query.executeQuery(sql);
    		BD_juegos.clear();
    		
    		while (resultados.next()) {
    			int id = resultados.getInt("id");
    			String nombre = resultados.getString("nombre");
    			BD_juegos.add(new Juego(nombre,id));
    			
    			System.out.println("cargado: " + id + "-" + nombre);
    		}
        	
        	System.out.println("Conexión con la BD exitosa!");
        	connection.close();   	
        	
    	}catch(SQLException e){
    		e.printStackTrace();
    		System.out.println("No se pudo conectar a la BD");
    	}
    }

    public void agregarJuego(Juego j) throws RemoteException {
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            String url = "jdbc:mysql://localhost:3306/conversion_moneda_steam";
            String username = "root";
            String password_BD = "";

            connection = DriverManager.getConnection(url, username, password_BD);

            String sql = "INSERT INTO juegos (nombre) VALUES (?)";
            ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, j.getNombre());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int idGenerado = rs.getInt(1);
                j = new Juego(j.getNombre(),idGenerado);
                BD_juegos.add(j);
                System.out.println("Juego agregado: " + idGenerado + " - " + j.getNombre());
            }

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al agregar juego.");
        }
    }

	@Override
	public ArrayList<Juego> obtenerJuegos() throws RemoteException {
		// TODO Auto-generated method stub
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
	        // Eliminar el juego de la lista
	        BD_juegos.remove(juegoAEliminar);

	        // Eliminar también de la base de datos
	        Connection connection = null;
	        PreparedStatement ps = null;
	        try {
	            String url = "jdbc:mysql://localhost:3306/conversion_moneda_steam";
	            String username = "root";
	            String password_BD = "";

	            connection = DriverManager.getConnection(url, username, password_BD);

	            String sql = "DELETE FROM juegos WHERE nombre = ?";
	            ps = connection.prepareStatement(sql);
	            ps.setString(1, nombre);
	            ps.executeUpdate();

	            System.out.println("Juego eliminado: " + nombre);
	            connection.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	            System.out.println("Error al eliminar juego.");
	        }
	    } else {
	        System.out.println("Juego no encontrado: " + nombre);
	    }
	}
 
}
