package Model;

import java.sql.SQLException;
import java.util.List;

public interface iPreliminary 
{		
	public int getGroup() throws ObjectDeprecatedException;
	public List<iFencer> getFencer() throws ObjectDeprecatedException;
	public int getRound() throws ObjectDeprecatedException;
	public int getLane() throws ObjectDeprecatedException;
	public boolean setTime(int round, int lane) throws SQLException, ObjectDeprecatedException;
	
	public void setFinished(boolean finisch) throws SQLException, ObjectDeprecatedException;
	
	public void setPoints(iFencer f, int points) throws SQLException, ObjectDeprecatedException;
	public int getPoints(iFencer f) throws SQLException, ObjectDeprecatedException;
	public int getOpponentPoints(iFencer f) throws SQLException, ObjectDeprecatedException;
	
	public boolean isFinished() throws ObjectDeprecatedException;
	public iFencer getWinner() throws ObjectDeprecatedException;
	
	public iTournament getTournament() throws ObjectDeprecatedException;
	
	public boolean removeParticipant(iFencer f) throws SQLException, ObjectDeprecatedException;
	public boolean addParticipant(iFencer f) throws SQLException, ObjectDeprecatedException;
	public boolean switchParticipantOut(iFencer out, iFencer in) throws SQLException, ObjectDeprecatedException;
	public boolean isFencer(Fencer f) throws ObjectDeprecatedException;
	
	public void setYellow(iFencer f, int count) throws SQLException, ObjectDeprecatedException;
	public void setRed(iFencer f, int count) throws SQLException, ObjectDeprecatedException;
	public void setBlack(iFencer f, int count) throws SQLException, ObjectDeprecatedException;
	public int getYellow(iFencer f) throws ObjectDeprecatedException;
	public int getRed(iFencer f) throws ObjectDeprecatedException;
	public int getBlack(iFencer f) throws ObjectDeprecatedException;
	
	public void delete() throws ObjectDeprecatedException;
}
