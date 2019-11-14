/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.rounds;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.DBConnection.DBEntity;
import model.DBConnection.DBFinalsPhase;
import model.DBConnection.DBTournamentMatch;
import model.Fencer;
import model.ObjectDeprecatedException;
import model.ObjectExistException;
import model.Tournament;
import model.iFencer;

/**
 *
 * @author Asgard
 */
public class FinalsMatch extends TournamentMatch implements DBEntity, iFinalsMatch
{

  @Override
  public void init() throws SQLException
  {
    DBFinalsPhase.loadFinalrounds();
  }

  @Override
  public void onStartUp() throws SQLException
  {
    for (Map.Entry<Integer, FinalsMatch> entry : finalsMatches.entrySet())
    {
      entry.getValue().connectRoundsAndAddToScore();
    }
  }

  @Override
  public void onExit()
  {
    Map<Integer, FinalsMatch> tmp = finalsMatches;
    finalsMatches = new HashMap<>();
    for (Map.Entry<Integer, FinalsMatch> entry : tmp.entrySet())
    {
      entry.getValue().invalidate();
    }
  }

  //#########################################################################
  private static Map<Integer, FinalsMatch> finalsMatches = new HashMap<>();

  public static FinalsMatch getFinalround(int id)
  {
    return finalsMatches.get(id);
  }

  public static List<iFinalsMatch> getFinalsMatchesOfTournament(Tournament t)
  {
    List<iFinalsMatch> ret = new ArrayList<>();
    for (Map.Entry<Integer, FinalsMatch> entry : finalsMatches.entrySet())
    {
      if (entry.getValue().getTournament().equals(t))
      {
        ret.add(entry.getValue());
      }
    }
    return ret;
  }

  //#########################################################################
  //Thos variables carry the roundIDs from Init Phase 1 to Phase 2 in which
  //the ID can be dereferenced
  private Integer winnerRoundID = null;
  private Integer loserRoundID = null;

  private FinalsMatch winnerRound = null;
  private FinalsMatch loserRound = null;

  public FinalsMatch(Map<String, Object> set) throws ObjectExistException, SQLException
  {
    super(set);
    this.winnerRoundID = (Integer) set.get("FinalGewinnerMatch".toUpperCase());
    this.loserRoundID = (Integer) set.get("FinalVerliererMatch".toUpperCase());

    finalsMatches.put(ID, this);
  }

  public FinalsMatch(Tournament t, int finalRound) throws SQLException
  {
    this.ID = DBFinalsPhase.createFinalround(t, finalRound);
    this.t = t;
    finalsMatches.put(ID, this);

    this.fencer1 = null;
    this.fencer2 = null;

    this.round = finalRound;
    this.lane = -1;
    this.pointsFor1 = 0;
    this.pointsFor2 = 0;

    this.finished = false;

    this.yellowFor1 = 0;
    this.redFor1 = 0;
    this.blackFor1 = 0;

    this.yellowFor2 = 0;
    this.redFor2 = 0;
    this.blackFor2 = 0;
    addMatchToScore();
  }

  /**
   * DON'T USE THIS! IT IS FOR THE USE OF THE INTERFACE ONLY AND WILL CRASH THE PROGRAMM IF USED OTHERWISE.
   */
  public FinalsMatch()
  {
  }

  private void addMatchToScore()
  {
    t.addFinalsMatchToScore(fencer1, this);
    t.addFinalsMatchToScore(fencer2, this);
  }

  private void connectRoundsAndAddToScore()
  {
    this.winnerRound = getFinalround(winnerRoundID);
    this.loserRound = getFinalround(loserRoundID);
    addMatchToScore();
  }

  public void addWinningRound(FinalsMatch winningRound) throws SQLException
  {
    if (winnerRound == null)
    {
      winnerRound = winningRound;
      DBTournamentMatch.setWinnerMatch(this, winnerRound.getID());
    }
  }

  public void addLoosingRound(FinalsMatch loosingRound) throws SQLException
  {
    if (winnerRound == null)
    {
      loserRound = loosingRound;
      DBTournamentMatch.setLoserMatch(this, loosingRound.getID());
    }
  }

  @Override
  protected void doDerivedAddParticipant(iFencer f)
  {
    t.addFinalsMatchToScore((Fencer) f, this);
  }

  @Override
  protected void doDerivedRemoveParticipant(iFencer f)
  {
    t.removeFinalsMatchFromScore((Fencer) f, this);
  }

  @Override
  protected boolean doDerivedSetTime(int round, int lane) throws SQLException
  {
    if (!t.isFinalsPhase())
    {
      return false;
    }
    return DBTournamentMatch.setFinalsMatchTime(this, round, lane);
  }

  @Override
  protected void doDerivedSwitchParticipantOut(iFencer out, iFencer in)
  {
    t.removeFinalsMatchFromScore((Fencer) out, this);
    t.addFinalsMatchToScore((Fencer) in, this);
  }

  @Override
  public iFinalsMatch getWinnerMatch() throws ObjectDeprecatedException
  {
    return winnerRound;
  }

  @Override
  public iFinalsMatch getLoserMatch() throws ObjectDeprecatedException
  {
    return loserRound;
  }

  @Override
  public List<iFinalsMatch> getPreviousMatches() throws ObjectDeprecatedException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int getFinalRound()
  {
    return round;
  }

  private void invalidate()
  {
    this.ID = -1;
  }

  @Override
  protected void doDerivedDelete()
  {
    t.removeFinalsMatchFromScore(fencer1, this);
    t.removeFinalsMatchFromScore(fencer2, this);
  }
}
