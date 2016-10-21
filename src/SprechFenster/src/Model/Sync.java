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
	
	Sync() throws SQLException
	{
		initAsso();
		
		this.con = DBConnector.getInstants();
		
		//Load all Fencer so that getAll() works like intended
		for(Integer integer : con.getAllFencer())
			Fencer.getFencer(integer);
		
		//Load all Tournaments so that getAll() works like intended
		for(Integer integer : con.getAllTournaments())
			Tournament.getTournament(integer);
		
		for(Integer integer : con.getAllPreliminarys())
			Preliminary.getPreliminary(integer);
		
		for(Integer integer : con.getAllFinalrounds())
			Finalround.getFinalround(integer);
	}
	
	void initAsso()
	{
		Fencer.sync = this;
		Finalround.sync = this;
		Preliminary.sync = this;
		Tournament.sync = this;
	}
	
	// ----- Fencer -----

	public iFencer createFencer(String name, String familyName) throws SQLException 
	{
		Fencer ret = con.loadFencer(con.createFencer(name, familyName));
		
		setChanged();               // Eine Änderung ist aufgetreten
		notifyObservers("New fencer.");  // Informiere Observer über Änderung
		
		return ret;
	}
	
	public List<iFencer> getAllFencer()
	{
		List<iFencer> ret = new ArrayList<>();
		for(Fencer f : Fencer.getAllFencer())
			ret.add(f);
		return ret;
	}
	
	Fencer loadFencer(int id) throws SQLException
	{
		return con.loadFencer(id);
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
	
	public List<iTournament> getAllTournaments()
	{
		List<iTournament> ret = new ArrayList<>();
		for(Tournament t : Tournament.getAllTournaments())
			ret.add(t);
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
	
	boolean isFencerParticipant(Tournament t, Fencer f) throws SQLException
	{
		return con.isFencerParticipant(t, f);
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
		Preliminary.deletePreliminarys(f);
		con.removeParticipant(f);
	}
	
	void addParticipant(Tournament t, Fencer f, int group) throws SQLException
	{
		con.addParticipant(t, f, group);
	}

	boolean setTimeForPreliminary(Preliminary p,int round,int lane) throws SQLException
	{
		return con.setTimeForPreliminary(p, round, lane);
	}
	
	void setPoints(int id, int fencer, int points) throws SQLException
	{
		con.setPoints(id, fencer, points);
	}
	
	void removeParticipantFromPrelim(Preliminary p, Fencer f) throws SQLException
	{
		con.removeParticipantFromPrelim(p, f);
	}
	
	void addParticipantToPrelim(Preliminary p, Fencer f) throws SQLException
	{
		con.addParticipantToPrelim(p, f);
	}
	
	void switchParticipantsInPrelim(Preliminary p, Fencer out, Fencer in) throws SQLException
	{
		con.switchParticipantsInPrelim(p, out, in);
	}
	
	void loadFinalround(int id) throws SQLException
	{
		con.loadFinalround(id);
	}
	
	boolean setTimeForFinalround(Finalround f, int round, int lane) throws SQLException
	{
		return con.setTimeForFinalround(f, round, lane);
	}
	
	void setPointsFR(int id, int fencer,int points) throws SQLException
	{
		con.setPointsFR(id, fencer, points);
	}
	
	void loadPreliminary(int id) throws SQLException
	{
		con.loadPreliminary(id);
	}
}
