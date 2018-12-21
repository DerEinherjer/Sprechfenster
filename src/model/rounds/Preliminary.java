package model.rounds;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.DBConnection.DBEntetyRepresenter;
import model.DBConnection.DBPreliminaryRepresenter;
import model.Fencer;
import model.ObjectDeprecatedException;
import model.ObjectExistException;
import model.Sync;
import model.Tournament;

public class Preliminary extends Round implements DBEntetyRepresenter, iPreliminary
{

    @Override
    public void init() throws SQLException
    {
        //The SQL-Table is Created in Round because it is also used in Finalround
        DBPreliminaryRepresenter.loadPreliminary();
    }

    @Override
    public void onStartUp() throws SQLException
    {
        for (Map.Entry<Integer, Preliminary> entry : preliminarys.entrySet())
        {
            entry.getValue().initPhase2();
        }
    }

    @Override
    public void onExit()
    {
        Map<Integer, Preliminary> tmp = preliminarys;
        preliminarys = new HashMap<>();
        for (Map.Entry<Integer, Preliminary> entry : tmp.entrySet())
        {
            entry.getValue().invalidate();
        }
    }
    
    //#########################################################################
  
    private static Map<Integer, Preliminary> preliminarys = new HashMap<>();
    
    public static List<iPreliminary> getPreliminaryOfTournament(Tournament t)
    {
        List<iPreliminary> ret = new ArrayList<>();
        for (Map.Entry<Integer, Preliminary> entry : preliminarys.entrySet())
        {
            if(entry.getValue().getTournament().equals(t))
                ret.add(entry.getValue());
        }
        return ret;
    }
    
    private void invalidate()
    {
        ID = -1;
        isValid = false;
    }
    
    public static void deletePreliminaryOfTournament(Tournament t)
    {
        List<Preliminary> tmp = new ArrayList<>();
        for (Map.Entry<Integer, Preliminary> entry : preliminarys.entrySet())
        {
            if(entry.getValue().getTournament().equals(t))
                tmp.add(entry.getValue());
        }
        
        for(Preliminary prelim : tmp)
        {
            try
            {
                prelim.delete();
            } 
            catch (SQLException ex)
            {
                Logger.getLogger(Preliminary.class.getName()).log(Level.SEVERE, null, ex);
            } 
            catch (ObjectDeprecatedException ex) {}//can be ignored savely
        }
    }
    
    //#########################################################################
    
    public Preliminary(Map<String, Object> set) throws ObjectExistException, SQLException
    {
        super(set);
        
        preliminarys.put(ID, this);
    }
    
    public Preliminary(Tournament t, Fencer f1, Fencer f2) throws SQLException
    {
        if(t.getParticipantGroup(f1) != t.getParticipantGroup(f2)) throw new IllegalArgumentException();
        
        this.ID = DBPreliminaryRepresenter.createPreliminary(t, f1, f2);
        
        preliminarys.put(ID, this);
        
        this.t = t;
        
        this.fencer1 = f1;
        this.fencer2 = f2;
        
        this.group = -1;
        this.round = -1;
        this.lane = -1;
        this.pointsFor1 = 0;
        this.pointsFor2 = 0;
        
        this.finished = false;
        
        this.yellowFor1 = 0;
        this.redFor1 = 0;
        this.blackFor1 = 0;
        
        
        this.yellowFor2 = 0;
        this.redFor2 = 0;
        this.blackFor2 = 0;
        
        t.addPreliminaryRoundToScore(fencer1, this);
        t.addPreliminaryRoundToScore(fencer2, this);
    }
            
    /**
     * DON'T USE THIS!
     * IT IS FOR THE USE OF THE INTERFACE ONLY AND WILL CRASH THE PROGRAMM IF 
     * USED OTHERWISE.
     */
    public Preliminary(){};
    
    @Override
    public void delete() throws SQLException, ObjectDeprecatedException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        
        isValid = false;
        preliminarys.remove(this);
    }
    
    protected void initPhase2()
    {
        t.addPreliminaryRoundToScore(fencer1, this);
        t.addPreliminaryRoundToScore(fencer2, this);
    }
    
    public int getPreliminaryGroup() throws SQLException
    {
        return t.getParticipantGroup(fencer1);
    }
}
