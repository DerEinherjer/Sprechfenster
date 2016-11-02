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
				p.setYellow(p.getFencer().get(0), 1);
				p.setYellow(p.getFencer().get(1), 2);
				if(p.getYellow(p.getFencer().get(0))!=1)
					System.out.println("Final Gelb 1");
				if(p.getYellow(p.getFencer().get(1))!=2)
					System.out.println("Final Gelb 2");
				p.setRed(p.getFencer().get(0), 1);
				p.setRed(p.getFencer().get(1), 2);
				if(p.getRed(p.getFencer().get(0))!=1)
					System.out.println("Final Rot 1");
				if(p.getRed(p.getFencer().get(1))!=2)
					System.out.println("Final Rot 2");
				p.setBlack(p.getFencer().get(0), 1);
				p.setBlack(p.getFencer().get(1), 2);
				if(p.getBlack(p.getFencer().get(0))!=1)
					System.out.println("Final Schwarz 1");
				if(p.getBlack(p.getFencer().get(1))!=2)
					System.out.println("Final Schwarz 2");
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
					if(fr.getFencer().size()==2&&!fr.isFinished())
					{
						found=true;
						fr.setPoints(fr.getFencer().get(0), points++);
						fr.setPoints(fr.getFencer().get(1), points++);
						fr.setYellow(fr.getFencer().get(0), 1);
						fr.setYellow(fr.getFencer().get(1), 2);
						if(fr.getYellow(fr.getFencer().get(0))!=1)
							System.out.println("Final Gelb 1 ("+((Finalround)fr).getID()+")"+fr.getYellow(fr.getFencer().get(1)));
						if(fr.getYellow(fr.getFencer().get(1))!=2)
							System.out.println("Final Gelb 2 ("+((Finalround)fr).getID()+")"+fr.getYellow(fr.getFencer().get(1)));
						fr.setRed(fr.getFencer().get(0), 1);
						fr.setRed(fr.getFencer().get(1), 2);
						if(fr.getRed(fr.getFencer().get(0))!=1)
							System.out.println("Final Rot 1 ("+((Finalround)fr).getID()+")"+fr.getRed(fr.getFencer().get(1)));
						if(fr.getRed(fr.getFencer().get(1))!=2)
							System.out.println("Final Rot 2 ("+((Finalround)fr).getID()+")"+fr.getRed(fr.getFencer().get(1)));
						fr.setBlack(fr.getFencer().get(0), 1);
						fr.setBlack(fr.getFencer().get(1), 2);
						if(fr.getBlack(fr.getFencer().get(0))!=1)
							System.out.println("Final Schwarz 1 ("+((Finalround)fr).getID()+")"+fr.getBlack(fr.getFencer().get(1)));
						if(fr.getBlack(fr.getFencer().get(1))!=2)
							System.out.println("Final Schwarz 2 ("+((Finalround)fr).getID()+")"+fr.getBlack(fr.getFencer().get(1)));
						fr.setFinished(true);
					}
				}
			}
		
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}	
	}
	
	static void printSchedule(iPreliminary schedule[][]) throws ObjectDeprecatedExeption
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
