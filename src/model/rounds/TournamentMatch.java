package model.rounds;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import model.DBConnection.DBEntity;
import model.DBConnection.DBTournamentMatch;
import model.EventPayload;
import model.Fencer;
import model.ObjectDeprecatedException;
import model.ObjectExistException;
import model.Tournament;
import model.iFencer;
import model.iTournament;

public class TournamentMatch extends Observable implements DBEntity, iMatch
{

  @Override
  public void init() throws SQLException
  {
    DBTournamentMatch.createTable();
    //There is nothing to load this class is abstract
  }

  @Override
  public void onStartUp() throws SQLException
  {

  }

  @Override
  public void onExit()
  {
  }

  //#########################################################################
  boolean isValid = true;

  int ID;
  Tournament t;

  Integer round = null;
  Integer lane = null;
  Fencer fencer1 = null;
  Fencer fencer2 = null;
  Integer pointsFor1 = null;
  Integer pointsFor2 = null;
  Boolean finished = null;

  Integer yellowFor1 = null;
  Integer redFor1 = null;
  Integer blackFor1 = null;
  Integer yellowFor2 = null;
  Integer redFor2 = null;
  Integer blackFor2 = null;

  /**
   * DON'T USE THIS! IT IS FOR THE USE OF THE INTERFACE ONLY AND WILL CRASH THE PROGRAMM IF USED OTHERWISE.
   */
  public TournamentMatch()
  {
  }

  ;

    public TournamentMatch(Map<String, Object> set) throws ObjectExistException, SQLException
  {
    this.ID = (Integer) set.get("ID");

    //The DB returns all colum names in caps
    this.t = Tournament.getTournament((Integer) set.get("TurnierID".toUpperCase()));

    this.round = (Integer) set.get("Runde".toUpperCase());
    this.lane = (Integer) set.get("Bahn".toUpperCase());
    this.fencer1 = Fencer.getFencer((Integer) set.get("Teilnehmer1".toUpperCase()));
    this.fencer2 = Fencer.getFencer((Integer) set.get("Teilnehmer2".toUpperCase()));
    this.pointsFor1 = (Integer) set.get("PunkteVon1".toUpperCase());
    this.pointsFor2 = (Integer) set.get("PunkteVon2".toUpperCase());
    this.finished = (Boolean) set.get("Beendet".toUpperCase());

    this.yellowFor1 = (Integer) set.get("GelbVon1".toUpperCase());
    this.yellowFor2 = (Integer) set.get("GelbVon2".toUpperCase());
    this.redFor1 = (Integer) set.get("RotVon1".toUpperCase());
    this.redFor2 = (Integer) set.get("RotVon2".toUpperCase());
    this.blackFor1 = (Integer) set.get("SchwarzVon1".toUpperCase());
    this.blackFor2 = (Integer) set.get("SchwarzVon2".toUpperCase());
  }

  public int getID()
  {
    return ID;
  }

