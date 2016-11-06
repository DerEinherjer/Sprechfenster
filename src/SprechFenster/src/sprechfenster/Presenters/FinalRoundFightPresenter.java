/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster.Presenters;

import Model.iFencer;
import Model.iFinalround;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import sprechfenster.LoggingUtilities;

/**
 *
 * @author Stefan
 */
public class FinalRoundFightPresenter
{

    iFinalround Fight;

    public FinalRoundFightPresenter(iFinalround fightToPresent)
    {
        if (fightToPresent == null)
        {
            throw new IllegalArgumentException("fightToPresent must not be null");
        }
        Fight = fightToPresent;
    }

    public iFinalround getFight()
    {
        return Fight;
    }

    @Override
    public boolean equals(Object other)
    {
        if (other == this)
        {
            return true;
        }
        else
        {
            if (!(other instanceof FinalRoundFightPresenter))
            {
                return false;
            }
            else
            {
                return Fight.equals(((FinalRoundFightPresenter) other).Fight);
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
        lane = Integer.toString(Fight.getLane());
        return lane;
    }

    public String getRound()
    {
        String round = "";
        round = Integer.toString(Fight.getRound());
        return round;
    }

    public String getStatus()
    {

        if (Fight.isFinished())
        {
            return "Beendet";
        }
        else
        {
            return "Offen";
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

    private String getFencerPoints(iFencer fencer)
    {
        if (fencer != null)
        {

            try
            {
                return Integer.toString(Fight.getPoints(fencer));
            }
            catch (SQLException ex)
            {
                LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
            }

        }
        return "-";
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
