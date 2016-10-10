package Model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Finalrounds implements iFinalrounds
{
	private int ID;
	private Tournament t;
	private DBConnector con;
	
	private Integer round = null;
	private Integer lane = null;
	private Fencer fencer1 = null;
	private Fencer fencer2 = null;
	private Integer pointsFor1 = null;
	private Integer pointsFor2 = null;
	private Boolean finished = null;
	
	private Finalrounds winnersround = null;
	private Finalrounds losersround = null;
	private Finalrounds preround1 = null;
	private Finalrounds preround2 = null;

	private Boolean propagated = false;
	
	static String getSQLString()
	{
		return "CREATE TABLE Finalrunden( ID int NOT NULL AUTO_INCREMENT UNIQUE,"
				+ "TurnierID int,"
				+ "Runde int,"
				+ "Bahn int,"
				+ "Teilnehmer1 int,"
				+ "Teilnehmer2 int,"
				+ "PunkteVon1 int,"
				+ "PunkteVon2 int,"
				+ "Beendet boolean,"
				+ "Vorher1 int,"
				+ "Vorher2 int,"
				+ "Gewinner int,"
				+ "Verlierer int);";
	}
	
	Finalrounds(int id, DBConnector con) 
	{
		this.ID = id;
		this.con = con;
	}
	
	void initTournament(Tournament t) {if(this.t == null) this.t= t;}
	void initRound(int round) {if(this.round == null) this.round = round;}
	void initLane(int lane) {if(this.lane == null) this.lane = lane;}
	void initFencer1(Fencer f) {if(this.fencer1 == null) this.fencer1 = f;}
	void initFencer2(Fencer f) {if(this.fencer2 == null) this.fencer2 = f;}
	
	void initPreround1(Finalrounds r) {if(this.preround1 == null) this.preround1=r;}
	void initPreround2(Finalrounds r) {if(this.preround2 == null) this.preround2=r;}
	void initWinnersRound(Finalrounds r) {if(this.winnersround == null) this.winnersround = r;}
	void initLosersRound(Finalrounds r) {if(this.losersround == null) this.losersround = r;}
	
	void initPointsFor(Fencer f, int points)
	{ 
		if(this.fencer1.equals(f)&&pointsFor1 == null)
			pointsFor1=points;
		else if(this.fencer2.equals(f)&&pointsFor2 == null)
			pointsFor2=points;
	}
	void initFinished(boolean finished) {if(this.finished==null) this.finished=finished;}
	
	int getID(){return ID;}
	public int getRound(){return round;}
	public int getLane(){return lane;}
	public List<iFencer> getFencer(){List<iFencer> ret = new ArrayList<>();ret.add(fencer1);ret.add(fencer2);return ret;}
	public boolean setTime(int round, int lane) throws SQLException
	{
		if(con.setTimeForFinalround(this, round, lane))
		{
			this.round = round;
			this.lane = lane;
			return true;
		}
		return false;
	}
	
	public void setPoints(iFencer f, int points) throws SQLException
	{
		if(!finished)
		{
			con.setPoints(ID, ((Fencer)f).getID(), points);
			if(fencer1.equals(f))
				pointsFor1 = points;
			if(fencer2.equals(f))
				pointsFor2 = points;
		}
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
	
	public iFencer getWinner()
	{
		if(finished)
		{
			if(pointsFor1>pointsFor2)
				return fencer1;
			if(pointsFor1<pointsFor2)
				return fencer2;
		}
		return null;
	}
	
	public void setFinished(boolean finish)
	{
		if(finished!=finish)
		{
			finished = finish;
			if(finished)
			{
				
			}
			else
			{
				
			}
		}
	}
}
