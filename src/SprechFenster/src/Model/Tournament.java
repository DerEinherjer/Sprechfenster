package Model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Tournament implements iTournament
{
	
	private int ID;
	private DBConnector con;
	
	private String name = null;
	private String date = null;
	private Integer groups = null;
	private Integer numberFinalrounds = null;
	private Integer lanes = null;
	private Map<Fencer, Boolean> entryFee = new HashMap<>();
	private Map<Fencer, Boolean> equipmentChecked = new HashMap<>();
	private Map<Fencer, Score> scores = new HashMap<>();
	
	static String getSQLString()
	{
		return "CREATE TABLE Turniere (ID int NOT NULL AUTO_INCREMENT UNIQUE,"
				   + "Name varchar(255),"
				   + "Datum varchar(11),"
				   + "Gruppen int,"
				   + "Finalrunden int,"
				   + "Bahnen int);";
	}
	
	Tournament(int id, DBConnector con) throws SQLException
	{
		this.ID = id;
		this.con = con;
		
		for(iFencer f : getAllParticipants())
		{
			scores.put((Fencer)f, new Score((Fencer)f));
		}
		
		try 
		{
			updatePreliminarys();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	private void updatePreliminarys() throws SQLException
	{
		for(Integer integer : con.getTournamentPreliminarys(this))
		{
			if(!preliminarys.containsKey(integer))
			{
				Preliminary tmp =  con.loadPreliminary(integer, this);
				preliminarys.put(integer, tmp);
				tmp.propagateScore();
			}
		}
	}
	
	void initName(String name){if(this.name == null) this.name = name;}
	void initDate(String date){if(this.date == null) this.date = date;}
	void initGroups(int groups){if(this.groups == null) this.groups = groups;}
	void initFinalRounds(int rounds){if(this.numberFinalrounds == null) this.numberFinalrounds = rounds;}
	void initLanes(int lanes){if(this.lanes == null) this.lanes = lanes;}
	
	int getID(){return ID;}
	public String getName(){return name;}
	public String getDate(){return date;}
	public int getGroups(){return groups;}
	public int getFinalRounds(){return numberFinalrounds;}
	public int getLanes(){return lanes;}

	public void setName(String name) throws SQLException
	{
		con.tournamentSetName(name, ID);
		this.name = name;
	}
	
	public void setDate(String date) throws SQLException
	{
		con.tournamentSetDate(date, ID);
		this.date = date;
	}
	
	public void setGroups(int groups) throws SQLException
	{
		List<iFencer> tmp = new ArrayList<>();
		for(iFencer f : getAllParticipants())
		{
			if(getParticipantGroup(f)>groups)
			{
				tmp.add(f);
				removeParticipant(f);
			}
		}
		
		con.tournamentSetGroups(groups, ID);
		this.groups = groups;
		
		for(iFencer f : tmp)
			addParticipant(f);
	}
	
	public void setFinalRounds(int rounds) throws SQLException
	{
		con.tournamentSetFinalRounds(rounds, ID);
		this.numberFinalrounds = rounds;
	}
	
	public void setLanes(int lanes) throws SQLException
	{
		for(Preliminary p : preliminarys.values())
		{
			if(p.getLane()>lanes)
				p.setTime(0, 0);
		}
		con.tournamentSetLanes(lanes, ID);
		this.lanes = lanes;
	}
	
	public String toString()
	{
		return ID+" | "+name;
	}
	
	public void addParticipant(iFencer f) throws SQLException
	{
		if(!(f instanceof Fencer)) return;
		int counts[] = con.getGroupsMemberCount(this); 
		int index = 0;
		for(int i = 1; i< counts.length; i++)
			if(counts[i]<counts[index])
				index = i;
		addParticipant(f, index+1);
	}
	
	public void addParticipant(iFencer f, int group) throws SQLException
	{
		if(!(f instanceof Fencer)) return;
		con.addParticipant(this, (Fencer)f, group);
		
		updatePreliminarys();
	}

	private Map<Integer, Preliminary> preliminarys = new HashMap<>();
	public List<iPreliminary> getAllPreliminary() throws SQLException 
	{
		List<iPreliminary> ret = new ArrayList<>();
		for(Integer integer : con.getTournamentPreliminarys(this))
		{
			if(!preliminarys.containsKey(integer))
				preliminarys.put(integer, con.loadPreliminary(integer, this));
			ret.add(preliminarys.get(integer));
		}
		return ret;
	}
	
	public boolean isParticipant(iFencer f) throws SQLException
	{
		if(!(f instanceof Fencer)) return false;
		return con.isFencerParticipant(this, (Fencer)f);
	}
	
	public void createPreliminaryTiming() throws SQLException
	{
		List<iPreliminary> prelim = getAllPreliminary();
		Map<iPreliminary, Integer> lastForPrelim = new HashMap<>();
		
		for(iPreliminary p : prelim)
		{
			lastForPrelim.put(p, -1);
		}
		
		for(int i = 0;!prelim.isEmpty(); i++)
		{
			iPreliminary next = prelim.get(0);
			for(iPreliminary p : prelim)
			{
				if(lastForPrelim.get(next)>lastForPrelim.get(p))
				{
					next = p;
				}
			}
			
			if(lastForPrelim.get(next)==i/lanes) continue;
			
			next.setTime((i/lanes)+1, (i%lanes)+1);
			
			prelim.remove(next);
			for(iPreliminary p : prelim)
			{
				if(p.getFencer().contains(next.getFencer().get(0))||p.getFencer().contains(next.getFencer().get(1)))
					lastForPrelim.put(p, i/lanes);
			}
		}
	}
	
	public iPreliminary[][] getPreliminarySchedule() throws SQLException
	{
		List<iPreliminary> list = getAllPreliminary();
		int last = 0;
		for(iPreliminary p: list)
			if(p.getRound()>last)
				last = p.getRound();
		
		iPreliminary[][] ret = new iPreliminary[last][lanes];
		
		for(iPreliminary p: list)
		{
			if(p.getRound()<1||p.getLane()<1) continue; //Noch nicht angesetzte begegnungen werden ignoriert
			ret[p.getRound()-1][p.getLane()-1]=p;
		}
		
		return ret;
	}
	
	public List<iFencer> getAllParticipants() throws SQLException
	{
		List<iFencer> ret = new ArrayList<>();
		for(Integer i : con.getAllParticipants(this))
			ret.add(iSync.getInstance().getFencer(i));
		return ret;
	}
	
	public int getParticipantGroup(iFencer f) throws SQLException
	{
		return con.getParticipantGroup(this, (Fencer)f);
	}
	
	public void removeParticipant(iFencer f) throws SQLException
	{
		con.removeParticipant((Fencer)f);
		
		preliminarys = new HashMap<>();
		scores = new HashMap<>();
		updatePreliminarys();
	}
	
	public void setEntryFee(iFencer f, boolean paid) throws SQLException
	{
		con.setEntryFee(this, (Fencer)f, paid);
		entryFee.put((Fencer)f, paid);
		
	}
	
	public void setEquipmentCheck(iFencer f, boolean checked) throws SQLException
	{
		con.setEquipmentCheck(this, (Fencer) f, checked);
		equipmentChecked.put((Fencer)f, checked);
	}
	
	public boolean getEntryFee(iFencer f) throws SQLException 
	{
		if(!entryFee.containsKey((Fencer)f))
			entryFee.put((Fencer)f, con.getEntryFee(this, (Fencer)f));
		return entryFee.get((Fencer)f);
	}
	
	public boolean getEquipmentCheck(iFencer f) throws SQLException
	{
		if(!equipmentChecked.containsKey((Fencer) f))
			equipmentChecked.put((Fencer)f, con.getEquipmentCheck(this, (Fencer)f));
		return equipmentChecked.get((Fencer)f);
	}
	
	void addWin(Fencer f) throws SQLException
	{
		if(isParticipant(f))
		{
			if(!scores.containsKey(f))
				scores.put(f, new Score(f));
			scores.get(f).addWin();
		}
	}
	
	void subWin(Fencer f) throws SQLException
	{
		if(isParticipant(f))
		{
			if(!scores.containsKey(f))
				scores.put(f, new Score(f));
			scores.get(f).subWin();
		}
	}
	
	void addHits(Fencer f, int points) throws SQLException
	{
		if(isParticipant(f))
		{
			if(!scores.containsKey(f))
				scores.put(f, new Score(f));
			scores.get(f).addHits(points);
		}
	}
	
	void addGotHit(Fencer f, int points) throws SQLException
	{
		if(isParticipant(f))
		{
			if(!scores.containsKey(f))
				scores.put(f, new Score(f));
			scores.get(f).addGotHit(points);
		}
	}
	
	public Score getScoreFrom(iFencer f)
	{
		return scores.get((Fencer)f);
	}
	
	public List<iScore> getScores() throws SQLException
	{
		List<iScore> ret = new ArrayList<>();
		for(iFencer f : getAllParticipants())
		{
			ret.add(scores.get((Fencer)f));
		}
		Collections.sort(ret);
		return ret;
	}
	
	public List<iScore>[] getScoresInGroups() throws SQLException
	{
		List<iScore> ret[] = new ArrayList[getGroups()];
		for(iFencer f : getAllParticipants())
		{
			ret[getParticipantGroup(f)-1].add(scores.get((Fencer)f));
		}
		for(int i=0;i<getGroups();i++)
			Collections.sort(ret[i]);
		return ret;
	}
	
	Map<Integer, Finalrounds> finalrounds = new HashMap<>();
	
	void add(Finalrounds r)
	{
		finalrounds.put(r.getID(), r);
	}
	
	Finalrounds loadAFinalRound(int id) throws SQLException
	{
		if(finalrounds.containsKey(id))
			return finalrounds.get(id);
		else
		{
			con.loadFinalround(id, this);
		}
		return null;
	}
	
	@Override
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof Tournament))return false;
	    if(((Tournament)other).getID()==ID)
	    	return true;
	    return false;
	}
}