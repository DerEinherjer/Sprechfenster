package Model;

import java.sql.SQLException;
import java.util.List;

public interface iFinalrounds 
{
	public List<iFencer> getFencer();
	public int getRound();
	public int getLane();
	public boolean setTime(int round, int lane) throws SQLException;
	
	public boolean removeParticipant(iFencer f) throws SQLException;
	public boolean addParticipant(iFencer f) throws SQLException;
	public boolean switchParticipantOut(iFencer out, iFencer in) throws SQLException; 
}
