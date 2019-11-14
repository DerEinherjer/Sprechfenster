package model.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static model.DBConnection.DBBaseClass.DBConnection;
import model.Fencer;
import model.ObjectDeprecatedException;
import model.iTournament;
import model.rounds.TournamentMatch;

public class DBTournamentMatch extends DBBaseClass
{

  public static String isFinalsMatch = "FinalMatch";

  private static String getSQLString()
  {
    return "CREATE TABLE IF NOT EXISTS Vorrunden (ID int NOT NULL AUTO_INCREMENT UNIQUE,"
            + "TurnierID int,"
            + "Gruppe int DEFAULT -1,"
            + "Runde int DEFAULT -1,"
            + "Bahn int DEFAULT -1,"
            + "Teilnehmer1 int DEFAULT -1,"
            + "Teilnehmer2 int DEFAULT -1,"
            + "PunkteVon1 int DEFAULT 0,"
            + "PunkteVon2 int DEFAULT 0,"
            + "Beendet boolean DEFAULT FALSE,"
            + "GelbVon1 int DEFAULT 0,"
            + "RotVon1 int DEFAULT 0,"
            + "SchwarzVon1 int DEFAULT 0,"
            + "GelbVon2 int DEFAULT 0,"
            + "RotVon2 int DEFAULT 0,"
            + "SchwarzVon2 int DEFAULT 0,"
            + "FinalGewinnerMatch int DEFAULT -1,"
            + "FinalVerliererMatch int DEFAULT -1,"
            + isFinalsMatch + " boolean DEFAULT false);";
  }

  public static void createTable() throws SQLException
  {
    DBConnection.prepareStatement(getSQLString()).executeUpdate();
  }

  private PreparedStatement stfp1Stmt = null;
  private PreparedStatement stfp2Stmt = null;

  private static PreparedStatement getTournamentLanes = null;
  private static PreparedStatement findQualificationRound = null;
  private static PreparedStatement updateQualificationRound = null;

  public static boolean setQualificationMatchTime(TournamentMatch p, int round, int lane) throws SQLException
  {
    if (round < 1 || lane < 1)
    {
      return false;
    }
    if (!CheckTournamentLaneNumber(lane, p.getTournament()))
    {
      return false;
    }
    ResultSet rs = findRoundWithID(p.getID());
    boolean roundExists = rs.next();
    roundExists &= !rs.getBoolean(isFinalsMatch);
    rs.close();
    if (!roundExists)
    {
      return false;
    }
    return setRoundAndLane(round, lane, p.getID());
  }

  private static ResultSet findRoundWithID(int id) throws SQLException
  {
    if (findQualificationRound == null)
    {
      String sql = "SELECT * FROM Vorrunden WHERE ID = ?";
      findQualificationRound = DBConnection.prepareStatement(sql);
    }
    findQualificationRound.setInt(1, id);
    return findQualificationRound.executeQuery();
  }

  private static boolean setRoundAndLane(int round, int lane, int matchID) throws SQLException
  {
    if (updateQualificationRound == null)
    {
      String sql = "UPDATE Vorrunden SET Runde = ?, Bahn = ? WHERE ID = ?;";
      updateQualificationRound = DBConnection.prepareStatement(sql);
    }
    updateQualificationRound.setInt(1, round);
    updateQualificationRound.setInt(2, lane);
    updateQualificationRound.setInt(3, matchID);
    updateQualificationRound.executeUpdate();
    return true;
  }

  public static boolean setFinalsMatchTime(TournamentMatch p, int round, int lane) throws SQLException
  {
    if (round < 1 || lane < 1)
    {
      return false;
    }
    if (!CheckTournamentLaneNumber(lane, p.getTournament()))
    {
      return false;
    }
    ResultSet rs = findRoundWithID(p.getID());
    boolean roundExists = rs.next();
    roundExists &= rs.getBoolean(isFinalsMatch);
    rs.close();
    if (!roundExists)
    {
      return false;
    }
    return setRoundAndLane(round, lane, p.getID());
  }

  private static boolean CheckTournamentLaneNumber(int lane, iTournament t) throws SQLException
  {
    if (getTournamentLanes == null)
    {
      String sql = "SELECT Bahnen FROM Turniere WHERE ID = ?";
      getTournamentLanes = DBConnection.prepareStatement(sql);
    }
    getTournamentLanes.setInt(1, t.getID());
    ResultSet rs = getTournamentLanes.executeQuery();
    boolean hasResult = rs.next();
    boolean laneOk = false;
    if (hasResult)
    {
      if (lane <= rs.getInt("Bahnen"))
      {
        laneOk = true;
      }
      rs.close();
      if (!laneOk)
      {
        return false;
      }
    }
    return true;
  }

