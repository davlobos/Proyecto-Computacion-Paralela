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
	
    public ServerImpl() throws RemoteException {
    	conectarBD();
        UnicastRemoteObject.exportObject(this, 0);
    }
    
    private ArrayList<Juego> BD_personas = new ArrayList<>();
    
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
    

	@Override
	public String getDataFromApi() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getUF() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ArrayList<Juego> getJuego() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void agregarJuego(Juego p) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
}
