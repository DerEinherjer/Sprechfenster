/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster;

import model.ObjectDeprecatedException;
import model.Sync;
import model.rounds.iPreliminary;
import model.iSync;
import model.iTournament;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Stefan
 */
public class TournamentQualificationPhaseController implements Initializable, Observer {

  @FXML
  VBox GroupsBox;
  @FXML
  VBox FightsBox;

  @FXML
  Button CreateQualificationRoundsButton;

  private iTournament Tournament;
  private final ArrayList<GroupTableController> GroupControllers = new ArrayList<GroupTableController>();
  private final ArrayList<QualificationFightTableController> FightControllers = new ArrayList<QualificationFightTableController>();

  /**
   * Initializes the groupController class.
   */
  @Override
  public void initialize (URL url, ResourceBundle rb) {
    iSync.getInstance().addObserver(this);
  }

  public void SetTournament (iTournament tournament) {
    Tournament = tournament;
    UpdateData();
  }

  @FXML
  private void HandleCreateQualificationRoundsButtonAction (ActionEvent event) {
    if (Tournament != null) {
      try {
        Tournament.createPreliminaryTiming();
        UpdateData();
      }
      catch (SQLException ex) {
        LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
      }
    }
  }

  private void UpdateData () {
    GroupControllers.clear();
    FightControllers.clear();
    FightsBox.getChildren().clear();
    GroupsBox.getChildren().clear();
    if (Tournament != null) {
      try {
        CreateQualificationRoundsButton.setDisable(GUIUtilities.IsTournamentStarted(Tournament));

        if (Tournament.preliminaryWithoutTiming() < Tournament.getPreliminaryCount()) {
          List<iPreliminary> qualificationFights = Tournament.getAllPreliminary();
          if (qualificationFights != null) {
            try {
              for (int groupNumber = 1; groupNumber <= Tournament.getGroups(); groupNumber++) {

                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("sprechfenster/resources/fxml/GroupTable.fxml"));
                Node groupTable = loader.load();
                GroupTableController groupController = loader.getController();
                groupController.SetGroupName("Gruppe " + Integer.toString(groupNumber));
                groupController.SetTournament(Tournament);
                groupController.SetPhase(GroupTableController.TournamentPhase.QualificationPhase);
                GroupControllers.add(groupController);
                GroupsBox.getChildren().add(groupTable);

                loader = new FXMLLoader(getClass().getClassLoader().getResource("sprechfenster/resources/fxml/QualificationFightTable.fxml"));
                Node fightTable = loader.load();
                QualificationFightTableController fightController = loader.getController();
                fightController.SetGroupNumber(groupNumber);
                fightController.SetTournament(Tournament);
                FightControllers.add(fightController);
                FightsBox.getChildren().add(fightTable);
              }
            }
            catch (IOException ex) {
              LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
            }
            qualificationFights.sort((a, b)
                    -> {
              try {
                return Integer.compare(a.getRound(), b.getRound());
              }
              catch (ObjectDeprecatedException ex) {
                LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
                return -1;
              }
            });
            for (QualificationFightTableController fightController : FightControllers) {
              fightController.SetFights(qualificationFights);
            }
            for (iPreliminary qualificationFight : qualificationFights) {
              int groupNumber;
              try {
                groupNumber = qualificationFight.getGroup();
              }
              catch (ObjectDeprecatedException ex) {
                LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
                groupNumber = Integer.MAX_VALUE;
              }
              if (groupNumber <= GroupControllers.size()) {
                GroupTableController groupController = GroupControllers.get(groupNumber - 1);
                try {
                  groupController.AddFencers(qualificationFight.getFencer());
                }
                catch (ObjectDeprecatedException ex) {
                  LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
                }
              }
            }
          }
        }
      }
      catch (SQLException ex) {
        LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
      }
    }
  }

  @Override
  public void update (Observable o, Object o1) {
    if (o1 instanceof Sync.change) {
      Sync.change changeType = (Sync.change) o1;
      if (changeType == Sync.change.createdPreliminary) {
        UpdateData();
      }
    }
    else {
      UpdateData();
    }
  }
}
