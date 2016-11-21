/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster.Presenters;

import Model.ObjectDeprecatedException;
import Model.iFencer;
import Model.iPreliminary;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import sprechfenster.LoggingUtilities;

/**
 *
 * @author Stefan
 */
public final class QualificationFightPresenter
{
    iPreliminary Fight;
    private IntegerProperty Round = new SimpleIntegerProperty();
    private IntegerProperty Group = new SimpleIntegerProperty();
    private IntegerProperty Lane = new SimpleIntegerProperty();
    private IntegerProperty FirstFencerPoints = new SimpleIntegerProperty();
    private IntegerProperty SecondFencerPoints = new SimpleIntegerProperty();
    private BooleanProperty Finished = new SimpleBooleanProperty();
    
    public QualificationFightPresenter(iPreliminary fightToPresent)
    {
        try
        {
            if(fightToPresent == null)
            {
                throw new IllegalArgumentException("fightToPresent must not be null");
            }
            Fight = fightToPresent;
            Round.setValue(Fight.getRound());
            Round.addListener((ChangeListener<Number>)this::setRound);
            Group.setValue(Fight.getGroup());
            Lane.setValue(Fight.getLane());
            Lane.addListener((ChangeListener<Number>)this::setLane);
            FirstFencerPoints.setValue(getFirstFencerPoints());
            FirstFencerPoints.addListener((ChangeListener<Number>)this::setFirstFencerPoints);
            SecondFencerPoints.setValue(getSecondFencerPoints());
            SecondFencerPoints.addListener((ChangeListener<Number>)this::setSecondFencerPoints);
            Finished.setValue(Fight.isFinished());
            Finished.addListener((ChangeListener<Boolean>)this::setFinished);
        }
        catch (ObjectDeprecatedException ex)
        {
            LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
        }
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
    
    public IntegerProperty RoundProperty()
    {
        return Round;
    }
    
    private void setRound(ObservableValue ov, Number oldValue, Number newValue)
    {
        try
        {
            Fight.setTime(newValue.intValue(), Fight.getLane());
            Round.setValue(newValue);
        }
        catch (SQLException | ObjectDeprecatedException ex)
        {
            LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public IntegerProperty GroupProperty()
    {
        return Group;
    }
    
    public IntegerProperty LaneProperty()
    {
        return Lane;
    }
    
    private void setLane(ObservableValue ov, Number oldValue, Number newValue)
    {
        try
        {
            Fight.setTime(Fight.getRound(), newValue.intValue());
            Lane.setValue(newValue);
        }
        catch (SQLException | ObjectDeprecatedException ex)
        {
            LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public IntegerProperty FirstFencerPoints()
    {
        return FirstFencerPoints;
    }
    
    private void setFirstFencerPoints(ObservableValue ov, Number oldValue, Number newValue)
    {
        try
        {
            Fight.setPoints(getFencer(0), newValue.intValue());
            FirstFencerPoints.setValue(getFirstFencerPoints());
        }
        catch (SQLException | ObjectDeprecatedException ex)
        {
            LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public IntegerProperty SecondFencerPoints()
    {
        return SecondFencerPoints;
    }
    
    private void setSecondFencerPoints(ObservableValue ov, Number oldValue, Number newValue)
    {
        try
        {
            Fight.setPoints(getFencer(1), newValue.intValue());
            FirstFencerPoints.setValue(getSecondFencerPoints());
        }
        catch (SQLException | ObjectDeprecatedException ex)
        {
            LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public BooleanProperty FinishedProperty()
    {
        return Finished;
    }

    private void setFinished(ObservableValue ov, Boolean oldValue, Boolean newValue)
    {
        try
        {
            Fight.setFinished(newValue);
            Finished.setValue(newValue);
        }
        catch (SQLException | ObjectDeprecatedException ex)
        {
            LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    public String getFirstFencerName()
    {
        iFencer fencer = getFencer(0);
        return getFencerName(fencer);
    }
    
    public int getFirstFencerPoints()
    {
        return getFencerPoints(getFencer(0));
    }
    
    public String getSecondFencerName()
    {
        iFencer fencer = getFencer(1);
        return getFencerName(fencer);
    }
    
    public int getSecondFencerPoints()
    {
        return getFencerPoints(getFencer(1));
    }
    
    public Boolean getFinished()
    {
        try
        {
            if(Fight.isFinished())
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (ObjectDeprecatedException ex)
        {
            LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public void setFinished(Boolean isFightFinished)
    {
        try
        {
            Fight.setFinished(isFightFinished);
        }
        catch (SQLException | ObjectDeprecatedException ex)
        {
           LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
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
    
    private int getFencerPoints(iFencer fencer)
    {
        if(fencer != null)
        {
            try
            {
                return Fight.getPoints(fencer);
            }
            catch (SQLException ex)
            {
                LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
            }
            catch (ObjectDeprecatedException ex)
            {
                LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        return 0;
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
        catch (ObjectDeprecatedException ex)
        {
            LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }
}