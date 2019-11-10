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
import model.Tournament;
import model.TournamentParticipation;
import model.rounds.QualificationMatch;
import model.rounds.TournamentMatch;

/**
 * This class initiat the connection to the database and the database structure. All DB*Representer classes inherit this class to communicate with the data- base.
 *
 * @author Asgard
 */
public abstract class DBBaseClass
{

  private static final String databaseURL = "jdbc:h2:~/SprechfensterData";
  protected static Connection DBConnection;

  private static final List<DBEntity> dbEntities = new ArrayList<>();

  public static void InitDatabase()
  {
    //Add all classes with db representation so that they get automaticaly
    //initiated. They need the DBEntetyRepresenter interface.

    //Order of entities is important since initialization in "init" needs to take interdependencies into account
    dbEntities.add(new Fencer());
    dbEntities.add(new Tournament());
    dbEntities.add(new TournamentMatch());
    dbEntities.add(new QualificationMatch());
    dbEntities.add(new TournamentParticipation());

    try
    {
      Class.forName("org.h2.Driver");

      DBConnection = DriverManager.getConnection(databaseURL, "", "");

      init();
    } //TODO: should we crash if one of these exceptions is thrown? is better logging possible? //TODO: should we crash if one of these exceptions is thrown? is better logging possible?
    catch (ClassNotFoundException | SQLException e)
    {
    }
  }

  /**
   * Helper function that converts a result object in an hash which has the colum names as keys and the values as values
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
      dtdbStmt = DBConnection.prepareStatement(sql);
    }

    dtdbStmt.execute();
    init();

  }
}
