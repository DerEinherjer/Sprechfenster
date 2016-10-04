package Model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sync extends iSync
{
	private DBConnector con;
	private static Sync sync;
	
	Sync()
	{
		this.con = DBConnector.getInstants();
	}
	
	// ----- Fencer -----
	private Map<Integer, Fencer> fencers = new HashMap<>();

	public iFencer createFencer(String name, String familyName) throws SQLException 
	{
		Fencer ret = con.loadFencer(con.createFencer(name, familyName));
		fencers.put(ret.getID(), ret);
		return ret;
	}
	
	public List<iFencer> getAllFencer() throws SQLException 
	{
		List<iFencer> ret = new ArrayList<>();
		for(Integer integer : con.getAllFencer())
		{
			if(!fencers.containsKey(integer))
				fencers.put(integer, con.loadFencer(integer));
			ret.add(fencers.get(integer));
		}
		return ret;
	}

	Fencer getFencer(int id) throws SQLException
	{
		if(!fencers.containsKey(id))
			fencers.put(id, con.loadFencer(id));
		return fencers.get(id);
			
	}
	
	// ----- Tournament -----
	private Map<Integer, Tournament> tournaments = new HashMap<>();
	
	public iTournament createTournament(String name) throws SQLException
	{
		Tournament ret = con.loadTournament(con.createTournament(name));
		tournaments.put(ret.getID(), ret);
		return ret;
	}
	
	public List<iTournament> getAllTournaments() throws SQLException
	{
		List<iTournament> ret = new ArrayList<>();
		
		for(Integer integer : con.getAllTournaments())
		{
			if(!tournaments.containsKey(integer))
				tournaments.put(integer, con.loadTournament(integer));
			ret.add(tournaments.get(integer));
		}
		
		return ret;
	}
	
	Tournament getTournament(int id) throws SQLException
	{
		if(!tournaments.containsKey(id))
			tournaments.put(id, con.loadTournament(id));
		return tournaments.get(id);
	}
	
}