  private static PreparedStatement spStmt = null;

  public static void setPoints(int prelimID, int pointsFencer1, int pointsFencer2) throws SQLException
  {
    if (spStmt == null)
    {
      String sql = "UPDATE Vorrunden SET PunkteVon1 = ?, PunkteVon2 = ? WHERE ID = ?;";
      spStmt = DBConnection.prepareStatement(sql);
    }

    spStmt.setInt(1, pointsFencer1);
    spStmt.setInt(2, pointsFencer2);
    spStmt.setInt(3, prelimID);
    spStmt.executeUpdate();
  }

  private static PreparedStatement rpfpStmt = null;

  public static void removeParticipant(TournamentMatch p, Fencer f) throws SQLException
  {
    if (rpfpStmt == null)
    {
      String sql = "UPDATE Vorrunden SET Teilnehmer1 = CASE WHEN Teilnehmer1 = ? THEN -1 ELSE Teilnehmer1 END, "
              + "Teilnehmer2 = CASE WHEN Teilnehmer2 = ? THEN -1 ELSE Teilnehmer2 END WHERE ID = ?;";
      rpfpStmt = DBConnection.prepareStatement(sql);
    }
    try
    {
      rpfpStmt.setInt(1, f.getID());
      rpfpStmt.setInt(2, f.getID());
      rpfpStmt.setInt(3, p.getID());
      rpfpStmt.executeUpdate();
    } catch (ObjectDeprecatedException e)
    {
    }//Can be ignored
  }

  private static PreparedStatement aptpStmt = null;

  public static void addParticipant(TournamentMatch p, Fencer f) throws SQLException
  {
    if (aptpStmt == null)
    {
      String sql = "UPDATE Vorrunden SET Teilnehmer1 = CASE WHEN Teilnehmer1 = -1 THEN ? ELSE Teilnehmer1 END, "
              + "Teilnehmer2 = CASE WHEN Teilnehmer2 = -1 AND Teilnehmer1 != -1 THEN ? ELSE Teilnehmer2 END WHERE ID = ?;";
      aptpStmt = DBConnection.prepareStatement(sql);
    }
    try
    {
      aptpStmt.setInt(1, f.getID());
      aptpStmt.setInt(2, f.getID());
      aptpStmt.setInt(3, p.getID());
      aptpStmt.executeUpdate();
    } catch (ObjectDeprecatedException e)
    {
    }
  }

  private static PreparedStatement spipStmt = null;

  public static void switchParticipants(TournamentMatch p, Fencer out, Fencer in) throws SQLException
  {
    if (spipStmt == null)
    {
      String sql = "UPDATE Vorrunden SET Teilnehmer1 = CASE WHEN Teilnehmer1 = ? AND Teilnehmer2 != ? THEN ? ELSE Teilnehmer1 END, "
              + "Teilnehmer2 = CASE WHEN Teilnehmer2 = ? AND Teilnehmer1 != ? THEN ? ELSE Teilnehmer2 END WHERE ID = ?;";
      spipStmt = DBConnection.prepareStatement(sql);
    }
    try
    {
      spipStmt.setInt(1, out.getID());
      spipStmt.setInt(2, in.getID());
      spipStmt.setInt(3, in.getID());
      spipStmt.setInt(4, out.getID());
      spipStmt.setInt(5, in.getID());
      spipStmt.setInt(6, in.getID());
      spipStmt.setInt(7, p.getID());
      spipStmt.executeUpdate();
    } catch (ObjectDeprecatedException e)
    {
    }
  }

  private static PreparedStatement sypStmt = null;

  public static void setYellow(TournamentMatch p, Fencer f, int count) throws SQLException
  {
    if (sypStmt == null)
    {
      String sql = "UPDATE Vorrunden SET GelbVon1 = CASE WHEN Teilnehmer1 = ? THEN ? ELSE GelbVon1 END,"
              + "GelbVon2 = CASE WHEN Teilnehmer2 = ? THEN ? ELSE GelbVon2 END "
              + "WHERE ID = ?";
      sypStmt = DBConnection.prepareStatement(sql);
    }

    try
    {
      sypStmt.setInt(1, f.getID());
      sypStmt.setInt(2, count);
      sypStmt.setInt(3, f.getID());
      sypStmt.setInt(4, count);
      sypStmt.setInt(5, p.getID());

      sypStmt.executeUpdate();
    } catch (ObjectDeprecatedException e)
    {
    }
  }

