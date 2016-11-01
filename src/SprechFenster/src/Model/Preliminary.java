package Model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Preliminary implements iPreliminary
{
	// -----
	private static Map<Integer, Preliminary> preliminarys = new HashMap<>();
	static Sync sync;
	
	static Preliminary getPreliminary(int id) throws SQLException
	{
		if(!preliminarys.containsKey(id))
			sync.loadPreliminary(id);
		return preliminarys.get(id);
	}
	
	static List<Preliminary> getPreliminarys(Tournament t)
	{
		List<Preliminary> ret = new ArrayList<>();
		for(Map.Entry<Integer, Preliminary> entry : preliminarys.entrySet())
		{
			if(entry.getValue().t.equals(t))
				ret.add(entry.getValue());
		}
		return ret;
	}
	
	static void deletePreliminarys(Fencer f) throws SQLException
	{
		List<Preliminary> remove = new ArrayList<>();
		for(Preliminary p : preliminarys.values())
		{
			if(p.isFencer(f))
			{
				remove.add(p);//DO NOT DELET HERE
							  //IT FUCKS UP THE ITERATOR
			}
		}
		
		for(Preliminary p : remove)
			p.delete();
		
	}
	// -----
	private int ID;
	private Tournament t;
	
	private Integer group = null;
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
	
	private Boolean propagated = false;
	
	static String getSQLString()
	{
		return "CREATE TABLE IF NOT EXISTS Vorrunden (ID int NOT NULL AUTO_INCREMENT UNIQUE,"
				+ "TurnierID int,"
				+ "Gruppe int,"
				+ "Runde int DEFAULT -1,"
				+ "Bahn int DEFAULT -1,"
				+ "Teilnehmer1 int DEFAULT -1,"
				+ "Teilnehmer2 int DEFAULT -1,"
				+ "PunkteVon1 int DEFAULT 0,"
				+ "PunkteVon2 int DEFAULT 0,"
				+ "Beendet boolean DEFAULT FALSE,"
				+ "GelbVon1 int DEFAULT 0,"
				+ "RotVon1 int DEFAULT 0,"
				+ "SchwarzVon1 int DEFAULT 0,"
				+ "GelbVon2 int DEFAULT 0,"
				+ "RotVon2 int DEFAULT 0,"
				+ "SchwarzVon2 int DEFAULT 0);";
	}
	
	Preliminary(Map<String, Object> set) throws ObjectExistExeption, SQLException
	{
		this.ID = (Integer) set.get("ID");
		
		if(preliminarys.containsKey(this.ID))
			throw new ObjectExistExeption(preliminarys.get(this.ID));
		preliminarys.put(this.ID, this);
		
		this.t = Tournament.getTournament((Integer) set.get("TurnierID".toUpperCase()));
		
		this.group = (Integer) set.get("Gruppe".toUpperCase());
		this.round = (Integer) set.get("Runde".toUpperCase());
		this.lane = (Integer) set.get("Bahn".toUpperCase());
		this.fencer1 = Fencer.getFencer((Integer)set.get("Teilnehmer1".toUpperCase())); 
		this.fencer2 = Fencer.getFencer((Integer)set.get("Teilnehmer2".toUpperCase()));
		this.pointsFor1 = (Integer) set.get("PunkteVon1".toUpperCase());
		this.pointsFor2 = (Integer) set.get("PunkteVon2".toUpperCase());
		this.finished = (Boolean) set.get("Beendet".toUpperCase());
	}
	
	int getID(){return ID;}
	public int getGroup(){return group;}
	public int getRound(){return round;}
	public int getLane(){return lane;}
	public List<iFencer> getFencer(){List<iFencer> ret = new ArrayList<>();ret.add(fencer1);ret.add(fencer2);return ret;}
	
	public String toString()
	{
		return group + " | " + fencer1.getFamilyName() + " | " + fencer2.getFamilyName();
	}

	public boolean setTime(int round, int lane) throws SQLException
	{
		if(finished || t.isPrelimFinished()) return false;
		if(sync.setTimeForPreliminary(this, round, lane))
		{
			this.round = round;
			this.lane = lane;
			return true;
		}
		return false;
	}
	
	public void setPoints(iFencer f, int points) throws SQLException
	{
		if(finished || t.isPrelimFinished()) return;
		
		if(!finished)
		{
			sync.setPoints(ID, ((Fencer)f).getID(), points);
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
		if(t.isPrelimFinished()) return;
		
		if(finish != finished)
		{
			finished=finish;
			if(finished)
			{
				propagated = true;
				
				if(pointsFor1>pointsFor2)
					t.addWinPrelim(fencer1);
				else if(pointsFor1 < pointsFor2)
					t.addWinPrelim(fencer2);
				
				t.addHitsPrelim(fencer1, pointsFor1);
				t.addHitsPrelim(fencer2, pointsFor2);
				t.addGotHitPrelim(fencer1, pointsFor2);
				t.addGotHitPrelim(fencer2, pointsFor1);
			}
			else
			{
				if(propagated)
				{
					if(pointsFor1>pointsFor2)
						t.subWinPrelim(fencer1);
					else if(pointsFor1 < pointsFor2)
						t.subWinPrelim(fencer2);
					
					t.addHitsPrelim(fencer1, -pointsFor1);
					t.addHitsPrelim(fencer2, -pointsFor2);
					t.addGotHitPrelim(fencer1, -pointsFor2);
					t.addGotHitPrelim(fencer2, -pointsFor1);
				}
			}
		}
	}
	
	void propagateScore() throws SQLException
	{
		propagated = true;
		
		if(finished)
		{
			if(pointsFor1>pointsFor2)
				t.addWinPrelim(fencer1);
			else if(pointsFor1 < pointsFor2)
				t.addWinPrelim(fencer2);
			
			t.addHitsPrelim(fencer1, pointsFor1);
			t.addHitsPrelim(fencer2, pointsFor2);
			t.addGotHitPrelim(fencer1, pointsFor2);
			t.addGotHitPrelim(fencer2, pointsFor1);
		}
	}
	
	public boolean isFinished()
	{
		return finished;
	}
	
	public iTournament getTournament()
	{
		return t;
	}
	
	public boolean removeParticipant(iFencer f) throws SQLException
	{
		if(finished || t.isPrelimFinished()) return false;
		
		if(fencer1!=null && fencer1.equals(f))
		{
			sync.removeParticipantFromPrelim(this, (Fencer) f);
			fencer1 = null;
			pointsFor1 = 0;
			return true;
		}
		else if(fencer2!=null && fencer2.equals(f))
		{
			sync.removeParticipantFromPrelim(this, (Fencer) f);
			fencer2 = null;
			pointsFor2 = 0;
			return true;
		}
		return false;
	}
	
	public boolean addParticipant(iFencer f) throws SQLException
	{
		if(finished || t.isPrelimFinished()) return false;
		
		if(fencer1 == null)
		{
			sync.addParticipantToPrelim(this, (Fencer) f);
			fencer1 = (Fencer)f;
			return true;
		}
		else if(fencer2 == null)
		{
			sync.addParticipantToPrelim(this, (Fencer) f);
			fencer2 = (Fencer)f;
			return true;
		}
		return false;
	}
	
	public boolean switchParticipantOut(iFencer out, iFencer in) throws SQLException
	{
		if(finished || t.isPrelimFinished()) return false;
		
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
	
	void delete()
	{
		if(finished || t.isPrelimFinished()) return; //TODO Finished trotzdem löschen??
		
		preliminarys.remove(this.ID);
		this.ID = -1;
	}
	
	boolean isFencer(Fencer f)
	{
		if(fencer1!=null && fencer1.equals(f))
			return true;
		if(fencer1!=null && fencer2.equals(f))
			return true;
		return false;
	}
	
	public void setYellow(iFencer f, int count) throws SQLException
	{
		if(f.equals(fencer1))
		{
			sync.setYellowPrelim(this, (Fencer)f, count);
			yellowFor1 = count;
		}
		else if(f.equals(fencer2))
		{
			sync.setYellowPrelim(this, (Fencer)f, count);
			yellowFor2 = count;
		}
	}
	
	public void setRed(iFencer f, int count) throws SQLException
	{
		if(f.equals(fencer1))
		{
			sync.setRedPrelim(this, (Fencer)f, count);
			redFor1 = count;
		}
		else if(f.equals(fencer2))
		{
			sync.setRedPrelim(this, (Fencer)f, count);
			redFor2 = count;
		}
	}
	
	public void setBlack(iFencer f, int count) throws SQLException
	{
		if(f.equals(fencer1))
		{
			sync.setBlackPrelim(this, (Fencer)f, count);
			blackFor1 = count;
		}
		else if(f.equals(fencer2))
		{
			sync.setBlackPrelim(this, (Fencer)f, count);
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
