/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster.Presenters;

import Model.iFencer;
import Model.iScore;
import Model.iTournament;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Stefan
 */
public class FencerPresenter {
    
    private iFencer Fencer;
    private iTournament Tournament;
    
    public FencerPresenter(iFencer fencerToPresent)
    {
        if(fencerToPresent == null)
        {
            throw new IllegalArgumentException("fencerToPresent must not be null");
        }
        Fencer = fencerToPresent;
    }
    
    public FencerPresenter(iFencer fencerToPresent, iTournament tournament)
    {
        this(fencerToPresent);
        Tournament = tournament;
    }
    
    
    public void AssignTournament(iTournament tournament)
    {
        Tournament = tournament;
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
            if(!(other instanceof FencerPresenter))
            {
                return false;
            }
            else
            {
                return Fencer.equals(((FencerPresenter)other).Fencer);
            }
        }
    }
    
    @Override 
    public int hashCode()
    {
        return Fencer.hashCode();
    }
    
    public iFencer getFencer()
    {
        return Fencer;
    }
    
    public String getFullName()
    {
        return Fencer.getFullName();
    }
    
    public String getFencingSchool()
    {
        return Fencer.getFencingSchool();
    }
    
    public String getAge()
    {
        LocalDate birthday = LocalDate.parse(Fencer.getBirthday(), DateTimeFormatter.ISO_DATE);
        LocalDate now = LocalDate.now();
        Period age = Period.between(birthday, now);
        return Integer.toString(age.getYears());
    }
    
    public String getQualificationRoundPoints()
    {
        if(Tournament != null)
        {
            iScore score = Tournament.getScoreFromPrelim(Fencer);
            if(score != null)
            {
                return String.format("%d/%d", score.getHits(), score.getGotHit());
            }
        }
        return "-/-";
    }
    
    public String getQualificationRoundWins()
    {
        if(Tournament != null)
        {
            iScore score = Tournament.getScoreFromPrelim(Fencer);
            if(score != null)
            {
                return Integer.toString(score.getWins());
            }
        }
        return "-";
    }
    
    public String getFinalRoundScore()
    {
        if(Tournament != null)
        {
            iScore score = Tournament.getScoreFromFinal(Fencer);
            if(score != null)
            {
                return String.format("%d/%d", score.getHits(), score.getGotHit());
            }
        }
        return "-";
    }
    
    public String getFinalRoundWins()
    {
        if(Tournament != null)
        {
            iScore score = Tournament.getScoreFromFinal(Fencer);
            if(score != null)
            {
                return Integer.toString(score.getWins());
            }
        }
        return "-";
    }
}
