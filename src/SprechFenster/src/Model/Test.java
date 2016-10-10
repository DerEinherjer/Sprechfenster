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
			
			/*iSync sync = iSync.getInstance();
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
				System.out.println("Punkte von Fechter 2 stimmen nicht.");*/
			
			iSync s = iSync.getInstance();
			iTournament t = s.createTournament("Turnier");
			t.setGroups(2);
			t.setLanes(2);
			
			iFencer f1 = s.createFencer("A", "A");
			iFencer f2 = s.createFencer("B", "B");
			iFencer f3 = s.createFencer("C", "C");
			iFencer f4 = s.createFencer("D", "D");
			t.addParticipant(f1);
			t.addParticipant(f2);
			t.addParticipant(f3);
			t.addParticipant(f4);
			
			t.createPreliminaryTiming();
			iPreliminary [][] shed = t.getPreliminarySchedule();
			for(int x = 0;x<shed.length;x++)
			{
				String line= "";
				for(int y = 0; y<shed[0].length;y++)
				{
					//System.out.println("HI?");
					if(y!=0)
						line+= " | ";
					line += shed[x][y].getGroup();
				}
				System.out.println(line);
			}
			List<iPreliminary> list = t.getAllPreliminary();
			
			for(iPreliminary p : list)
			{
				p.setPoints(p.getFencer().get(0),	(int)(Math.random()*16+1));
				p.setPoints(p.getFencer().get(1),	(int)(Math.random()*16+1));
				p.setFinished(true);
			}
			List<iScore> scores = t.getScores();
			for(int i = 0;i<scores.size();i++)
			{
				System.out.println(scores.get(i).getFencer()+" | "+scores.get(i).getWins()+" | "+scores.get(i).getHits()+" | "+scores.get(i).getGotHit());
			}
			

		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}	
	}
}
