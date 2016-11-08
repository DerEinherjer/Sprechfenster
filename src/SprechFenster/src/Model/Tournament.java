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
	private Boolean finishedPreliminary = null;
	private Map<Fencer, Boolean> entryFee = new HashMap<>();
	private Map<Fencer, Boolean> equipmentChecked = new HashMap<>();
	private Map<Fencer, Score> scoresPrelim = new HashMap<>();
	private Map<Fencer, Score> scoresFinal = new HashMap<>();
	private Map<Fencer, Boolean> dropedOut = new HashMap<>();
	
	static String getSQLString()
	{
		return "CREATE TABLE IF NOT EXISTS Turniere (ID int NOT NULL AUTO_INCREMENT UNIQUE,"
				   + "Name varchar(255) DEFAULT 'Nicht Angegeben',"
				   + "Datum varchar(11) DEFAULT '1970-01-01',"
				   + "Gruppen int DEFAULT 2,"
				   + "Finalrunden int DEFAULT 2,"
				   + "Bahnen int DEFAULT 2,"
				   + "InFinalrunden BOOLEAN DEFAULT FALSE);";
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
		this.finishedPreliminary = (Boolean) set.get("InFinalrunden".toUpperCase());
		
		for(iFencer f : getAllParticipants())
		{
			scoresPrelim.put((Fencer) f, new Score((Fencer)f));
			scoresFinal.put((Fencer) f, new Score((Fencer) f));
			entryFee.put((Fencer) f, sync.getEntryFee(this, (Fencer)f));
			dropedOut.put((Fencer) f, sync.getDropedOut(this, (Fencer)f));
			equipmentChecked.put((Fencer) f, sync.getEquipmentCheck(this, (Fencer) f));
		}
		
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
			try
			{
				if(p.getLane()>lanes)
					p.setTime(0, 0);
			}
			catch(ObjectDeprecatedExeption e){}
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
		
		scoresPrelim.put((Fencer) f, new Score((Fencer)f));
		scoresFinal.put((Fencer) f, new Score((Fencer)f));
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
			try
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
			catch(ObjectDeprecatedExeption e){}
		}
	}
	
	public iPreliminary[][] getPreliminarySchedule() throws SQLException
	{
		List<iPreliminary> list = getAllPreliminary();
		int last = 0;
		for(iPreliminary p: list)
			try
			{
				if(p.getRound()>last)
					last = p.getRound();
			}
			catch(ObjectDeprecatedExeption e){list.remove(p);}
		
		iPreliminary[][] ret = new iPreliminary[last][lanes];
		
		for(iPreliminary p: list)
		{
			try
			{
				if(p.getRound()<1||p.getLane()<1) continue; //Noch nicht angesetzte begegnungen werden ignoriert
				ret[p.getRound()-1][p.getLane()-1]=p;
			}
			catch(ObjectDeprecatedExeption e){list.remove(p);}
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
		if(isParticipant(f))
		{
			sync.removeParticipant((Fencer)f);
			
			scoresPrelim.remove((Fencer)f);
			scoresFinal.remove((Fencer)f);
		}
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
	
	void addWinPrelim(Fencer f) throws SQLException
	{
		if(isParticipant(f))
		{
			if(!scoresPrelim.containsKey(f))
				scoresPrelim.put(f, new Score(f));
			scoresPrelim.get(f).addWin();
		}
	}
	
	void addWinFinal(Fencer f) throws SQLException
	{
		if(isParticipant(f))
		{
			if(!scoresFinal.containsKey(f))
				scoresFinal.put(f, new Score(f));
			scoresFinal.get(f).addWin();
		}
	}
	
	void subWinPrelim(Fencer f) throws SQLException
	{
		if(isParticipant(f))
		{
			if(!scoresPrelim.containsKey(f))
				scoresPrelim.put(f, new Score(f));
			scoresPrelim.get(f).subWin();
		}
	}
	
	void subWinFinal(Fencer f) throws SQLException
	{
		if(isParticipant(f))
		{
			if(!scoresFinal.containsKey(f))
				scoresFinal.put(f, new Score(f));
			scoresFinal.get(f).subWin();
		}
	}
	
	void addHitsPrelim(Fencer f, int points) throws SQLException
	{
		if(isParticipant(f))
		{
			if(!scoresPrelim.containsKey(f))
				scoresPrelim.put(f, new Score(f));
			scoresPrelim.get(f).addHits(points);
		}
	}
	
	void addHitsFinal(Fencer f, int points) throws SQLException
	{
		if(isParticipant(f))
		{
			if(!scoresFinal.containsKey(f))
				scoresFinal.put(f, new Score(f));
			scoresFinal.get(f).addHits(points);
		}
	}
	
	void addGotHitPrelim(Fencer f, int points) throws SQLException
	{
		if(isParticipant(f))
		{
			if(!scoresPrelim.containsKey(f))
				scoresPrelim.put(f, new Score(f));
			scoresPrelim.get(f).addGotHit(points);
		}
	}
	
	void addGotHitFinal(Fencer f, int points) throws SQLException
	{
		if(isParticipant(f))
		{
			if(!scoresFinal.containsKey(f))
				scoresFinal.put(f, new Score(f));
			scoresFinal.get(f).addGotHit(points);
		}
	}
	
	public Score getScoreFromPrelim(iFencer f)
	{
		return scoresPrelim.get((Fencer)f);
	}
	
	public Score getScoreFromFinal(iFencer f)
	{
		return scoresFinal.get((Fencer)f);
	}
	
	public List<iScore> getScoresPrelim() throws SQLException
	{
		List<iScore> ret = new ArrayList<>();
		for(iFencer f : getAllParticipants())
		{
			ret.add(scoresPrelim.get((Fencer)f));
		}
		Collections.sort(ret);
		return ret;
	}
	
	public List<iScore> getScoresFinal() throws SQLException
	{
		List<iScore> ret = new ArrayList<>();
		for(iFencer f : getAllParticipants())
		{
			ret.add(scoresFinal.get((Fencer)f));
		}
		Collections.sort(ret);
		return ret;
	}
	
	public List<iScore>[] getScoresInGroups() throws SQLException
	{
		List<iScore> ret[] = new ArrayList[getGroups()];
		for(int i=0;i<ret.length;i++)
			ret[i] = new ArrayList<>();
		
		for(iFencer f : getAllParticipants())
		{
			ret[getParticipantGroup(f)-1].add(scoresPrelim.get((Fencer)f));
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
	
	private void createFinalrounds() throws SQLException
	{
		sync.createFinalRounds(this);//TODO
		setTimingForFinals();
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
			try
			{
				if(p.getLane()<1||p.getRound()<1)
					ret ++;
			}
			catch(ObjectDeprecatedExeption e){}
		return ret;
	}
	
	public void addPreliminary() throws SQLException
	{
		sync.addPreliminary(this);
	}
	
	void setFinishedPreliminary() throws SQLException
	{
		sync.setFinishedPreliminary(this);
		finishedPreliminary = true;
	}
	
	public boolean isPreliminaryFinished()
	{
		return finishedPreliminary;
	}
	
	public boolean finishPreliminary() throws SQLException
	{
		if(allPreliminaryFinished())
		{
			createFinalrounds();
			
			int finalist = (int) Math.pow(2, numberFinalrounds);
			int finalistPerGroup = finalist/groups;
			int wildcards = finalist - (finalistPerGroup*groups);
			List<Finalround> f = new ArrayList<>();
			
			List<iScore>[] s = getScoresInGroups();
			
			//Find the round in the leaves of the tree and collect them in f
			for(Finalround tmp : Finalround.getFinalrounds(this))
			{
				if(!tmp.hasPrerounds())
					f.add(tmp);
			}
			
			//First part of selection:
			//As long as I can take 1 from every group (have more groups than free places) take one from every group
			for(int i = 0; i<finalist-wildcards; i++)
			{
				if(s[i%groups].size()>i/groups)//Has that group enouth members?
					f.get((i+1)%(f.size())).addParticipant(s[i%groups].get(i/groups).getFencer());
				else
					wildcards++;
			}
			
			//Sort everyone out who is allready in the finals
			for(int i = 0; i<finalist-wildcards; i++)
				if(s[i%groups].size()>i/groups)
					s[i%groups].remove(s[i%groups].get(i/groups));
			
			List<iScore> wilds = new ArrayList<>();
			
			//Collect everyone who is NOT in the final in wilds
			for(int i = 0; i< groups; i++)
				for(int c = 0; c < s[i].size(); c++)
					wilds.add(s[i].get(c));
			
			//Get from all who are not allready in the final those who have the best hit-gotHit
			for(int i = 0; i < wildcards; i++)
			{
				int best = 0;
				for(int c = 1; c < wilds.size(); c ++)
					if(wilds.get(best).getHitDifference()<wilds.get(c).getHitDifference())
						best = c;
				
				int c = 0;
				while(!f.get(c++).addParticipant(wilds.get(best).getFencer()));
			}
			
			for(Finalround tmp : f)
			{
				if(tmp.getFencer().size()>0)
					scoresFinal.put((Fencer)tmp.getFencer().get(0), new Score((Fencer)tmp.getFencer().get(0)));
				if(tmp.getFencer().size()>1)
					scoresFinal.put((Fencer)tmp.getFencer().get(1), new Score((Fencer)tmp.getFencer().get(1)));
			}
			
					
			setFinishedPreliminary();
		}
		return false;
	}
	
	boolean allPreliminaryFinished()
	{
		for(Preliminary p : Preliminary.getPreliminarys(this))
		{
			try
			{
				if(!p.isFinished())
					return false;
			}
			catch(ObjectDeprecatedExeption e){}
		}
		return true;
	}
	
	public List<iFinalround> getAllFinalrounds() 
	{
		List<iFinalround> ret = new ArrayList<>();
		for(Finalround f :Finalround.getFinalrounds(this))
			ret.add(f);
		return ret;
	}
	
	public void dropOut(iFencer f) throws SQLException
	{
		if(!finishedPreliminary)
		{
			for(Preliminary p : Preliminary.getPreliminarys(this))
			{
				try
				{
					if(p.isFencer((Fencer)f))
					{
						for(iFencer tmp : p.getFencer())
						{
							if(tmp.equals(f))
								p.setPoints(tmp, 0);
							else
								p.setPoints(tmp, 5);
						}
						p.setFinished(true);
						dropedOut.put((Fencer) f, true);
					}
				}
				catch(ObjectDeprecatedExeption e){}
			}
		}
		else
		{
			for(Finalround fr : Finalround.getFinalrounds(this))
			{
				if(fr.isFencer(f))
				{
					for(iFencer tmp : fr.getFencer())
					{
						if(tmp.equals(f))
							fr.setPoints(tmp, 0);
						else
							fr.setPoints(tmp, 5);
					}
					fr.setFinished(true);
					dropedOut.put((Fencer) f, true);
				}
			}
		}
	}
	
	private void setTimingForFinals() throws SQLException
	{
		List<Finalround> f = new ArrayList<>();
		for(Finalround tmp : Finalround.getFinalrounds(this))
			if(tmp.getPre1()==null&&tmp.getPre2()==null)
				f.add(tmp);
		
		while(true)
		{
			int round = 1;
			int lane = 1;
			List<Finalround> next = new ArrayList<>();
			Finalround losersround = null;
			
			for(Finalround tmp : f)
			{
				tmp.setTime(round, lane);
				lane++;
				if(lane>this.lanes)
				{
					lane=1;
					round++;
				}
				if(tmp.getLoserRound()!=null)
					losersround = tmp.getLoserRound();
				if(tmp.getWinnerround()!=null&&!next.contains(tmp.getWinnerround()))
					next.add(tmp.getWinnerround());
			}
			lane = 1;
			round++;
			if(losersround!=null)
				losersround.setTime(round++, lane);
			
			if(next.isEmpty())
				break;
			else
				f = next;
		}
			
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