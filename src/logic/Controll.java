package logic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Controll 
{
	private static String url = "jdbc:h2:~/SprechfensterData";
	
	private Connection con = null;
	private Statement stmt = null;
	
	public Controll()
	{
		try 
		{
			Class.forName("org.h2.Driver");
			
			con = DriverManager.getConnection(url, "", "");
			stmt = con.createStatement();
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		createTabels();
	}
	
	private void createTabels()
	{
		String sql = "CREATE TABELE Turniere (ID int,Name varchar(255))";
		try 
		{
			stmt.executeUpdate(sql);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
}
