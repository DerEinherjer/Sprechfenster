package Model;

import Model.Rounds.Preliminary;
import Model.Rounds.Finalround;
import Model.Rounds.iFinalround;
import Model.Rounds.iPreliminary;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Tournament implements iTournament
{
	// -----
	private static Map<Integer, Tournament> tournaments = new HashMap<>();
	static Sync sync;
	
        public static void ClearDatabaseCache()
        {
            tournaments.clear();
        }
        
	public static Tournament getTournament(int id) throws SQLException
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
	
	public Tournament(Map<String, Object> set) throws ObjectExistException, SQLException
	{
		this.ID = (Integer) set.get("ID");
		
		if(tournaments.containsKey(this.ID))
			throw new ObjectExistException(tournaments.get(this.ID));
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
		if(finishedPreliminary) return;
		
		sync.tournamentSetFinalRounds(rounds, ID);
		this.numberFinalrounds = rounds;
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
			catch(ObjectDeprecatedException e){}
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
			catch(ObjectDeprecatedException e){}
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
			catch(ObjectDeprecatedException e){list.remove(p);}
		
		iPreliminary[][] ret = new iPreliminary[last][lanes];
		
		for(iPreliminary p: list)
		{
			try
			{
				if(p.getRound()<1||p.getLane()<1) continue; //Noch nicht angesetzte begegnungen werden ignoriert
				ret[p.getRound()-1][p.getLane()-1]=p;
			}
			catch(ObjectDeprecatedException e){list.remove(p);}
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
	
	public void addWinPrelim(Fencer f) throws SQLException
	{
		if(isParticipant(f))
		{
			if(!scoresPrelim.containsKey(f))
				scoresPrelim.put(f, new Score(f));
			scoresPrelim.get(f).addWin();
		}
	}
	
	public void addWinFinal(Fencer f) throws SQLException
	{
		if(isParticipant(f))
		{
			if(!scoresFinal.containsKey(f))
				scoresFinal.put(f, new Score(f));
			scoresFinal.get(f).addWin();
		}
	}
	
	public void subWinPrelim(Fencer f) throws SQLException
	{
		if(isParticipant(f))
		{
			if(!scoresPrelim.containsKey(f))
				scoresPrelim.put(f, new Score(f));
			scoresPrelim.get(f).subWin();
		}
	}
	
	public void subWinFinal(Fencer f) throws SQLException
	{
		if(isParticipant(f))
		{
			if(!scoresFinal.containsKey(f))
				scoresFinal.put(f, new Score(f));
			scoresFinal.get(f).subWin();
		}
	}
	
	public void addHitsPrelim(Fencer f, int points) throws SQLException
	{
		if(isParticipant(f))
		{
			if(!scoresPrelim.containsKey(f))
				scoresPrelim.put(f, new Score(f));
			scoresPrelim.get(f).addHits(points);
		}
	}
	
	public void addHitsFinal(Fencer f, int points) throws SQLException
	{
		if(isParticipant(f))
		{
			if(!scoresFinal.containsKey(f))
				scoresFinal.put(f, new Score(f));
			scoresFinal.get(f).addHits(points);
		}
	}
	
	public void addGotHitPrelim(Fencer f, int points) throws SQLException
	{
		if(isParticipant(f))
		{
			if(!scoresPrelim.containsKey(f))
				scoresPrelim.put(f, new Score(f));
			scoresPrelim.get(f).addGotHit(points);
		}
	}
	
	public void addGotHitFinal(Fencer f, int points) throws SQLException
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
	
	private void createFinalrounds() throws SQLException, ObjectDeprecatedException
	{
		sync.createFinalRounds(this);
		setTimingForFinals();
	}
	
	private boolean isFinalroundsCreated() throws SQLException
	{
		return sync.finalroundsCount(this) != 0;
	}
	
	/*void printTree()
	{
		Finalround f = Finalround.getFinalrounds(this).get(0);
		while(f.getWinner()!=null)
			f = ((Finalround) f).getWinnerRound();
		f.printTree();
	}*/

	public int preliminaryWithoutTiming() throws SQLException 
	{
		int ret = 0;
		for(iPreliminary p : getAllPreliminary())
			try
			{
				if(p.getLane()<1||p.getRound()<1)
					ret ++;
			}
			catch(ObjectDeprecatedException e){}
		return ret;
	}
	
	public void addPreliminary() throws SQLException
	{
		sync.addPreliminary(this);
	}
	
	private void setFinishedPreliminary() throws SQLException
	{
            if(allPreliminaryFinished())
            {
		sync.setFinishedPreliminary(this);
		finishedPreliminary = true;
            }
                
	}
	
	public boolean isPreliminaryFinished()
	{
		return finishedPreliminary;
	}
        
        public boolean finishPreliminary() throws SQLException, ObjectDeprecatedException
        {
            if(allPreliminaryFinished())
            {
                createFinalrounds();
                setFinishedPreliminary();
                
                List<iScore>[] groupScores = getScoresInGroups();
                List<iScore> finalists = new ArrayList<>();
                List<iScore> wildcards = new ArrayList<>();
                
                int numberOfFinalists = (int) Math.pow(2, numberFinalrounds);
                
                //Get the first x fencers all groups 
                for(int i = 0;i< groups;i++)
                {
                    for(int c = 0; c <groupScores[i].size();c++)
                    {
                        if(c<(int)(numberOfFinalists/groups))
                            finalists.add(groupScores[i].get(c));
                        else
                            wildcards.add(groupScores[i].get(c));
                    }
                }
                
                //If need%groups != 0: take the best, out of all groups, who are
                //not participating in the finals yet
                for(int i = 0; i < (numberOfFinalists%groups);i++)
                {
                    finalists.add(wildcards.get(i));
                }
                //Sort the list so that best Fencer is first
                Collections.sort(finalists);
                
                //Get all Finalrounds wich are part of the first/lowest level
		List<Finalround> leafFinalRounds = new ArrayList<>();
                for(Finalround finalRound : Finalround.getFinalrounds(this))
		{
                    if(!finalRound.hasPrerounds())
                        leafFinalRounds.add(finalRound);
		}
                
                int c = 0;
                for(Finalround round : leafFinalRounds)
                {
                    try 
                    {
                        //Take the best remaining fencer
                        round.addParticipant(finalists.get(c).getFencer());
                        //Take the worst remaining fencer
                        round.addParticipant(finalists.get(finalists.size()-c-1).getFencer());
                    } 
                    catch (ObjectDeprecatedException ex) 
                    {
                        //We can ignore that because we created them a few lines above
                        Logger.getLogger(Tournament.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                return true;
            }
            else
            {
                return false;
            }
        }
	
	/*public boolean finishPreliminary() throws SQLException
	{
		if(allPreliminaryFinished())
		{
			createFinalrounds();
			setFinishedPreliminary();
			
			int numberOfFinalists = (int) Math.pow(2, numberFinalrounds);
			int wildcards = 0;
			List<Finalround> leafFinalRounds = new ArrayList<>();
			
			List<iScore>[] groupScores = getScoresInGroups();
			
			//Find the round in the leaves of the tree and collect them in leafFinalRounds
			for(Finalround finalRound : Finalround.getFinalrounds(this))
			{
				if(!finalRound.hasPrerounds())
					leafFinalRounds.add(finalRound);
			}
			
			int groupIndex;
                        int roundIndex;
                        int scoreIndex;
                        iFinalround leafFinalRound;
                        iScore fencerScore;
			for(int i = 0; i< numberOfFinalists; i++)
			{
                            //we iterate through groups and scores in the following way:
                            //take the best fencer score from each group until every group has been visited once.
                            //The first fencer is added to the first round, the second fencer to the second, and so on.
                            //then take the second best fencer score from each group until every group has been visited twice.
                            //And so on until we have enough fencers for the Finalists.
                            //If we can't get a score from a group because it doesn't have enough, we add a wildcard.
                            //The wildcard score is taken from a different group later (if posssible). Otherwise, it's a free win wildcard.
                            
                            //this index iterates through the groups
                            groupIndex = i%groups;
                            //this index is 0 for the first iteration through the groups, 1 for the second, etc.
                            scoreIndex = i/groups;
                            //this index iterates through the final rounds. We add 1 to i to mix the different groups.
                            //Otherwise it could happen that if two fencers from the same group advance into the finals,
                            //they would have to fight each other in the first round.
                            roundIndex = (i+1)%leafFinalRounds.size();  
                            
                            if(groupScores[groupIndex].size()>scoreIndex)//if group has enough scores
                                {
                                    leafFinalRound = leafFinalRounds.get(roundIndex);
                                    fencerScore = groupScores[groupIndex].get(scoreIndex);
                                    try 
                                    {
                                        leafFinalRound.addParticipant(fencerScore.getFencer());
                                    } 
                                    catch (ObjectDeprecatedException ex) 
                                    {
                                        //A deleted finalround is still in the finalround hash.
                                        Logger.getLogger(Tournament.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
				else
                                {
                                    //else we need to get a fencer from a different group later. Or add a free win if no fencer is available.
                                    wildcards++;
                                }
			}
			
			//Sort everyone out who is allready in the finals
			for(int i = 0; i<numberOfFinalists-wildcards; i++)
				if(groupScores[i%groups].size()>i/groups)
					groupScores[i%groups].remove(groupScores[i%groups].get(i/groups));
			
			List<iScore> wilds = new ArrayList<>();
			
			//Collect everyone who is NOT in the final in wilds
			for(int i = 0; i< groups; i++)
				for(int c = 0; c < groupScores[i].size(); c++)
					wilds.add(groupScores[i].get(c));
			
			//Get from all who are not allready in the final those who have the best hit-gotHit
			for(int i = 0; i < wildcards; i++)
			{
				int best = 0;
				for(int c = 1; c < wilds.size(); c ++)
					if(wilds.get(best).getHitDifference()<wilds.get(c).getHitDifference())
						best = c;
				
				int c = 0;
				while(!leafFinalRounds.get(c++).addParticipant(wilds.get(best).getFencer()));
			}
			
			for(Finalround tmp : leafFinalRounds)
			{
				if(tmp.getFencer().size()>0)
					scoresFinal.put((Fencer)tmp.getFencer().get(0), new Score((Fencer)tmp.getFencer().get(0)));
				if(tmp.getFencer().size()>1)
					scoresFinal.put((Fencer)tmp.getFencer().get(1), new Score((Fencer)tmp.getFencer().get(1)));
			}
			
					
                        return true;
		}
		return false;
	}*/
	
	boolean allPreliminaryFinished()
	{
		for(Preliminary p : Preliminary.getPreliminarys(this))
		{
			try
			{
				if(!p.isFinished())
					return false;
			}
			catch(ObjectDeprecatedException e){}
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
					if(p.isFencer((Fencer)f)&&!p.isFinished())
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
				catch(ObjectDeprecatedException e){}
			}
		}
		else
		{
			for(Finalround fr : Finalround.getFinalrounds(this))
			{
                            try
                            {
                                if(fr.isFencer(f)&&!fr.isFinished())
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
                            catch (ObjectDeprecatedException ex) 
                            {
                                //Can be ignored safly because we don't need to 
                                //remove a droped out fencer from a depricated fight
                            }
				
			}
		}
	}
	
	private void setTimingForFinals() throws SQLException, ObjectDeprecatedException
	{
		int round = 1;
                for(int i=0;;i++)
                {
                    List<iFinalround> matchOfRound = new ArrayList<>();
                    //get all matches for this round of the final
                    for(iFinalround match : getAllFinalrounds())
                        if(match.getFinalRound()==i)
                            matchOfRound.add(match);
                    
                    if(matchOfRound.size()==1)
                    {
                        for(iFinalround match : getAllFinalrounds())
                            if(match.getFinalRound()==-1)
                            {
                                match.setTime(round, 1);
                                break;
                            }
                        round++;
                        matchOfRound.get(0).setTime(round, 1);
                        
                        return;
                    }
                    
                    int lane = 1;
                    for(iFinalround match : matchOfRound)
                    {
                        match.setTime(round, lane);
                        lane++;
                        if(lane>getLanes())
                        {
                            lane = 1;
                            round++;
                        }
                        
                    }
                    round++;
                    lane = 1;
                }
 	}
	
	public int getYellowFor(iFencer f) throws ObjectDeprecatedException
	{
		int ret = 0;
		
		for(Preliminary p : Preliminary.getPreliminarys(this))
		{
			if(p.isFencer((Fencer)f))
				ret+=p.getYellow(f);
		}
		
		for(Finalround fr : Finalround.getFinalrounds(this))
		{
			if(fr.isFencer(f))
				ret+=fr.getYellow(f);
		}
		return ret;
	}
	
	public int getRedFor(iFencer f) throws ObjectDeprecatedException
	{
		int ret = 0;
		
		for(Preliminary p : Preliminary.getPreliminarys(this))
		{
			if(p.isFencer((Fencer)f))
				ret+=p.getRed(f);
		}
		
		for(Finalround fr : Finalround.getFinalrounds(this))
		{
			if(fr.isFencer(f))
				ret+=fr.getRed(f);
		}
		return ret;
	}
	
	public int getBlackFor(iFencer f) throws ObjectDeprecatedException
	{
		int ret = 0;
		
		for(Preliminary p : Preliminary.getPreliminarys(this))
		{
			if(p.isFencer((Fencer)f))
				ret+=p.getBlack(f);
		}
		
		for(Finalround fr : Finalround.getFinalrounds(this))
		{
			if(fr.isFencer(f))
				ret+=fr.getBlack(f);
		}
		return ret;
	}
	
	public String getComment(iFencer f) throws SQLException
	{
            if(isParticipant(f))
                return sync.getComment(this, (Fencer) f);
            return "";
	}
	
	public void setComment(iFencer f, String comment) throws SQLException
	{
		sync.setComment(this, (Fencer) f, comment);
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