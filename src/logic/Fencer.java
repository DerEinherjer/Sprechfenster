package logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Fencer 
{
	private int ID;
	private Connection con;
	
	protected Fencer(int ID, Connection con)
	{
		this.ID = ID;
		this.con = con;
	}
	
	protected int getID()
	{
		return ID;
	}
	
	public int getGroup()
	{
		String sql = "SELECT Gruppe FROM Teilnahme WHERE TurnierID = "+Tournament.ID+" AND FechterID = "+ID+";";
		PreparedStatement stmt;
		try 
		{
			stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			return rs.getInt("Gruppe");
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
}
