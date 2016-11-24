/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster.Presenters;

import Model.Sync;
import Model.iFencer;
import Model.iScore;
import Model.iSync;
import Model.iTournament;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Observable;
import java.util.Observer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Stefan
 */
public class FencerPresenter implements Observer {
    
    private iFencer Fencer;
    private iTournament Tournament;
    private SimpleStringProperty QualificationRoundPoints = new SimpleStringProperty();
    private SimpleStringProperty FinalRoundScore = new SimpleStringProperty();
    private SimpleStringProperty QualificationRoundWins = new SimpleStringProperty();
    private SimpleStringProperty FinalRoundWins = new SimpleStringProperty();
    private SimpleStringProperty FullName = new SimpleStringProperty();
    private SimpleStringProperty FencingSchool = new SimpleStringProperty();
    private SimpleStringProperty Age = new SimpleStringProperty();
    
    public FencerPresenter(iFencer fencerToPresent, iTournament tournament)
    {
        if(fencerToPresent == null)
        {
            throw new IllegalArgumentException("fencerToPresent must not be null");
        }
        Fencer = fencerToPresent;
        Tournament = tournament;
        UpdateData();
        RegisterObserver();
    }
    
    private void RegisterObserver()
    {
        iSync.getInstance().addObserver(this);
    }
    
    private void UpdateData()
    {
        QualificationRoundPoints.setValue(getQualificationRoundPoints());
        FinalRoundScore.setValue(getFinalRoundScore());
        QualificationRoundWins.setValue(getQualificationRoundWins());
        FinalRoundWins.setValue(getFinalRoundWins());
        FullName.setValue(Fencer.getFullName());
        FencingSchool.setValue(Fencer.getFencingSchool());
        Age.setValue(getAge());
    }
    
    public iFencer getFencer()
    {
        return Fencer;
    }
    
    public StringProperty FullNameProperty()
    {
        return FullName;
    }
    
    public StringProperty FencingSchoolProperty()
    {
        return FencingSchool;
    }

    public StringProperty AgeProperty()
    {
        return Age;
    }
    
    public StringProperty QualificationRoundPointsProperty()
    {
        return QualificationRoundPoints;
    }
    
    public StringProperty QualificationRoundWinsProperty()
    {
        return QualificationRoundWins;
    }
    
    public StringProperty FinalRoundScoreProperty()
    {
        return FinalRoundScore;
    }
    
    public StringProperty FinalRoundWinsProperty()
    {
        return FinalRoundWins;
    }
    
    private String getAge()
    {
        LocalDate birthday = LocalDate.parse(Fencer.getBirthday(), DateTimeFormatter.ISO_DATE);
        LocalDate now = LocalDate.now();
        Period age = Period.between(birthday, now);
        return Integer.toString(age.getYears());
    }
    
    private String getQualificationRoundPoints()
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
    
    private String getQualificationRoundWins()
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
    
    private String getFinalRoundScore()
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
    
    private String getFinalRoundWins()
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

    @Override
    public void update(Observable o, Object o1)
    {
        if (o1 instanceof Sync.change)
        {
            Sync.change changeType = (Sync.change) o1;
            if (changeType == Sync.change.changedFencerValue
                    || changeType == Sync.change.finishedPreliminary
                    || changeType == Sync.change.unfinishedPreliminary
                    || changeType == Sync.change.finishedFinalround
                    ||changeType == Sync.change.unfinishedFinalround)
            {
                UpdateData();
            }
        }
        else
        {
            UpdateData();
        }
    }
}
