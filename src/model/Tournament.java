package model;

import model.rounds.Preliminary;
import model.rounds.Finalround;
import model.rounds.iFinalround;
import model.rounds.iPreliminary;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import static model.rounds.Round.sync;

public class Tournament extends Observable implements iTournament {
  // -----

  private static Map<Integer, Tournament> tournaments = new HashMap<>();
  static Sync sync;

  public static void ClearDatabaseCache () {
    tournaments.clear();
  }

  public static Tournament getTournament (int id) throws SQLException {
    if (!tournaments.containsKey(id)) {
      sync.loadTournament(id);
    }
    return tournaments.get(id);
  }

  static List<Tournament> getAllTournaments () {
    List<Tournament> ret = new ArrayList<>();
    for (Map.Entry<Integer, Tournament> entry : tournaments.entrySet()) {
      ret.add(entry.getValue());
    }
    return ret;
  }
  // -----
  private int ID;

  private boolean isValid = true;

  private String name = null;
  private String date = null;
  private Integer groups = null;
  private Integer numberFinalrounds = null;
  private Integer lanes = null;
  private Integer status = null;
  /**
   * 0  Preparing Phase         (Fencer Registration, ...)
   * 1  Preliminary
   * 2  Finalephase
   * 3  Completed
   */
  
  
  //private Boolean finishedPreliminary = null;
  private Map<Fencer, Boolean> entryFee = new HashMap<>();
  private Map<Fencer, Boolean> equipmentChecked = new HashMap<>();
  private Map<Fencer, Score> scoresPrelim = new HashMap<>();
  private Map<Fencer, Score> scoresFinal = new HashMap<>();
  private Map<Fencer, Boolean> dropedOut = new HashMap<>();

  static String getSQLString () {
    return "CREATE TABLE IF NOT EXISTS Turniere (ID int NOT NULL AUTO_INCREMENT UNIQUE,"
            + "Name varchar(255) DEFAULT 'Nicht Angegeben',"
            + "Datum varchar(11) DEFAULT '1970-01-01',"
            + "Gruppen int DEFAULT 2,"
            + "Finalrunden int DEFAULT 2,"
            + "Bahnen int DEFAULT 2,"
            + "Status int DEFAULT 0);";
            //+ "InFinalrunden BOOLEAN DEFAULT FALSE);";
  }

  public Tournament (Map<String, Object> set) throws ObjectExistException, SQLException {
    this.ID = (Integer) set.get("ID");

    if (tournaments.containsKey(this.ID)) {
      throw new ObjectExistException(tournaments.get(this.ID));
    }
    tournaments.put(this.ID, this);

    this.name = (String) set.get("Name".toUpperCase());
    this.date = (String) set.get("Datum".toUpperCase());
    this.groups = (Integer) set.get("Gruppen".toUpperCase());
    this.numberFinalrounds = (Integer) set.get("Finalrunden".toUpperCase());
    this.lanes = (Integer) set.get("Bahnen".toUpperCase());
    this.status = (Integer) set.get("Status".toUpperCase());
    //this.finishedPreliminary = (Boolean) set.get("InFinalrunden".toUpperCase());

    for (iFencer f : getAllParticipants()) {
      scoresPrelim.put((Fencer) f, new Score((Fencer) f));
      scoresFinal.put((Fencer) f, new Score((Fencer) f));
      entryFee.put((Fencer) f, sync.getEntryFee(this, (Fencer) f));
      dropedOut.put((Fencer) f, sync.getDropedOut(this, (Fencer) f));
      equipmentChecked.put((Fencer) f, sync.getEquipmentCheck(this, (Fencer) f));
    }
    
    sync.observeThis(this);
    
    setChanged();
    notifyObservers(new EventPayload(this, EventPayload.Type.tournamentCreated));
  }

  @Override
  public int getID () {
    return ID;
  }

  @Override
  public String getName () {
    return name;
  }

  @Override
  public String getDate () {
    return date;
  }

  @Override
  public int getGroups () {
    return groups;
  }

