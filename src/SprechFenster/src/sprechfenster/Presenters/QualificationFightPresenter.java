/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster.Presenters;

import Model.ObjectDeprecatedExeption;
import Model.iFencer;
import Model.iPreliminary;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sprechfenster.LoggingUtilities;

/**
 *
 * @author Stefan
 */
public class QualificationFightPresenter
{
    iPreliminary Fight;
    
    public QualificationFightPresenter(iPreliminary fightToPresent)
    {
        if(fightToPresent == null)
        {
            throw new IllegalArgumentException("fightToPresent must not be null");
        }
        Fight = fightToPresent;
    }
    
    public iPreliminary getFight()
    {
        return Fight;
    }
    
    @Override
    public boolean equals(Object other)
    {
        if(other == this)
        {
            return true;
        }
        else
        {
            if(!(other instanceof QualificationFightPresenter))
            {
                return false;
            }
            else
            {
                return Fight.equals(((QualificationFightPresenter)other).Fight);
            }
        }
    }
    
    @Override 
    public int hashCode()
    {
        return Fight.hashCode();
    }
    
    public String getFirstFencerName()
    {
        iFencer fencer = getFencer(0);
        return getFencerName(fencer);
    }
    
    public String getFirstFencerPoints()
    {
        return getFencerPoints(getFencer(0));
    }
    
    public String getSecondFencerName()
    {
        iFencer fencer = getFencer(1);
        return getFencerName(fencer);
    }
    
    public String getSecondFencerPoints()
    {
        return getFencerPoints(getFencer(1));
    }
    
    public String getLane()
    {
        String lane = "";
        try
        {
            lane = Integer.toString(Fight.getLane());
        }
        catch (ObjectDeprecatedExeption ex)
        {
            LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
        }
        return lane;
    }
    
    public String getRound()
    {
        String round = "";
        try
        {
            round = Integer.toString(Fight.getRound());
        }
        catch (ObjectDeprecatedExeption ex)
        {
            LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
        }
        return round;
    }
    
    public String getGroup()
    {
        String group = "";
        try
        {
            group = Integer.toString(Fight.getGroup());
        }
        catch (ObjectDeprecatedExeption ex)
        {
            LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
        }
        return group;
    }
    
    public String getStatus()
    {
        try
        {
            if(Fight.isFinished())
            {
                return "Beendet";
            }
            else
            {
                return "Offen";
            }
        }
        catch (ObjectDeprecatedExeption ex)
        {
            LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
            return "-";
        }
    }
    
    private String getFencerName(iFencer fencer)
    {
        if(fencer != null)
        {
            return fencer.getFullName();
        }
        else
        {
            return "-";
        }
    }
    
    private String getFencerPoints(iFencer fencer)
    {
        if(fencer != null)
        {
            try
            {
                return Integer.toString(Fight.getPoints(fencer));
            }
            catch (SQLException ex)
            {
                LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
            }
            catch (ObjectDeprecatedExeption ex)
            {
                LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        return "-";
    }
    
    private iFencer getFencer(int index)
    {
        try
        {
            List<iFencer> fencers = Fight.getFencer();
            if(fencers != null && fencers.size() > index)
            {
                iFencer fencer = fencers.get(index);
                return fencer;
            }
            else
            {
                return null;
            }
        }
        catch (ObjectDeprecatedExeption ex)
        {
            LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
