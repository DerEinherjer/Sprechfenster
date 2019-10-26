/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.DBConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Fencer;
import model.TournamentParticipation;
import model.Tournament;
import model.rounds.QualificationMatch;
import model.rounds.TournamentMatch;

/**
 * This class initiat the connection to the database and the database structure.
 * All DB*Representer classes inherit this class to communicate with the data-
 * base.
 *
 * @author Asgard
 */
public abstract class DBBaseClass
{

  static String databaseURL = "jdbc:h2:~/SprechfensterData";
  static Connection con;

  private static List<DBEntity> dbEntities = new ArrayList<>();

  static
  {
    //Add all classes with db representation so that they get automaticaly
    //initiated. They need the DBEntetyRepresenter interface.
    dbEntities.add(new Fencer());
    dbEntities.add(new Tournament());
    dbEntities.add(new TournamentParticipation());
    dbEntities.add(new TournamentMatch());
    dbEntities.add(new QualificationMatch());

    try
    {
      Class.forName("org.h2.Driver");

      con = DriverManager.getConnection(databaseURL, "", "");

      init();
    } //TODO: should we crash if one of these exceptions is thrown? is better logging possible?
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
    } catch (SQLException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Helper function that converts a result object in an hash which has the
   * colum names as keys and the values as values
   *
   * @param rs ResultSet object
   * @return Hash which has colum value pairs
   * @throws SQLException yes
   */
  static Map<String, Object> rowToHash(ResultSet rs) throws SQLException
  {
    Map<String, Object> ret = new HashMap<>();
    ResultSetMetaData md = rs.getMetaData();
    int columns = md.getColumnCount();
    for (int i = 1; i <= columns; ++i)
    {
      ret.put(md.getColumnName(i), rs.getObject(i));
    }
    return ret;
  }

  private static void init() throws SQLException
  {
    //Class intern initialization
    for (DBEntity r : dbEntities)
    {
      r.init();
    }

    //Initialization witch need interaction with other classes
    for (DBEntity r : dbEntities)
    {
      r.onStartUp();
    }
  }

  private static void shutDown()
  {
    for (DBEntity r : dbEntities)
    {
      r.onExit();
    }
  }

  public static void reset() throws SQLException
  {
    shutDown();

    init();
  }

  private static PreparedStatement dtdbStmt = null;

  public static void initTestPhase() throws SQLException
  {
    if (dtdbStmt == null)
    {
      String sql = "DROP ALL OBJECTS;";
      dtdbStmt = con.prepareStatement(sql);
    }

    dtdbStmt.execute();
    init();

  }
}
