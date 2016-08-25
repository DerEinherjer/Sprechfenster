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
	
	protected static Connection con = null;
	
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
			sql = "CREATE TABLE Turniere (ID int NOT NULL AUTO_INCREMENT UNIQUE,"
					   + "Name varchar(255),"
					   + "Datum varchar(11),"
					   + "Gruppen int,"
					   + "Finalrunden int,"
					   + "Bahnen int);";
			stmt = con.prepareStatement(sql);
			stmt.executeUpdate();
		} 
		catch (SQLException e) {if(e.getErrorCode()!=42101)e.printStackTrace();}//42101 -> Table exists
		
		
		try 
		{
			sql = "CREATE TABLE Fechter (ID int NOT NULL AUTO_INCREMENT UNIQUE,"
					   + "Vorname varchar(255),"
					   + "Nachname varchar(255),"
					   + "Geburtstag varchar(255),"
					   + "Fechtschule varchar(255),"
					   + "Nationalitaet varchar(255));";
			stmt = con.prepareStatement(sql);
			stmt.executeUpdate();
		} 
		catch (SQLException e) {if(e.getErrorCode()!=42101)e.printStackTrace();}
		
		try
		{
			sql = "CREATE TABLE Vorrunden( ID int NOT NULL AUTO_INCREMENT UNIQUE,"
										+ "TurnierID int,"
										+ "Gruppe int,"
										+ "TerminID int,"
										+ "Teilnehmer1 int,"
										+ "Teilnehmer2 int,)";
			stmt = con.prepareStatement(sql);
			stmt.executeUpdate();
		}
		catch (SQLException e){if(e.getErrorCode()!=42101)e.printStackTrace();}
		
		try
		{
			sql = "CREATE TABLE Finalrunden(ID int NOT NULL AUTO_INCREMENT UNIQUE,"
										 + "Teilnehmer1 int,"
										 + "Teilnehmer2 int,"
										 + "FolgeBegegnung int,"
										 + "Vorbegegnung1 int,"
										 + "Vorbegegnung2 int)";
			stmt = con.prepareStatement(sql);
			stmt.executeUpdate();
		}
		catch (SQLException e){if(e.getErrorCode()!=42101)e.printStackTrace();}
		
		try
		{
			sql = "CREATE TABLE Teilnahme(TurnierID int NOT NULL AUTO_INCREMENT UNIQUE,"
										 + "FechterID int,"
										 + "Gruppe int)";
			stmt = con.prepareStatement(sql);
			stmt.executeUpdate();
		}
		catch (SQLException e){if(e.getErrorCode()!=42101)e.printStackTrace();}
	}
	
	public Tournament createTournament(String name, String datum)
	{
		PreparedStatement stmt = null;
		
		String sql = "SELECT COUNT(*) FROM Turniere WHERE Name = ? OR Datum = ?;";
		try 
		{
			stmt = con.prepareStatement(sql);
			stmt.setString(1, name);
			stmt.setString(2, datum);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			if(rs.getInt(1)<1)
			{
				sql = "INSERT INTO Turniere (Name, Datum, Gruppen, Finalrunden, Bahnen)"
						+ " VALUES (?, ?, 2, 2, 2);";
				stmt = con.prepareStatement(sql);
				stmt.setString(1, name);
				stmt.setString(2, datum);
				stmt.executeUpdate();
			}
			else
			{
				System.out.println("Existiert Bereits");
				//TODO Existiert bereits (Mit diesem Namen ODER an diesem Tag)
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
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