  @Override
  public int getFinalRounds () {
    return numberFinalrounds;
  }

  @Override
  public int getLanes () {
    return lanes;
  }

  @Override
  public void setName (String name) throws SQLException {
    sync.tournamentSetName(name, ID);
    this.name = name;
    setChanged();
    notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
  }

  @Override
  public void setDate (String date) throws SQLException {
    sync.tournamentSetDate(date, ID);
    this.date = date;
    setChanged();
    notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
  }

  @Override
  public void setGroups (int groups) throws SQLException {
    if(status != 0) return;
      
    if (this.groups == groups) {
      return;
    }

    List<iFencer> tmp = new ArrayList<>();
    for (Fencer f : Fencer.getFencer(this)) {
      if (getParticipantGroup(f) > groups || true) //TODO: Mit Stefan verhalten abstimmen
      {
        tmp.add(f);
        removeParticipant(f);
      }
    }

    sync.tournamentSetGroups(groups, ID);
    this.groups = groups;

    for (iFencer f : tmp) {
      addParticipant(f);
    }
    setChanged();
    notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
  }

  @Override
  public void setFinalRounds (int rounds) throws SQLException {
    if(status != 0) return;

    sync.tournamentSetFinalRounds(rounds, ID);
    this.numberFinalrounds = rounds;
    setChanged();
    notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
  }

  @Override
  public void setLanes (int lanes) throws SQLException {  
    if(status != 0) return;
      
    for (Preliminary p : Preliminary.getPreliminarys(this)) {
      try {
        if (p.getLane() > lanes) {
          p.setTime(0, 0);
        }
      }
      catch (ObjectDeprecatedException e) {
      }
    }
    sync.tournamentSetLanes(lanes, ID);
    this.lanes = lanes;
    setChanged();
    notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
  }

  @Override
  public String toString () {
    return ID + " | " + name;
  }

  @Override
  public void addParticipant (iFencer f) throws SQLException {
    if(status != 0) return;
      
    if (!(f instanceof Fencer)) {
      return;
    }
    int counts[] = sync.getGroupsMemberCount(this);
    int index = 0;
    for (int i = 1; i < counts.length; i++) {
      if (counts[i] < counts[index]) {
        index = i;
      }
    }
    addParticipant(f, index + 1);
  }

  @Override
  public void addParticipant (iFencer f, int group) throws SQLException { 
    if(status != 0) return;
    
    if (!(f instanceof Fencer)) {
      return;
    }
    sync.addParticipant(this, (Fencer) f, group);

    scoresPrelim.put((Fencer) f, new Score((Fencer) f));
    scoresFinal.put((Fencer) f, new Score((Fencer) f));
    entryFee.put((Fencer) f, sync.getEntryFee(this, (Fencer) f));
    equipmentChecked.put((Fencer) f, sync.getEquipmentCheck(this, (Fencer) f));
    setChanged();
    notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
  }

  @Override
  public List<iPreliminary> getAllPreliminary () throws SQLException {
    List<iPreliminary> ret = new ArrayList<>();
    for (Preliminary tmp : Preliminary.getPreliminarys(this)) {
      ret.add(tmp);
    }
    return ret;
  }

  @Override
  public boolean isParticipant (iFencer f) throws SQLException {
    if (!(f instanceof Fencer)) {
      return false;
    }
    return sync.isFencerParticipant(this, (Fencer) f);
  }


