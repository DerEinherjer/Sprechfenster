package model.rounds;

import model.Fencer;
import model.ObjectDeprecatedException;
import model.ObjectExistException;
import model.Tournament;
import model.iFencer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Preliminary extends Round implements iPreliminary {
  // -----

  private static Map<Integer, Preliminary> preliminarys = new HashMap<>();

  public static void ClearDatabaseCache () {
    preliminarys.clear();
  }

  public static Preliminary getPreliminary (int id) throws SQLException {
    if (!preliminarys.containsKey(id)) {
      sync.loadPreliminary(id);
    }
    return preliminarys.get(id);
  }

  public static List<Preliminary> getPreliminarys (Tournament t) {
    List<Preliminary> ret = new ArrayList<>();
    for (Map.Entry<Integer, Preliminary> entry : preliminarys.entrySet()) {
      if (entry.getValue().t.equals(t)) {
        ret.add(entry.getValue());
      }
    }
    return ret;
  }

  public static void deletePreliminarys (Fencer f) throws SQLException {
    List<Preliminary> remove = new ArrayList<>();
    for (Preliminary p : preliminarys.values()) {
      try {
        if (p.isFencer(f)) {
          remove.add(p);//DO NOT DELET HERE
          //IT FUCKS UP THE ITERATOR
        }
      }
      catch (ObjectDeprecatedException e) {
      }
    }

    for (Preliminary p : remove) {
      try {
        p.delete();
      }
      catch (ObjectDeprecatedException e) {
      }
    }
  }

  public static void deleteAllPreliminaryRoundsForTournament (Tournament t) throws SQLException {
    List<Preliminary> remove = new ArrayList<>();
    for (Preliminary p : preliminarys.values()) {
      try {
        if (p.getTournament().equals(t)) {
          remove.add(p);//DO NOT DELET HERE
          //IT FUCKS UP THE ITERATOR
        }
      }
      catch (ObjectDeprecatedException e) {
      }
    }

    for (Preliminary p : remove) {
      try {
        p.delete();
      }
      catch (ObjectDeprecatedException e) {
      }
    }
  }

  public Preliminary (Map<String, Object> set) throws ObjectExistException, SQLException {
    super(set);

    //Checks if there is allready an Object for this ID
    if (preliminarys.containsKey(this.ID)) {
      throw new ObjectExistException(preliminarys.get(this.ID));
    }
    preliminarys.put(this.ID, this);

    //Propagates the score to the Turnament if the fight is finished
    propagateScore();
  }

  public String toString () {
    if (!isValid) {
      return "InvalidObject";
    }
    return group + " | " + fencer1.getFamilyName() + " | " + fencer2.getFamilyName();
  }

  public void setFinished (boolean finish) throws SQLException, ObjectDeprecatedException {
    if (!isValid) {
      throw new ObjectDeprecatedException();
    }

    if (t.isPreliminaryFinished()) {
      return;
    }

    if (finish != finished) {
      finished = finish;
      if (finished) {
        if (pointsFor1 > pointsFor2) {
          t.addWinPrelim(fencer1);
        }
        else {
          if (pointsFor1 < pointsFor2) {
            t.addWinPrelim(fencer2);
          }
        }

        t.addHitsPrelim(fencer1, pointsFor1);
        t.addHitsPrelim(fencer2, pointsFor2);
        t.addGotHitPrelim(fencer1, pointsFor2);
        t.addGotHitPrelim(fencer2, pointsFor1);

        sync.setPrelimFinished(this, finished);
      }
      else {
        if (pointsFor1 > pointsFor2) {
          t.subWinPrelim(fencer1);
        }
        else {
          if (pointsFor1 < pointsFor2) {
            t.subWinPrelim(fencer2);
          }
        }

        t.addHitsPrelim(fencer1, -pointsFor1);
        t.addHitsPrelim(fencer2, -pointsFor2);
        t.addGotHitPrelim(fencer1, -pointsFor2);
        t.addGotHitPrelim(fencer2, -pointsFor1);

        sync.setPrelimFinished(this, finished);
      }
    }
  }

  private void propagateScore () throws SQLException {
    if (finished) {
      if (pointsFor1 > pointsFor2) {
        t.addWinPrelim(fencer1);
      }
      else {
        if (pointsFor1 < pointsFor2) {
          t.addWinPrelim(fencer2);
        }
      }

      t.addHitsPrelim(fencer1, pointsFor1);
      t.addHitsPrelim(fencer2, pointsFor2);
      t.addGotHitPrelim(fencer1, pointsFor2);
      t.addGotHitPrelim(fencer2, pointsFor1);
    }
  }

  public void delete () throws SQLException, ObjectDeprecatedException {
    if (!isValid) {
      throw new ObjectDeprecatedException();
    }

    if (finished) //This is needet in case finished rounds will be deletable
    {
      setFinished(false);
    } //It will keep the score of the tournament correct.
    sync.deletePreliminaryFromDatabase(this.ID);
    preliminarys.remove(this.ID);
    this.ID = -1;
    isValid = false;
  }

  public boolean addParticipant (iFencer f) throws SQLException, ObjectDeprecatedException {
    if (t.isPreliminaryFinished()) {
      return false;
    }
    else {
      return super.addParticipant(f);
    }
  }

  public void setPoints (iFencer f, int points) throws SQLException, ObjectDeprecatedException {
    if (t.isPreliminaryFinished()) {
      return;
    }
    else {
      super.setPoints(f, points);
    }
  }
}//
