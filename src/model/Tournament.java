package model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import model.DBConnection.DBEntetyRepresenter;
import model.DBConnection.DBTournamentRepresenter;
import static model.DBConnection.DBTournamentRepresenter.createTournamnet;
import model.rounds.Finalround;
import model.rounds.Preliminary;
import model.rounds.Round;
import model.rounds.iFinalround;
import model.rounds.iPreliminary;

public class Tournament extends Observable implements DBEntetyRepresenter, iTournament
{
    @Override
    public void init() throws SQLException
    {
        DBTournamentRepresenter.createTable();
        DBTournamentRepresenter.loadTournaments();
    }
    
    @Override
    public void onStartUp() throws SQLException
    {
        for(Map.Entry<Integer, Tournament> entry : tournaments.entrySet())
        {
            entry.getValue().updateParticipants();
        }
    }

    @Override
    public void onExit()
    {
        Map<Integer, Tournament> tmp = tournaments;
        tournaments = new HashMap<>();
        
        for (Map.Entry<Integer, Tournament> entry : tmp.entrySet())
        {
            entry.getValue().invalidate();
        }
    }
    
    //#########################################################################
    
    //static Sync sync;
    private static Map<Integer, Tournament> tournaments = new HashMap<>();
    
    static List<Tournament> getAllTournaments()
    {
        List<Tournament> ret = new ArrayList<>();
        for(Map.Entry<Integer, Tournament> entry : tournaments.entrySet())
        {
            ret.add(entry.getValue());
        }
        return ret;
    }
    
    public static Tournament getTournament(int id)
    {
        return tournaments.get(id);
    }
    
    //#########################################################################
    
    private int ID;

    private boolean isValid = true;

    private String name = null;
    private String date = null;
    private Integer groups = null;
    private Integer numberFinalrounds = null;
    private Integer lanes = null;
    private Status status = null;
    private Boolean separatePreliminaryGroups = null;
   
    enum Status
    {
        PreparingPhase(0),
        PreliminaryPhase(1),
        FinalPhase(2),
        Completed(3);
        
        private int value;
        
        private Status(int value)
        {
            this.value = value;
        }
        
        public static Status valueOf(int value)
        {
            switch(value)
            {
                case 0: return PreparingPhase;
                case 1: return PreliminaryPhase;
                case 2: return FinalPhase;
                case 3: return Completed;
                default: return PreparingPhase;
            }
        }
    }
    
    private Map<Integer, Participation2> participants = new HashMap<>();
    private Map<Fencer, Score> preliminaryScore = new HashMap<>();

    /**
     * DON'T USE THIS!
     * IT IS FOR THE USE OF THE INTERFACE ONLY AND WILL CRASH THE PROGRAMM IF 
     * USED OTHERWISE.
     */
    public Tournament(){}
    
    public Tournament(String name) throws SQLException
    {
        this.ID = createTournamnet(name);
        tournaments.put(ID, this);
        
        this.name = name;
        this.date = "1970-01-01";
        this.groups = 2;
        this.numberFinalrounds = 2;
        this.lanes = 2;
        this.status = Status.PreparingPhase;
        this.separatePreliminaryGroups = true;
    }
    
    public Tournament (Map<String, Object> set) throws ObjectExistException, SQLException
    {
        this.ID = (Integer) set.get("ID");

        if (tournaments.containsKey(this.ID))
        {
            throw new ObjectExistException(tournaments.get(this.ID));
        }
        tournaments.put(this.ID, this);

        this.name = (String) set.get("Name".toUpperCase());
        this.date = (String) set.get("Datum".toUpperCase());
        this.groups = (Integer) set.get("Gruppen".toUpperCase());
        this.numberFinalrounds = (Integer) set.get("Finalrunden".toUpperCase());
        this.lanes = (Integer) set.get("Bahnen".toUpperCase());
        this.status = Status.valueOf(Integer.parseInt((String)set.get("Status".toUpperCase())));
        this.separatePreliminaryGroups = (Boolean) set.get("VorgruppenSeparieren".toUpperCase());//TODO: Include in Test-Case
        
        switch(status)
        {
            case Completed:
            case FinalPhase:
            case PreliminaryPhase:
                
            case PreparingPhase:
        }
    }
    
    @Override
    public int getID() 
    {
        if(!isValid) throw new ObjectDeprecatedException();
        return ID;
    }

    @Override
    public String getName()
    {
        if(!isValid) throw new ObjectDeprecatedException();
        return name;
    }

