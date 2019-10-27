/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import model.Fencer;
import model.ObjectExistException;

/**
 *
 * @author Asgard
 */
public abstract class DBFencer extends DBBaseClass
{

  private static String getSQLString()
  {
    return "CREATE TABLE IF NOT EXISTS Fechter (ID int NOT NULL AUTO_INCREMENT UNIQUE,"
            + "Vorname varchar(255) DEFAULT 'Max',"
            + "Nachname varchar(255) DEFAULT 'Musterman',"
            + "Geburtstag varchar(255) DEFAULT '1970-01-01',"
            + "Fechtschule varchar(255) DEFAULT 'Keine Fechtschule',"
            + "Nationalitaet varchar(255) DEFAULT 'Nicht Angegeben');";
  }

  public static void createTable() throws SQLException
  {
    con.prepareStatement(getSQLString()).executeUpdate();
  }

  private static PreparedStatement cfStmt = null;

  public static int createFencer(String firstname, String familyname) throws SQLException
  {
    if (firstname == null || familyname == null)
    {
      return -1;
    }

    if (cfStmt == null || cfStmt.isClosed())
    {
      String sql = "INSERT INTO Fechter (Vorname, Nachname) VALUES (?, ?);";
      cfStmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    }

    cfStmt.setString(1, firstname);
    cfStmt.setString(2, familyname);
    cfStmt.executeUpdate();
    ResultSet rs = cfStmt.getGeneratedKeys();

    if (rs.next())
    {
      return rs.getInt(1);
    } else
    {
      throw new SQLException("Didn't return ID of the new fencer.");
    }
  }

  private static PreparedStatement lfStmt = null;

  public static void loadFencer() throws SQLException
  {
    if (lfStmt == null || lfStmt.isClosed())
    {
      String sql = "SELECT * FROM Fechter;";
      lfStmt = con.prepareStatement(sql);
    }

    ResultSet rs = lfStmt.executeQuery();
    while (rs.next())
    {
      try
      {
        new Fencer(rowToHash(rs));
      } catch (ObjectExistException ex)
      {
      }//Can be ignored savely
    }

    rs.close();
  }

  private static PreparedStatement fsnStmt = null;

  public static void fencerSetName(String name, int id) throws SQLException
  {
    if (fsnStmt == null || fsnStmt.isClosed())
    {
      String sql = "UPDATE Fechter SET Vorname = ? WHERE ID = ?;";
      fsnStmt = con.prepareStatement(sql);
    }
    fsnStmt.setString(1, name);
    fsnStmt.setInt(2, id);
    fsnStmt.executeUpdate();
  }

  private static PreparedStatement fsfnStmt = null;

  public static void fencerSetFamilyName(String name, int id) throws SQLException
  {
    if (fsfnStmt == null || fsfnStmt.isClosed())
    {
      String sql = "UPDATE Fechter SET Nachname = ? WHERE ID = ?;";
      fsfnStmt = con.prepareStatement(sql);
    }
    fsfnStmt.setString(1, name);
    fsfnStmt.setInt(2, id);
    fsfnStmt.executeUpdate();
  }

  private static PreparedStatement fsbStmt = null;

  public static void fencerSetBirthday(String date, int id) throws SQLException
  {
    if (fsbStmt == null || fsbStmt.isClosed())
    {
      String sql = "UPDATE Fechter SET Geburtstag = ? WHERE ID = ?;";
      fsbStmt = con.prepareStatement(sql);
    }
    fsbStmt.setString(1, date);
    fsbStmt.setInt(2, id);
    fsbStmt.executeUpdate();
  }

  private static PreparedStatement fsfsStmt = null;

  public static void fencerSetFencingSchool(String school, int id) throws SQLException
  {
    if (fsfsStmt == null || fsfsStmt.isClosed())
    {
      String sql = "UPDATE Fechter SET Fechtschule = ? WHERE ID = ?;";
      fsfsStmt = con.prepareStatement(sql);
    }
    fsfsStmt.setString(1, school);
    fsfsStmt.setInt(2, id);
    fsfsStmt.executeUpdate();
  }

  private static PreparedStatement fsnatStmt = null;

  public static void fencerSetNationality(String nation, int id) throws SQLException
  {
    if (fsnatStmt == null || fsnatStmt.isClosed())
    {
      String sql = "UPDATE Fechter SET Nationalitaet = ? WHERE ID = ?;";
      fsnatStmt = con.prepareStatement(sql);
    }
    fsnatStmt.setString(1, nation);
    fsnatStmt.setInt(2, id);
    fsnatStmt.executeUpdate();
  }

  private static PreparedStatement rfStmt = null;

  public static void removeFencer(int id) throws SQLException
  {
    if (rfStmt == null || rfStmt.isClosed())
    {
      String sql = "DELETE FROM Fechter WHERE ID = ?;";
      rfStmt = con.prepareStatement(sql);
    }

    rfStmt.setInt(1, id);
    rfStmt.executeUpdate();
  }
}
