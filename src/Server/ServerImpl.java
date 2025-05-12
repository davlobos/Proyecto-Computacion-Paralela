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
import common.Persona;

public class ServerImpl implements InterfazDeServer {
    private ArrayList<Persona> BD_personas = new ArrayList<>();

    public ServerImpl() throws RemoteException {
    	conectarBD();
        UnicastRemoteObject.exportObject(this, 0);
        crearBD();
    }

    private void crearBD() {
        BD_personas.add(new Persona("matias", 27));
        BD_personas.add(new Persona("maria eugenia", 31));
    }
    
//del video del profe
    
    public void conectarBD() {
    	
    	Connection connection = null;
    	Statement query = null;
    	PreparedStatement test = null;
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
    		
    		while (resultados.next()) {
    			int id = resultados.getInt("id");
    			String nombre = resultados.getString("nombre");
    			
    			
    			System.out.println(id + " " + nombre);
    		}
        	
        	System.out.println("Conexión con la BD exitosa!");
        	connection.close();   	
        	
    	}catch(SQLException e){
    		e.printStackTrace();
    		System.out.println("No se pudo conectar a la BD");
    	}
    }

    public ArrayList<Persona> getPersona() {
        return BD_personas;
    }

    public void agregarPersona(Persona p) throws RemoteException {
        BD_personas.add(p);
        System.out.println("Nueva persona agregada: " + p.getNombre());
    }
}
