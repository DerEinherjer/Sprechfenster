package Model;

import java.sql.SQLException;
import java.util.List;

public class Test 
{
	public static void main(String args[])
	{
		try 
		{
			Sync sync = Sync.getInstance();
			
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
			
			for(Preliminary preliminary : t1.getAllPreliminary())
				System.out.println(preliminary.toString());
			

		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}	
	}
}
