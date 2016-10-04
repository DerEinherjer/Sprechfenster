package Model;

import java.sql.SQLException;
import java.util.List;

public interface iPreliminary 
{		
	public int getGroup();
	public List<Fencer> getFencer();
	public int getRound();
	public int getLane();
	public boolean setTime(int round, int lane) throws SQLException;
	
	public void setFinisched(boolean finisch) throws SQLException;
	
	public void setPoints(iFencer f, int points) throws SQLException;
	public int getPoints(iFencer f) throws SQLException;
	public int getOpponentPoints(iFencer f) throws SQLException;
	
	public iFencer getWinner();
}
