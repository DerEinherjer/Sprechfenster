package model.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Fencer;
import model.ObjectExistException;
import model.Tournament;
import model.rounds.Preliminary;

public class DBPreliminaryRepresenter extends DBSuperClass
{
    
    private static PreparedStatement lpStmt = null;
    public static void loadPreliminary () throws SQLException
    {
        if (lpStmt == null)
        {
            String sql = "SELECT * FROM Vorrunden WHERE FinalStrucktur = -1;";//-1 means is not a finalround
            lpStmt = con.prepareStatement(sql);
        }

        ResultSet rs = lpStmt.executeQuery();
        
        while(rs.next())
        {
            try
            {
                new Preliminary(rowToHash(rs));
            }
            catch (ObjectExistException e){}
        }

        rs.close();
    }

    private static PreparedStatement cpStmt = null;
    public static int createPreliminary(Tournament t, Fencer f1, Fencer f2) throws SQLException
    {
        if(cpStmt == null)
        {
            String sql = "INSERT INTO Vorrunden (TurnierID, Gruppe, Teilnehmer1, Teilnehmer2) VALUES (?, ?, ?, ?);";
            cpStmt = con.prepareStatement(sql);
        }
      
        cpStmt.setInt(1, t.getID());
        cpStmt.setInt(2, t.getParticipantGroup(f1));
        cpStmt.setInt(3, f1.getID());
        cpStmt.setInt(4, f2.getID());

        cpStmt.executeUpdate();
        ResultSet rs = cpStmt.getGeneratedKeys();

        if (rs.next())
            return rs.getInt(1);
        else 
          throw new SQLException("Didn't return ID of the new preliminary.");
    }
}
