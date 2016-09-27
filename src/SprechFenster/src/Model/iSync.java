package Model;

import java.sql.SQLException;
import java.util.List;

public abstract class iSync 
{
	private static Sync sync = null;
	
	public static iSync getInstance()
	{
		if(sync==null)
			sync = new Sync();
		return sync;
	}
	
	public iFencer createFencer(String name, String familyName) throws SQLException{return null;}
	public List<iFencer> getAllFencer() throws SQLException{return null;}
	
	public iTournament createTournament(String name) throws SQLException{return null;}
	public List<iTournament> getAllTournaments() throws SQLException{return null;}
	
	Fencer getFencer(int id) throws SQLException{return null;}
}
