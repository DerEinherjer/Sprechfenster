package model.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static model.DBConnection.DBTournamentMatch.isFinalsMatch;
import model.Fencer;
import model.ObjectExistException;
import model.Tournament;
import model.rounds.QualificationMatch;

public class DBQualificationPhase extends DBBaseClass
{

  private static PreparedStatement lpStmt = null;

  public static void loadQualificationRound() throws SQLException
  {
    if (lpStmt == null)
    {
      String sql = "SELECT * FROM Vorrunden WHERE NOT " + DBTournamentMatch.isFinalsMatch + ";";
      lpStmt = DBConnection.prepareStatement(sql);
    }

    ResultSet rs = lpStmt.executeQuery();

    while (rs.next())
    {
      try
      {
        //adds the qualification match to the global map as a side-effect
        QualificationMatch match = new QualificationMatch(rowToHash(rs));
      } catch (ObjectExistException e)
      {
      }
    }

    rs.close();
  }

  private static PreparedStatement cpStmt = null;

  public static int createQualificationPhase(Tournament t, Fencer f1, Fencer f2) throws SQLException
  {
    if (cpStmt == null)
    {
      String sql = "INSERT INTO Vorrunden (TurnierID, Gruppe, Teilnehmer1, Teilnehmer2, " + isFinalsMatch + ") VALUES (?, ?, ?, ?, ?);";
      cpStmt = DBConnection.prepareStatement(sql);
    }

    cpStmt.setInt(1, t.getID());
    cpStmt.setInt(2, t.getParticipantGroup(f1));
    cpStmt.setInt(3, f1.getID());
    cpStmt.setInt(4, f2.getID());
    cpStmt.setBoolean(5, false);

    cpStmt.executeUpdate();
    ResultSet rs = cpStmt.getGeneratedKeys();

    if (rs.next())
    {
      return rs.getInt(1);
    } else
    {
      throw new SQLException("Didn't return ID of the new preliminary.");
    }
  }
}
