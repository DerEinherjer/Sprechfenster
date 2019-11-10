package model.rounds;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.DBConnection.DBEntity;
import model.DBConnection.DBQualificationPhase;
import model.Fencer;
import model.ObjectDeprecatedException;
import model.ObjectExistException;
import model.Tournament;

public class QualificationMatch extends TournamentMatch implements DBEntity, iQualificationMatch
{

  private static Map<Integer, QualificationMatch> qualificationMatches = new HashMap<>();

  @Override
  public void init() throws SQLException
  {
    //The SQL-Table is Created in Round because it is also used in Finalround
    DBQualificationPhase.loadQualificationRound();
  }

  @Override
  public void onStartUp() throws SQLException
  {
    for (Map.Entry<Integer, QualificationMatch> entry : qualificationMatches.entrySet())
    {
      entry.getValue().initPhase2();
    }
  }

  @Override
  public void onExit()
  {
    Map<Integer, QualificationMatch> tmp = qualificationMatches;
    qualificationMatches = new HashMap<>();
    for (Map.Entry<Integer, QualificationMatch> entry : tmp.entrySet())
    {
      entry.getValue().invalidate();
    }
  }

  //#########################################################################
  public static List<iQualificationMatch> getQualificationMatchesOfTournament(Tournament t)
  {
    List<iQualificationMatch> ret = new ArrayList<>();
    for (Map.Entry<Integer, QualificationMatch> entry : qualificationMatches.entrySet())
    {
      if (entry.getValue().getTournament().equals(t))
      {
        ret.add(entry.getValue());
      }
    }
    return ret;
  }

  private void invalidate()
  {
    ID = -1;
    isValid = false;
  }

  public static void deleteQualificationMatchOfTournament(Tournament t)
  {
    List<QualificationMatch> tmp = new ArrayList<>();
    for (Map.Entry<Integer, QualificationMatch> entry : qualificationMatches.entrySet())
    {
      if (entry.getValue().getTournament().equals(t))
      {
        tmp.add(entry.getValue());
      }
    }

    for (QualificationMatch prelim : tmp)
    {
      try
      {
        prelim.delete();
      } catch (SQLException ex)
      {
        Logger.getLogger(QualificationMatch.class.getName()).log(Level.SEVERE, null, ex);
      } catch (ObjectDeprecatedException ex)
      {
      }//can be ignored savely
    }
  }

  //#########################################################################
  public QualificationMatch(Map<String, Object> set) throws ObjectExistException, SQLException
  {
    super(set);

    qualificationMatches.put(ID, this);
    t.addQualificationMatchToScore(fencer1, this);
    t.addQualificationMatchToScore(fencer2, this);
  }

  public QualificationMatch(Tournament t, Fencer f1, Fencer f2) throws SQLException
  {
    if (t.getParticipantGroup(f1) != t.getParticipantGroup(f2))
    {
      throw new IllegalArgumentException();
    }

    this.ID = DBQualificationPhase.createQualificationPhase(t, f1, f2);
    qualificationMatches.put(ID, this);

    this.t = t;

    this.fencer1 = f1;
    this.fencer2 = f2;

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

    t.addQualificationMatchToScore(fencer1, this);
    t.addQualificationMatchToScore(fencer2, this);
  }

  /**
   * DON'T USE THIS! IT IS FOR THE USE OF THE INTERFACE ONLY AND WILL CRASH THE PROGRAMM IF USED OTHERWISE.
   */
  public QualificationMatch()
  {
  }

  ;

    @Override
  public void delete() throws SQLException, ObjectDeprecatedException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }

    isValid = false;
    qualificationMatches.remove(this);
  }

  protected void initPhase2()
  {
    t.addQualificationMatchToScore(fencer1, this);
    t.addQualificationMatchToScore(fencer2, this);
  }

  @Override
  public int getQualificationGroup() throws SQLException
  {
    return t.getParticipantGroup(fencer1);
  }
}
