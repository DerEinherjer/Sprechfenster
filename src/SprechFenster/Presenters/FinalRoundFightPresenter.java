/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster.Presenters;

import Model.ObjectDeprecatedException;
import Model.Sync;
import Model.iFencer;
import Model.iFinalround;
import Model.iSync;
import java.sql.SQLException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
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
public final class FinalRoundFightPresenter implements Observer
{

    iFinalround Fight;
    private IntegerProperty Round = new SimpleIntegerProperty();
    private ChangeListener<Number> RoundListener = this::setRound;
    private IntegerProperty Lane = new SimpleIntegerProperty();
    private ChangeListener<Number> LaneListener = this::setLane;
    private IntegerProperty FirstFencerPoints = new SimpleIntegerProperty();
    private ChangeListener<Number> FirstFencerPointsListener = this::setFirstFencerPoints;
    private IntegerProperty SecondFencerPoints = new SimpleIntegerProperty();
    private ChangeListener<Number> SecondFencerPointsListener = this::setSecondFencerPoints;
    private BooleanProperty Finished = new SimpleBooleanProperty();
    private ChangeListener<Boolean> FinishedListener = this::setFinished;

    public FinalRoundFightPresenter(iFinalround fightToPresent)
    {
        if (fightToPresent == null)
        {
            throw new IllegalArgumentException("fightToPresent must not be null");
        }
        Fight = fightToPresent;
        UpdateData();
        iSync.getInstance().addObserver(this);
    }
    
    private void AddListeners()
    {
        Round.addListener(RoundListener);
        Lane.addListener(LaneListener);
        FirstFencerPoints.addListener(FirstFencerPointsListener);
        SecondFencerPoints.addListener(SecondFencerPointsListener);
        Finished.addListener(FinishedListener);
    } 
    private void RemoveListeners()
    {
        Round.removeListener(RoundListener);
        Lane.removeListener(LaneListener);
        FirstFencerPoints.removeListener(FirstFencerPointsListener);
        SecondFencerPoints.removeListener(SecondFencerPointsListener);
        Finished.removeListener(FinishedListener);
    }

    private void UpdateData()
    {
        RemoveListeners();
        Round.setValue(Fight.getRound());
        Lane.setValue(Fight.getLane());
        FirstFencerPoints.setValue(getFirstFencerPoints());
        SecondFencerPoints.setValue(getSecondFencerPoints());
        Finished.setValue(Fight.isFinished());
        AddListeners();
    }

    @Override
    public void update(Observable o, Object o1)
    {
        if (o1 instanceof Sync.change)
        {
            Sync.change changeType = (Sync.change) o1;
            if (changeType == Sync.change.changedFinalround
                    || changeType == Sync.change.finishedFinalround
                    || changeType == Sync.change.unfinishedFinalround)
            {
                UpdateData();
            }
        }
        else
        {
            UpdateData();
        }
    }

    public iFinalround getFight()
    {
        return Fight;
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
        }
        catch (SQLException ex)
        {
            LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
        }
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
        }
        catch (SQLException ex)
        {
            LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public IntegerProperty FirstFencerPointsProperty()
    {
        return FirstFencerPoints;
    }

    private void setFirstFencerPoints(ObservableValue ov, Number oldValue, Number newValue)
    {
        try
        {
            Fight.setPoints(getFencer(0), newValue.intValue());
        }
        catch (SQLException ex)
        {
            LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public IntegerProperty SecondFencerPointsProperty()
    {
        return SecondFencerPoints;
    }

    private void setSecondFencerPoints(ObservableValue ov, Number oldValue, Number newValue)
    {
        try
        {
            Fight.setPoints(getFencer(1), newValue.intValue());
        }
        catch (SQLException ex)
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

    private String getFencerName(iFencer fencer)
    {
        if (fencer != null)
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
        if (fencer != null)
        {

            try
            {
                return Fight.getPoints(fencer);
            }
            catch (SQLException ex)
            {
                LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
            }

        }
        return 0;
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
        }
        catch (SQLException | ObjectDeprecatedException ex)
        {
            LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    private iFencer getFencer(int index)
    {

        List<iFencer> fencers = Fight.getFencer();
        if (fencers != null && fencers.size() > index)
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
