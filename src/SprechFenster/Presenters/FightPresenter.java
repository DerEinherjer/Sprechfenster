package sprechfenster.Presenters;

import Model.ObjectDeprecatedException;
import Model.Sync;
import Model.iFencer;
import Model.Rounds.iRound;
import Model.iSync;
import java.sql.SQLException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
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
public final class FightPresenter implements Observer
{

    private iRound Fight;
    private final IntegerProperty Round = new SimpleIntegerProperty();
    private final ChangeListener<Number> RoundListener = this::setRound;
    private final IntegerProperty Group = new SimpleIntegerProperty();
    private final IntegerProperty Lane = new SimpleIntegerProperty();
    private final ChangeListener<Number> LaneListener = this::setLane;
    private final IntegerProperty FirstFencerPoints = new SimpleIntegerProperty();
    private final ChangeListener<Number> FirstFencerPointsListener = this::setFirstFencerPoints;
    private final IntegerProperty SecondFencerPoints = new SimpleIntegerProperty();
    private final ChangeListener<Number> SecondFencerPointsListener = this::setSecondFencerPoints;
    private final BooleanProperty Finished = new SimpleBooleanProperty();
    private final ChangeListener<Boolean> FinishedListener = this::setFinished;
    
    public FightPresenter(iRound fightToPresent)
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
        try
        {
            RemoveListeners();
            Round.setValue(Fight.getRound());
            Group.setValue(Fight.getGroup());
            Lane.setValue(Fight.getLane());
            FirstFencerPoints.setValue(getFirstFencerPoints());
            SecondFencerPoints.setValue(getSecondFencerPoints());
            Finished.setValue(Fight.isFinished());
            AddListeners();
        }
        catch (ObjectDeprecatedException ex)
        {
            LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void update(Observable o, Object o1)
    {
        if (o1 instanceof Sync.change)
        {
            Sync.change changeType = (Sync.change) o1;
            if (changeType == Sync.change.changedPreliminary
                    || changeType == Sync.change.finishedPreliminary
                    || changeType == Sync.change.unfinishedPreliminary
                    || changeType == Sync.change.changedFencerValue
                    || changeType == Sync.change.changedCards)
            {
                UpdateData();
            }
        }
    }

    public iRound getFight()
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
        }
        catch (SQLException | ObjectDeprecatedException ex)
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
        catch (SQLException | ObjectDeprecatedException ex)
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

    private int getFirstFencerPoints()
    {
        return getFencerPoints(getFencer(0));
    }

    public String getSecondFencerName()
    {
        iFencer fencer = getFencer(1);
        return getFencerName(fencer);
    }

    private int getSecondFencerPoints()
    {
        return getFencerPoints(getFencer(1));
    }

    private Boolean getFinished()
    {
        try
        {
            if (Fight.isFinished())
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

    private void setFinished(Boolean isFightFinished)
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
        catch (ObjectDeprecatedException ex)
        {
            LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }
}

