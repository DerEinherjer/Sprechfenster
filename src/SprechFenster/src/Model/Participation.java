package Model;

class Participation 
{
	static String getSQLString()
	{
		return "CREATE TABLE IF NOT EXISTS Teilnahme (TurnierID int,"
				 + "FechterID int,"
				 + "Gruppe int,"
				 + "Startgeld boolean DEFAULT FALSE,"
				 + "Ausruestungskontrolle boolean DEFAULT FALSE)";
	}
}