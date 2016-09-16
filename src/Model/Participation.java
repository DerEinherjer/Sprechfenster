package Model;

public class Participation 
{
	static String getSQLString()
	{
		return "CREATE TABLE Teilnahme(TurnierID int,"
				 + "FechterID int,"
				 + "Gruppe int)";
	}
}