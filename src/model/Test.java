package model;

import model.rounds.iFinalround;
import model.rounds.iPreliminary;
import java.util.List;

public class Test 
{
    public static void main(String args[])
    {
        try 
        {
            iSync sync = iSync.getInstance();
            
            iTournament t = sync.createTournament("Der ultimative Test");
            t.setFinalRounds(2);
            t.setGroups(2);
            t.setLanes(2);
            
            iFencer fencers[] = new iFencer[26];
            
            for(char i = 'A'; i<='Z'; i++)
            {
                fencers[i-'A'] = sync.createFencer(i+"", i+"", "", "", "");
                fencers[i-'A'].setBirthday("Geburtstag");
                if(!fencers[i-'A'].getBirthday().equals("Geburtstag"))
                    System.out.println("Geburtstag wird nicht richtig behandelt");
                t.addParticipant(fencers[i-'A']);
            }
            
            t.createPreliminaryTiming();
            printSchedule(t.getPreliminarySchedule());   
            
            int count = t.getPreliminaryCount();
            t.getAllPreliminary().get(0).delete();
            if(count-1 == t.getPreliminaryCount())
                System.out.println("Turnier findet ein Prelim weniger");
            else
                System.out.println("Turnier findet gelöschtes Prelim immer noch.");
            
            for(iPreliminary p : t.getAllPreliminary())
            {
                List<iFencer> tmp = p.getFencer();
                iFencer f1 = tmp.get(0);
                iFencer f2 = tmp.get(1);
                
                if(f1.getName().charAt(0)>f2.getName().charAt(0))
                {
                    p.setPoints(f2, 2);
                    p.setPoints(f1, 1);
                    p.setFinished(true);
                }
                else
                {
                    p.setPoints(f1, 2);
                    p.setPoints(f2, 1);
                    p.setFinished(true);
                }
            }
            
            for(iScore s : t.getScoresPrelim())
                System.out.println(s.toString());
            
            System.out.println("Vorrunde wird beendet");
            if(!t.finishPreliminary())
            {
                System.out.println("Konnte Prelim nicht abschließen.");
                System.exit(-1);
            }
            System.out.println("Vorrunde wurde beendet");
        
            
            
            System.out.println("Anzahl Finalrunden: "+ t.getAllFinalrounds().size());
            
            boolean fertig = false;
            while(!fertig)
            {
                fertig = true;
                for(iFinalround f : t.getAllFinalrounds())
                {
                    if(!f.isFinished() && f.getFencer().size() == 2)
                    {
                        fertig = false;
                        iFencer f1 = f.getFencer().get(0);
                        iFencer f2 = f.getFencer().get(1);
                        if(f1.getName().charAt(0)>f2.getName().charAt(0))
                        {
                            f.setPoints(f2, 2);
                            f.setPoints(f1, 1);
                            f.setFinished(true);
                        }
                        else
                        {
                            f.setPoints(f1, 2);
                            f.setPoints(f2, 1);
                            f.setFinished(true);
                        }
                    }
                }
            }
                        
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }	
    }
	
        static void printSchedule(iPreliminary schedule[][]) throws ObjectDeprecatedException
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
