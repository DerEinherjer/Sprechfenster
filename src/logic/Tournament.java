package logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Tournament //TODO Singelton 
{
	protected static int ID;
	
	protected Tournament(int ID)
	{
		Tournament.ID = ID;
	}
	
	public void addParticipant(Fencer fencer)
	{
		if(!isParticipant(fencer))
		{//SELECT OrderID, COUNT(OrderID) AS Count FROM OrderDetails WHERE Quantity >=10 GROUP BY OrderID ORDER BY Count ASC, OrderID ASC;
			int group = 1;
			String sql = "SELECT Gruppe, COUNT(Gruppe) AS Count FROM Teilnahme WHERE TurnierID ="+ID+" GROUP BY Gruppe ORDER BY Count ASC, Gruppe ASC;";
			PreparedStatement stmt;
			try 
			{
				stmt = Controll.con.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery();

				boolean groups[] = new boolean[getGruppen()];
				for(int i=0;i<groups.length;i++)groups[i]=false;
				if(rs.next())
				{
					group = rs.getInt("Gruppe");			//Bestimmt die Gruppen mit den Wenigsten teilnehmer aus der Mengen Teilnehmer >0
					do
					{
						groups[rs.getInt("Gruppe")-1] = true;
					}
					while(rs.next());
				}
				
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
			sql = "INSERT INTO Teilnahme (TurnierID, FechterID, Gruppe) VALUES ("+ID+", "+fencer.getID()+", "+group+");";
			try 
			{
				stmt = Controll.con.prepareStatement(sql);
				stmt.executeUpdate();
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
			createVorrundenBegegnungen(fencer);
		}
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
				stmt= Controll.con.prepareStatement(sql);
				stmt.executeUpdate();
				
				sql = "UPDATE Teilnahme SET Gruppe = "+group+" WHERE TurnierID = "+ID+" AND FechterID = "+fencerID+";";
				stmt = Controll.con.prepareStatement(sql);
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
				stmt = Controll.con.prepareStatement(sql);
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
		String sql = "SELECT COUNT(*) AS Count FROM Teilnahme WHERE TurnierID = "+ID+" AND FechterID = "+fencer.getID()+";";
		PreparedStatement stmt;
		try 
		{
			stmt = Controll.con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			if(rs.getInt("Count")==0)
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
			stmt = Controll.con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			rs.next();
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
			PreparedStatement stmt = Controll.con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			return rs.getInt("Gruppen");
		}
		catch(SQLException e){}
		return -1;
	}
	
	private void createVorrundenBegegnungen(Fencer fencer)
	{
		String sql = "SELECT FechterID FROM Teilnahme WHERE TurnierID = "+ID+" AND FechterID != "+fencer.getID()
					+" AND Gruppe = "+getGroup(fencer)+";";
		PreparedStatement stmt;
		try 
		{
			stmt = Controll.con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			sql ="INSERT INTO Vorrunden (TurnierID, Gruppe, TerminID, Teilnehmer1, Teilnehmer2)"
					+ "VALUES ("+ID+", "+getGroup(fencer)+", -1, "+fencer.getID()+", ?);";
			stmt = Controll.con.prepareStatement(sql);
			while(rs.next())
			{
				stmt.setInt(1, rs.getInt("FechterID"));
				stmt.executeUpdate();
			}
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getGroup(Fencer fencer)
	{
		String sql = "SELECT Gruppe FROM Teilnahme WHERE TurnierID = "+ID+" AND FechterID = "+fencer.getID()+";";
		PreparedStatement stmt;
		try 
		{
			stmt = Controll.con.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			if(rs.next())
			return rs.getInt("Gruppe");
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public String getVorrunden() throws SQLException
	{
		String ret = "";
		String sql = "SELECT * FROM Vorrunden WHERE TurnierID = "+ID+";";
		PreparedStatement stmt = Controll.con.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		while(rs.next())
		{
			ret+=rs.getString("Teilnehmer1")+" : "+rs.getString("Teilnehmer2")+"\n";
		}
		return ret;
	}
	
	public List<Fencer> getFencer() throws SQLException
	{
		List<Fencer> ret = new ArrayList<Fencer>();
		String sql = "SELECT FechterID FROM Teilnahme WHERE TurnierID ="+ID+";";
		PreparedStatement stmt = Controll.con.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		while(rs.next())
		{
			ret.add(new Fencer(rs.getInt("FechterID")));
		}
		return ret;
	}
	
	public void printFencerInfo() throws SQLException
	{
		List<Fencer> fencers = getFencer();
		for(Fencer fencer : fencers)
		{
			System.out.println(getGroup(fencer)+"\t"+fencer.getName()+" "+fencer.getFamilyName());
		}
	}
}