  private void createPreliminaryTiming () throws SQLException, ObjectDeprecatedException {
    if(status != 0) return;
    
    List<iPreliminary> prelim = getAllPreliminary();
    Map<iFencer, Integer> lastForPrelim = new HashMap<>();

    for (iPreliminary p : prelim) {
      lastForPrelim.put(p.getFencer().get(0), -1);
      lastForPrelim.put(p.getFencer().get(1), -1);
    }

    //Iterate over every lane for every round until every fight has a timeing
    for (int time = 1; !prelim.isEmpty(); time++) 
    {
        for(int lane = 1; lane <= this.lanes; lane++)
        {
            iPreliminary next = null;
            int lastFight = Integer.MAX_VALUE;
            
            //Get match witch the fighter who waits the longest
            for (iPreliminary p : prelim) 
            {
                int f1 = lastForPrelim.get(p.getFencer().get(0));
                int f2 = lastForPrelim.get(p.getFencer().get(1));
                //If one of the fighter already fight at this moment ignore the match
                if(f1 != time && f2 != time)
                {
                    //Take the match whith the fencer who hasn't fought the longest
                    if(f1 < lastFight || f2 < lastFight)
                    {
                        time = (f1<f2)? f1 : f2;
                        next = p;
                    }
                }
            }
            if(next == null) break;//All not already places PrelimFights have a fighter who is already fighting at this point in time
            
            //Delete the match from the 
            prelim.remove(next);
            next.setTime(time, lane);
            lastForPrelim.put(next.getFencer().get(0), time);
            lastForPrelim.put(next.getFencer().get(1), time);
        }
    }
  }

  @Override
  public iPreliminary[][] getPreliminarySchedule () throws SQLException {
    List<iPreliminary> list = getAllPreliminary();
    int last = 0;
    for (iPreliminary p : list) {
      try {
        if (p.getRound() > last) {
          last = p.getRound();
        }
      }
      catch (ObjectDeprecatedException e) {
        list.remove(p);
      }
    }

    iPreliminary[][] ret = new iPreliminary[last][lanes];

    for (iPreliminary p : list) {
      try {
        if (p.getRound() < 1 || p.getLane() < 1) {
          continue; //Noch nicht angesetzte begegnungen werden ignoriert
        }
        ret[p.getRound() - 1][p.getLane() - 1] = p;
      }
      catch (ObjectDeprecatedException e) {
        list.remove(p);
      }
    }

    return ret;
  }

  @Override
  public List<iFencer> getAllParticipants () throws SQLException {
    List<iFencer> ret = new ArrayList<>();
    for (Fencer f : Fencer.getFencer(this)) {
      ret.add(f);
    }
    return ret;
  }

  @Override
  public int getParticipantGroup (iFencer f) throws SQLException {
    return sync.getParticipantGroup(this, (Fencer) f);
  }

  @Override
  public void removeParticipant (iFencer f) throws SQLException {
    if(status != 0) return;
    
    if (isParticipant(f)) {
      sync.removeParticipant((Fencer) f);

      scoresPrelim.remove((Fencer) f);
      scoresFinal.remove((Fencer) f);
      
      setChanged();
      notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
    }
  }

  @Override
  public void setEntryFee (iFencer f, boolean paid) throws SQLException {
    sync.setEntryFee(this, (Fencer) f, paid);
    entryFee.put((Fencer) f, paid);
    
    
    setChanged();
    notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
  }

  @Override
  public void setEquipmentCheck (iFencer f, boolean checked) throws SQLException {
    sync.setEquipmentCheck(this, (Fencer) f, checked);
    equipmentChecked.put((Fencer) f, checked);
    setChanged();
    notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
  }

  @Override
  public boolean getEntryFee (iFencer f) throws SQLException {
    if (!entryFee.containsKey((Fencer) f)) {
      entryFee.put((Fencer) f, sync.getEntryFee(this, (Fencer) f));
    }
    return entryFee.get((Fencer) f);
  }

  @Override
  public boolean getEquipmentCheck (iFencer f) throws SQLException {
    if (!equipmentChecked.containsKey((Fencer) f)) {
      equipmentChecked.put((Fencer) f, sync.getEquipmentCheck(this, (Fencer) f));
    }
    return equipmentChecked.get((Fencer) f);
  }

  public void addWinPrelim (Fencer f) throws SQLException {
    if(status != 1) return;
    
    if (isParticipant(f)) {
      if (!scoresPrelim.containsKey(f)) {
        scoresPrelim.put(f, new Score(f));
      }
      scoresPrelim.get(f).addWin();
    }
  }

