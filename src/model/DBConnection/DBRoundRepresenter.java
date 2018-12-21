package model.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static model.DBConnection.DBSuperClass.con;
import model.Fencer;
import model.ObjectDeprecatedException;
import model.rounds.Round;

public class DBRoundRepresenter extends DBSuperClass
{
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
            + "FinalStrucktur int DEFAULT -1);";
    }

    public static void createTable() throws SQLException
    {
        con.prepareStatement(getSQLString()).executeUpdate();
    }
    
    private PreparedStatement stfp1Stmt = null;
    private PreparedStatement stfp2Stmt = null;

    private static PreparedStatement stStmt1 = null;
    private static PreparedStatement stStmt2 = null;
    private static PreparedStatement stStmt3 = null;
    public static boolean setTime (Round p, int round, int lane) throws SQLException
    {
        if(stStmt1 == null)
        {
            String sql = "SELECT * FROM Vorrunden WHERE Runde = ? AND Bahn = ?;";
            stStmt1 = con.prepareStatement(sql);
            
            sql = "SELECT Bahnen FROM Turniere WHERE ID = ?;";
            stStmt2 = con.prepareStatement(sql);
            
            sql = "UPDATE Vorrunden SET Runde = ? AND Bahn = ? WHERE ID = ?;";
            stStmt3 = con.prepareStatement(sql);
        }
         
        stStmt1.setInt(1, round);
        stStmt1.setInt(2, lane);
        
        ResultSet rs = stStmt1.executeQuery();
        
        if(rs.next())
        {
            rs.close();
            return false;
        }
        rs.close();
        
        stStmt2.setInt(1, p.getTournament().getID());
        
        rs = stStmt2.executeQuery();
        
        if(!rs.next())
        {
            rs.close();
            return false;//somthing is fucked up
        }
        
        if(lane>=rs.getInt("Bahnen"))
        {
            rs.close();
            return false;//Slot already taken
        }
        rs.close();
        
        stStmt3.setInt(1, p.getID());
        stStmt3.setInt(2, round);
        stStmt3.setInt(3, lane);
        
        stStmt3.executeUpdate();
        
        return true;
    }
    
    private static PreparedStatement spStmt = null;
    public static void setPoints(int prelimID, int fencerID, int points) throws SQLException
    {
        if (spStmt == null)
        {
            String sql = "UPDATE Vorrunden SET PunkteVon1 = CASE WHEN Teilnehmer1 = ? THEN ? ELSE PunkteVon1 END, "
                    + "PunkteVon2 = CASE WHEN Teilnehmer2 = ? THEN ? ELSE PunkteVon2 END WHERE ID = ?;";
            spStmt = con.prepareStatement(sql);
        }

        spStmt.setInt(1, fencerID);
        spStmt.setInt(2, points);
        spStmt.setInt(3, fencerID);
        spStmt.setInt(4, points);
        spStmt.setInt(5, prelimID);
        spStmt.executeUpdate();
    }
    
    private static PreparedStatement rpfpStmt = null;
    public static void removeParticipant(Round p, Fencer f) throws SQLException
    {
        if (rpfpStmt == null)
        {
            String sql = "UPDATE Vorrunden SET Teilnehmer1 = CASE WHEN Teilnehmer1 = ? THEN -1 ELSE Teilnehmer1 END, "
                       + "Teilnehmer2 = CASE WHEN Teilnehmer2 = ? THEN -1 ELSE Teilnehmer2 END WHERE ID = ?;";
            rpfpStmt = con.prepareStatement(sql);
        }
        try
        {
            rpfpStmt.setInt(1, f.getID());
            rpfpStmt.setInt(2, f.getID());
            rpfpStmt.setInt(3, p.getID());
            rpfpStmt.executeUpdate();
        }
        catch (ObjectDeprecatedException e) {}//Can be ignored
    }
    
    private static PreparedStatement aptpStmt = null;

    public static void addParticipant(Round p, Fencer f) throws SQLException
    {
        if (aptpStmt == null)
        {
            String sql = "UPDATE Vorrunden SET Teilnehmer1 = CASE WHEN Teilnehmer1 = -1 THEN ? ELSE Teilnehmer1 END, "
                       + "Teilnehmer2 = CASE WHEN Teilnehmer2 = -1 AND Teilnehmer1 != -1 THEN ? ELSE Teilnehmer2 END WHERE ID = ?;";
            aptpStmt = con.prepareStatement(sql);
        }
        try
        {
            aptpStmt.setInt(1, f.getID());
            aptpStmt.setInt(2, f.getID());
            aptpStmt.setInt(3, p.getID());
            aptpStmt.executeUpdate();
            }
        catch (ObjectDeprecatedException e) {}
    }
    
    private static PreparedStatement spipStmt = null;
    public static void switchParticipants(Round p, Fencer out, Fencer in) throws SQLException
    {
        if (spipStmt == null)
        {
            String sql = "UPDATE Vorrunden SET Teilnehmer1 = CASE WHEN Teilnehmer1 = ? AND Teilnehmer2 != ? THEN ? ELSE Teilnehmer1 END, "
                       + "Teilnehmer2 = CASE WHEN Teilnehmer2 = ? AND Teilnehmer1 != ? THEN ? ELSE Teilnehmer2 END WHERE ID = ?;";
            spipStmt = con.prepareStatement(sql);
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
        }
        catch (ObjectDeprecatedException e) {}
    }
    
    private static PreparedStatement sypStmt = null;
    public static void setYellow(Round p, Fencer f, int count) throws SQLException
    {
        if (sypStmt == null)
        {
            String sql = "UPDATE Vorrunden SET GelbVon1 = CASE WHEN Teilnehmer1 = ? THEN ? ELSE GelbVon1 END,"
                       + "GelbVon2 = CASE WHEN Teilnehmer2 = ? THEN ? ELSE GelbVon2 END "
                       + "WHERE ID = ?";
            sypStmt = con.prepareStatement(sql);
        }
        
        try
        {
            sypStmt.setInt(1, f.getID());
            sypStmt.setInt(2, count);
            sypStmt.setInt(3, f.getID());
            sypStmt.setInt(4, count);
            sypStmt.setInt(5, p.getID());

            sypStmt.executeUpdate();
        }
        catch (ObjectDeprecatedException e) {}
    }
    
    private static PreparedStatement srpStmt = null;

    public static void setRed(Round p, Fencer f, int count) throws SQLException
    {
        if (srpStmt == null)
        {
            String sql = "UPDATE Vorrunden SET RotVon1 = CASE WHEN Teilnehmer1 = ? THEN ? ELSE RotVon1 END,"
                       + "RotVon2 = CASE WHEN Teilnehmer2 = ? THEN ? ELSE RotVon2 END "
                       + "WHERE ID = ?";
            srpStmt = con.prepareStatement(sql);
        }
        
        try
        {
            srpStmt.setInt(1, f.getID());
            srpStmt.setInt(2, count);
            srpStmt.setInt(3, f.getID());
            srpStmt.setInt(4, count);
            srpStmt.setInt(5, p.getID());

            srpStmt.executeUpdate();
        }
        catch (ObjectDeprecatedException e){}
    }

    private static PreparedStatement sbpStmt = null;

    public static void setBlack(Round p, Fencer f, int count) throws SQLException
    {
        if (sbpStmt == null)
        {
            String sql = "UPDATE Vorrunden SET SchwarzVon1 = CASE WHEN Teilnehmer1 = ? THEN ? ELSE SchwarzVon1 END,"
                       + "SchwarzVon2 = CASE WHEN Teilnehmer2 = ? THEN ? ELSE SchwarzVon2 END "
                       + "WHERE ID = ?";
            sbpStmt = con.prepareStatement(sql);
        }
        
        try
        {
            sbpStmt.setInt(1, f.getID());
            sbpStmt.setInt(2, count);
            sbpStmt.setInt(3, f.getID());
            sbpStmt.setInt(4, count);
            sbpStmt.setInt(5, p.getID());

            sbpStmt.executeUpdate();
        }
        catch (ObjectDeprecatedException e) {}
    }
    
    private static PreparedStatement sfStmt = null;
    public static void setFinished(Round r, boolean finish) throws SQLException
    {
        if(sfStmt == null)
        {
            String sql = "UPDATE Vorrunden SET Beendet = ? WHERE ID = ?;";
            sfStmt = con.prepareStatement(sql);
        }
        
        try
        { 
            sfStmt.setBoolean(1, finish);
            sfStmt.setInt(2, r.getID());

            sfStmt.executeUpdate();
        }
        catch (ObjectDeprecatedException e) {}
    }
}
