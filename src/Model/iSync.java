package Model;

import java.sql.SQLException;
import java.util.List;

public interface iSync 
{
	public iFencer createFencer(String name, String familyName) throws SQLException;
	public List<iFencer> getAllFencer() throws SQLException;
	
	public iTournament createTournament(String name) throws SQLException;
	public List<iTournament> getAllTournaments() throws SQLException;
}