  public void addWinFinal (Fencer f) throws SQLException {
    if(status != 2) return;
    
    if (isParticipant(f)) {
      if (!scoresFinal.containsKey(f)) {
        scoresFinal.put(f, new Score(f));
      }
      scoresFinal.get(f).addWin();
    }
  }

  public void subWinPrelim (Fencer f) throws SQLException {
    if(status != 1) return;
    
    if (isParticipant(f)) {
        
    System.out.println("isParticipant==true");
      if (!scoresPrelim.containsKey(f)) {
        scoresPrelim.put(f, new Score(f));
      }
    System.out.println("ok");
      scoresPrelim.get(f).subWin();
    }
  }

  public void subWinFinal (Fencer f) throws SQLException {
    if(status != 2) return;
    
    if (isParticipant(f)) {
      if (!scoresFinal.containsKey(f)) {
        scoresFinal.put(f, new Score(f));
      }
      scoresFinal.get(f).subWin();
    }
  }

  public void addHitsPrelim (Fencer f, int points) throws SQLException {
    if(status != 1) return;
    
    if (isParticipant(f)) {
      if (!scoresPrelim.containsKey(f)) {
        scoresPrelim.put(f, new Score(f));
      }
      scoresPrelim.get(f).addHits(points);
    }
  }

  public void addHitsFinal (Fencer f, int points) throws SQLException {
    if(status != 2) return;
    
    if (isParticipant(f)) {
      if (!scoresFinal.containsKey(f)) {
        scoresFinal.put(f, new Score(f));
      }
      scoresFinal.get(f).addHits(points);
    }
  }

  public void addGotHitPrelim (Fencer f, int points) throws SQLException {
    if(status != 1) return;
    
    if (isParticipant(f)) {
      if (!scoresPrelim.containsKey(f)) {
        scoresPrelim.put(f, new Score(f));
      }
      scoresPrelim.get(f).addGotHit(points);
    }
  }

  public void addGotHitFinal (Fencer f, int points) throws SQLException {
    if(status != 2) return;
    
    if (isParticipant(f)) {
      if (!scoresFinal.containsKey(f)) {
        scoresFinal.put(f, new Score(f));
      }
      scoresFinal.get(f).addGotHit(points);
    }
  }

  @Override
  public Score getScoreFromPrelim (iFencer f) {
    return scoresPrelim.get((Fencer) f);
  }

  @Override
  public Score getScoreFromFinal (iFencer f) {
    return scoresFinal.get((Fencer) f);
  }

  @Override
  public List<iScore> getScoresPrelim () throws SQLException {
    List<iScore> ret = new ArrayList<>();
    for (iFencer f : getAllParticipants()) {
      ret.add(scoresPrelim.get((Fencer) f));
    }
    Collections.sort(ret);
    return ret;
  }

  @Override
  public List<iScore> getScoresFinal () throws SQLException {
    List<iScore> ret = new ArrayList<>();
    for (iFencer f : getAllParticipants()) {
      ret.add(scoresFinal.get((Fencer) f));
    }
    Collections.sort(ret);
    return ret;
  }

  @Override
  public List<iScore>[] getScoresInGroups () throws SQLException {
    List<iScore> ret[] = new ArrayList[getGroups()];
    for (int i = 0; i < ret.length; i++) {
      ret[i] = new ArrayList<>();
    }

    for (iFencer f : getAllParticipants()) {
      ret[getParticipantGroup(f) - 1].add(scoresPrelim.get((Fencer) f));
    }
    for (int i = 0; i < getGroups(); i++) {
      Collections.sort(ret[i]);
    }
    return ret;
  }

  @Override
  public int getPreliminaryCount () throws SQLException {
    return getAllPreliminary().size();
  }

  @Override
  public List<iFencer> getParticipantsOfGroup (int group) throws SQLException {
    List<iFencer> ret = new ArrayList<>();

    for (iFencer f : getAllParticipants()) {
      if (getParticipantGroup(f) == group) {
        ret.add(f);
      }
    }

    return ret;
  }

