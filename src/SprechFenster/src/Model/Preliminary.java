package Model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class Preliminary implements iPreliminary
{
	private int ID;
	private DBConnector con;
	
	private Integer tournamentID = null;
	private Integer group = null;
	private Integer round = null;
	private Integer lane = null;
	private Fencer fencer1 = null;
	private Fencer fencer2 = null;
	private Integer pointsFor1 = null;
	private Integer pointsFor2 = null;
	
	static String getSQLString()
	{
		return "CREATE TABLE Vorrunden( ID int NOT NULL AUTO_INCREMENT UNIQUE,"
				+ "TurnierID int,"
				+ "Gruppe int,"
				+ "Runde int,"
				+ "Bahn int,"
				+ "Teilnehmer1 int,"
				+ "Teilnehmer2 int,"
				+ "PunkteVon1 int,"
				+ "PunkteVon2 int);";
	}
	
	public Preliminary(int id, DBConnector con) 
	{
		this.ID = id;
		this.con = con;
	}

	void initTurnamentID(int id) {if(this.tournamentID == null) this.tournamentID = id;}
	void initGroup(int group) {if(this.group == null) this.group = group;}
	void initRound(int round) {if(this.round == null) this.round = round;}
	void initLane(int lane) {if(this.lane == null) this.lane = lane;}
	void initFencer1(Fencer f) {if(this.fencer1 == null) this.fencer1 = f;}
	void initFencer2(Fencer f) {if(this.fencer2 == null) this.fencer2 = f;}
	void initPointsFor(Fencer f, int points)
	{ 
		if(this.fencer1.equals(f)&&pointsFor1 == null)
			pointsFor1=points;
		else if(this.fencer2.equals(f)&&pointsFor2 == null)
			pointsFor2=points;
	}
	
	int getID(){return ID;}
	public int getGroup(){return group;}
	public int getRound(){return round;}
	public int getLane(){return lane;}
	public List<Fencer> getFencer(){List<Fencer> ret = new ArrayList<>();ret.add(fencer1);ret.add(fencer2);return ret;}
	
	public String toString()
	{
		return group + " | " + fencer1.getFamilyName() + " | " + fencer2.getFamilyName();
	}

	public boolean setTime(int round, int lane) throws SQLException
	{
		if(con.setTimeForPreliminary(this, round, lane))
		{
			this.round = round;
			this.lane = lane;
			return true;
		}
		return false;
	}
	
	public void setPoints(iFencer f, int points) throws SQLException
	{
		con.setPoints(ID, ((Fencer)f).getID(), points);
		if(fencer1.equals(f))
			pointsFor1 = points;
		if(fencer2.equals(f))
			pointsFor2 = points;
	}
	
	public int getPoints(iFencer f) throws SQLException
	{
		if(fencer1.equals(f))
			return pointsFor1;
		if(fencer2.equals(f))
			return pointsFor2;
		return -1;
	}
	
	public int getOpponentPoints(iFencer f) throws SQLException
	{
		if(fencer1.equals(f))
			return pointsFor2;
		if(fencer2.equals(f))
			return pointsFor1;
		return -1;
	}
	
	@Override
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof Preliminary))return false;
	    if(((Preliminary)other).getID()==ID)
	    	return true;
	    return false;
	}
}
