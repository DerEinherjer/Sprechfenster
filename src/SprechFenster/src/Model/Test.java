package Model;

import java.sql.SQLException;
import java.util.List;

public class Test 
{
	public static void main(String args[])
	{
		try 
		{
			iSync sync = iSync.getInstance();
			
			
			iFencer[] fencers = new Fencer[26];
			char a = 'A';
			for(int i = 0;i<26;i++)
			{
				fencers[i] = sync.createFencer(a+"", a+"");
				a++;
			}
			
			iTournament t = sync.createTournament("Test");
			

			System.out.println("Fechter: "+Fencer.getFencer((Tournament)t).size());
			
			for(int i = 0; i < 16; i++)
				t.addParticipant(fencers[i]);
			

			System.out.println("Fechter: "+Fencer.getFencer((Tournament)t).size());
			
			System.out.println("Vorrunden: "+t.getAllPreliminary().size());
			
			t.setGroups(4);
			System.out.println(t.getParticipantsOfGroup(1));
			System.out.println(t.getParticipantsOfGroup(2));
			System.out.println(t.getParticipantsOfGroup(3));
			System.out.println(t.getParticipantsOfGroup(4));

			System.out.println("Vorrunden: "+t.getAllPreliminary().size());
			
			t.createPreliminaryTiming();
			printSchedule( t.getPreliminarySchedule());
			
			
			t.setFinalRounds(3);
			((Tournament)t).printTree();
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
