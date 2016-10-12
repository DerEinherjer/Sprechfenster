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
	// -----
	private int ID;
	private Tournament t;
	private DBConnector con;
	
	private Integer group = null;
	private Integer round = null;
	private Integer lane = null;
	private Fencer fencer1 = null;
	private Fencer fencer2 = null;
	private Integer pointsFor1 = null;
	private Integer pointsFor2 = null;
	private Boolean finished = null;
	
	private Boolean propagated = false;
	
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
				+ "PunkteVon2 int,"
				+ "Beendet boolean;";
	}
	
	Preliminary(Map<String, Object> set, DBConnector con) throws ObjectExistExeption, SQLException
	{
		this.ID = (Integer) set.get("ID");
		this.con = con;
		
		if(preliminarys.containsKey(this.ID))
			throw new ObjectExistExeption(preliminarys.get(this.ID));
		preliminarys.put(this.ID, this);
		
		this.t = iSync.getInstance().getTournament((Integer) set.get("TurnierID"));
		this.group = (Integer) set.get("Gruppe");
		this.round = (Integer) set.get("Runde");
		this.lane = (Integer) set.get("Bahn");
		this.fencer1 = iSync.getInstance().getFencer((Integer)set.get("Teilnehmer1")); 
		this.fencer2 = iSync.getInstance().getFencer((Integer)set.get("Teilnehmer2"));
		this.pointsFor1 = (Integer) set.get("PunkteVon1");
		this.pointsFor2 = (Integer) set.get("PunkteVon2");
		this.finished = (Boolean) set.get("Beendet");
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
	
	public void setFinished(boolean finish) throws SQLException 
	{
		if(finish != finished)
		{
			finished=finish;
			if(finished)
			{
				propagated = true;
				
				if(pointsFor1>pointsFor2)
					t.addWin(fencer1);
				else if(pointsFor1 < pointsFor2)
					t.addWin(fencer2);
				
				t.addHits(fencer1, pointsFor1);
				t.addHits(fencer2, pointsFor2);
				t.addGotHit(fencer1, pointsFor2);
				t.addGotHit(fencer2, pointsFor1);
			}
			else
			{
				if(propagated)
				{
					if(pointsFor1>pointsFor2)
						t.subWin(fencer1);
					else if(pointsFor1 < pointsFor2)
						t.subWin(fencer2);
					
					t.addHits(fencer1, -pointsFor1);
					t.addHits(fencer2, -pointsFor2);
					t.addGotHit(fencer1, -pointsFor2);
					t.addGotHit(fencer2, -pointsFor1);
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
				t.addWin(fencer1);
			else if(pointsFor1 < pointsFor2)
				t.addWin(fencer2);
			
			t.addHits(fencer1, pointsFor1);
			t.addHits(fencer2, pointsFor2);
			t.addGotHit(fencer1, pointsFor2);
			t.addGotHit(fencer2, pointsFor1);
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
		if(fencer1.equals(f))
		{
			con.removeParticipantFromPrelim(this, (Fencer) f);
			fencer1 = null;
			return true;
		}
		else if(fencer2.equals(f))
		{
			con.removeParticipantFromPrelim(this, (Fencer) f);
			fencer2 = null;
			return true;
		}
		return false;
	}
	
	public boolean addParticipant(iFencer f) throws SQLException
	{
		if(fencer1 == null)
		{
			con.addParticipantToPrelim(this, (Fencer) f);
			fencer1 = (Fencer)f;
			return true;
		}
		else if(fencer2 == null)
		{
			con.addParticipantToPrelim(this, (Fencer) f);
			fencer2 = (Fencer)f;
			return true;
		}
		return false;
	}
	
	public boolean switchParticipantOut(iFencer out, iFencer in) throws SQLException
	{
		if(fencer1.equals(out)&&!fencer2.equals(in))
		{
			con.switchParticipantsInPrelim(this, (Fencer) out, (Fencer) in);
			fencer1 = (Fencer)in;
			return true;
		}
		else if(fencer2.equals(out)&&!fencer1.equals(in))
		{
			con.switchParticipantsInPrelim(this, (Fencer) out, (Fencer) in);
			fencer2 = (Fencer)in;
			return true;
		}
		return false;
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
