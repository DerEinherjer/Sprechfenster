package Model;

import java.sql.SQLException;
import java.util.List;

public interface iPreliminary 
{		
	public int getGroup();
	public List<iFencer> getFencer();
	public int getRound();
	public int getLane();
	public boolean setTime(int round, int lane) throws SQLException;
	
	public void setFinished(boolean finisch) throws SQLException;
	
	public void setPoints(iFencer f, int points) throws SQLException;
	public int getPoints(iFencer f) throws SQLException;
	public int getOpponentPoints(iFencer f) throws SQLException;
	
	public boolean isFinished();
	public iFencer getWinner();
	
	public iTournament getTournament();
	
	public boolean removeParticipant(iFencer f) throws SQLException;
	public boolean addParticipant(iFencer f) throws SQLException;
	public boolean switchParticipantOut(iFencer out, iFencer in) throws SQLException; 
	
	public void setYellow(iFencer f, int count) throws SQLException;
	public void setRed(iFencer f, int count) throws SQLException;
	public void setBlack(iFencer f, int count) throws SQLException;
	public int getYellow(iFencer f);
	public int getRed(iFencer f);
	public int getBlack(iFencer f);
}
