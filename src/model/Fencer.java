package model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import static model.rounds.Round.sync;

public class Fencer extends Observable implements iFencer {
  // -----

  static Sync sync;
  private static Map<Integer, Fencer> fencers = new HashMap<>();

  public static void ClearDatabaseCache () {
    fencers.clear();
  }

  public static Fencer getFencer (int id) throws SQLException {
    if (!fencers.containsKey(id)) {
      sync.loadFencer(id);
    }
    return fencers.get(id);
  }

  static List<Fencer> getFencer (Tournament t) throws SQLException {
    List<Fencer> ret = new ArrayList<>();
    for (Map.Entry<Integer, Fencer> entry : fencers.entrySet()) {
      if (sync.isFencerParticipant(t, entry.getValue())) {
        ret.add(entry.getValue());
      }
    }
    return ret;
  }

  static List<Fencer> getAllFencer () {
    List<Fencer> ret = new ArrayList<>();
    for (Map.Entry<Integer, Fencer> entry : fencers.entrySet()) {
      ret.add(entry.getValue());
    }
    return ret;
  }

  // -----
  private int ID;

  private boolean isValid = true;

  private String name = null;
  private String familyName = null;
  private String birthday = null;
  private String fencingSchool = null;
  private String nationality = null;

  static String getSQLString () {
    return "CREATE TABLE IF NOT EXISTS Fechter (ID int NOT NULL AUTO_INCREMENT UNIQUE,"
            + "Vorname varchar(255) DEFAULT 'Max',"
            + "Nachname varchar(255) DEFAULT 'Musterman',"
            + "Geburtstag varchar(255) DEFAULT '1970-01-01',"
            + "Fechtschule varchar(255) DEFAULT 'Keine Fechtschule',"
            + "Nationalitaet varchar(255) DEFAULT 'Nicht Angegeben');";
  }

  Fencer (Map<String, Object> set) throws ObjectExistException {
    this.ID = (Integer) set.get("ID");

    if (fencers.containsKey(this.ID)) {
      throw new ObjectExistException(fencers.get(this.ID));
    }
    fencers.put(this.ID, this);

    this.name = (String) set.get("Vorname".toUpperCase());
    this.familyName = (String) set.get("Nachname".toUpperCase());
    this.birthday = (String) set.get("Geburtstag".toUpperCase());
    this.fencingSchool = (String) set.get("Fechtschule".toUpperCase());
    this.nationality = (String) set.get("Nationalitaet".toUpperCase());
    
    sync.observeThis(this);
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
  public String getFamilyName () {
    return familyName;
  }

  @Override
  public String getFullName () {
    return name + " " + familyName;
  }

  @Override
  public String getBirthday () {
    return birthday;
  }

  @Override
  public String getFencingSchool () {
    return fencingSchool;
  }

  @Override
  public String getNationality () {
    return nationality;
  }

  @Override
  public void setName (String name) throws SQLException {
    this.name = name;
    sync.fencerSetName(name, ID);
    setChanged();
    notifyObservers(Sync.change.changedFencerValue);
  }

  @Override
  public void setFamilyName (String name) throws SQLException {
    this.familyName = name;
    sync.fencerSetFamilyName(name, ID);
    setChanged();
    notifyObservers(Sync.change.changedFencerValue);
  }

  @Override
  public void setBirthday (String date) throws SQLException {
    this.birthday = date;
    sync.fencerSetBirthday(date, ID);
    setChanged();
    notifyObservers(Sync.change.changedFencerValue);
  }

  @Override
  public void setFencingSchool (String school) throws SQLException {
    this.fencingSchool = school;
    sync.fencerSetFencingSchool(school, ID);
    setChanged();
    notifyObservers(Sync.change.changedFencerValue);
  }

  @Override
  public void setNationality (String nation) throws SQLException {
    this.nationality = nation;
    sync.fencerSetNationality(nation, ID);
    setChanged();
    notifyObservers(Sync.change.changedFencerValue);
  }

  @Override
  public String toString () {
    return ID + " | " + name + " | " + familyName;
  }

  @Override
  public boolean equals (Object other) {
    if (other == null) {
      return false;
    }
    if (other == this) {
      return true;
    }
    if (!(other instanceof Fencer)) {
      return false;
    }
    if (((Fencer) other).getID() == ID) {
      return true;
    }
    return false;
  }

  public void delete () throws SQLException {
    if (!isValid) {
      return;
    }

    for (Tournament t : Tournament.getAllTournaments()) {
      if (t.isParticipant(this)) {
        t.removeParticipant(this);
      }
    }

    fencers.remove(ID);
    sync.deleteFencerFromDatabase(ID);
    ID = -1;
    isValid = false;
  }
}
