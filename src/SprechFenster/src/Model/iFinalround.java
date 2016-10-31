package Model;

import java.sql.SQLException;
import java.util.List;

public interface iFinalround 
{
	public List<iFencer> getFencer();
	public int getRound();
	public int getLane();
	public boolean setTime(int round, int lane) throws SQLException;
	
	public boolean removeParticipant(iFencer f) throws SQLException;
	public boolean addParticipant(iFencer f) throws SQLException;
	public boolean switchParticipantOut(iFencer out, iFencer in) throws SQLException; 
	
	public void setFinished(boolean finisch) throws SQLException;
	
	public void setPoints(iFencer f, int points) throws SQLException;
	public int getPoints(iFencer f) throws SQLException;
	public int getOpponentPoints(iFencer f) throws SQLException;
}
