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
		
		setChanged();               // Eine Änderung ist aufgetreten
		notifyObservers("New fencer.");  // Informiere Observer über Änderung
		
		return ret;
	}
	
	Fencer loadFencer(int id) throws SQLException
	{
		return con.loadFencer(id);
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
		
		setChanged();               // Eine Änderung ist aufgetreten
		notifyObservers("New tournament.");  // Informiere Observer über Änderung
		
		return ret;
	}
	
	Tournament loadTournament(int id) throws SQLException
	{
		return con.loadTournament(id);
	}
	
	void tournamentSetName(String name, int id) throws SQLException
	{
		con.tournamentSetName(name, id);
	}
	
	void tournamentSetDate(String date,int id) throws SQLException
	{
		con.tournamentSetDate(date, id);
	}
	
	void tournamentSetGroups(int groups, int id) throws SQLException
	{
		con.tournamentSetGroups(groups, id);
	}
	
	void tournamentSetFinalRounds(int rounds, int id) throws SQLException
	{
		con.tournamentSetFinalRounds(rounds, id);
	}
	
	void createFinalrounds(Tournament t) throws SQLException
	{
		con.createFinalRounds(t);
	}
	
	void tournamentSetLanes(int lanes, int id) throws SQLException
	{
		con.tournamentSetLanes(lanes, id);
	}
	
	int[] getGroupsMemberCount(Tournament t) throws SQLException
	{
		return con.getGroupsMemberCount(t);
	}
	
	boolean isFencerParticipant(Tournament t, Fencer f)
	{
		return isFencerParticipant(t, f);
	}
	
	void fencerSetName(String name, int id) throws SQLException
	{
		con.fencerSetName(name, id);
	}
	
	void fencerSetFamilyName(String name, int id) throws SQLException
	{
		con.fencerSetFamilyName(name, id);
	}
	
	void fencerSetBirthday(String date, int id) throws SQLException
	{
		con.fencerSetBirthday(date, id);
	}
	
	void fencerSetFencingSchool(String school, int id) throws SQLException
	{
		con.fencerSetFencingSchool(school, id);
	}
	
	void fencerSetNationality(String nation, int id) throws SQLException
	{
		con.fencerSetNationality(nation, id);
	}
	
	int getParticipantGroup(Tournament t, Fencer f) throws SQLException
	{
		return con.getParticipantGroup(t, f);
	}
	
	void setEntryFee(Tournament t, Fencer f,boolean paid) throws SQLException
	{
		con.setEntryFee(t, f, paid);
	}
	
	void setEquipmentCheck(Tournament t, Fencer f,boolean checked) throws SQLException
	{
		con.setEquipmentCheck(t, f, checked);
	}
	
	boolean getEntryFee(Tournament t, Fencer f) throws SQLException
	{
		return con.getEntryFee(t, f);
	}
	
	boolean getEquipmentCheck(Tournament t, Fencer f) throws SQLException
	{
		return con.getEquipmentCheck(t, f);
	}
	
	void createFinalRounds(Tournament t) throws SQLException
	{
		con.createFinalRounds(t);
	}
	
	int finalroundsCount(Tournament t) throws SQLException
	{
		return con.finalroundsCount(t);
	}
	
	void removeParticipant(Fencer f) throws SQLException
	{
		con.removeParticipant(f);
	}
	
	void addParticipant(Tournament t, Fencer f, int group) throws SQLException
	{
		con.addParticipant(t, f, group);
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
