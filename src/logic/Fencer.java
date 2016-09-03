package logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Fencer 
{
	private int ID;
	
	protected Fencer(int ID)
	{
		this.ID = ID;
	}
	
	protected int getID()
	{
		return ID;
	}
	
	public static Fencer getFencer(String vorname, String nachname) throws SQLException
	{
		if(vorname==null && nachname==null)
			return null;
		
		String sql = "SELECT ID FROM Fechter WHERE";
		if(vorname!=null)
			sql += " Vorname = ?";
		if(vorname!=null && nachname!=null)
			sql += " AND";
		if(nachname!=null)
			sql += " Nachname = ?";
		sql += ";";
		PreparedStatement stmt = Controll.con.prepareStatement(sql);
		if(vorname!=null)
			stmt.setString(1, vorname);
		if(vorname!=null && nachname!=null)
			stmt.setString(2, nachname);
		else if(nachname!=null)
			stmt.setString(1, nachname);
		ResultSet rs = stmt.executeQuery();
		if(rs.next())
		{
			return new Fencer(rs.getInt(1));
		}
		return null;
	}
	
	public static Fencer createFencer(String vorname, String nachname) throws SQLException
	{
		if(vorname==null || nachname==null)
			return null;
		if(getFencer(vorname, nachname)==null)
		{
			String sql ="INSERT INTO Fechter (Vorname, Nachname) VALUES (?, ?);";
			PreparedStatement stmt = Controll.con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, vorname);
			stmt.setString(2, nachname);
			stmt.execute();
			ResultSet rs = stmt.getGeneratedKeys();
			rs.next();
			return new Fencer(rs.getInt(1));
		}
		return null;
	}
	
	public String toString()
	{
		String sql = "SELECT Vorname, Nachname FROM Fechter WHERE ID = "+ID+";";
		PreparedStatement stmt;
		try 
		{
			stmt = Controll.con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			if(rs.next())
			{
				return rs.getString("Vorname")+" "+rs.getString("Nachname");
			}
		}
		catch (SQLException e) {}
		
		return "";
		
	}
	
	public String getName() throws SQLException
	{
		String sql = "SELECT Vorname FROM Fechter WHERE ID = "+ID+";";
		PreparedStatement stmt = Controll.con.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		return rs.getString("Vorname");
	}
	
	public String getFamilyName() throws SQLException
	{
		String sql = "SELECT Nachname FROM Fechter WHERE ID = "+ID+";";
		PreparedStatement stmt = Controll.con.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		return rs.getString("Nachname");
	}
}
