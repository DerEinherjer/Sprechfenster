package model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import model.DBConnection.DBEntity;
import model.DBConnection.DBTournament;
import model.rounds.FinalsMatch;
import model.rounds.QualificationMatch;
import model.rounds.TournamentMatch;
import model.rounds.iFinalsMatch;
import model.rounds.iQualificationMatch;

public class Tournament extends Observable implements DBEntity, iTournament
{

  // <editor-fold defaultstate="collapsed" desc=" Types ">
  enum Status
  {
    PreparingPhase(0),
    QualificationPhase(1),
    FinalsPhase(2),
    Completed(3);

    private int value;

    private Status(int value)
    {
      this.value = value;
    }

    public static Status valueOf(int value)
    {
      switch (value)
      {
        case 0:
          return PreparingPhase;
        case 1:
          return QualificationPhase;
        case 2:
          return FinalsPhase;
        case 3:
          return Completed;
        default:
          return PreparingPhase;
      }
    }
  }

// </editor-fold>
  // <editor-fold defaultstate="collapsed" desc=" statics ">
  private static Map<Integer, Tournament> tournaments = new HashMap<>();

  public static List<Tournament> getAllTournaments()
  {
    List<Tournament> ret = new ArrayList<>();
    for (Map.Entry<Integer, Tournament> entry : tournaments.entrySet())
    {
      ret.add(entry.getValue());
    }
    return ret;
  }

  public static Tournament getTournament(int id)
  {
    return tournaments.get(id);
  }

// </editor-fold>
  //#########################################################################
  private int ID;
  private boolean isValid = true;
  private String name = null;
  private String date = null;
  private Integer groups = null;
  private Integer numberFinalrounds = null;
  private Integer lanes = null;
  private Status status = null;
  private Boolean separateQualificationGroups = null;
  private Map<Fencer, TournamentParticipation> fencerToParticipation = new HashMap<>();
  private Map<Fencer, Score> qualificationScore = new HashMap<>();
  private Map<Fencer, Score> finalsScore = new HashMap<>();

  /**
   * DON'T USE THIS! IT IS FOR THE USE OF THE INTERFACE ONLY AND WILL CRASH THE PROGRAMM IF USED OTHERWISE.
   */
  public Tournament()
  {
  }

  public Tournament(String name) throws SQLException
  {
    this.ID = DBTournament.createTournament(name);
    this.name = name;
    this.date = "1970-01-01";
    this.groups = 2;
    this.numberFinalrounds = 2;
    this.lanes = 2;
    this.status = Status.PreparingPhase;
    this.separateQualificationGroups = true;
    tournaments.put(ID, this);
    DBTournament.setDate(this.ID, date);
    DBTournament.setFinalRounds(this.ID, this.numberFinalrounds);
    DBTournament.setGroups(this.ID, this.groups);
    DBTournament.setStatus(this.ID, status.value);

  }

