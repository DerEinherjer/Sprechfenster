package model.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.ObjectExistException;
import model.Tournament;

public class DBTournament extends DBBaseClass
{

  private static String getSQLString()
  {
    return "CREATE TABLE IF NOT EXISTS Turniere (ID int NOT NULL AUTO_INCREMENT UNIQUE,"
            + "Name varchar(255) DEFAULT 'Nicht Angegeben',"
            + "Datum varchar(11) DEFAULT '1970-01-01',"
            + "Gruppen int DEFAULT 2,"
            + "Finalrunden int DEFAULT 2,"
            + "Bahnen int DEFAULT 2,"
            + "Status int DEFAULT 0,"
            + "VorgruppenSeparieren boolean DEFAULT FALSE);";
  }

  public static void createTable() throws SQLException
  {
    DBConnection.prepareStatement(getSQLString()).executeUpdate();
  }

  private static PreparedStatement ctStmt = null;

  public static int createTournament(String name) throws SQLException
  {
    if (ctStmt == null || ctStmt.isClosed())
    {
      String sql = "INSERT INTO Turniere (Name) VALUES (?);";
      ctStmt = DBConnection.prepareStatement(sql);
    }
    ctStmt.setString(1, name);
    ctStmt.executeUpdate();
    ResultSet rs = ctStmt.getGeneratedKeys();

    if (rs.next())
    {
      return rs.getInt(1);
    } else
    {
      throw new SQLException("Didn't return ID of the new fencer.");
    }
  }

  private static PreparedStatement ltStmt = null;

  public static void loadTournaments() throws SQLException
  {
    if (ltStmt == null || ltStmt.isClosed())
    {
      String sql = "SELECT * FROM Turniere;";
      ltStmt = DBConnection.prepareStatement(sql);
    }

    ResultSet rs = ltStmt.executeQuery();

    while (rs.next())
    {
      try
      {
        Tournament t = new Tournament(rowToHash(rs));//adds tournament to static list as a side effect
      } catch (ObjectExistException ex)
      {
      }//Can be ignored savely
    }
    rs.close();
  }

  private static PreparedStatement snStmt = null;

  public static void setName(int id, String name) throws SQLException
  {
    if (snStmt == null || snStmt.isClosed())
    {
      String sql = "UPDATE Turniere SET Name = ? WHERE ID = ?;";
      snStmt = DBConnection.prepareStatement(sql);
    }
    snStmt.setString(1, name);
    snStmt.setInt(2, id);
    snStmt.executeUpdate();
  }

  private static PreparedStatement sdStmt = null;

  public static void setDate(int id, String date) throws SQLException
  {
    if (sdStmt == null || sdStmt.isClosed())
    {
      String sql = "UPDATE Turniere SET Datum = ? WHERE ID = ?;";
      sdStmt = DBConnection.prepareStatement(sql);
    }
    sdStmt.setString(1, date);
    sdStmt.setInt(2, id);
    sdStmt.executeUpdate();
  }

  private static PreparedStatement sgStmt = null;

  public static void setGroups(int id, int groups) throws SQLException
  {
    if (sgStmt == null || sgStmt.isClosed())
    {
      String sql = "UPDATE Turniere SET Gruppen = ? WHERE ID = ?;";
      sgStmt = DBConnection.prepareStatement(sql);
    }
    sgStmt.setInt(1, groups);
    sgStmt.setInt(2, id);
    sgStmt.executeUpdate();
  }

  private static PreparedStatement sfrStmt = null;

  public static void setFinalRounds(int id, int rounds) throws SQLException
  {
    if (sfrStmt == null || sfrStmt.isClosed())
    {
      String sql = "UPDATE Turniere SET Finalrunden = ? WHERE ID = ?;";
      sfrStmt = DBConnection.prepareStatement(sql);
    }
    sfrStmt.setInt(1, rounds);
    sfrStmt.setInt(2, id);
    sfrStmt.executeUpdate();
  }

  private static PreparedStatement tslStmt = null;

  public static void setLanes(int id, int lanes) throws SQLException
  {
    if (tslStmt == null || tslStmt.isClosed())
    {
      String sql = "UPDATE Turniere SET Bahnen = ? WHERE ID = ?;";
      tslStmt = DBConnection.prepareStatement(sql);
    }
    tslStmt.setInt(1, lanes);
    tslStmt.setInt(2, id);
    tslStmt.executeUpdate();
  }

  private static PreparedStatement setStatusStmt = null;

  public static void setStatus(int id, int status) throws SQLException
  {
    if (setStatusStmt == null || setStatusStmt.isClosed())
    {
      String sql = "UPDATE Turniere SET Status = ? WHERE ID = ?;";
      setStatusStmt = DBConnection.prepareStatement(sql);
    }
    setStatusStmt.setInt(1, status);
    setStatusStmt.setInt(2, id);
    setStatusStmt.executeUpdate();
  }
}
