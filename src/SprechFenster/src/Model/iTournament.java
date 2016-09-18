package Model;

import java.sql.SQLException;
import java.util.List;

public interface iTournament 
{
	public String getName();
	public String getDate();
	public int getGroups();
	public int getFinalRounds();
	public int getLanes();
	
	public void setName(String name) throws SQLException;
	public void setDate(String date) throws SQLException;
	public void setGroups(int groups) throws SQLException;
	public void setFinalRounds(int rounds) throws SQLException;
	public void setLanes(int lanes) throws SQLException;
	
	public void addParticipant(iFencer f) throws SQLException;
	public void addParticipant(iFencer f, int group) throws SQLException;
	public boolean isParticipant(iFencer f) throws SQLException;
	
	public List<Preliminary> getAllPreliminary() throws SQLException;
	
}