package Model;

import java.sql.SQLException;
import java.util.List;

public class Test 
{
	public static void main(String args[])
	{
		try 
		{
			/*Sync sync = Sync.getInstance();
			
			iFencer f1 = sync.createFencer("Setsuna", "Seiei");
			iFencer f2 = sync.createFencer("Tieria", "Erde");
			iFencer f3 = sync.createFencer("Neil", "Dylandy");
			iFencer f4 = sync.createFencer("Allelujah", "Haptism");
			
			List<iFencer> flist = sync.getAllFencer();
			
			for(iFencer fencer : flist)
				System.out.println(fencer.toString());
			
			iTournament t1 = sync.createTournament("Gundam 00 Season 1");
			iTournament t2 = sync.createTournament("Gundam 00 Season 2");
			
			List<iTournament> tlist = sync.getAllTournaments();
			
			for(iTournament tournament : tlist)
				System.out.println(tournament.toString());
			
			System.out.println(t1.isParticipant(f1));
			t1.addParticipant(f1);
			System.out.println(t1.isParticipant(f1));
			System.out.println(t1.isParticipant(f2));
			t1.addParticipant(f2);
			System.out.println(t1.isParticipant(f2));
			System.out.println(t1.isParticipant(f3));
			t1.addParticipant(f3);
			System.out.println(t1.isParticipant(f3));
			System.out.println(t1.isParticipant(f4));
			t1.addParticipant(f4);
			System.out.println(t1.isParticipant(f4));
			
			for(iPreliminary preliminary : t1.getAllPreliminary())
				System.out.println(preliminary.toString());*/
			
			iSync sync = iSync.getInstance();
			iTournament t = sync.createTournament("ZeitPlan Test");
			t.setLanes(5);
			
			for(char i = 'A'; i<='Z'; i++)
				t.addParticipant(sync.createFencer(i+"", i+""));
			
			t.createPreliminaryTiming();
			
			iPreliminary[][] s = t.getPreliminarySchedule();
			
			for(int x = 0; x <s.length;x++)
			{
				String line = "";
				for(int y = 0; y<s[0].length;y++)
				{
					if(line!="")
						line+="|";
					if(s[x][y]!=null)
					{
						line += " " + s[x][y].getFencer().get(0).getName()+" : "+ s[x][y].getFencer().get(1).getName() +" ";
					}
					else
						line += " " + " " + "   " + " " + " ";  
				}
				System.out.println(line);
			}
			
			iPreliminary p = t.getAllPreliminary().get(0);
			iFencer f1 = p.getFencer().get(0);
			iFencer f2 = p.getFencer().get(1);
			
			int pf1 = 2;
			int pf2 = 5;
			p.setPoints(f1, pf1);
			p.setPoints(f2, pf2);
			
			if(p.getPoints(f1)==pf1)
				System.out.println("Punkte von Fechter 1 stimmen.");
			else
				System.out.println("Punkte von Fechter 1 stimmen nicht.");
			

			if(p.getPoints(f2)==pf2)
				System.out.println("Punkte von Fechter 2 stimmen.");
			else
				System.out.println("Punkte von Fechter 2 stimmen nicht.");

		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}	
	}
}
