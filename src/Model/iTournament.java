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
	public List<iFencer> getAllParticipants() throws SQLException;
	public List<iFencer> getParticipantsOfGroup(int group) throws SQLException;
	public int getParticipantGroup(iFencer f) throws SQLException;
	public void removeParticipant(iFencer f) throws SQLException;
	
	public void setEntryFee(iFencer f, boolean paid) throws SQLException;
	public void setEquipmentCheck(iFencer f, boolean checked) throws SQLException;
	public boolean getEntryFee(iFencer f) throws SQLException;
	public boolean getEquipmentCheck(iFencer f) throws SQLException;
	
	public List<iPreliminary> getAllPreliminary() throws SQLException;
	public int getPreliminaryCount() throws SQLException;
	public void createPreliminaryTiming() throws SQLException;
	public iPreliminary[][] getPreliminarySchedule() throws SQLException;
	public int preliminaryWithoutTiming() throws SQLException;
	public void addPreliminary() throws SQLException;
	
	public iScore getScoreFromPrelim(iFencer f);
	public iScore getScoreFromFinal(iFencer f);
	public List<iScore> getScoresPrelim() throws SQLException;
	public List<iScore> getScoresFinal() throws SQLException;
	public List<iScore>[] getScoresInGroups() throws SQLException;
	
	public boolean finishPreliminary() throws SQLException;
        public boolean isPreliminaryFinished();
	
	public List<iFinalround> getAllFinalrounds();
	
	public int getYellowFor(iFencer f) throws ObjectDeprecatedException;
	public int getRedFor(iFencer f) throws ObjectDeprecatedException;
	public int getBlackFor(iFencer f) throws ObjectDeprecatedException;
	
	public void dropOut(iFencer f) throws SQLException;
	
	public String getComment(iFencer f) throws SQLException;
	public void setComment(iFencer f, String comment) throws SQLException;
}
