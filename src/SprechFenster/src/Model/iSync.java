package Model;

import java.sql.SQLException;
import java.util.List;
import java.util.Observable;


public abstract class iSync extends Observable
{
	private static Sync sync = null;
	
	public static iSync getInstance()
	{
		try 
		{
			if(sync==null)
				sync = new Sync();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return sync;
	}
	
	public iFencer createFencer(String name, String familyName) throws SQLException{return null;}
	public List<iFencer> getAllFencer() throws SQLException{return null;}
	
	public iTournament createTournament(String name) throws SQLException{return null;}
	public List<iTournament> getAllTournaments() throws SQLException{return null;}
	
}
