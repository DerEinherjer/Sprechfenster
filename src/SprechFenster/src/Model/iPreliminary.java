package Model;

import java.sql.SQLException;
import java.util.List;

public interface iPreliminary 
{		
	public int getGroup() throws ObjectDeprecatedExeption;
	public List<iFencer> getFencer() throws ObjectDeprecatedExeption;
	public int getRound() throws ObjectDeprecatedExeption;
	public int getLane() throws ObjectDeprecatedExeption;
	public boolean setTime(int round, int lane) throws SQLException, ObjectDeprecatedExeption;
	
	public void setFinished(boolean finisch) throws SQLException, ObjectDeprecatedExeption;
	
	public void setPoints(iFencer f, int points) throws SQLException, ObjectDeprecatedExeption;
	public int getPoints(iFencer f) throws SQLException, ObjectDeprecatedExeption;
	public int getOpponentPoints(iFencer f) throws SQLException, ObjectDeprecatedExeption;
	
	public boolean isFinished() throws ObjectDeprecatedExeption;
	public iFencer getWinner() throws ObjectDeprecatedExeption;
	
	public iTournament getTournament() throws ObjectDeprecatedExeption;
	
	public boolean removeParticipant(iFencer f) throws SQLException, ObjectDeprecatedExeption;
	public boolean addParticipant(iFencer f) throws SQLException, ObjectDeprecatedExeption;
	public boolean switchParticipantOut(iFencer out, iFencer in) throws SQLException, ObjectDeprecatedExeption;
	public boolean isFencer(Fencer f) throws ObjectDeprecatedExeption;
	
	public void setYellow(iFencer f, int count) throws SQLException, ObjectDeprecatedExeption;
	public void setRed(iFencer f, int count) throws SQLException, ObjectDeprecatedExeption;
	public void setBlack(iFencer f, int count) throws SQLException, ObjectDeprecatedExeption;
	public int getYellow(iFencer f) throws ObjectDeprecatedExeption;
	public int getRed(iFencer f) throws ObjectDeprecatedExeption;
	public int getBlack(iFencer f) throws ObjectDeprecatedExeption;
	
	public void delete() throws ObjectDeprecatedExeption;
}
