package logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Tournament 
{
	private int ID;
	private Connection con;
	
	protected Tournament(int ID, Connection con)
	{
		this.ID = ID;
		this.con = con;
		changeVorrunde = true;
		changeFinalrunden = true;
	}
	
	public void createFinalrunden()
	{
		if(changeFinalrunden)
		{
			changeFinalrunden = false;
		}
	}
	
	public void createVorrunden()
	{
		if(changeVorrunde)
		{
			try 
			{
				changeVorrunde = false;
				String sql = "DELETE FROM Vorrunden WHERE TurnierID ="+ID+";";
				PreparedStatement stmt = con.prepareStatement(sql);
				stmt.executeQuery();
				
				
				
				sql = "SELECT * FROM Turniere WHERE ID = "+ID+";";
				
				int teilnehmer = 0;
				int gruppen = 0;
			
			
				stmt = con.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery();
				
				teilnehmer = rs.getInt("Teilnehmer");
				gruppen = rs.getInt("Gruppen");
			
			
				int nummern [] = new int[gruppen];
				
				for (int i = 0; i<teilnehmer-1; i++)
				{
					for(int c = i+1; c<teilnehmer; c++)
					{
						if(i%gruppen == c%gruppen)
						{
							int gruppe = i%gruppen+1;
							sql = "INSERT INTO Vorrunden (TurnierID, Gruppe, Nummer, Teilnehmer1, Teilnehmer2)"
									+ "VALUES ("+ID+", "+gruppe+", "+(++nummern[gruppe])+", "+i+", "+c+");";
							stmt = con.prepareStatement(sql);
						}
					}
				}
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
	}
}
