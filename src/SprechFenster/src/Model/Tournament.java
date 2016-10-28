package Model;

import java.sql.SQLException;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Tournament implements iTournament
{
	// -----
	private static Map<Integer, Tournament> tournaments = new HashMap<>();
	static Sync sync;
	
	static Tournament getTournament(int id) throws SQLException
	{
		if(!tournaments.containsKey(id))
			sync.loadTournament(id);
		return tournaments.get(id);
	}
	
	static List<Tournament> getAllTournaments()
	{
		List<Tournament> ret = new ArrayList<>();
		for(Map.Entry<Integer, Tournament> entry : tournaments.entrySet())
			ret.add(entry.getValue());
		return ret;
	}
	// -----
	private int ID;
	
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
		return "CREATE TABLE IF NOT EXISTS Turniere (ID int NOT NULL AUTO_INCREMENT UNIQUE,"
				   + "Name varchar(255) DEFAULT 'Nicht Angegeben',"
				   + "Datum varchar(11) DEFAULT '1970-01-01',"
				   + "Gruppen int DEFAULT 2,"
				   + "Finalrunden int DEFAULT 2,"
				   + "Bahnen int DEFAULT 2);";
	}
	
	public Tournament(Map<String, Object> set) throws ObjectExistExeption, SQLException
	{
		if(set == null)
			System.out.println("Nullpointer");
		this.ID = (Integer) set.get("ID");
		
		if(tournaments.containsKey(this.ID))
			throw new ObjectExistExeption(tournaments.get(this.ID));
		tournaments.put(this.ID, this);
		
		this.name = (String) set.get("Name".toUpperCase());
		this.date = (String) set.get("Datum".toUpperCase());
		this.groups = (Integer) set.get("Gruppen".toUpperCase());
		this.numberFinalrounds = (Integer) set.get("Finalrunden".toUpperCase());
		this.lanes = (Integer) set.get("Bahnen".toUpperCase());
		
		for(iFencer f : getAllParticipants())
		{
			scores.put((Fencer) f, new Score((Fencer)f));
			entryFee.put((Fencer) f, sync.getEntryFee(this, (Fencer)f));
			equipmentChecked.put((Fencer) f, sync.getEquipmentCheck(this, (Fencer) f));
		}
		
		if(!isFinalroundsCreated())
			createFinalround();
		
	}
	
	int getID(){return ID;}
	public String getName(){return name;}
	public String getDate(){return date;}
	public int getGroups(){return groups;}
	public int getFinalRounds(){return numberFinalrounds;}
	public int getLanes(){return lanes;}

	public void setName(String name) throws SQLException
	{
		sync.tournamentSetName(name, ID);
		this.name = name;
	}
	
	public void setDate(String date) throws SQLException
	{
		sync.tournamentSetDate(date, ID);
		this.date = date;
	}
	
	public void setGroups(int groups) throws SQLException
	{
		if(this.groups==groups)
			return;
		
		List<iFencer> tmp = new ArrayList<>();
		for(Fencer f : Fencer.getFencer(this))
		{
			if(getParticipantGroup(f)>groups||true)	//TODO Mit Stefan verhalten abstimmen
			{
				tmp.add(f);
				removeParticipant(f);
			}
		}
		
		sync.tournamentSetGroups(groups, ID);
		this.groups = groups;
		
		for(iFencer f : tmp)
			addParticipant(f);
	}
	
	public void setFinalRounds(int rounds) throws SQLException
	{
		sync.tournamentSetFinalRounds(rounds, ID);
		this.numberFinalrounds = rounds;
		
		sync.createFinalrounds(this);
	}
	
	public void setLanes(int lanes) throws SQLException
	{
		for(Preliminary p : Preliminary.getPreliminarys(this))
		{
			if(p.getLane()>lanes)
				p.setTime(0, 0);
		}
		sync.tournamentSetLanes(lanes, ID);
		this.lanes = lanes;
	}
	
	public String toString()
	{
		return ID+" | "+name;
	}
	
	public void addParticipant(iFencer f) throws SQLException
	{
		if(!(f instanceof Fencer)) return;
		int counts[] = sync.getGroupsMemberCount(this); 
		int index = 0;
		for(int i = 1; i< counts.length; i++)
			if(counts[i]<counts[index])
				index = i;
		addParticipant(f, index+1);
	}
	
	public void addParticipant(iFencer f, int group) throws SQLException
	{
		if(!(f instanceof Fencer)) return;
		sync.addParticipant(this, (Fencer)f, group);
		
		scores.put((Fencer) f, new Score((Fencer)f));
		entryFee.put((Fencer) f, sync.getEntryFee(this, (Fencer)f));
		equipmentChecked.put((Fencer) f, sync.getEquipmentCheck(this, (Fencer) f));
	}

	public List<iPreliminary> getAllPreliminary() throws SQLException 
	{
		List<iPreliminary> ret = new ArrayList<>(); 
		for(Preliminary tmp : Preliminary.getPreliminarys(this))
			ret.add(tmp);
		return ret;
	}
	
	public boolean isParticipant(iFencer f) throws SQLException
	{
		if(!(f instanceof Fencer)) return false;
		return sync.isFencerParticipant(this, (Fencer)f);
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
		for(Fencer f : Fencer.getFencer(this))
			ret.add(f);
		return ret;
	}
	
	public int getParticipantGroup(iFencer f) throws SQLException
	{
		return sync.getParticipantGroup(this, (Fencer)f);
	}
	
	public void removeParticipant(iFencer f) throws SQLException
	{
		sync.removeParticipant((Fencer)f);
		
		scores.remove((Fencer)f);
	}
	
	public void setEntryFee(iFencer f, boolean paid) throws SQLException
	{
		sync.setEntryFee(this, (Fencer)f, paid);
		entryFee.put((Fencer)f, paid);
		
	}
	
	public void setEquipmentCheck(iFencer f, boolean checked) throws SQLException
	{
		sync.setEquipmentCheck(this, (Fencer) f, checked);
		equipmentChecked.put((Fencer)f, checked);
	}
	
	public boolean getEntryFee(iFencer f) throws SQLException 
	{
		if(!entryFee.containsKey((Fencer)f))
			entryFee.put((Fencer)f, sync.getEntryFee(this, (Fencer)f));
		return entryFee.get((Fencer)f);
	}
	
	public boolean getEquipmentCheck(iFencer f) throws SQLException
	{
		if(!equipmentChecked.containsKey((Fencer) f))
			equipmentChecked.put((Fencer)f, sync.getEquipmentCheck(this, (Fencer)f));
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
	
	public int getPreliminaryCount() throws SQLException 
	{
		return getAllPreliminary().size();
	}
	
	public List<iFencer> getParticipantsOfGroup(int group) throws SQLException
	{
		List<iFencer> ret = new ArrayList<>();
		
		for(iFencer f : getAllParticipants())
		{
			if(getParticipantGroup(f)==group)
				ret.add(f);
		}
		
		return ret;
	}
	
	private Finalround createFinalround() throws SQLException
	{
		sync.createFinalRounds(this);
		return null;
	}
	
	private boolean isFinalroundsCreated() throws SQLException
	{
		return sync.finalroundsCount(this) != 0;
	}
	
	void printTree()
	{
		Finalround f = Finalround.getFinalrounds(this).get(0);
		while(f.getWinner()!=null)
			f = ((Finalround) f).getWinnerround();
		f.printTree();
	}

	public int preliminaryWithoutTiming() throws SQLException 
	{
		int ret = 0;
		for(iPreliminary p : getAllPreliminary())
			if(p.getLane()<1||p.getRound()<1)
				ret ++;
		return ret;
	}
	
	public void addPreliminary() throws SQLException
	{
		sync.addPreliminary(this);
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