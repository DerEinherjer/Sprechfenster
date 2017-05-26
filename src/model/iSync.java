package model;

import java.sql.SQLException;
import java.util.List;
import java.util.Observable;

public abstract class iSync extends Observable {

  private static Sync sync = null;

  public static iSync getInstance () {
    try {
      if (sync == null) {
        sync = new Sync();
      }
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
    return sync;
  }

  public abstract iFencer createFencer (String name, String familyName, String birthDay, String nationality, String fencingSchool) throws SQLException;

  public abstract List<iFencer> getAllFencer () throws SQLException;

  public abstract iFencer getFencerByID (int fencerID) throws SQLException;

  public abstract iTournament createTournament (String name) throws SQLException;

  public abstract List<iTournament> getAllTournaments () throws SQLException;

}
