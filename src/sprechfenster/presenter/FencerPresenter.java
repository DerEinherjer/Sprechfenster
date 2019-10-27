/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster.presenter;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.iFencer;
import model.iScore;
import model.iTournament;
import sprechfenster.LoggingUtilities;

/**
 *
 * @author Stefan
 */
public class FencerPresenter implements Observer
{

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
    if (fencerToPresent == null)
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
    //TODO: fix observer registration
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
    try
    {
      LocalDate birthday = LocalDate.parse(Fencer.getBirthday(), DateTimeFormatter.ISO_DATE);
      LocalDate now = LocalDate.now();
      Period age = Period.between(birthday, now);
      return Integer.toString(age.getYears());
    } catch (DateTimeParseException e)
    {
      LoggingUtilities.LOGGER.log(Level.SEVERE, null, e);
    }
    return "0";
  }

  private String getQualificationRoundPoints()
  {
    if (Tournament != null)
    {
      iScore score = Tournament.getFencersScoreFromQualificationPhase(Fencer);
      if (score != null)
      {
        return String.format("%d/%d", score.getHits(), score.getGotHit());
      }
    }
    return "0/0";
  }

  private String getQualificationRoundWins()
  {
    if (Tournament != null)
    {
      iScore score = Tournament.getFencersScoreFromQualificationPhase(Fencer);
      if (score != null)
      {
        return Integer.toString(score.getWins());
      }
    }
    return "0";
  }

  private String getFinalRoundScore()
  {
    if (Tournament != null)
    {
      iScore score = Tournament.getFencersScoreFromFinalsPhase(Fencer);
      if (score != null)
      {
        return String.format("%d/%d", score.getHits(), score.getGotHit());
      }
    }
    return "0/0";
  }

  private String getFinalRoundWins()
  {
    if (Tournament != null)
    {
      iScore score = Tournament.getFencersScoreFromFinalsPhase(Fencer);
      if (score != null)
      {
        return Integer.toString(score.getWins());
      }
    }
    return "0";
  }

  @Override
  public void update(Observable o, Object o1)
  {
    //TODO: reimplement conditional update?
    /*if (o1 instanceof Sync.change) {
      Sync.change changeType = (Sync.change) o1;
      if (changeType == Sync.change.changedFencerValue
              || changeType == Sync.change.finishedPreliminary
              || changeType == Sync.change.unfinishedPreliminary
              || changeType == Sync.change.finishedFinalround
              || changeType == Sync.change.unfinishedFinalround) {
        UpdateData();
      }
    }
    else {*/
    UpdateData();
    //}
  }
}
