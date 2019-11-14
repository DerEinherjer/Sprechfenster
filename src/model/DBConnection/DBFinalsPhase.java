/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static model.DBConnection.DBBaseClass.DBConnection;
import static model.DBConnection.DBTournamentMatch.isFinalsMatch;
import model.ObjectExistException;
import model.Tournament;
import model.rounds.FinalsMatch;

/**
 *
 * @author Asgard
 */
public class DBFinalsPhase extends DBBaseClass
{

  private static PreparedStatement loadFinalRounds = null;
  private static PreparedStatement lfStmt2 = null;

  public static void loadFinalrounds() throws SQLException
  {
    if (loadFinalRounds == null)
    {
      String sql = "SELECT * FROM Vorrunden WHERE " + DBTournamentMatch.isFinalsMatch + ";";
      loadFinalRounds = DBConnection.prepareStatement(sql);
    }

    ResultSet rs = loadFinalRounds.executeQuery();

    while (rs.next())
    {
      Map<String, Object> tmp = rowToHash(rs);
      try
      {
        FinalsMatch match = new FinalsMatch(tmp);
      } catch (ObjectExistException ex)
      {
        Logger.getLogger(DBFinalsPhase.class.getName()).log(Level.SEVERE, null, ex);
      }

    }

    rs.close();
  }

  private static PreparedStatement createFinalRound = null;

  public static int createFinalround(Tournament t, int finalRound) throws SQLException
  {
    if (createFinalRound == null)
    {
      String sql = "INSERT INTO Vorrunden (TurnierID, Runde, " + isFinalsMatch + ") VALUES (?, ?, ?);";
      createFinalRound = DBConnection.prepareStatement(sql);
    }

    createFinalRound.setInt(1, t.getID());
    createFinalRound.setInt(2, finalRound);
    createFinalRound.setBoolean(3, true);

    createFinalRound.executeUpdate();
    ResultSet rs = createFinalRound.getGeneratedKeys();

    if (rs.next())
    {
      return rs.getInt(1);
    } else
    {
      throw new SQLException("Didn't return ID of the new final round.");
    }
  }
}