  @Override
  public int getRound() throws ObjectDeprecatedException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    return round;
  }

  @Override
  public int getLane() throws ObjectDeprecatedException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    return lane;
  }

  @Override
  public List<iFencer> getFencer() throws ObjectDeprecatedException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    List<iFencer> ret = new ArrayList<>();
    if (fencer1 != null)
    {
      ret.add(fencer1);
    }
    if (fencer2 != null)
    {
      ret.add(fencer2);
    }
    return ret;
  }

  @Override
  public iTournament getTournament() throws ObjectDeprecatedException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }

    return t;
  }

  @Override
  public boolean setTime(int round, int lane) throws SQLException, ObjectDeprecatedException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    if (finished || t.isPreparingPhase())
    {
      //wrong tournament phase
      return false;
    }
    boolean derivedSuccess = doDerivedSetTime(round, lane);
    if (derivedSuccess)
    {
      this.round = round;
      this.lane = lane;

      setChanged();
      notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
      return true;
    }
    return derivedSuccess;
  }

  protected boolean doDerivedSetTime(int round, int lane) throws SQLException
  {
    //does nothing in this class but enabled derived classes to do custom steps
    return true;
  }

  /*
    *   Because of different side effects this has to be handled in the subclasses
   */
  @Override
  public void setFinished(boolean finish) throws SQLException, ObjectDeprecatedException
  {
    if (this.finished == finish)
    {
      return;
    }

    DBTournamentMatch.setFinished(this, finish);
    this.finished = finish;
    DoDerivedSetFinished(finish);
    setChanged();
    notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
  }

  protected void DoDerivedSetFinished(boolean finish) throws SQLException
  {
    //Does nothing in base class, but derived classes can use it
  }

  @Override
  public void setPoints(iFencer f, int points) throws SQLException, ObjectDeprecatedException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }

    if (fencer1.equals(f))
    {

      pointsFor1 = points;
    } else if (fencer2.equals(f))
    {
      pointsFor2 = points;
    }
    DBTournamentMatch.setPoints(this.ID, pointsFor1, pointsFor2);
    setChanged();
    notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
  }

  @Override
  public int getPoints(iFencer f) throws ObjectDeprecatedException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }

    if (fencer1.equals(f))
    {
      return pointsFor1;
    }
    if (fencer2.equals(f))
    {
      return pointsFor2;
    }
    return -1;
  }

  @Override
  public int getOpponentPoints(iFencer f) throws ObjectDeprecatedException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }

    if (fencer1.equals(f))
    {
      return pointsFor2;
    }
    if (fencer2.equals(f))
    {
      return pointsFor1;
    }
    return -1;
  }

  @Override
  public boolean isFinished() throws ObjectDeprecatedException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }

    return finished;
  }

  @Override
  public iFencer getWinner() throws ObjectDeprecatedException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }

    if (!finished)
    {
      return null;
    }

    if (pointsFor1 == pointsFor2)
    {
      return null;
    }

    if (pointsFor1 > pointsFor2)
    {
      return fencer1;
    } else
    {
      return fencer2;
    }
  }

  @Override
  public iFencer getLoser() throws ObjectDeprecatedException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }

    if (!finished)
    {
      return null;
    }

    if (pointsFor1 == pointsFor2)
    {
      return null;
    }

    if (pointsFor1 > pointsFor2)
    {
      return fencer2;
    } else
    {
      return fencer1;
    }
  }

  @Override
  public boolean removeParticipant(iFencer f) throws SQLException, ObjectDeprecatedException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }

    if (finished)
    {
      return false;
    }
    if (fencer1 == f || fencer2 == f)
    {
      doDerivedAddParticipant(f);
      DBTournamentMatch.removeParticipant(this, (Fencer) f);
      if (fencer1.equals(f))
      {
        fencer1 = null;
      }
      if (fencer2.equals(f))
      {
        fencer2 = null;
      }
      setChanged();
      notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
      return true;

    }
    return false;
  }

  protected void doDerivedRemoveParticipant(iFencer f)
  {
    //does nothing in base class but derived classes use it
  }

  @Override
  public boolean addParticipant(iFencer f) throws SQLException, ObjectDeprecatedException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }

    if (finished)
    {
      return false;
    }

    if (fencer1 == null)
    {
      fencer1 = (Fencer) f;
    } else if (fencer2 == null)
    {
      fencer2 = (Fencer) f;
    } else
    {
      return false;
    }
    doDerivedAddParticipant(f);
    DBTournamentMatch.addParticipant(this, (Fencer) f);
    setChanged();
    notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
    return true;
  }

  protected void doDerivedAddParticipant(iFencer f)
  {
    //empty in this class but adds a possiblity for derived classes to add custom steps
  }

  @Override
  public boolean switchParticipantOut(iFencer out, iFencer in) throws SQLException, ObjectDeprecatedException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }

    if (finished)
    {
      return false;
    }
    if (fencer1 == out || fencer2 == out)
    {
      switchFencerOut(out, in);
      if (fencer1.equals(out))
      {
        fencer1 = (Fencer) in;
      }
      if (fencer2.equals(out))
      {
        fencer2 = (Fencer) in;
      }
      return true;
    }
    return false;
  }

  private void switchFencerOut(iFencer out, iFencer in) throws SQLException
  {
    doDerivedSwitchParticipantOut(out, in);
    DBTournamentMatch.switchParticipants(this, (Fencer) out, (Fencer) in);
    setChanged();
    notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
  }

  protected void doDerivedSwitchParticipantOut(iFencer out, iFencer in)
  {
    //does nothing in base class, but inherited classes use it
  }

  @Override
  public boolean isFencerInMatch(iFencer f) throws ObjectDeprecatedException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }
    if (f == null)
    {
      return false;
    }

    if (fencer1.equals(f) || fencer2.equals(f))
    {
      return true;
    }
    return false;
  }

  @Override
  public void setYellow(iFencer f, int count) throws SQLException, ObjectDeprecatedException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }

    if (finished)
    {
      return;
    }

    if (fencer1.equals(f))
    {
      DBTournamentMatch.setYellow(this, (Fencer) f, count);
      yellowFor1 = count;

      setChanged();
      notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
      return;
    }
    if (fencer2.equals(f))
    {
      DBTournamentMatch.setYellow(this, (Fencer) f, count);
      yellowFor2 = count;

      setChanged();
      notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
      return;
    }
  }

  @Override
  public void setRed(iFencer f, int count) throws SQLException, ObjectDeprecatedException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }

    if (finished)
    {
      return;
    }

    if (fencer1.equals(f))
    {
      DBTournamentMatch.setRed(this, (Fencer) f, count);
      redFor1 = count;

      setChanged();
      notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
      return;
    }
    if (fencer2.equals(f))
    {
      DBTournamentMatch.setRed(this, (Fencer) f, count);
      redFor2 = count;

      setChanged();
      notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
      return;
    }
  }

  @Override
  public void setBlack(iFencer f, int count) throws SQLException, ObjectDeprecatedException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }

    if (finished)
    {
      return;
    }

    if (fencer1.equals(f))
    {
      DBTournamentMatch.setBlack(this, (Fencer) f, count);
      blackFor1 = count;

      setChanged();
      notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
      return;
    }
    if (fencer2.equals(f))
    {
      DBTournamentMatch.setBlack(this, (Fencer) f, count);
      blackFor2 = count;

      setChanged();
      notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
      return;
    }
  }

  @Override
  public int getYellow(iFencer f) throws ObjectDeprecatedException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }

    if (fencer1.equals(f))
    {
      return yellowFor1;
    }
    if (fencer2.equals(f))
    {
      return yellowFor2;
    }
    return -1;
  }

  @Override
  public int getRed(iFencer f) throws ObjectDeprecatedException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }

    if (fencer1.equals(f))
    {
      return redFor1;
    }
    if (fencer2.equals(f))
    {
      return redFor2;
    }
    return -1;
  }

  @Override
  public int getBlack(iFencer f) throws ObjectDeprecatedException
  {
    if (!isValid)
    {
      throw new ObjectDeprecatedException();
    }

    if (fencer1.equals(f))
    {
      return blackFor1;
    }
    if (fencer2.equals(f))
    {
      return blackFor2;
    }
    return -1;
  }

  /*
    *   Because of different side effects this has to be handled in the subclasses
   */
  @Override
  public void delete() throws SQLException, ObjectDeprecatedException
  {
    doDerivedDelete();
    //TODO: actually delete match from database
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  protected void doDerivedDelete()
  {

  }
}