  private void createFinalrounds () throws SQLException, ObjectDeprecatedException {
    sync.createFinalRounds(this);
    setTimingForFinals();
  }

  private boolean isFinalroundsCreated () throws SQLException {
    return sync.finalroundsCount(this) != 0;
  }

  /*void printTree()
	{
		Finalround f = Finalround.getFinalrounds(this).get(0);
		while(f.getWinner()!=null)
			f = ((Finalround) f).getWinnerRound();
		f.printTree();
	}*/

  private void createFinalePairing () throws SQLException, ObjectDeprecatedException {
    createFinalrounds();

    List<iScore>[] groupScores = getScoresInGroups();
    List<iScore> finalists = new ArrayList<>();
    List<iScore> wildcards = new ArrayList<>();

    int numberOfFinalists = (int) Math.pow(2, numberFinalrounds);

    //Get the first x fencers all groups 
    for (int i = 0; i < groups; i++) {
      for (int c = 0; c < groupScores[i].size(); c++) {
        if (c < (int) (numberOfFinalists / groups)) {
          finalists.add(groupScores[i].get(c));
        }
        else {
          wildcards.add(groupScores[i].get(c));
        }
      }
    }

    Collections.sort(wildcards);
    //If need%groups != 0: take the best, out of all groups, who are
    //not participating in the finals yet
    for (int i = 0; i < (numberOfFinalists % groups); i++) {
      finalists.add(wildcards.get(i));
    }
    //Sort the list so that best Fencer is first
    Collections.sort(finalists);

    //Get all Finalrounds wich are part of the first/lowest level
    List<Finalround> leafFinalRounds = new ArrayList<>();
    for (Finalround finalRound : Finalround.getFinalrounds(this)) {
      if (!finalRound.hasPrerounds()) {
        leafFinalRounds.add(finalRound);
      }
    }

    int c = 0;
    for (Finalround round : leafFinalRounds) {
      try {
        //Take the best remaining fencer
        round.addParticipant(finalists.get(c).getFencer());
        //Take the worst remaining fencer
        round.addParticipant(finalists.get(finalists.size() - c - 1).getFencer());
        System.out.println("Pairing: " + finalists.get(c) + "\t" + finalists.get(finalists.size() - c - 1));
        c++;
      }
      catch (ObjectDeprecatedException ex) {
        //We can ignore that because we created them a few lines above
        Logger.getLogger(Tournament.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
      
    setChanged();
    notifyObservers(Sync.change.finishedPreliminary);
  }

  boolean allPreliminaryFinished () {
    for (Preliminary p : Preliminary.getPreliminarys(this)) {
      try {
        if (!p.isFinished()) {
          return false;
        }
      }
      catch (ObjectDeprecatedException e) {}
    }
    return true;
  }

  @Override
  public List<iFinalround> getAllFinalrounds () {
    List<iFinalround> ret = new ArrayList<>();
    for (Finalround f : Finalround.getFinalrounds(this)) {
      ret.add(f);
    }
    return ret;
  }

  @Override
  public void dropOut (iFencer f) throws SQLException {
    if (status == 0)
    {
        removeParticipant(f);
    }
    else if (status == 1) 
    {
      for (Preliminary p : Preliminary.getPreliminarys(this)) {
        try {
          if (p.isFencer((Fencer) f) && !p.isFinished()) {
            for (iFencer tmp : p.getFencer()) {
              if (tmp.equals(f)) {
                p.setPoints(tmp, 0);
              }
              else {
                p.setPoints(tmp, 5);
              }
            }
            p.setFinished(true);
            dropedOut.put((Fencer) f, true);
          }
        }
        catch (ObjectDeprecatedException e) {
        }
      }
    }
    else if (status == 2)
    {
      for (Finalround fr : Finalround.getFinalrounds(this)) {
        try {
          if (fr.isFencer(f) && !fr.isFinished()) {
            for (iFencer tmp : fr.getFencer()) {
              if (tmp.equals(f)) {
                fr.setPoints(tmp, 0);
              }
              else {
                fr.setPoints(tmp, 5);
              }
            }
            fr.setFinished(true);
            dropedOut.put((Fencer) f, true);
          }
        }
        catch (ObjectDeprecatedException ex) {
          //Can be ignored safly because we don't need to 
          //remove a droped out fencer from a depricated fight
        }
      }
    }
  }

  private void setTimingForFinals () throws SQLException, ObjectDeprecatedException {
    int matchRound = 1;
    int lane = 1;
    List<iFinalround> allMatches = getAllFinalrounds();
    List<iFinalround> matchesOfRound = new ArrayList<iFinalround>();
    for (int finalRound = 1; finalRound <= allMatches.size(); finalRound++) {
      for (iFinalround match : allMatches) {
        matchesOfRound.clear();
        if (match.getFinalRound() == finalRound) {
          matchesOfRound.add(match);
          match.setTime(matchRound, lane);
          lane++;
          if (lane > getLanes()) {
            lane = 1;
            matchRound++;
          }
        }
      }
      if (matchesOfRound.size() == 1) {
        //the last final round has only one match (the final match).
        //Additionally there is the match for third place, find it and 
        //schedule it for the round after the final match
        for (iFinalround match : allMatches) {
          if (match.getFinalRound() == -1) {
            match.setTime(matchRound, 1);
            break;
          }
        }
      }
    }
  }

  @Override
  public int getYellowFor (iFencer f) throws ObjectDeprecatedException {
    int ret = 0;

    for (Preliminary p : Preliminary.getPreliminarys(this)) {
      if (p.isFencer((Fencer) f)) {
        ret += p.getYellow(f);
      }
    }

    for (Finalround fr : Finalround.getFinalrounds(this)) {
      if (fr.isFencer(f)) {
        ret += fr.getYellow(f);
      }
    }
    return ret;
  }

  @Override
  public int getRedFor (iFencer f) throws ObjectDeprecatedException {
    int ret = 0;

    for (Preliminary p : Preliminary.getPreliminarys(this)) {
      if (p.isFencer((Fencer) f)) {
        ret += p.getRed(f);
      }
    }

    for (Finalround fr : Finalround.getFinalrounds(this)) {
      if (fr.isFencer(f)) {
        ret += fr.getRed(f);
      }
    }
    return ret;
  }

  @Override
  public int getBlackFor (iFencer f) throws ObjectDeprecatedException {
    int ret = 0;

    for (Preliminary p : Preliminary.getPreliminarys(this)) {
      if (p.isFencer((Fencer) f)) {
        ret += p.getBlack(f);
      }
    }

    for (Finalround fr : Finalround.getFinalrounds(this)) {
      if (fr.isFencer(f)) {
        ret += fr.getBlack(f);
      }
    }
    return ret;
  }

  @Override
  public String getComment (iFencer f) throws SQLException {
    if (isParticipant(f)) {
      return sync.getComment(this, (Fencer) f);
    }
    return "";
  }

  @Override
  public void setComment (iFencer f, String comment) throws SQLException {
    sync.setComment(this, (Fencer) f, comment);
  }

  @Override
  public boolean equals (Object other) {
    if (other == null) {
      return false;
    }
    if (other == this) {
      return true;
    }
    if (!(other instanceof Tournament)) {
      return false;
    }
    if (((Tournament) other).getID() == ID) {
      return true;
    }
    return false;
  }

  @Override
  public void delete () throws SQLException {
    if (!isValid) {
      return;
    }

    setChanged();
    notifyObservers(new EventPayload(this, EventPayload.Type.tournamentDeleted));
    
    Preliminary.deleteAllPreliminaryRoundsForTournament(this);
    Finalround.deleteAllFinalRoundsOfTournament(this);

    tournaments.remove(ID);
    sync.deleteTournamentFromDatabase(ID);
    ID = -1;
    isValid = false;
  }
  
    public void createPreliminaryTiming (boolean separateGroups) throws SQLException, ObjectDeprecatedException
    {
        List<List<iPreliminary>> timingGroups = new ArrayList<>();
        if(separateGroups)
        {
            for(int i=0;i<getGroups();i++)
                timingGroups.add(new ArrayList<iPreliminary>());
            for(iPreliminary p: getAllPreliminary())
            {
                timingGroups.get(p.getGroup()-1).add(p);
            }
        }
        else
        {
            timingGroups.add(getAllPreliminary());
        }
      
        int actuellTiming = 1;
        Map<iFencer, Integer> lastFight = new HashMap<>();
        for(iFencer f : getAllParticipants())
        {
            lastFight.put(f, 0);
        }
        for(int i=0;i<timingGroups.size();i++)
        {
            while(timingGroups.get(i).size()>0)
            {
                for(int l = 1; l<=lanes;l++)
                {
                    iPreliminary p = null;
                    int last = Integer.MAX_VALUE;
                    for(iPreliminary tmp : timingGroups.get(i))
                    {
                        iFencer f1 = tmp.getFencer().get(0);
                        iFencer f2 = tmp.getFencer().get(1);
                        if(lastFight.get(f1)<last && lastFight.get(f1)!=actuellTiming)
                        {
                            last = lastFight.get(f1);
                            p = tmp;
                        }
                        if(lastFight.get(f1)<last && lastFight.get(f2)!=actuellTiming)
                        {
                            last = lastFight.get(f1);
                            p = tmp;
                        }
                    }
                    if(p==null) break;
                    p.setTime(actuellTiming, l);
                    lastFight.put(p.getFencer().get(0), actuellTiming);
                    lastFight.put(p.getFencer().get(1), actuellTiming);
                }
                actuellTiming++;
            }
        }
    }
  
  private void createPreliminarySchedule(boolean seperateGroups) throws SQLException, ObjectDeprecatedException
  {
      sync.createPreliminaryFights(this);
      createPreliminaryTiming(seperateGroups);
  }
  
  public void startPreliminary() throws SQLException
  {
      if(status == 0)
      {
          sync.setTournamentStatus(this, 1);
          status = 1;
          
          sync.addPreliminary(this);
          
          
          setChanged();
          notifyObservers(EventPayload.Type.tournamentStateChanged);
      }
  }
  
  public void abortPreliminary() throws SQLException
  {
      if(status == 1)
      {
          sync.setTournamentStatus(this, 0);
          status = 0;
          
          Preliminary.getPreliminarys(this);
          
          setChanged();
          notifyObservers(EventPayload.Type.tournamentStateChanged);
      }
  }
  
  public void startFinalrounds() throws SQLException, ObjectDeprecatedException
  {
      if(status == 1)
      {
          sync.setTournamentStatus(this, 2);
          status = 2;
          
          createFinalePairing();
          
          setChanged();
          notifyObservers(EventPayload.Type.tournamentStateChanged);
      }
  }
  
  public void abortFinalrounds() throws SQLException
  {
      if(status == 2)
      {
          sync.setTournamentStatus(this, 1);
          status = 1;
          
          Finalround.deleteAllFinalRoundsOfTournament(this);
          
          setChanged();
          notifyObservers(EventPayload.Type.tournamentStateChanged);
      }
  }
  
  public void finishTournament() throws SQLException
  {
      if(status == 2)
      {
          sync.setTournamentStatus(this, 3);
          status = 3;
          
          setChanged();
          notifyObservers(EventPayload.Type.tournamentStateChanged);
      }
  }
  
  public void reopenFinalrounds() throws SQLException
  {
      if(status == 3)
      {
          sync.setTournamentStatus(this, 2);
          status = 2;
          
          setChanged();
          notifyObservers(EventPayload.Type.tournamentStateChanged);
      }
  }
  
  public boolean isPreparingPhase()
  {
      return status == 0;
  }
  
  public boolean isPreliminaryPhase()
  {
      return status == 1;
  }
  
  public boolean isFinalPhase()
  {
      return status == 2;
  }
  
  public boolean isFinished()
  {
      return status == 3;
  }
}
