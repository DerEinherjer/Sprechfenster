/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster;

import model.ObjectDeprecatedException;
import model.iFencer;
import model.rounds.iRound;
import model.iTournament;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Stefan
 */
public class EditFightDialogController implements Initializable {

  @FXML
  private AnchorPane MainDialogPane;
  @FXML
  private ComboBox<String> RoundComboBox;
  @FXML
  private ComboBox<String> LaneComboBox;
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
  private Button FinishedButton;
  @FXML
  private Button CanceledButton;

  private iRound Fight;
  private iTournament Tournament;
  private ArrayList<String> FencerNames;

  /**
   * Initializes the controller class.
   */
  @Override
  public void initialize (URL url, ResourceBundle rb) {
    GUIUtilities.FillNumberComboBox(RoundComboBox, 1, 20);
    GUIUtilities.FillNumberComboBox(FirstFencerPointsComboBox, 0, 30);
    GUIUtilities.FillNumberComboBox(SecondFencerPointsComboBox, 0, 30);
  }

  public void SetData (iRound fight, iTournament tournament, ArrayList<String> fencerNames) {
    Fight = fight;
    Tournament = tournament;
    FencerNames = fencerNames;
    updateData();
  }

  private void updateData () {
    if (Fight != null && Tournament != null) {
      try {
        iFencer firstFencer = Fight.getFencer().get(0);
        iFencer secondFencer = Fight.getFencer().get(1);
        FirstFencerComboBox.getItems().setAll(FencerNames);
        SecondFencerComboBox.getItems().setAll(FencerNames);
        for (String name : FencerNames) {
          if (name.equals(firstFencer.getFullName())) {
            FirstFencerComboBox.getSelectionModel().select(name);
          }
          if (name.equals(secondFencer.getFullName())) {
            SecondFencerComboBox.getSelectionModel().select(name);
          }
        }
        GUIUtilities.FillNumberComboBox(LaneComboBox, 1, Tournament.getLanes());
        LaneComboBox.getSelectionModel().select(Fight.getLane() - 1);
        GUIUtilities.FillNumberComboBox(RoundComboBox, 1, 20);
        RoundComboBox.getSelectionModel().select(Fight.getRound() - 1);
        FirstFencerPointsComboBox.getSelectionModel().select(Fight.getPoints(Fight.getFencer().get(0)));
        SecondFencerPointsComboBox.getSelectionModel().select(Fight.getPoints(Fight.getFencer().get(1)));
        FightFinishedCheckBox.setSelected(Fight.isFinished());
      }
      catch (ObjectDeprecatedException ex) {
        LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
      }
    }
  }

  @FXML
  private void handleFinishedButtonAction (ActionEvent event) {
    if (Fight != null && Tournament != null) {
      try {
        Boolean allValuesOk = true;
        iFencer firstFencer = null;
        iFencer secondFencer = null;
        String firstFencerName = FirstFencerComboBox.getValue().trim();
        String secondFencerName = SecondFencerComboBox.getValue().trim();
        for (iFencer fencer : Tournament.getAllParticipants()) {
          if (firstFencer == null && firstFencerName.equals(fencer.getFullName())) {
            firstFencer = fencer;
          }
          if (secondFencer == null && secondFencerName.equals(fencer.getFullName())) {
            secondFencer = fencer;
          }
        }
        if (firstFencer == null || secondFencer == null || firstFencer == secondFencer) {
          allValuesOk = false;
        }
        int lane = Integer.parseInt(LaneComboBox.getValue());
        int round = Integer.parseInt(RoundComboBox.getValue());
        int firstFencerPoints = Integer.parseInt(FirstFencerPointsComboBox.getValue());
        int secondFencerPoints = Integer.parseInt(SecondFencerPointsComboBox.getValue());
        Boolean fightIsFinished = FightFinishedCheckBox.isSelected();

        if (allValuesOk) {
          try {
            if(!Fight.getFencer().contains(firstFencer)
                || !Fight.getFencer().contains(secondFencer))
            {
                Fight.removeParticipant(Fight.getFencer().get(0));
                Fight.removeParticipant(Fight.getFencer().get(0));
                Fight.addParticipant(firstFencer);
                Fight.addParticipant(secondFencer);
            }
            Fight.setPoints(firstFencer, firstFencerPoints);
            Fight.setPoints(secondFencer, secondFencerPoints);
            Fight.setTime(round, lane);
            Fight.setFinished(fightIsFinished);
          }
          catch (ObjectDeprecatedException ex) {
            LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
          }
        }
      }
      catch (NumberFormatException| SQLException ex) {
        LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
      }
      finally {
        CloseDialog();
      }
    }
  }

  @FXML
  private void handleCanceledButtonAction (ActionEvent event) {
    CloseDialog();
  }

  private void CloseDialog () {
    GetDialogStage().close();
  }

  private Stage GetDialogStage () {
    return (Stage) MainDialogPane.getScene().getWindow();
  }

}
