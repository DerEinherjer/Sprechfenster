package model;

import model.rounds.Preliminary;
import model.rounds.Finalround;
import model.rounds.Round;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Sync extends iSync
{
	private DBConnector con;
	
	public enum change
	{
		createdFencer,
		createdTournament,
		changedFencerValue,
		changedTournamentValue,
		removedParticipant,
		addedParticipant,
		changedPreliminary,
		changedFinalround,
		createdPreliminary,
		changedCards,
		finishedPreliminary,
		finishedFinalround,
		unfinishedPreliminary,
		unfinishedFinalround,
                beganFinalPhase,
	}
        
                
        public void setDatabaseSavePoint() throws SQLException
        {
            con.SetDatabaseSavepoint();
        }
        
        public void restoreDatabaseSavePoint() throws SQLException
        {
            con.RestoreDatabase();
        }
        
        public void setDatabaseURL(String url)
        {
            con.SetDatabaseURL(url);
        }
        
        public String getDatabaseURL()
        {
            return con.GetDatabaseURL();
        }
	
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
		Round.sync = this;
		Tournament.sync = this;
	}
	
	// ----- Fencer -----

	public iFencer createFencer(String name, String familyName) throws SQLException 
	{
		Fencer ret = con.loadFencer(con.createFencer(name, familyName));
		
		setChanged();             
		notifyObservers(change.createdFencer);
		
		return ret;
	}
	
	public List<iFencer> getAllFencer()
	{
		List<iFencer> ret = new ArrayList<>();
		for(Fencer f : Fencer.getAllFencer())
			ret.add(f);
		return ret;
	}
        
        public iFencer getFencerByID(int fencerID) throws SQLException
        {
            return Fencer.getFencer(fencerID);
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
		
		setChanged();
		notifyObservers(change.createdTournament);
		
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
		setChanged();
		notifyObservers(change.changedTournamentValue);
	}
	
	void tournamentSetDate(String date,int id) throws SQLException
	{
		con.tournamentSetDate(date, id);
		setChanged();
		notifyObservers(change.changedTournamentValue);
	}
	
	void tournamentSetGroups(int groups, int id) throws SQLException
	{
		con.tournamentSetGroups(groups, id);
		setChanged();
		notifyObservers(change.changedTournamentValue);
	}
	
	void tournamentSetFinalRounds(int rounds, int id) throws SQLException
	{
		con.tournamentSetFinalRounds(rounds, id);
		setChanged();
		notifyObservers(change.changedTournamentValue);
	}
	
	void tournamentSetLanes(int lanes, int id) throws SQLException
	{
		con.tournamentSetLanes(lanes, id);
		setChanged();
		notifyObservers(change.changedTournamentValue);
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
		setChanged();
		notifyObservers(change.changedFencerValue);
	}
	
	void fencerSetFamilyName(String name, int id) throws SQLException
	{
		con.fencerSetFamilyName(name, id);
		setChanged();
		notifyObservers(change.changedFencerValue);
	}
	
	void fencerSetBirthday(String date, int id) throws SQLException
	{
		con.fencerSetBirthday(date, id);
		setChanged();
		notifyObservers(change.changedFencerValue);
	}
	
	void fencerSetFencingSchool(String school, int id) throws SQLException
	{
		con.fencerSetFencingSchool(school, id);
		setChanged();
		notifyObservers(change.changedFencerValue);
	}
	
	void fencerSetNationality(String nation, int id) throws SQLException
	{
		con.fencerSetNationality(nation, id);
		setChanged();
		notifyObservers(change.changedFencerValue);
	}
	
	int getParticipantGroup(Tournament t, Fencer f) throws SQLException
	{
		return con.getParticipantGroup(t, f);
	}
	
	void setEntryFee(Tournament t, Fencer f,boolean paid) throws SQLException
	{
		con.setEntryFee(t, f, paid);
		setChanged();
		notifyObservers(change.changedTournamentValue);
	}
	
	void setEquipmentCheck(Tournament t, Fencer f,boolean checked) throws SQLException
	{
		con.setEquipmentCheck(t, f, checked);
		setChanged();
		notifyObservers(change.changedTournamentValue);
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
            setChanged();
            notifyObservers(change.changedTournamentValue);
	}
	
	int finalroundsCount(Tournament t) throws SQLException
	{
		return con.finalroundsCount(t);
	}
	
	public void removeParticipant(Fencer f) throws SQLException
	{
		Preliminary.deletePreliminarys(f);
		con.removeParticipant(f);

		setChanged();
		notifyObservers(change.removedParticipant);
	}
	
	void addParticipant(Tournament t, Fencer f, int group) throws SQLException
	{
		con.addParticipant(t, f, group);

		setChanged();
		notifyObservers(change.addedParticipant);
	}

	public boolean setTime(Round p,int round,int lane) throws SQLException
	{
		boolean ret = con.setTime(p, round, lane);

		setChanged();
		notifyObservers(change.changedPreliminary);
		return ret;
	}
	
	public void setPoints(int id, int fencer, int points) throws SQLException
	{
		con.setPoints(id, fencer, points);
		setChanged();
		notifyObservers(change.changedPreliminary);
	}
	
	public void removeParticipantFromPrelim(Round p, Fencer f) throws SQLException
	{
		con.removeParticipantFromPrelim(p, f);
		setChanged();
		notifyObservers(change.changedPreliminary);
	}
	
	public void addParticipantToPrelim(Round p, Fencer f) throws SQLException
	{
		con.addParticipantToPrelim(p, f);
		setChanged();
		notifyObservers(change.changedPreliminary);
	}
	
	public void switchParticipantsInPrelim(Round p, Fencer out, Fencer in) throws SQLException
	{
		con.switchParticipantsInPrelim(p, out, in);
		setChanged();
		notifyObservers(change.changedPreliminary);
	}
	
	public void loadFinalround(int id) throws SQLException
	{
		con.loadFinalround(id);
	}
	
	boolean setTimeForFinalround(Finalround f, int round, int lane) throws SQLException
	{
		/*boolean ret = con.setTimeForFinalround(f, round, lane);
		setChanged();
		notifyObservers(change.changedFinalround);
		return ret;*/
                return false;
	}
	
	public void loadPreliminary(int id) throws SQLException
	{
		con.loadPreliminary(id);
	}
	
	void addPreliminary(Tournament t) throws SQLException
	{
		con.addPreliminary(t);
		setChanged();
		notifyObservers(change.createdPreliminary);
	}

	public void setFinishedPreliminary(Tournament t) throws SQLException 
	{
		con.setFinishedPreliminary(t);
                setChanged();
                notifyObservers(change.beganFinalPhase);
	}
	
	public void setYellowPrelim(Round p, Fencer f, int count) throws SQLException
	{
		con.setYellowPrelim(p, f, count);
		setChanged();
		notifyObservers(change.changedCards);
	}
	
	public void setRedPrelim(Round p, Fencer f, int count) throws SQLException
	{
		con.setRedPrelim(p, f, count);
		setChanged();
		notifyObservers(change.changedCards);
	}
        
	public void setBlackPrelim(Round p, Fencer f, int count) throws SQLException
	{
		con.setBlackPrelim(p, f, count);
		setChanged();
		notifyObservers(change.changedCards);
	}
	
	boolean getDropedOut(Tournament t,Fencer f) throws SQLException
	{
		return con.getDropedOut(t, f);
	}
	
	String getComment(Tournament t,Fencer f) throws SQLException
	{
		return con.getComment(t, f);
	}
	
	void setComment(Tournament t, Fencer f, String comment) throws SQLException
	{
		con.setComment(t, f, comment);
	}

	public void setPrelimFinished(Round p, Boolean finished) throws SQLException, ObjectDeprecatedException 
	{
		con.setPrelimFinished(p, finished);
		setChanged();
		if(finished)
			notifyObservers(change.finishedPreliminary);
		else
			notifyObservers(change.unfinishedPreliminary);
	}
        
    public void removePreliminary(int id) throws SQLException 
    {
       con.removePreliminary(id);
    }
    
    public void removeFinalround(int id)
    {
        throw new UnsupportedOperationException();
    }
}
