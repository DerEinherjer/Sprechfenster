/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster;

import model.ObjectDeprecatedException;
import model.Sync;
import model.rounds.iFinalround;
import model.iSync;
import model.iTournament;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import sprechfenster.presenter.FightPresenter;

/**
 * FXML Controller class
 *
 * @author Stefan
 */
public class TournamentEliminationPhaseController implements Initializable, Observer {

  @FXML
  Button CreateEliminationRoundsButton;
  @FXML
  FlowPane FencersPane;
  @FXML
  TableView<FightPresenter> FightsTableView;
  @FXML
  TableColumn<FightPresenter, Integer> RoundTableColumn;
  @FXML
  TableColumn<FightPresenter, Integer> LaneTableColumn;
  @FXML
  TableColumn<FightPresenter, String> FirstFencerTableColumn;
  @FXML
  TableColumn<FightPresenter, Integer> FirstFencerPointsTableColumn;
  @FXML
  TableColumn<FightPresenter, Integer> SecondFencerPointsTableColumn;
  @FXML
  TableColumn<FightPresenter, String> SecondFencerTableColumn;
  @FXML
  TableColumn EditTableColumn;
  @FXML
  TableColumn<FightPresenter, Boolean> FinishedTableColumn;

  private iTournament Tournament;
  private final ArrayList<GroupTableController> GroupControllers = new ArrayList<GroupTableController>();
  private final LimitedIntegerStringConverter StringToRoundNumber = new LimitedIntegerStringConverter(1, 1);
  private final LimitedIntegerStringConverter StringToLaneNumber = new LimitedIntegerStringConverter(1, 1);
  private final LimitedIntegerStringConverter StringToPointsConverter = new LimitedIntegerStringConverter(Integer.MAX_VALUE, 0);
  private GroupTableController FencerListController;

  /**
   * Initializes the controller class.
   */
  @Override
  public void initialize (URL url, ResourceBundle rb) {
    iSync.getInstance().addObserver(this);
    RoundTableColumn.setCellValueFactory(new PropertyValueFactory<>("Round"));
    RoundTableColumn.setCellFactory(TextFieldTableCell.forTableColumn(StringToRoundNumber));
    LaneTableColumn.setCellValueFactory(new PropertyValueFactory<>("Lane"));
    LaneTableColumn.setCellFactory(TextFieldTableCell.forTableColumn(StringToLaneNumber));
    FirstFencerTableColumn.setCellValueFactory(new PropertyValueFactory<>("FirstFencerName"));
    FirstFencerPointsTableColumn.setCellValueFactory(new PropertyValueFactory<>("FirstFencerPoints"));
    FirstFencerPointsTableColumn.setCellFactory(TextFieldTableCell.forTableColumn(StringToPointsConverter));
    SecondFencerTableColumn.setCellValueFactory(new PropertyValueFactory<>("SecondFencerName"));
    SecondFencerPointsTableColumn.setCellValueFactory(new PropertyValueFactory<>("SecondFencerPoints"));
    SecondFencerPointsTableColumn.setCellFactory(TextFieldTableCell.forTableColumn(StringToPointsConverter));
    FinishedTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(FinishedTableColumn));
    FinishedTableColumn.setCellValueFactory(new PropertyValueFactory<>("Finished"));
    EditTableColumn.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

    Callback<TableColumn<FightPresenter, String>, TableCell<FightPresenter, String>> editCellFactory
            = //
            (final TableColumn<FightPresenter, String> param)
            -> {
      final TableCell<FightPresenter, String> cell = new TableCell<FightPresenter, String>() {
        final Button EditButton = new Button("Ändern");

        @Override
        public void updateItem (String item, boolean empty) {
          super.updateItem(item, empty);
          if (empty) {
            setGraphic(null);
            setText(null);
          }
          else {
            EditButton.setOnAction((ActionEvent event)
                    -> {
              try {
                FightPresenter fight = getTableView().getItems().get(getIndex());
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(
                        "sprechfenster/resources/fxml/EditFightDialog.fxml"
                ));
                Parent dialog = loader.<Parent>load();
                EditFightDialogController controller = loader.getController();
                controller.SetData(fight.getFight(), Tournament);
                Stage stage = new Stage();
                stage.setTitle("Gefecht bearbeiten");
                stage.setScene(new Scene(dialog));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(EditButton.getScene().getWindow());
                stage.showAndWait();
              }
              catch (IOException ex) {
                LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
              }

            });
            setGraphic(EditButton);
            setText(null);
          }
        }
      };
      return cell;
    };

    EditTableColumn.setCellFactory(editCellFactory);
  }

  public void SetTournament (iTournament tournament) {
    Tournament = tournament;
    UpdateData();
  }

  @FXML
  private void handleCreateEliminiationRoundsButtonAction (ActionEvent event) {
    if (Tournament != null) {
      try {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION,
                "Die Vorrunden können nicht mehr verändert werden, wenn das Finale begonnen wird. Fortfahren?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
          Tournament.finishPreliminary();
        }
      }
      catch (SQLException ex) {
        LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
      }
      catch (ObjectDeprecatedException ex) //finishPreliminary wirf neue Exception hab das hier mal abgefangen
      {
        LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
      }
    }
  }

  private void UpdateData () {
    if (Tournament != null) {
      try {
        FightsTableView.getItems().clear();
        FencersPane.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("sprechfenster/resources/fxml/GroupTable.fxml"));
        Node groupTable = loader.load();
        GroupTableController controller = loader.getController();
        controller.SetGroupName("Fechter");
        controller.SetTournament(Tournament);
        controller.SetPhase(GroupTableController.TournamentPhase.FinalPhase);
        StringToLaneNumber.setMinAndMaxValues(Tournament.getLanes(), 1);
        FencerListController = controller;
        FencersPane.getChildren().add(groupTable);
        controller.AddFencers(Tournament.getAllParticipants());
        CreateEliminationRoundsButton.setDisable(Tournament.isPreliminaryFinished() || Tournament.preliminaryWithoutTiming() > 0);
        int maxRound = 0;
        for (iFinalround finalRound : Tournament.getAllFinalrounds()) {
          maxRound = Math.max(maxRound, finalRound.getRound());
          if (finalRound.getFencer().size() == 2) {
            FightsTableView.getItems().add(new FightPresenter(finalRound));
          }
        }
        StringToRoundNumber.setMinAndMaxValues(maxRound, 1);
      }
      catch (IOException | SQLException | ObjectDeprecatedException ex) {
        LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
      }
    }
  }

  @Override
  public void update (Observable o, Object o1) {
    if (o1 instanceof Sync.change) {
      Sync.change changeType = (Sync.change) o1;
      if (changeType == Sync.change.beganFinalPhase) {
        UpdateData();
      }
    }
    else {
      UpdateData();
    }
  }

}