  private static PreparedStatement srpStmt = null;

  public static void setRed(TournamentMatch p, Fencer f, int count) throws SQLException
  {
    if (srpStmt == null)
    {
      String sql = "UPDATE Vorrunden SET RotVon1 = CASE WHEN Teilnehmer1 = ? THEN ? ELSE RotVon1 END,"
              + "RotVon2 = CASE WHEN Teilnehmer2 = ? THEN ? ELSE RotVon2 END "
              + "WHERE ID = ?";
      srpStmt = DBConnection.prepareStatement(sql);
    }

    try
    {
      srpStmt.setInt(1, f.getID());
      srpStmt.setInt(2, count);
      srpStmt.setInt(3, f.getID());
      srpStmt.setInt(4, count);
      srpStmt.setInt(5, p.getID());

      srpStmt.executeUpdate();
    } catch (ObjectDeprecatedException e)
    {
    }
  }

  private static PreparedStatement sbpStmt = null;

  public static void setBlack(TournamentMatch p, Fencer f, int count) throws SQLException
  {
    if (sbpStmt == null)
    {
      String sql = "UPDATE Vorrunden SET SchwarzVon1 = CASE WHEN Teilnehmer1 = ? THEN ? ELSE SchwarzVon1 END,"
              + "SchwarzVon2 = CASE WHEN Teilnehmer2 = ? THEN ? ELSE SchwarzVon2 END "
              + "WHERE ID = ?";
      sbpStmt = DBConnection.prepareStatement(sql);
    }

    try
    {
      sbpStmt.setInt(1, f.getID());
      sbpStmt.setInt(2, count);
      sbpStmt.setInt(3, f.getID());
      sbpStmt.setInt(4, count);
      sbpStmt.setInt(5, p.getID());

      sbpStmt.executeUpdate();
    } catch (ObjectDeprecatedException e)
    {
    }
  }

  private static PreparedStatement sfStmt = null;

  public static void setFinished(TournamentMatch r, boolean finish) throws SQLException
  {
    if (sfStmt == null)
    {
      String sql = "UPDATE Vorrunden SET Beendet = ? WHERE ID = ?;";
      sfStmt = DBConnection.prepareStatement(sql);
    }

    try
    {
      sfStmt.setBoolean(1, finish);
      sfStmt.setInt(2, r.getID());

      sfStmt.executeUpdate();
    } catch (ObjectDeprecatedException e)
    {
    }
  }

  private static PreparedStatement setIsFinalsRound = null;

  public static void setIsFinalsMatch(TournamentMatch r, boolean isFinalsMatch) throws SQLException
  {
    if (setIsFinalsRound == null)
    {
      String sql = "UPDATE Vorrunden SET " + isFinalsMatch + " = ? WHERE ID = ?;";
      setIsFinalsRound = DBConnection.prepareStatement(sql);
    }

    try
    {
      setIsFinalsRound.setBoolean(1, isFinalsMatch);
      setIsFinalsRound.setInt(2, r.getID());

      setIsFinalsRound.executeUpdate();
    } catch (ObjectDeprecatedException e)
    {
    }
  }

  private static PreparedStatement setWinnerMatch = null;

  public static void setWinnerMatch(TournamentMatch r, int winnerMatchID) throws SQLException
  {
    if (setWinnerMatch == null)
    {
      String sql = "UPDATE Vorrunden SET FinalGewinnerMatch = ? WHERE ID = ?;";
      setWinnerMatch = DBConnection.prepareStatement(sql);
    }

    try
    {
      setWinnerMatch.setInt(1, winnerMatchID);
      setWinnerMatch.setInt(2, r.getID());

      setWinnerMatch.executeUpdate();
    } catch (ObjectDeprecatedException e)
    {
    }
  }

  private static PreparedStatement setLoserMatch = null;

  public static void setLoserMatch(TournamentMatch r, int loserMatchID) throws SQLException
  {
    if (setLoserMatch == null)
    {
      String sql = "UPDATE Vorrunden SET FinalVerliererMatch = ? WHERE ID = ?;";
      setLoserMatch = DBConnection.prepareStatement(sql);
    }

    try
    {
      setLoserMatch.setInt(1, loserMatchID);
      setLoserMatch.setInt(2, r.getID());

      setLoserMatch.executeUpdate();
    } catch (ObjectDeprecatedException e)
    {
    }
  }
}
