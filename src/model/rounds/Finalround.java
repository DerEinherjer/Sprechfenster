/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.rounds;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.DBConnection.DBEntetyRepresenter;
import model.DBConnection.DBFinalroundRepresenter;
import model.DBConnection.DBPreliminaryRepresenter;
import model.ObjectDeprecatedException;
import model.ObjectExistException;

/**
 *
 * @author Asgard
 */
public class Finalround extends Round implements DBEntetyRepresenter, iFinalround
{
    @Override
    public void init() throws SQLException
    {
        //The SQL-Table is Created in Round because it is also used in Finalround
        DBFinalroundRepresenter.createTable();
        DBFinalroundRepresenter.loadFnialrounds();
    }

    @Override
    public void onStartUp() throws SQLException
    {
        for (Map.Entry<Integer, Finalround> entry : finalrounds.entrySet())
        {
            entry.getValue().initPhase2();
        }
    }
    
    @Override
    public void onExit()
    {
        Map<Integer, Finalround> tmp = finalrounds;
        finalrounds = new HashMap<>();
        for (Map.Entry<Integer, Finalround> entry : tmp.entrySet())
        {
            entry.getValue().invalidate();
        }
    }
    
    //#########################################################################
    private static Map<Integer, Finalround> finalrounds = new HashMap<>();
    
    public static Finalround getFinalround(int id)
    {
        return finalrounds.get(id);
    }

    //#########################################################################
    
    private Integer finalRound = null;
    
    //Thos variables carry the roundIDs from Init Phase 1 to Phase 2 in which
    //the ID can be dereferenced
    private Integer winnerRoundID = null;
    private Integer loserRoundID = null;
    
    private Finalround winnerRound = null;
    private Finalround loserRound = null;
    
    public Finalround(Map<String, Object> set) throws ObjectExistException, SQLException
    {
        super(set);
        
        this.finalRound = (Integer) set.get("FinalRunde".toUpperCase());
        
        this.winnerRoundID = (Integer) set.get("GewinnerRunde".toUpperCase());
        this.loserRoundID = (Integer) set.get("VerliererRunde".toUpperCase());
        
        finalrounds.put(ID, this);
    }
    
    public Finalround(int finalRound)
    {
        this.ID = DBFinalroundRepresenter.createFinalround(t, finalRound);
        
        finalrounds.put(ID, this);
        
        this.t = t;
        
        this.fencer1 = null;
        this.fencer2 = null;
        
        this.finalRound = finalRound;
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
    }
    
    protected void initPhase2()
    {
        this.winnerRound = getFinalround(winnerRoundID);
        this.loserRound = getFinalround(loserRoundID);
    }
    
    public void addWinningRound(Finalround winningRound)
    {
        if(winnerRound==null)
        {
            winnerRound = winningRound;
        }
    }
    
    public void addLoosingRound(Finalround loosingRound)
    {
        if(winnerRound==null)
        {
            loserRound = loosingRound;
        }
    }
    
    @Override
    public iFinalround getWinnerRound() throws ObjectDeprecatedException
    {
        return winnerRound;
    }

    @Override
    public iFinalround getLoserRound() throws ObjectDeprecatedException
    {
        return loserRound;
    }

    @Override
    public List<iFinalround> getPrerounds() throws ObjectDeprecatedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getFinalRound()
    {
        return finalRound;
    }

    private void invalidate()
    {
        this.ID = -1;
    }
}
