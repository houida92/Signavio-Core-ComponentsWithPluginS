package com.signavio.warehouse.configuration.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 * 
 * @author Ahmed Fathallah
 * 
 */
public class BDconnection {

	Connection connection = null;
	String driverName = "com.mysql.jdbc.Driver"; //for MySql
	String serverName = "localhost";
	//String serverName = "157.159.110.224";
	String portNumber = "3306";
	
	String uri ="jdbc:mysql://"+serverName+":3306/";
	String username = "root"; 
	//String username = "ahmed"; 
	String password = "root"; 
	//String password = "ahmed"; 
	
	public BDconnection()
	{
		uri=uri+"db";
		//uri=uri+"ahmed_db";
	}
	
	public BDconnection(String db)
	{
		uri=uri+db;
	}
	
	public Connection doConnection()
	{
		try
		{
			// Load the JDBC driver
			Class.forName(driverName);
			// Create a connection to the database
			connection = DriverManager.getConnection(uri, username, password);
		}
		catch (ClassNotFoundException e)
		{
			// Could not find the database driver
			System.out.println("ClassNotFoundException : "+e.getMessage());
			return null;
		}
		catch (SQLException e)
		{
			// Could not connect to the database
			System.out.println(e.getMessage());
			return null;
		}
		return connection;
	}
}
