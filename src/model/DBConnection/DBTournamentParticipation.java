package model.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static model.DBConnection.DBBaseClass.rowToHash;
import model.ObjectExistException;
import model.TournamentParticipation;
import org.h2.command.ddl.PrepareProcedure;

public class DBTournamentParticipation extends DBBaseClass
{

  private static String getSQLString()
  {
    return "CREATE TABLE IF NOT EXISTS Teilnahme (ID int NOT NULL AUTO_INCREMENT UNIQUE,"
            + "TurnierID int,"
            + "FechterID int,"
            + "Gruppe int,"
            + "Startgeld boolean DEFAULT FALSE,"
            + "Ausruestungskontrolle boolean DEFAULT FALSE,"
            + "Ausgeschieden boolean DEFAULT FALSE,"
            + "Kommentar VARCHAR(255) DEFAULT '');";
  }

  public static void createTable() throws SQLException
  {
    con.prepareStatement(getSQLString()).executeUpdate();
  }

  private static PreparedStatement cpStmt = null;

  public static int createParticipation(int tournamentID, int fencerID, int group) throws SQLException
  {
    if (cpStmt == null || cpStmt.isClosed())
    {
      String sql = "INSERT INTO Teilnahme (TurnierID, FechterID, Gruppe) VALUES (?, ?, ?);";
      cpStmt = con.prepareStatement(sql);
    }

    cpStmt.setInt(1, tournamentID);
    cpStmt.setInt(2, fencerID);
    cpStmt.setInt(3, group);
    cpStmt.executeUpdate();
    ResultSet rs = cpStmt.getGeneratedKeys();

    if (rs.next())
    {
      return rs.getInt(1);
    } else
    {
      throw new SQLException("Didn't return ID of the new fencer.");
    }
  }

  private static PreparedStatement lpStmt = null;

  public static void loadParticipations() throws SQLException
  {
    if (lpStmt == null || lpStmt.isClosed())
    {
      String sql = "SELECT * FROM Teilnahme;";
      lpStmt = con.prepareStatement(sql);
    }

    ResultSet rs = lpStmt.executeQuery();

    while (rs.next())
    {
      try
      {
        new TournamentParticipation(rowToHash(rs));
      } catch (ObjectExistException ex)
      {
      }//Can be ignored savely
    }
    rs.close();

    return;
  }

  private static PreparedStatement dpStmt = null;

  public static void deleteParticipation(int id) throws SQLException
  {
    if (dpStmt == null || dpStmt.isClosed())
    {
      String sql = "DELETE FROM Teilnahme WHERE ID = ?;";
      dpStmt = con.prepareStatement(sql);
    }

    dpStmt.setInt(1, id);
    dpStmt.executeUpdate();
  }

  private static PreparedStatement uecStmt = null;

  public static void updateEquepmentCheck(int id, boolean checked) throws SQLException
  {
    if (uecStmt == null || uecStmt.isClosed())
    {
      String sql = "UPDATE Teilnahme SET Ausruestungskontrolle = ? WHERE ID = ?;";
      uecStmt = con.prepareStatement(sql);
    }

    uecStmt.setBoolean(1, checked);
    uecStmt.setInt(2, id);
    uecStmt.executeUpdate();
  }

  private static PreparedStatement uefStmt = null;

  public static void updateEntryFee(int id, boolean paid) throws SQLException
  {
    if (uefStmt == null || uefStmt.isClosed())
    {
      String sql = "UPDATE Teilnahme SET Startgeld = ? WHERE ID = ?;";
      uefStmt = con.prepareStatement(sql);
    }

    uefStmt.setBoolean(1, paid);
    uefStmt.setInt(2, id);
    uefStmt.executeUpdate();
  }

  private static PreparedStatement ucStmt = null;

  public static void updateComment(int id, String comment) throws SQLException
  {
    if (ucStmt == null || ucStmt.isClosed())
    {
      String sql = "UPDATE Teilnahme SET Kommentar = ? WHERE ID = ?;";
      ucStmt = con.prepareStatement(sql);
    }

    ucStmt.setString(1, comment);
    ucStmt.setInt(2, id);
    ucStmt.executeUpdate();
  }
}
