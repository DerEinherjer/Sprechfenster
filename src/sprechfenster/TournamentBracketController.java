/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import model.ObjectDeprecatedException;
import model.iFencer;
import model.iTournament;
import model.rounds.iFinalsMatch;

/**
 * FXML Controller class
 *
 * @author Stefan
 */
public class TournamentBracketController extends Observable implements Initializable
{

  @FXML
  private AnchorPane BracketAnchorPane;
  @FXML
  private ComboBox<String> FirstFencerComboBox;
  @FXML
  private ComboBox<String> SecondFencerComboBox;
  @FXML
  private ComboBox<String> FirstFencerPointsComboBox;
  @FXML
  private ComboBox<String> SecondFencerPointsComboBox;
  @FXML
  private CheckBox FightFinishedCheckBox;
  @FXML
  private ComboBox<String> LaneComboBox;
  @FXML
  private Label LaneLabel;

  private iFinalsMatch Fight;
  private iTournament Tournament;
  private ArrayList<String> FencerNames;

  /**
   * Initializes the controller class.
   */
  @Override
  public void initialize(URL url, ResourceBundle rb)
  {
    GUIUtilities.FillNumberComboBox(LaneComboBox, 1, 20);
    GUIUtilities.FillNumberComboBox(FirstFencerPointsComboBox, 0, 30);
    GUIUtilities.FillNumberComboBox(SecondFencerPointsComboBox, 0, 30);
  }

  public void SetData(iFinalsMatch fight, iTournament tournament, ArrayList<String> fencerNames)
  {
    Fight = fight;
    Tournament = tournament;
    FencerNames = fencerNames;
    updateData();
  }

  private void ParseAndSetValues()
  {
    try
    {
      int lane = Integer.parseInt(LaneComboBox.getValue());
      int firstFencerPoints = Integer.parseInt(FirstFencerPointsComboBox.getValue());
      int secondFencerPoints = Integer.parseInt(SecondFencerPointsComboBox.getValue());
      Boolean fightIsFinished = FightFinishedCheckBox.isSelected();

      String firstFencerName = FirstFencerComboBox.getValue().trim();
      String secondFencerName = SecondFencerComboBox.getValue().trim();
      iFencer firstFencer = null;
      iFencer secondFencer = null;
      for (iFencer fencer : Tournament.getAllParticipants())
      {
        if (firstFencer == null && firstFencerName.equals(fencer.getFullName()))
        {
          firstFencer = fencer;
        }
        if (secondFencer == null && secondFencerName.equals(fencer.getFullName()))
        {
          secondFencer = fencer;
        }
      }
      if (!Fight.getFencer().contains(firstFencer))
      {
        Fight.switchParticipantOut(Fight.getFencer().get(0), firstFencer);
      }
      if (!Fight.getFencer().contains(secondFencer))
      {
        Fight.switchParticipantOut(Fight.getFencer().get(1), secondFencer);
      }
      if (Fight.getPoints(firstFencer) != firstFencerPoints)
      {
        Fight.setPoints(firstFencer, firstFencerPoints);
      }
      if (Fight.getPoints(secondFencer) != secondFencerPoints)
      {
        Fight.setPoints(secondFencer, secondFencerPoints);
      }
      if (Fight.getLane() != lane)
      {
        Fight.setTime(Fight.getRound(), lane);
      }
      if (Fight.isFinished() != fightIsFinished)
      {
        Fight.setFinished(fightIsFinished);
      }
    } catch (SQLException | ObjectDeprecatedException ex)
    {
      LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
    }
  }

  public void handleAnyAction(ActionEvent event)
  {
    ParseAndSetValues();
    setChanged();
    notifyObservers();
  }

  public void updateData()
  {
    if (Fight != null && Tournament != null)
    {
      try
      {
        if (Fight.getFencer().size() == 2)
        {
          FirstFencerComboBox.getItems().setAll(FencerNames);
          SecondFencerComboBox.getItems().setAll(FencerNames);
          iFencer firstFencer = Fight.getFencer().get(0);
          iFencer secondFencer = Fight.getFencer().get(1);
          for (String name : FencerNames)
          {
            if (name.equals(firstFencer.getFullName()))
            {
              FirstFencerComboBox.getSelectionModel().select(name);
            }
            if (name.equals(secondFencer.getFullName()))
            {
              SecondFencerComboBox.getSelectionModel().select(name);
            }
          }
          FirstFencerPointsComboBox.getSelectionModel().select(Fight.getPoints(Fight.getFencer().get(0)));
          SecondFencerPointsComboBox.getSelectionModel().select(Fight.getPoints(Fight.getFencer().get(1)));
        } else
        {
          FirstFencerComboBox.getSelectionModel().clearSelection();
          SecondFencerComboBox.getSelectionModel().clearSelection();
        }
        GUIUtilities.FillNumberComboBox(LaneComboBox, 1, Tournament.getLanes());
        LaneComboBox.getSelectionModel().select(Fight.getLane() - 1);
        FightFinishedCheckBox.setSelected(Fight.isFinished());
      } catch (ObjectDeprecatedException ex)
      {
        LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
      }
    }
  }

}