  public Tournament(Map<String, Object> set) throws ObjectExistException, SQLException
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
    this.status = Status.valueOf((Integer) set.get("Status".toUpperCase()));
    this.separateQualificationGroups = (Boolean) set.get("VorgruppenSeparieren".toUpperCase());//TODO: Include in Test-Case
  }

  @Override
  public void init() throws SQLException
  {
    DBTournament.createTable();
    DBTournament.loadTournaments();
  }

  @Override
  public void onStartUp() throws SQLException
  {
    for (Map.Entry<Integer, Tournament> entry : tournaments.entrySet())
    {
      entry.getValue().loadParticipantsAndScores();
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

  @Override
  public int getID()
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    return ID;
  }

  @Override
  public String getName()
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    return name;
  }

  @Override
  public String getDate()
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    return date;
  }

  @Override
  public int getGroups()
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    return groups;
  }

  @Override
  public int getFinalRounds()
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    return numberFinalrounds;
  }

  @Override
  public int getLanes()
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    return lanes;
  }

  @Override
  public void setName(String name) throws SQLException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    if (status != Status.PreparingPhase)
    {
      return;
    }

    DBTournament.setName(ID, name);
    this.name = name;

    setChanged();
    notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
  }

  @Override
  public void setDate(String date) throws SQLException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    if (status != Status.PreparingPhase)
    {
      return;
    }

    DBTournament.setDate(ID, date);
    this.date = date;

    setChanged();
    notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
  }

  @Override
  public void setGroups(int groups) throws SQLException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    if (status != Status.PreparingPhase)
    {
      return;
    }

    if (this.groups == groups)
    {
      return;
    }

    DBTournament.setGroups(ID, groups);
    this.groups = groups;

    setChanged();
    notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
  }

  @Override
  public void setFinalRounds(int rounds) throws SQLException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    if (status != Status.PreparingPhase)
    {
      return;
    }

    DBTournament.setFinalRounds(ID, rounds);
    this.numberFinalrounds = rounds;

    setChanged();
    notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
  }

  @Override
  public void setLanes(int lanes) throws SQLException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    if (status != Status.PreparingPhase)
    {
      return;
    }

    DBTournament.setLanes(ID, lanes);
    this.lanes = lanes;

    setChanged();
    notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
  }

  @Override
  public void addParticipant(iFencer f) throws SQLException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    if (status != Status.PreparingPhase)
    {
      return;
    }

    int min = Integer.MAX_VALUE;
    int group = -1;
    for (int i = 1; i <= groups; i++)
    {
      int tmp = getParticipantsOfGroup(i).size();
      if (tmp < min)
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
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    if (status != Status.PreparingPhase)
    {
      return;
    }

    fencerToParticipation.put((Fencer) f, new TournamentParticipation(this, (Fencer) f, group));

    setChanged();
    notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
  }

  @Override
  public boolean isParticipant(iFencer f) throws SQLException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    return fencerToParticipation.get((Fencer) f) != null;
  }

  @Override
  public List<iFencer> getAllParticipants() throws SQLException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    List<iFencer> ret = new ArrayList<>();
    for (Fencer f : fencerToParticipation.keySet())
    {
      ret.add(f);
    }
    return ret;
  }

  @Override
  public List<iFencer> getParticipantsOfGroup(int group) throws SQLException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    List<iFencer> ret = new ArrayList<>();
    for (Map.Entry<Fencer,TournamentParticipation> fencerParticipations : fencerToParticipation.entrySet())
    {
      if(fencerParticipations.getValue().getGroup() == group)
      {
        ret.add(fencerParticipations.getKey());
      }
    }
    return ret;
  }

  @Override
  public int getParticipantGroup(iFencer f) throws SQLException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    Fencer fencer = (Fencer) f;
    if (fencer != null)
    {
      TournamentParticipation participation = fencerToParticipation.get(fencer);
      if (participation != null)
      {
        return participation.getGroup();
      }
    }
    return -1;
  }

  @Override
  public void removeParticipant(iFencer f) throws SQLException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    if (status != Status.PreparingPhase)
    {
      return;
    }

    TournamentParticipation tmp = fencerToParticipation.get((Fencer)f);
    fencerToParticipation.remove((Fencer)f);
    tmp.delete();
  }

  @Override
  public void dropOut(iFencer f) throws SQLException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void setEntryFee(iFencer f, boolean paid) throws SQLException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    if (status != Status.PreparingPhase)
    {
      return;
    }
    if (!isParticipant(f))
    {
      return;
    }

    fencerToParticipation.get((Fencer) f).setEntryFee(paid);

    setChanged();
    notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
  }

  @Override
  public void setEquipmentCheck(iFencer f, boolean checked) throws SQLException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    if (status != Status.PreparingPhase)
    {
      return;
    }
    if (!isParticipant(f))
    {
      return;
    }

    fencerToParticipation.get((Fencer) f).setEquipmentCheck(checked);

    setChanged();
    notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
  }

  @Override
  public boolean getEntryFee(iFencer f) throws SQLException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    if (!isParticipant(f))
    {
      return false;
    }

    return fencerToParticipation.get((Fencer) f).getEntryFee();
  }

  @Override
  public boolean getEquipmentCheck(iFencer f) throws SQLException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    if (!isParticipant(f))
    {
      return false;
    }

    return fencerToParticipation.get((Fencer) f).getEquipmentCheck();
  }

  @Override
  public List<iQualificationMatch> getAllQualificationMatches() throws SQLException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    if (status == Status.PreparingPhase)
    {
      return new ArrayList<>();
    }

    return QualificationMatch.getQualificationMatchesOfTournament(this);
  }

  @Override
  public iQualificationMatch[][] getQualificationMatchSchedule() throws SQLException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }

    List<iQualificationMatch> prelims = QualificationMatch.getQualificationMatchesOfTournament(this);

    int lanes = -1;
    int rounds = -1;
    for (iQualificationMatch tmp : prelims)
    {
      if (tmp.getLane() > lanes)
      {
        lanes = tmp.getLane();
      }
      if (tmp.getRound() > rounds)
      {
        rounds = tmp.getRound();
      }
    }

    iQualificationMatch[][] ret = new iQualificationMatch[rounds + 1][lanes + 1];

    for (iQualificationMatch tmp : prelims)
    {
      ret[tmp.getRound()][tmp.getLane()] = tmp;
    }

    return ret;
  }

  @Override
  public iScore getFencersScoreFromQualificationPhase(iFencer f)
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    if (status == Status.PreparingPhase)
    {
      return null;
    }
    Fencer fencer = (Fencer) f;
    if (!qualificationScore.containsKey(fencer))
    {
      qualificationScore.put(fencer, new Score(fencer));
    }

    return (iScore) qualificationScore.get(fencer);
  }

  @Override
  public iScore getFencersScoreFromFinalsPhase(iFencer f)
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    if (status == Status.PreparingPhase || status == Status.QualificationPhase)
    {
      return null;
    }
    Fencer fencer = (Fencer) f;
    if (!finalsScore.containsKey(fencer))
    {
      finalsScore.put(fencer, new Score(fencer));
    }
    return (iScore) finalsScore.get(fencer);
  }

  @Override
  public List<iScore> getQualifcationPhaseScores() throws SQLException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    if (status == Status.PreparingPhase)
    {
      return null;
    }
    return new ArrayList<>(qualificationScore.values());
  }

  @Override
  public List<iScore> getFinalsPhaseScores() throws SQLException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    if (status == Status.PreparingPhase || status == Status.QualificationPhase)
    {
      return null;
    }
    return new ArrayList<>(finalsScore.values());
  }

  @Override
  public List<iFinalsMatch> getAllFinalsMatches()
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    return FinalsMatch.getFinalsMatchesOfTournament(this);
  }

  @Override
  public int getYellowFor(iFencer f) throws ObjectDeprecatedException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int getRedFor(iFencer f) throws ObjectDeprecatedException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int getBlackFor(iFencer f) throws ObjectDeprecatedException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public String getComment(iFencer f) throws SQLException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    if (!isParticipant(f))
    {
      return "";
    }

    return fencerToParticipation.get((Fencer) f).getComment();
  }

  @Override
  public void setComment(iFencer f, String comment) throws SQLException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    if (!isParticipant(f))
    {
      return;
    }

    fencerToParticipation.get((Fencer) f).setComment(comment);

    setChanged();
    notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
  }

  @Override
  public void delete() throws SQLException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void startQualificationPhase() throws SQLException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }

    status = Status.QualificationPhase;
    DBTournament.setStatus(ID, status.value);

    List<List<QualificationMatch>> qualificationMatchesOfGroup = new ArrayList<>();

    for (int groupIndex = 0; groupIndex < groups; groupIndex++)
    {
      qualificationMatchesOfGroup.add(new ArrayList<>());
      List<iFencer> fencersOfGroup = getParticipantsOfGroup(groupIndex + 1);
      for (int firstFencer = 0; firstFencer < fencersOfGroup.size() - 1; firstFencer++)
      {
        for (int secondFencer = firstFencer + 1; secondFencer < fencersOfGroup.size(); secondFencer++)
        {
          QualificationMatch match = new QualificationMatch(this, (Fencer) fencersOfGroup.get(firstFencer), (Fencer) fencersOfGroup.get(secondFencer));
          qualificationMatchesOfGroup.get(groupIndex).add(match);
        }
      }
    }

    //Should the preliminarys not be separated by groups shove them all in the first group
    if (!separateQualificationGroups)
    {
      for (int i = qualificationMatchesOfGroup.size() - 1; i > 0; i--)
      {
        qualificationMatchesOfGroup.get(0).addAll(qualificationMatchesOfGroup.get(i));
        qualificationMatchesOfGroup.remove(i);
      }
    }

    int currentRound = 1;
    //Iterate over the groups and do the matchmaking seperatly
    for (int groupIndex = 0; groupIndex < qualificationMatchesOfGroup.size(); groupIndex++)
    {
      List<QualificationMatch> unscheduledMatchesOfGroup = qualificationMatchesOfGroup.get(groupIndex);
      List<iFencer> fencersInGroup = getParticipantsOfGroup(groupIndex + 1);
      Map<iFencer, Integer> fencerToLastFightRound = new HashMap<>();

      for (iFencer f : fencersInGroup)
      {
        fencerToLastFightRound.put(f, 0);
      }

      for (; !unscheduledMatchesOfGroup.isEmpty(); currentRound++)
      {
        QualificationMatch nextMatchToSchedule = null;
        int lane;
        for (lane = 1; lane <= this.lanes; lane++)
        {
          nextMatchToSchedule = null;

          //try to find the next match that gives the longest balanced wait time for the fencers.
          //This means that the sum of the rounds the fencers of the match are waiting since the last match should be the maximum possible value.
          unscheduledMatchesOfGroup.sort((a, b) ->
          {
            int sumOfWaitTimeA = GetSumOfWaitTime(a, fencerToLastFightRound);
            int sumOfWaitTimeB = GetSumOfWaitTime(b, fencerToLastFightRound);
            return Integer.compare(sumOfWaitTimeA, sumOfWaitTimeB);
          });

          //Find scheduleable match
          for (QualificationMatch canidateForNextMatch : unscheduledMatchesOfGroup)
          {
            //get the last round each fencer fought in
            int lastRoundFencer1FoughtIn = fencerToLastFightRound.get(canidateForNextMatch.getFencer().get(0));
            int lastRoundFencer2FoughtIn = fencerToLastFightRound.get(canidateForNextMatch.getFencer().get(1));
            //If one of the fighter already has to fight in the current round ignore this match for this round
            if (lastRoundFencer1FoughtIn != currentRound && lastRoundFencer2FoughtIn != currentRound)
            {
              //select this match
              nextMatchToSchedule = canidateForNextMatch;
              break;
            }
          }
          if (nextMatchToSchedule == null)
          {
            //don't try to find a match for the remaining lanes, it is not possible
            break;
          } else
          {
            //schedule the match
            unscheduledMatchesOfGroup.remove(nextMatchToSchedule);
            nextMatchToSchedule.setTime(currentRound, lane);
            fencerToLastFightRound.put(nextMatchToSchedule.getFencer().get(0), currentRound);
            fencerToLastFightRound.put(nextMatchToSchedule.getFencer().get(1), currentRound);
            //then try to schedule another match on the next lane
          }
        }
        //continue with the next round if any matches are remaining after we scheduled a match on each lane
      }
    }
  }

  private int GetSumOfWaitTime(iQualificationMatch match, Map<iFencer, Integer> fencerToLastFightRound)
  {
    return fencerToLastFightRound.get(match.getFencer().get(0)) + fencerToLastFightRound.get(match.getFencer().get(1));
  }

  @Override
  public void abortQualificationPhase() throws SQLException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }

    status = Status.PreparingPhase;
    DBTournament.setStatus(ID, status.value);

    QualificationMatch.deleteQualificationMatchOfTournament(this);
  }

  @Override
  public void startFinalsPhase() throws SQLException, ObjectDeprecatedException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }

    if (!areAllQualificationMatchesFinished())
    {
      return;
    }

    status = Status.FinalsPhase;
    DBTournament.setStatus(this.ID, status.value);

    //match for first place aka "the final match"
    FinalsMatch theFinal = new FinalsMatch(this, this.numberFinalrounds);
    theFinal.setTime(this.numberFinalrounds, 1);

    List<FinalsMatch> unpopulatedFinalsMatches = new ArrayList<>();
    unpopulatedFinalsMatches.add(theFinal);

    int laneLimit = this.lanes;
    int currentLane = 1;

    //build tree of the finals matches starting from the top:
    //
    //       1 <- final match, round == this.numberFinalrounds
    //     2   2 <- matches of round this.numberFinalrounds-1
    //    3 3 3 3 <- matches of round this.numberFinalrounds-2
    for (int i = 0; i < this.numberFinalrounds - 1; i++)
    {
      int currentRound = this.numberFinalrounds - 1 -i;
      //ancestor matches are the matches which decide which fencer fences in the match.
      //Every match has two ancestor matches, except the matches in the first round, which are populated by the best fencers in the qualification round
      //the winners of the ancestor matches populate the connected match in the next round of the finals
      List<FinalsMatch> ancestorMatches = new ArrayList<>();
      for (FinalsMatch existingFinalsRound : unpopulatedFinalsMatches)
      {
        //create 2 ancestor matches for each match
        for(int j = 0; j < 2; j++)
        {
          FinalsMatch ancestorMatch = new FinalsMatch(this, currentRound);
          ancestorMatch.setTime(currentRound, currentLane);
          ancestorMatch.addWinningRound(existingFinalsRound);
          ancestorMatches.add(ancestorMatch);

          currentLane++;
          if(currentLane > this.lanes)
          {
            //wrap around if exceeding max lane
            currentLane = 1;
          }
        }
      }
      if(i == 0)
      {
        //the loosers of the semi-finals fence a special match for the third place
        FinalsMatch matchForThirdPlace = new FinalsMatch(this, -1 /*magic number identifying the third place match*/);
        matchForThirdPlace.setTime(matchForThirdPlace.getRound(), theFinal.getLane());
        ancestorMatches.get(0).addLoosingRound(matchForThirdPlace);
        ancestorMatches.get(1).addLoosingRound(matchForThirdPlace);
        //match for third place is now fully populated, and has no descendants, so no further handling needed
      }
      //now all "unpopulated" matches have two ancestor matches which will populate them.
      //but the ancestor matches need to be populated themselves.
      unpopulatedFinalsMatches = ancestorMatches;
    }

    //Number of fencer entering the final
    int finalFencers = (int) Math.pow(2, this.numberFinalrounds - 1) * 2;

    //this many fencers from each group are guaranteed a place in the finals
    int finalFencerPerGroup = (int) (finalFencers / groups);

    List<Score> qualificationScoresOfFencersInFinals = new ArrayList<>();
    List<Score> wildcards = new ArrayList<>();

    //Get the first X fencers from every group and put all other fencers in
    //the wildcard pot
    for (int i = 0; i < groups; i++)
    {
      List<Score> tmp = getScoreFromQualificationGroup(i+1);
      Collections.sort(tmp);
      Collections.reverse(tmp); //best scoring fencer now is at the beginning of the list
      for (int r = 0; r < tmp.size(); r++)
      {
        if (r < finalFencerPerGroup)
        {
          //add fencer directly into the finals
          qualificationScoresOfFencersInFinals.add(tmp.get(r));
        } else
        {
          //add fencer to remaining fencers which do not advance to the finals directly
          wildcards.add(tmp.get(r));
        }
      }
    }

    //Fill up the empty final with the best fencers from the wildcard pot
    Collections.sort(wildcards);
    Collections.reverse(wildcards);
    for (int i = 0; i < (finalFencers - qualificationScoresOfFencersInFinals.size()); i++)
    {
      qualificationScoresOfFencersInFinals.add(wildcards.get(i));
    }

    Collections.sort(qualificationScoresOfFencersInFinals);
    Collections.reverse(qualificationScoresOfFencersInFinals);

    //populate matches of the first round of the finals with the best scoring fencers from the qualification round.
    //The higher scoring fencer fences against the worst scoring fencer, the second best against the second worst, etc.
    int worstScoringFencerIndex = qualificationScoresOfFencersInFinals.size()-1;
    for (int i = 0; i < finalFencers / 2; i++)
    {
      iFencer higherScoreFencer = qualificationScoresOfFencersInFinals.get(i).getFencer();
      iFencer lowerScoringFencer = qualificationScoresOfFencersInFinals.get(worstScoringFencerIndex - i).getFencer();
      FinalsMatch matchToPopulate = unpopulatedFinalsMatches.get(i);
      matchToPopulate.addParticipant(higherScoreFencer);
      matchToPopulate.addParticipant(lowerScoringFencer);
    }
  }

  @Override
  public void abortFinalsPhase() throws SQLException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void finishTournament() throws SQLException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void reopenFinalsPhase() throws SQLException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean isPreparingPhase()
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    return status == Status.PreparingPhase;
  }

  @Override
  public boolean isQualificationPhase()
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    return status == Status.QualificationPhase;
  }

  @Override
  public boolean isFinalsPhase()
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    return status == Status.FinalsPhase;
  }

  @Override
  public boolean isFinished()
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    return status == Status.Completed;
  }

  public void addQualificationMatchToScore(Fencer f, QualificationMatch match)
  {
    addMatchToScore(f, match, qualificationScore);
  }

  public void removeQualificationMatchFromScore(Fencer f, QualificationMatch match)
  {
    removeMatchFromScore(f, match, qualificationScore);
  }

  public void addFinalsMatchToScore(Fencer f, FinalsMatch match)
  {
    addMatchToScore(f, match, finalsScore);
  }

  public void removeFinalsMatchFromScore(Fencer f, FinalsMatch match)
  {
    removeMatchFromScore(f, match, finalsScore);
  }

  private void addMatchToScore(Fencer fencerToAddScoreTo, TournamentMatch matchToAdd, Map<Fencer,Score> scoreMap)
  {
     if(!scoreMap.containsKey(fencerToAddScoreTo))
    {
      scoreMap.put(fencerToAddScoreTo, new Score(fencerToAddScoreTo));
    }
    scoreMap.get(fencerToAddScoreTo).addMatch(matchToAdd);
  }

  private void removeMatchFromScore(Fencer fencerToRemoveMatchFrom, TournamentMatch matchToRemove, Map<Fencer,Score> scoreMap)
  {
    if (scoreMap.containsKey(fencerToRemoveMatchFrom))
    {
      scoreMap.get(fencerToRemoveMatchFrom).removeMatch(matchToRemove);
    }
  }

  private void invalidate()
  {
    ID = -1;
    isValid = false;
  }

  private List<Score> getScoreFromQualificationGroup(int group) throws SQLException
  {
    List<Score> ret = new ArrayList<>();
    for (iFencer f : getParticipantsOfGroup(group))
    {
      Fencer fencerFromGroup = (Fencer) f;
      if(qualificationScore.containsKey(fencerFromGroup))
      {
        ret.add(qualificationScore.get(fencerFromGroup));
      }
    }
    return ret;
  }

  private void loadParticipantsAndScores()
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }

    for(Map.Entry<Integer, TournamentParticipation> entry : TournamentParticipation.getAllParticipantsForTournament(this).entrySet())
    {
      fencerToParticipation.put(Fencer.getFencer(entry.getKey()), entry.getValue());
    }
  }

  @Override
  public boolean areAllQualificationMatchesFinished()
  {
    for (iQualificationMatch p : QualificationMatch.getQualificationMatchesOfTournament(this))
    {
      if (!p.isFinished())
      {
        return false;
      }
    }
    return true;
  }

}
