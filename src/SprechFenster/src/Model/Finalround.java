package Model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Finalround implements iFinalrounds
{
	// -----
	private static Sync sync = (Sync) iSync.getInstance();
	private static Map<Integer, Finalround> finalrounds = new HashMap<>();
	
	static Finalround getFinalround(int id) throws SQLException
	{
		if(!finalrounds.containsKey(id))
			sync.loadFinalround(id);
		return finalrounds.get(id);
	}
	
	static List<Finalround> getFinalrounds(Tournament t)
	{
		List<Finalround> ret = new ArrayList<>();
		for(Finalround f : finalrounds.values())
			if(f.t.equals(t))
				ret.add(f);
		return ret;
	}
	
	// -----
	private int ID;
	private Tournament t;
	
	private Integer round = null;
	private Integer lane = null;
	private Fencer fencer1 = null;
	private Fencer fencer2 = null;
	private Integer pointsFor1 = null;
	private Integer pointsFor2 = null;
	private Boolean finished = null;
	
	private Finalround winnersround = null;
	private Finalround losersround = null;
	private Finalround preround1 = null;
	private Finalround preround2 = null;
	
	static String getSQLString()
	{
		return "CREATE TABLE IF NOT EXISTS Finalrunden(ID int NOT NULL AUTO_INCREMENT UNIQUE,"
				+ "TurnierID int,"
				+ "Runde int DEFAULT -1,"
				+ "Bahn int DEFAULT -1,"
				+ "Teilnehmer1 int DEFAULT -1,"
				+ "Teilnehmer2 int DEFAULT -1,"
				+ "PunkteVon1 int DEFAULT 0,"
				+ "PunkteVon2 int DEFAULT 0,"
				+ "Beendet boolean DEFAULT false,"
				+ "Gewinner int DEFAULT -1,"
				+ "Verlierer int DEFAULT -1);";
	}
	
	Finalround(Map<String, Object> set) throws ObjectExistExeption, SQLException
	{
		this.ID = (Integer) set.get("ID");
		
		if(finalrounds.containsKey(this.ID))
			throw new ObjectExistExeption(finalrounds.get(this.ID));
		finalrounds.put(this.ID, this);
		
		this.t = Tournament.getTournament((Integer) set.get("TurnierID".toUpperCase()));
		this.round = (Integer) set.get("Runde".toUpperCase());
		this.lane = (Integer) set.get("Bahn".toUpperCase());
		this.fencer1 = Fencer.getFencer((Integer) set.get("Teilnehmer1".toUpperCase()));
		this.fencer2 = Fencer.getFencer((Integer) set.get("Teilnehmer2".toUpperCase()));
		this.pointsFor1 = (Integer) set.get("PunkteVon1".toUpperCase());
		this.pointsFor2 = (Integer) set.get("PunkteVon2".toUpperCase());
		this.finished = (Boolean) set.get("Beendet".toUpperCase());
		this.winnersround = Finalround.getFinalround((Integer) set.get("Gewinner".toUpperCase()));
		this.losersround = Finalround.getFinalround((Integer) set.get("Verlierer".toUpperCase()));
		
		if(this.winnersround!=null)
			this.winnersround.initPrerounds(this);
		if(this.losersround!=null)
			this.losersround.initPrerounds(this);
	}
	
	void initPrerounds(Finalround f)
	{
		if(preround1==null)
			preround1 = f;
		else if(preround2==null)
			preround2 = f;
	}
	
	
	int getID(){return ID;}
	public int getRound(){return round;}
	public int getLane(){return lane;}
	public List<iFencer> getFencer(){List<iFencer> ret = new ArrayList<>();ret.add(fencer1);ret.add(fencer2);return ret;}
	public boolean setTime(int round, int lane) throws SQLException
	{
		if(sync.setTimeForFinalround(this, round, lane))
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
			sync.setPointsFR(ID, ((Fencer)f).getID(), points);
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
				//TODO
			}
			else
			{
				//TODO
			}
		}
	}
	
	Finalround getWinnerround()
	{
		return winnersround;
	}
	
	int printTree()
	{
		int deep = 0;
		if(preround1!=null)
			deep = preround1.printTree();
		String line = "";
		for(int i = 0; i< deep; i++)
			line+="\t";
		line+=ID;
		System.out.println(line);
		if(preround2!=null)
			deep = preround2.printTree();
		return deep+1;
	}
	
	@Override
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof Finalround))return false;
	    if(((Finalround)other).getID()==ID)
	    	return true;
	    return false;
	}
}
