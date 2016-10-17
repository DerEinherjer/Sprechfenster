package Model;

import java.sql.SQLException;
import java.util.List;

public class Test 
{
	public static void main(String args[])
	{
		try 
		{
			iSync sync = iSync.getInstance();
			
			iTournament t = sync.createTournament("Test");

		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}	
	}
}
