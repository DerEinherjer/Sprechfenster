/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster.Presenters;

import Model.iFencer;
import Model.iPreliminary;
import java.util.List;

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
    
    public String GetFirstFencerName()
    {
        iFencer fencer = GetFencer(0);
        return GetFencerName(fencer);
    }
    
    public String GetSecondFencerName()
    {
        iFencer fencer = GetFencer(1);
        return GetFencerName(fencer);
    }
    
    public String GetLane()
    {
        return Integer.toString(Fight.getLane());
    }
    
    public String GetRound()
    {
        return Integer.toString(Fight.getRound());
    }
    
    public String GetGroup()
    {
        return Integer.toString(Fight.getGroup());
    }
    
    private String GetFencerName(iFencer fencer)
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
    
    private iFencer GetFencer(int index)
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
}
