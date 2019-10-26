package model.DBConnection;

import java.sql.SQLException;

/**
 *
 * @author Asgard
 */
public interface DBEntity
{

  /**
   * This trigges the creation of the DBTable(if it does not exist already) and
   * loading existing entrys.
   *
   * @throws SQLException Something went wrong (obvisly)
   */
  void init() throws SQLException;

  /**
   * This triggers the second stage of the initialisation in which things can be
   * done that needs other entrys loaded.
   */
  void onStartUp() throws SQLException;

  void onExit();
}
