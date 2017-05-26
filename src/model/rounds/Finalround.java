package model.rounds;

import model.Fencer;
import model.ObjectDeprecatedException;
import model.ObjectExistException;
import model.Tournament;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Finalround extends Round implements iFinalround {

  private static Map<Integer, Finalround> finalrounds = new HashMap<>();

  public static void ClearDatabaseCache () {
    finalrounds.clear();
  }

  public static Finalround getFinalround (int id) throws SQLException {
    if (!finalrounds.containsKey(id)) {
      sync.loadFinalround(id);
    }
    return finalrounds.get(id);
  }

  public static List<Finalround> getFinalrounds (Tournament t) {
    List<Finalround> ret = new ArrayList<>();
    for (Finalround f : finalrounds.values()) {
      try {
        if (f.getTournament().equals(t)) {
          ret.add(f);
        }
      }
      catch (ObjectDeprecatedException ex) {
        //Depricated Objectes can be ignored safely
      }
    }
    return ret;
  }

  public static void deleteAllFinalRoundsOfTournament (Tournament tournamentToDelete) {
    List<Finalround> roundsToDelete = new ArrayList<>();
    roundsToDelete.addAll(finalrounds.values());
    for (Finalround f : roundsToDelete) {
      try {
        if (f.getTournament().equals(tournamentToDelete)) {
          finalrounds.remove(f.getID());
        }
      }
      catch (ObjectDeprecatedException ex) {
        //Depricated Objectes can be ignored safely
      }
    }
  }

  public static String getSQLString () {
    return "CREATE TABLE IF NOT EXISTS Finalrunden (ID int NOT NULL AUTO_INCREMENT UNIQUE,"
            + "GewinnerRunde int DEFAULT -1,"
            + "VerliererRunde int DEFAULT -1,"
            + "FinalRunde int DEFAULT -1);";
  }

  // --------------------------------------------------------
  private Finalround winnersround = null;
  private Finalround losersround = null;
  private Finalround preround1 = null;
  private Finalround preround2 = null;
  private Integer finalrunde = null;

  public Finalround (Map<String, Object> set) throws ObjectExistException, SQLException {
    super(set);

    if (finalrounds.containsKey(this.ID)) {
      throw new ObjectExistException(finalrounds.get(this.ID));
    }
    finalrounds.put(this.ID, this);

    this.finalrunde = (Integer) set.get("FinalRunde".toUpperCase());

    try {
      this.winnersround = Finalround.getFinalround((Integer) set.get("GewinnerRunde".toUpperCase()));
    }
    catch (Exception e) {
    }//Catches null-pointer for the finalround

    try {
      this.losersround = Finalround.getFinalround((Integer) set.get("VerliererRunde".toUpperCase()));
    }
    catch (Exception e) {
    }//Catches nullpointer for all except the halffinal

    if (winnersround != null) {
      winnersround.initPrerounds(this);
    }
    if (losersround != null) {
      losersround.initPrerounds(this);
    }
  }

  void initPrerounds (Finalround f) {
    if (preround1 == null) {
      preround1 = f;
    }
    else {
      if (preround2 == null) {
        preround2 = f;
      }
    }
  }

  @Override
  public void setFinished (boolean finish) throws SQLException, ObjectDeprecatedException {
    if (this.isFinished() != finish) {
      finished = finish;
      if (finished) {
        if (winnersround != null) {
          winnersround.addParticipant(getWinner());
        }
        if (losersround != null) {
          losersround.addParticipant(getLoser());
        }

        t.addWinFinal((Fencer) getWinner());

        t.addHitsFinal(fencer1, pointsFor1);
        t.addHitsFinal(fencer2, pointsFor2);

        t.addGotHitFinal(fencer1, pointsFor2);
        t.addGotHitFinal(fencer2, pointsFor1);

        sync.setPrelimFinished(this, finished);
      }
      else {
        if (winnersround != null) {
          winnersround.removeParticipant(getWinner());
        }
        if (losersround != null) {
          losersround.removeParticipant(getLoser());
        }

        t.subWinFinal((Fencer) getWinner());

        t.addHitsFinal(fencer1, -pointsFor1);
        t.addHitsFinal(fencer2, -pointsFor2);

        t.addGotHitFinal(fencer1, -pointsFor2);
        t.addGotHitFinal(fencer2, -pointsFor1);

        sync.setPrelimFinished(this, finished);
      }
    }
  }

  @Override
  public Finalround getWinnerRound () throws ObjectDeprecatedException {
    if (!isValid) {
      throw new ObjectDeprecatedException();
    }
    return winnersround;
  }

  @Override
  public Finalround getLoserRound () throws ObjectDeprecatedException {
    if (!isValid) {
      throw new ObjectDeprecatedException();
    }
    return losersround;
  }

  @Override
  public List<iFinalround> getPrerounds () throws ObjectDeprecatedException {
    if (!isValid) {
      throw new ObjectDeprecatedException();
    }
    List<iFinalround> ret = new ArrayList<>();
    if (preround1 != null) {
      ret.add(preround1);
    }

    if (preround2 != null) {
      ret.add(preround2);
    }
    return ret;
  }

  @Override
  public void delete () throws SQLException, ObjectDeprecatedException {
    if (!isValid) {
      throw new ObjectDeprecatedException();
    }
    if (finished) //This is needet in case finished rounds will be deletable
    {
      setFinished(false); //It will keep the score of the tournament correct.
    }
    sync.deleteFinalRoundFromDatabase(this.ID);
    finalrounds.remove(this.ID);
    this.ID = -1;
    isValid = false;
  }

  @Override
  public int getFinalRound () {
    return finalrunde;
  }

  public boolean hasPrerounds () {
    return preround1 != null && preround2 != null;
  }
}
