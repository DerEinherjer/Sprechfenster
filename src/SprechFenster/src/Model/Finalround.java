package Model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sun.security.action.GetLongAction;

public class Finalround implements iFinalround
{
	// -----
	static Sync sync;
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
	
	private Integer yellowFor1 = null;
	private Integer redFor1 = null;
	private Integer blackFor1 = null;
	private Integer yellowFor2 = null;
	private Integer redFor2 = null;
	private Integer blackFor2 = null;
	
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
				+ "Verlierer int DEFAULT -1,"
				+ "GelbVon1 int DEFAULT 0,"
				+ "RotVon1 int DEFAULT 0,"
				+ "SchwarzVon1 int DEFAULT 0,"
				+ "GelbVon2 int DEFAULT 0,"
				+ "RotVon2 int DEFAULT 0,"
				+ "SchwarzVon2 int DEFAULT 0);";
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
		
		this.yellowFor1 = (Integer) set.get("GeldVon1".toUpperCase());
		this.redFor1 = (Integer) set.get("RotVon1".toUpperCase());
		this.blackFor1 = (Integer) set.get("SchwarzVon1".toUpperCase());
		this.yellowFor2 = (Integer) set.get("GeldVon2".toUpperCase());
		this.redFor2 = (Integer) set.get("RotVon2".toUpperCase());
		this.blackFor2 = (Integer) set.get("SchwarzVon2".toUpperCase());
		
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
	public List<iFencer> getFencer()
	{
		List<iFencer> ret = new ArrayList<>();
		if(fencer1!=null) ret.add(fencer1);
		if(fencer2!=null) ret.add(fencer2);
		return ret;
	}
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
	
	public void setFinished(boolean finish) throws SQLException
	{
		if(finished!=finish)
		{
			finished = finish;
			if(finished)
			{
				if(winnersround!=null)
					winnersround.addParticipant(getWinner());
				if(losersround!=null)
					losersround.addParticipant(getLoser());
				
				t.addWinFinal((Fencer)getWinner());
				
				t.addHitsFinal(fencer1, pointsFor1);
				t.addHitsFinal(fencer2, pointsFor2);
				
				t.addGotHitFinal(fencer1, pointsFor2);
				t.addGotHitFinal(fencer2, pointsFor1);
			}
			else
			{
				if(winnersround!=null)
					winnersround.removeParticipant(getWinner());
				if(losersround!=null)
					losersround.removeParticipant(getLoser());
				
				t.subWinFinal((Fencer)getWinner());

				t.addHitsFinal(fencer1, -pointsFor1);
				t.addHitsFinal(fencer2, -pointsFor2);
				
				t.addGotHitFinal(fencer1, -pointsFor2);
				t.addGotHitFinal(fencer2, -pointsFor1);
			}
		}
	}
	
	Fencer getLoser()
	{
		if(finished)
		{
			if(pointsFor1>pointsFor2)
				return fencer2;
			if(pointsFor1<pointsFor2)
				return fencer1;
		}
		return null;
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

	public boolean removeParticipant(iFencer f) throws SQLException 
	{
		if(fencer1!=null && fencer1.equals(f))
		{
			sync.removeParticipantFromFinal(this, (Fencer) f);
			fencer1 = null;
			pointsFor1 = 0;
			return true;
		}
		else if(fencer2!=null && fencer2.equals(f))
		{
			sync.removeParticipantFromFinal(this, (Fencer) f);
			fencer2 = null;
			pointsFor2 = 0;
			return true;
		}
		return false;
	}

	public boolean addParticipant(iFencer f) throws SQLException 
	{

		if(fencer1 == null)
		{
			sync.addParticipantToFinal(this, (Fencer) f);
			fencer1 = (Fencer)f;
			return true;
		}
		else if(fencer2 == null)
		{
			sync.addParticipantToFinal(this, (Fencer) f);
			fencer2 = (Fencer)f;
			return true;
		}
		return false;
	}
	
	boolean hasPrerounds()
	{
		if(preround1==null && preround2==null)
			return false;
		return true;
	}

	public boolean switchParticipantOut(iFencer out, iFencer in) throws SQLException 
	{
		if(fencer1.equals(out)&&!fencer2.equals(in))
		{
			sync.switchParticipantsInPrelim(this, (Fencer) out, (Fencer) in);
			fencer1 = (Fencer)in;
			pointsFor1 = 0;
			return true;
		}
		else if(fencer2.equals(out)&&!fencer1.equals(in))
		{
			sync.switchParticipantsInPrelim(this, (Fencer) out, (Fencer) in);
			fencer2 = (Fencer)in;
			pointsFor2 = 0;
			return true;
		}
		return false;
	}
	
	public void setYellow(iFencer f, int count) throws SQLException
	{
		if(f.equals(fencer1))
		{
			sync.setYellowFinal(this, (Fencer)f, count);
			yellowFor1 = count;
		}
		else if(f.equals(fencer2))
		{
			sync.setYellowFinal(this, (Fencer)f, count);
			yellowFor2 = count;
		}
	}
	
	public void setRed(iFencer f, int count) throws SQLException
	{
		if(f.equals(fencer1))
		{
			sync.setRedFinal(this, (Fencer)f, count);
			redFor1 = count;
		}
		else if(f.equals(fencer2))
		{
			sync.setRedFinal(this, (Fencer)f, count);
			redFor2 = count;
		}
	}
	
	public void setBlack(iFencer f, int count) throws SQLException
	{
		if(f.equals(fencer1))
		{
			sync.setBlackFinal(this, (Fencer)f, count);
			blackFor1 = count;
		}
		else if(f.equals(fencer2))
		{
			sync.setBlackFinal(this, (Fencer)f, count);
			blackFor2 = count;
		}
	}
	
	public int getYellow(iFencer f)
	{
		if(f.equals(fencer1))
			return yellowFor1;
		if(f.equals(fencer2))
			return yellowFor2;
		return -1;
	}
	
	public int getRed(iFencer f)
	{
		if(f.equals(fencer1))
			return redFor1;
		if(f.equals(fencer2))
			return redFor2;
		return -1;
	}
	
	public int getBlack(iFencer f)
	{
		if(f.equals(fencer1))
			return blackFor1;
		if(f.equals(fencer2))
			return blackFor2;
		return -1;
	}
	
	public boolean isFinished()
	{
		return finished;
	}
	
	public boolean isFencer(iFencer f)
	{
		if(fencer1.equals(f)||fencer2.equals(f))
			return true;
		return false;
	}
}
