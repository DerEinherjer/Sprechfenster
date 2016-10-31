package Model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Test 
{
	public static void main(String args[])
	{
		try 
		{
			
			iSync sync = iSync.getInstance();
			
			iTournament t = sync.createTournament("Name");
			
			iFencer[] f = new iFencer[8];
			
			for(char i = 'A'; i < 'I'; i++)
			{
				f[i-'A'] = sync.createFencer(i+"", i+"");
				t.addParticipant(f[i-'A']);
			}
			
			int points = 0;
			for(iPreliminary p : t.getAllPreliminary())
			{
				p.setPoints(p.getFencer().get(0), points++);
				p.setPoints(p.getFencer().get(1), points++);
				p.setFinished(true);
			}
			
			if(t.finishPreliminary())
				System.out.println("Konnte vorrunden nicht beenden.");
			
			((Tournament)t).printTree();
			
			boolean found = true;
			while(found)
			{
				found = false;
				for(iFinalround fr : t.getAllFinalrounds())
				{
					if(fr.getFencer().size()==2)
					{
						fr.setPoints(fr.getFencer().get(0), points++);
						fr.setPoints(fr.getFencer().get(1), points++);
						
						fr.setFinished(true);
					}
				}
			}
		
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}	
	}
	
	static void printSchedule(iPreliminary schedule[][])
	{
		for(int x = 0; x < schedule.length; x++)
		{
			String line = "";
			for(int y = 0; y < schedule[0].length ; y++)
			{
				if(y!=0)
					line+=" | ";
				if(schedule[x][y] != null)
					line+= schedule[x][y].getFencer().get(0).getFamilyName()+schedule[x][y].getFencer().get(1).getFamilyName();
			}
			System.out.println(line);
		}
	}
}
