package logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Tournament 
{
	private int ID;
	private Connection con; //Static?
	
	protected Tournament(int ID, Connection con)
	{
		this.ID = ID;
		this.con = con;
	}
	
	public void addParticipant(Fencer fencer)
	{
		if(!isParticipant(fencer))
		{//SELECT OrderID, COUNT(OrderID) AS Count FROM OrderDetails WHERE Quantity >=10 GROUP BY OrderID ORDER BY Count ASC, OrderID ASC;
			int group = 1;
			String sql = "SELECT Gruppe, COUNT(Gruppe) AS Count FROM Teilnahme WHERE TurnierID ="+ID+" GROUPE BY Gruppe ORDER BY Count ASC, Gruppe ASC;";
			PreparedStatement stmt;
			try 
			{
				stmt = con.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery();
				group = rs.getInt("Gruppe"); 

				boolean groups[] = new boolean[getGruppen()];
				do
				{
					groups[rs.getInt("Gruppe")] = true;
				}
				while(rs.next());
				for(int i=0;i<getGruppen();i++)
				{
					if(!groups[i])
					{
						group = i+1;//Konvertierung von Index zu "Name"
						break;
					}
				}
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
			sql = "INSER INTO Teilnehmer (TurnierID, FechterID, Gruppe) VALUES ("+ID+", "+fencer.getID()+", "+group+");";
			try 
			{
				stmt = con.prepareStatement(sql);
				stmt.executeUpdate();
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
		createVorrundenBegegnungen(fencer);
	}
	
	public void addParticipant(Fencer fencer, int group)
	{
		String sql = "";
		int fencerID = fencer.getID();
		PreparedStatement stmt = null;
		int groups = getGruppen();
		
		if(group<1 || group>groups)
				return;//Keine Gülltige gruppe;
		
		if(isParticipant(fencer))
		{
			if(getGroupNummer(fencer)==group)
				return; //Fechter ist bereits in dieser Gruppe
			
			sql = "DELETE FROM Vorrunden WHERE TurnierID = "+ID+" AND (Teilnehmer1 = "+fencerID+" OR "
					                                                 +"Teilnehmer2 = "+fencerID+");";
			try 
			{
				stmt= con.prepareStatement(sql);
				stmt.executeUpdate();
				
				sql = "UPDATE Teilnahme SET Gruppe = "+group+" WHERE TurnierID = "+ID+" AND FechterID = "+fencerID+";";
				stmt = con.prepareStatement(sql);
				stmt.executeUpdate();
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
			
		}
		else
		{
			sql = "INSER INTO Teilnehmer (TurnierID, FechterID, Gruppe) VALUES ("+ID+", "+fencer.getID()+", "+group+");";
			try 
			{
				stmt = con.prepareStatement(sql);
				stmt.executeUpdate();
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
		createVorrundenBegegnungen(fencer);
	}
	
	public boolean isParticipant(Fencer fencer)
	{
		String sql = "SELECT COUNT(*) FROM Teilnahme WHERE Turniere = "+ID+" AND Fechter = "+fencer.getID()+";";
		PreparedStatement stmt;
		try 
		{
			stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			if(rs.getInt(0)==0)
				return false;
			else
				return true;
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return true;
	}
	
	public int getGroupNummer(Fencer fencer)
	{
		String sql = "SELECT Group FROM Teilnahme WHERE Turniere = "+ID+" AND Fechter = "+fencer.getID()+";";
		PreparedStatement stmt;
		try 
		{
			stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			return rs.getInt("Gruppe");
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return -1;
	}
	
	public int getGruppen()
	{
		String sql = "SELECT Gruppen FROM Turniere WHERE ID ="+ID+";";
		try
		{
			PreparedStatement stmt = con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			return rs.getInt("Gruppen");
		}
		catch(SQLException e){}
		return -1;
	}
	
	private void createVorrundenBegegnungen(Fencer fencer)
	{
		
	}
}