    @Override
    public String getDate()
    {
        if(!isValid) throw new ObjectDeprecatedException();
        return date;
    }

    @Override
    public int getGroups()
    {
        if(!isValid) throw new ObjectDeprecatedException();
        return groups;
    }

    @Override
    public int getFinalRounds()
    {
        if(!isValid) throw new ObjectDeprecatedException();
        return numberFinalrounds;
    }

    @Override
    public int getLanes()
    {
        if(!isValid) throw new ObjectDeprecatedException();
        return lanes;
    }

    @Override
    public void setName(String name) throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        if(status != Status.PreparingPhase) return;
        
        DBTournamentRepresenter.setName(ID, name);
        this.name = name;
        
        setChanged();
        notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
    }

    @Override
    public void setDate(String date) throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        if(status != Status.PreparingPhase) return;
        
        DBTournamentRepresenter.setDate(ID, date);
        this.date = date;
        
        setChanged();
        notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
    }
    
    @Override
    public void setGroups(int groups) throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        if(status != Status.PreparingPhase) return;
      
        if (this.groups == groups)
        {
            return;
        }

        DBTournamentRepresenter.setGroups(ID, groups);
        this.groups = groups;
        
        setChanged();
        notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
    }
    
    @Override
    public void setFinalRounds(int rounds) throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        if(status != Status.PreparingPhase) return;

        DBTournamentRepresenter.setFinalRounds(ID, rounds);
        this.numberFinalrounds = rounds;

        setChanged();
        notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
    }
    
    @Override
    public void setLanes(int lanes) throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        if(status != Status.PreparingPhase) return;
      
        DBTournamentRepresenter.setLanes(ID, lanes);
        this.lanes = lanes;
        
        setChanged();
        notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
    }
    
    @Override
    public void addParticipant(iFencer f) throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        if(status != Status.PreparingPhase) return;
        
        int min = Integer.MAX_VALUE;
        int group = -1;
        for(int i = 1; i <= groups; i++)
        {
            int tmp = Participation2.getParticipantsFromGroup(this, i).size();
            if(tmp<min)
            {
                min = tmp;
                group = i;
            }
        }
        addParticipant(f, group);
    }
    
    @Override
    public void addParticipant(iFencer f, int group) throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        if(status != Status.PreparingPhase) return;
        
        participants.put(((Fencer)f).getID(),new Participation2(this, (Fencer) f, group));
        
        setChanged();
        notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
    }
    
    private void updateParticipants()
    {
        if(!isValid) throw new ObjectDeprecatedException();
        
        participants = Participation2.getAllParticipantsForTournament(this);
    }

    @Override
    public boolean isParticipant(iFencer f) throws SQLException 
    {
        if(!isValid) throw new ObjectDeprecatedException();
        return participants.get(((Fencer)f).getID())!=null;
    }

    @Override
    public List<iFencer> getAllParticipants() throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        List<iFencer> ret = new ArrayList<>();
        for (Map.Entry<Integer, Participation2> entry : participants.entrySet())
        {
            ret.add(entry.getValue().getFencer());
        }
        return ret;
    }

    @Override
    public List<iFencer> getParticipantsOfGroup(int group) throws SQLException 
    {
        if(!isValid) throw new ObjectDeprecatedException();
        List<iFencer> ret = new ArrayList<>();
        for(Participation2 tmp : Participation2.getParticipantsFromGroup(this, group))
        {
            ret.add(tmp.getFencer());
        }
        return ret;
    }

    @Override
    public int getParticipantGroup(iFencer f) throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        if(participants.get((Fencer)f)!=null)
        {
            return participants.get((Fencer)f).getGroup();
        }
        else
            return -1;
    }

    @Override
    public void removeParticipant(iFencer f) throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        if(status != Status.PreparingPhase) return;
        
        Participation2 tmp = participants.get(f.getID());
        participants.remove(f.getID());
        
        tmp.delete();
    }

    @Override
    public void dropOut(iFencer f) throws SQLException 
    {
        if(!isValid) throw new ObjectDeprecatedException();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setEntryFee(iFencer f, boolean paid) throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        if(status != Status.PreparingPhase) return;
        if(!isParticipant(f)) return;
        
        participants.get(((Fencer)f).getID()).setEntryFee(paid);
        
        setChanged();
        notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
    }

    @Override
    public void setEquipmentCheck(iFencer f, boolean checked) throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        if(status != Status.PreparingPhase) return;
        if(!isParticipant(f)) return;
        
        participants.get(((Fencer)f).getID()).setEquipmentCheck(checked);
        
        setChanged();
        notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
    }

    @Override
    public boolean getEntryFee(iFencer f) throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        if(!isParticipant(f)) return false;
        
        return participants.get(((Fencer)f).getID()).getEntryFee();
    }

    @Override
    public boolean getEquipmentCheck(iFencer f) throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        if(!isParticipant(f)) return false;
        
        return participants.get(((Fencer)f).getID()).getEquipmentCheck();
    }

    @Override
    public List<iPreliminary> getAllPreliminary() throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        if(status == Status.PreparingPhase) return new ArrayList<>();
        
        return Preliminary.getPreliminaryOfTournament(this);
    }

    @Override
    public int getPreliminaryCount() throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public iPreliminary[][] getPreliminarySchedule() throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        
        List<iPreliminary> prelims = Preliminary.getPreliminaryOfTournament(this);
        
        int lanes = -1;
        int rounds = -1;
        for(iPreliminary tmp : prelims)
        {
            if(tmp.getLane()>lanes)
                lanes = tmp.getLane();
            if(tmp.getRound()>rounds)
                rounds = tmp.getRound();
        }
        
        iPreliminary[][] ret = new iPreliminary[rounds+1][lanes+1];
        
        for(iPreliminary tmp : prelims)
        {
            ret[tmp.getRound()][tmp.getLane()] = tmp;
        }
        
        return ret;
    }

    @Override
    public iScore getScoreFromPrelim(iFencer f)
    {
        if(!isValid) throw new ObjectDeprecatedException();
        if(status == Status.PreparingPhase) return null;
        
        if(!preliminaryScore.containsKey(f))
            preliminaryScore.put((Fencer)f, new Score((Fencer)f));
        
        return (iScore)preliminaryScore.get(f);
    }

    @Override
    public iScore getScoreFromFinal(iFencer f)
    {
        if(!isValid) throw new ObjectDeprecatedException();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<iScore> getScoresPrelim() throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<iScore> getScoresFinal() throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<iScore>[] getScoresInGroups() throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<iFinalround> getAllFinalrounds()
    {
        if(!isValid) throw new ObjectDeprecatedException();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getYellowFor(iFencer f) throws ObjectDeprecatedException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getRedFor(iFencer f) throws ObjectDeprecatedException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getBlackFor(iFencer f) throws ObjectDeprecatedException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getComment(iFencer f) throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        if(!isParticipant(f)) return "";
        
        return participants.get(((Fencer)f).getID()).getComment();
    }

    @Override
    public void setComment(iFencer f, String comment) throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        if(!isParticipant(f)) return;
        
        participants.get(((Fencer)f).getID()).setComment(comment);
        
        setChanged();
        notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
    }
    
    private void createPeliminaryMatches() throws SQLException
    {
        for(int i = 0; i < this.groups; i++)
        {
            List<iFencer> fencers = getParticipantsOfGroup(i);
            
            for(int x = 0; x < fencers.size()-1; x++)
            {
                for(int y = x+1; y < fencers.size(); y++)
                {
                    
                }
            }
        }
    }

    @Override
    public void delete() throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void startPreliminary() throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        
        List<List<Preliminary>> prelims = new ArrayList<>();
        
        for(int g = 0;g < groups; g++)
        {
            prelims.add(new ArrayList<>());
            List<iFencer> tmp = getParticipantsOfGroup(g);
            for(int i = 0; i < tmp.size()-1; i++)
            {
                for(int r = i+1; r < tmp.size(); r++)
                {
                    prelims.get(g).add(new Preliminary(this, (Fencer)tmp.get(i), (Fencer)tmp.get(r)));
                }
            }
        }
        
        //Should the preliminarys not be separated by groups shove them in the prelims[0] list
        if(!separatePreliminaryGroups)
        {
            for(int i = prelims.size()-1; i > 0; i--)
            {
                prelims.get(0).addAll(prelims.get(i));
                prelims.remove(i);
            }
        }
        
        int time = 0;
        
        //Iterate over the groups and do the matchmaking seperatly (except if the groups got merged in the loop above)
        for(int i = 0; i < prelims.size(); i++)
        {
            List<Preliminary> prelim = prelims.get(i);
            Map<iFencer, Integer> lastFights = new HashMap<>();
            
            for (iPreliminary p : prelim)
            {
                lastFights.put(p.getFencer().get(0), -1);
                lastFights.put(p.getFencer().get(1), -1);
            }
            
            for (; !prelim.isEmpty(); time++) 
            {
                for(int lane = 0; lane < this.lanes; lane++)
                {
                    Preliminary next = null;
                    int lastFight = Integer.MAX_VALUE;

                    //Get match witch the fighter who waits the longest
                    for (Preliminary p : prelim) 
                    {
                        int f1 = lastFights.get(p.getFencer().get(0));
                        int f2 = lastFights.get(p.getFencer().get(1));
                        //If one of the fighter already fight at this moment ignore the match
                        if(f1 != time && f2 != time)
                        {
                            //Take the match whith the fencer who hasn't fought the longest
                            if(f1 < lastFight || f2 < lastFight)
                            {
                                lastFight = (f1<f2)? f1 : f2;
                                next = p;
                            }
                        }
                    }
                    if(next == null) break;//All not already places PrelimFights have a fighter who is already fighting at this point in time
                    
                    //Delete the match from the 
                    prelim.remove(next);
                    next.setTime(time, lane);
                    lastFights.put(next.getFencer().get(0), time);
                    lastFights.put(next.getFencer().get(1), time);
                }
            }
            
        }
        
        status = Status.PreliminaryPhase;
    }
    
    private boolean areAllPrelimsFinished()
    {
        for(iPreliminary p : Preliminary.getPreliminaryOfTournament(this))
        {
            if(!p.isFinished())
                return false;
        }
        return true;
    }
    
    public List<iPreliminary> getPreliminaryFromGroup(int group) throws SQLException
    {
        List<iPreliminary> ret = new ArrayList<>();
        for(iPreliminary p : Preliminary.getPreliminaryOfTournament(this))
        {
            if(p.getPreliminaryGroup()==group)
               ret.add(p);
        }
        return ret;
    }
    
    private List<Score> getScoreFromGroup() throws SQLException
    {
        List<Score> ret = new ArrayList<>();
        for(int g = 0; g < groups; g++)
        {
            for(iFencer f : getParticipantsOfGroup(g))
            {
                if(!preliminaryScore.containsKey(f))
                    preliminaryScore.put((Fencer) f, new Score((Fencer) f));
                ret.add((Score)preliminaryScore.get(f));
            }
        }
        return ret;
    }

    @Override
    public void abortPreliminary() throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        
        status = Status.PreparingPhase;
        
        Preliminary.deletePreliminaryOfTournament(this);
    }

    @Override
    public void startFinalrounds() throws SQLException, ObjectDeprecatedException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        
        if(!areAllPrelimsFinished()) return;
        
        //Number of fencer entering the final
        int finalFencers =(int) Math.pow(this.numberFinalrounds, 2);
        
        int finalFencerPerGroup = (int)(finalFencers/groups);
        
        List<Score> finalFencer = new ArrayList<>();
        List<Score> wildcarts = new ArrayList<>();
        
        //Get the first X fencers from every group and put all other fencers in
        //the wildcart pot
        for(int i = 0; i < groups; i++)
        {
            List<Score> tmp = getScoreFromGroup();
            Collections.sort(tmp);//Collection.reverse()
            for(int r = 0; r < tmp.size(); r++)
            {
                if(r < finalFencerPerGroup)
                    finalFencer.add(tmp.get(r));
                else
                    wildcarts.add(tmp.get(r));
            }
        }
        
        //Fill up the empty final with the best fencers from the wildcard pot
        Collections.sort(wildcarts);
        for(int i = 0; i < (finalFencers-finalFencer.size()); i++)
        {
            finalFencer.add(wildcarts.get(i));
        }
        
        Collections.sort(finalFencer);
        
        Finalround theFinal = new Finalround(this.numberFinalrounds-1);
        
        List<Finalround> tmp = new ArrayList<>();
        tmp.add(theFinal);
        
        for(int i = 0; i < this.numberFinalrounds-1 ; i++)
        {
            List<Finalround> tmp2 = new ArrayList<>();
            for(Finalround around : tmp)
            {
                Finalround tmp3 = new Finalround(this.numberFinalrounds-2-i);
                tmp3.addWinningRound(around);
                tmp2.add(tmp3);
                
                tmp3 = new Finalround(this.numberFinalrounds-2-i);
                tmp3.addWinningRound(around);
                tmp2.add(tmp3);
            }
            
            tmp = tmp2;
        }
        
        for(int i = 0; i < finalFencers/2; i++)
        {
            tmp.get(i).addParticipant(finalFencer.get(i).fencer);
            tmp.get(i).addParticipant(finalFencer.get(finalFencer.size()-1-i).fencer);
        }
        
        //TODO: Event werfen
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void abortFinalrounds() throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void finishTournament() throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void reopenFinalrounds() throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isPreparingPhase()
    {
        if(!isValid) throw new ObjectDeprecatedException();
        return status == Status.PreparingPhase;
    }

    @Override
    public boolean isPreliminaryPhase()
    {
        if(!isValid) throw new ObjectDeprecatedException();
        return status == Status.PreliminaryPhase;
    }

    @Override
    public boolean isFinalPhase()
    {
        if(!isValid) throw new ObjectDeprecatedException();
        return status == Status.FinalPhase;
    }

    @Override
    public boolean isFinished()
    {
        if(!isValid) throw new ObjectDeprecatedException();
        return status == Status.Completed;
    }
    
    private void invalidate()
    {
        ID = -1;
        isValid = false;
    }
    
    public void debug()//TODO: Delete
    {
        System.out.println("Anzahl einträge: "+participants.entrySet().size());
        for (Map.Entry<Integer, Participation2> entry : participants.entrySet())
        {
            System.out.println(entry.getKey()+" | "+entry.getValue().getID()+" | "+entry.getValue().getFencer().getName()+" | "+entry.getValue().getFencer().getID());
        }
        System.out.println("\n");
    }
    
    public void printSchedule() throws SQLException
    {
        iPreliminary[][] schedule = getPreliminarySchedule();
        
        for(int i = 0; i < schedule.length; i++)
        {
            for(int c = 0; c < schedule[i].length; c++)
            {
                System.out.print(schedule[i][c].getFencer().get(0).getName()+""+schedule[i][c].getFencer().get(1).getName()+"\t");
            }
            
            System.out.println("");
        }
    }
    
    public void addPreliminaryRoundToScore(Fencer f, Round r)
    {
        if(!preliminaryScore.containsKey(f))
            preliminaryScore.put(f, new Score(f));
        
        preliminaryScore.get(f).addMatch(r);
    }
    
    public void removePreliminaryRoundFromScore(Fencer f, Round r)
    {
        if(preliminaryScore.containsKey(f))
            preliminaryScore.get(f).removeMatch(r);
    }
    
    class Score extends Observable implements iScore, Observer, Comparable
    {
        private Fencer fencer;
        
        private int wins = 0;
        private int hits = 0;
        private int gotHit = 0;

        List<Round> matches = new ArrayList<>();
        
        protected Score(Fencer f)
        {
            this.fencer = f;
        }

        public void addMatch(Round r)
        {
            if(!r.isFencer(fencer)) return;
            
            matches.add(r);
            r.addObserver(this);
        }
        
        public void removeMatch(Round r)
        {
            matches.remove(r);
            r.deleteObserver(this);
            update(null, EventPayload.Type.valueChanged);
        }
        
        public int getWins()
        {
            return wins;
        }
        
        public int getHits()
        {
            return hits;
        }
        
        public int getGotHit()
        {
            return gotHit;
        }
        
        @Override
        public void update(Observable o, Object arg)
        {
            if(((EventPayload)arg).type == EventPayload.Type.valueChanged)
            {
                wins = 0;
                hits = 0;
                gotHit = 0;

                for(Round r : matches)
                {
                    try 
                    {
                        if(r.isFinished())
                        {
                            if(r.getWinner().equals(fencer))
                                wins++;

                            hits+=r.getPoints(fencer);
                            gotHit+=r.getPoints(fencer);
                        }
                    }
                    catch(ObjectDeprecatedException e)
                    {
                        //Depricated object wasen't removed properply; adjust and ignor
                        matches.remove(r);
                    }
                }
            }
            
            setChanged();
            notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
        }

        @Override
        public int compareTo(Object o)
        {
            Score tmp = (Score) o;
            if(this.wins > tmp.getWins()) return 1;
            if(this.wins < tmp.getWins()) return -1;
            if(this.hits > tmp.getHits()) return 1;
            if(this.hits < tmp.getHits()) return -1;
            if(this.gotHit > tmp.getGotHit()) return 1;
            if(this.gotHit < tmp.getGotHit()) return -1;
            return 0;
        }
        
        
    }
}
