package logic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Controll 
{
	private static String url = "jdbc:h2:~/SprechfensterData";
	
	private Connection con = null;
	
	public Controll()
	{
		try 
		{
			Class.forName("org.h2.Driver");
			
			con = DriverManager.getConnection(url, "", "");
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
		PreparedStatement stmt = null;
		String sql = null;
		try 
		{
			sql = "CREATE TABELE Turniere (ID int,"
					   + "Name varchar(255),"
					   + "Datum varchar(11),"
					   + "Gruppen int,"
					   + "Finalrunden int,"
					   + "Bahnen int);";
			stmt = con.prepareStatement(sql);
			stmt.executeQuery();
		} 
		catch (SQLException e) {}
		
		
		try 
		{
			sql = "CREATE TABELE Fechter (ID int,"
					   + "Vorname varchar(255),"
					   + "Nachname varchar(255),"
					   + "Geburtstag verchar(255),"
					   + "Fechtschule varchar(255),"
					   + "Nationalitaet varchar(255));";
			stmt = con.prepareStatement(sql);
			stmt.executeQuery();
		} 
		catch (SQLException e) {}
		
		try
		{
			sql = "CREATE TABLE Vorrunden( ID int,"
										+ "TurnierID int,"
										+ "Gruppe int,"
										+ "TerminID int,"
										+ "Teilnehmer1 int,"
										+ "Teilnehmer2 int,)";
			stmt = con.prepareStatement(sql);
			stmt.executeQuery();
		}
		catch (SQLException e){}
		
		try
		{
			sql = "CREATE TABLE Finalrunden(ID int,"
										 + "Teilnehmer1 int,"
										 + "Teilnehmer2 int,"
										 + "FolgeBegegnung int,"
										 + "Vorbegegnung1 int,"
										 + "Vorbegegnung2 int)";
			stmt = con.prepareStatement(sql);
			stmt.executeQuery();
		}
		catch (SQLException e){}
		
		try
		{
			sql = "CREATE TABLE Teilnahme(TurnierID int,"
										 + "FechterID int,"
										 + "Gruppe int)";
			stmt = con.prepareStatement(sql);
			stmt.executeQuery();
		}
		catch (SQLException e){}
	}
	
	public Tournament createTournament(String Name, String Datum)
	{
		PreparedStatement stmt = null;
		
		String sql = "SELECT COUNT(*) FROM Turnire WHERE Name = ? OR Datum = ?;";
		try 
		{
			stmt = con.prepareStatement(sql);
			stmt.setString(0, Name);
			stmt.setString(1, Datum);
			ResultSet rs = stmt.executeQuery();
			if(rs.getInt(0)<1)
			{
				sql = "INSER INTO Turniere (Name, Datum, Gruppen, Teilnehmer, Finalrunden, Bahnen)"
						+ " VALUES (?, ?, 2, 8, 2, 2);";
				
			}
			else
			{
				//Existiert bereits (Mit diesem Namen ODER an diesem Tag)
			}
		} 
		catch (SQLException e) 
		{
			//Handle Exeption
		}
		
		return null;
	}
	
	public List<String> getTournaments() throws SQLException
	{
		List<String> ret = new ArrayList<String>();
		String sql = "SELECT ID, Name, Datum FROM Turniere";
		PreparedStatement stmt = con.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		while(rs.next())
		{
			String tmp ="";
			tmp = rs.getString("ID")+" | ";
			tmp += rs.getString("Name")+" | ";
			tmp += rs.getString("Datum");
			ret.add(tmp);
		}
		return ret;
	}
	
	public Tournament getTournament(int ID) throws SQLException
	{
		String sql = "SELECT COUNT(*) AS Count FROM Turniere WHERE ID = "+ID+";";
		PreparedStatement stmt = con.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		if(rs.getInt("Count")==1)
		{
			return new Tournament(ID, con);
		}
		return null;
	}
}
