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
import static model.DBConnection.DBSuperClass.con;
import model.ObjectExistException;
import model.Tournament;
import model.rounds.Finalround;
import model.rounds.Preliminary;

/**
 *
 * @author Asgard
 */
public class DBFinalroundRepresenter extends DBSuperClass
{
    private static String getSQLString()
    {
        return "CREATE TABLE IF NOT EXISTS Finalrunden (ID int NOT NULL AUTO_INCREMENT UNIQUE,"
            + "GewinnerRunde int DEFAULT -1,"
            + "VerliererRunde int DEFAULT -1,"
            + "FinalRunde int DEFAULT -1);";
    }
    
    public static void createTable() throws SQLException
    {
        con.prepareStatement(getSQLString()).executeUpdate();
    }
    
    private static PreparedStatement lfStmt1 = null;
    private static PreparedStatement lfStmt2 = null;
    public static void loadFnialrounds() throws SQLException
    {
        if (lfStmt1 == null)
        {
            String sql = "SELECT * FROM Vorrunden WHERE FinalStrucktur != -1;";
            lfStmt1 = con.prepareStatement(sql);

            sql = "SELECT * FROM Finalrunden WHERE ID = ?;";
            lfStmt2 = con.prepareStatement(sql);
        }

        ResultSet rs = lfStmt1.executeQuery();
        
        while(rs.next())
        {
                Map<String, Object> tmp = rowToHash(rs);
                
                lfStmt2.setInt(1, rs.getInt("FinalStrucktur"));
                
                ResultSet rs2 = lfStmt2.executeQuery();
                
                tmp.putAll(rowToHash(rs2));
                try
                {
                    new Finalround(tmp);
                } 
                catch (ObjectExistException ex)
                {
                    Logger.getLogger(DBFinalroundRepresenter.class.getName()).log(Level.SEVERE, null, ex);
                }
           
        }

        rs.close();
    }

    public static int createFinalround(Tournament t, int finalRound)
    {
        return -1;
    }
}
