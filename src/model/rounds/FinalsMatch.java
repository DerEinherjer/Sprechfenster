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
import model.ObjectDeprecatedException;
import model.ObjectExistException;
import model.Tournament;

/**
 *
 * @author Asgard
 */
public class FinalsMatch extends TournamentMatch implements DBEntity, iFinalsMatch
{

  @Override
  public void init() throws SQLException
  {
    //The SQL-Table is Created in Round because it is also used in Finalround
    DBFinalsPhase.createTable();
    DBFinalsPhase.loadFinalrounds();
  }

  @Override
  public void onStartUp() throws SQLException
  {
    for (Map.Entry<Integer, FinalsMatch> entry : finalsMatches.entrySet())
    {
      entry.getValue().initPhase2();
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
  private Integer finalRound = null;

  //Thos variables carry the roundIDs from Init Phase 1 to Phase 2 in which
  //the ID can be dereferenced
  private Integer winnerRoundID = null;
  private Integer loserRoundID = null;

  private FinalsMatch winnerRound = null;
  private FinalsMatch loserRound = null;

  public FinalsMatch(Map<String, Object> set) throws ObjectExistException, SQLException
  {
    super(set);

    this.finalRound = (Integer) set.get("FinalRunde".toUpperCase());

    this.winnerRoundID = (Integer) set.get("GewinnerRunde".toUpperCase());
    this.loserRoundID = (Integer) set.get("VerliererRunde".toUpperCase());

    finalsMatches.put(ID, this);
  }

  public FinalsMatch(int finalRound)
  {
    this.ID = DBFinalsPhase.createFinalround(t, finalRound);

    finalsMatches.put(ID, this);

    this.t = t;

    this.fencer1 = null;
    this.fencer2 = null;

    this.finalRound = finalRound;
    this.round = -1;
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
  }

  protected void initPhase2()
  {
    this.winnerRound = getFinalround(winnerRoundID);
    this.loserRound = getFinalround(loserRoundID);
  }

  public void addWinningRound(FinalsMatch winningRound)
  {
    if (winnerRound == null)
    {
      winnerRound = winningRound;
    }
  }

  public void addLoosingRound(FinalsMatch loosingRound)
  {
    if (winnerRound == null)
    {
      loserRound = loosingRound;
    }
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
    return finalRound;
  }

  private void invalidate()
  {
    this.ID = -1;
  }
}
