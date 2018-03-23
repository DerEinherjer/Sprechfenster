/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster;

import model.ObjectDeprecatedException;
import model.rounds.iPreliminary;
import model.Sync;
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
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.iFencer;
import sprechfenster.presenter.FightPresenter;

/**
 * FXML Controller class
 *
 * @author Stefan
 */
public class QualificationFightTableController implements Initializable, Observer {

  @FXML
  TitledPane MainPane;

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
  TableColumn<FightPresenter, Integer> GroupTableColumn;
  @FXML
  TableColumn EditTableColumn;
  @FXML
  TableColumn<FightPresenter, Boolean> FinishedTableColumn;

  private iTournament Tournament;
  private ArrayList<String> FencerNames;
  private int GroupNumber;
  private int LaneNumber;
  private final LimitedIntegerStringConverter StringToRoundNumber = new LimitedIntegerStringConverter(1, 1);
  private final LimitedIntegerStringConverter StringToLaneNumber = new LimitedIntegerStringConverter(1, 1);
  private final LimitedIntegerStringConverter StringToPointsConverter = new LimitedIntegerStringConverter(Integer.MAX_VALUE, 0);

  /**
   * Initializes the controller class.
   */
  @Override
  public void initialize (URL url, ResourceBundle rb) {
    FightsTableView.setFixedCellSize(30);
    FightsTableView.prefHeightProperty().bind(FightsTableView.fixedCellSizeProperty().multiply(Bindings.size(FightsTableView.getItems()).add(1.01)));

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
    GroupTableColumn.setCellValueFactory(new PropertyValueFactory<>("Group"));
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
                sprechfenster.EditFightDialogController controller = loader.getController();
                controller.SetData(fight.getFight(), Tournament, FencerNames);
                Stage stage = new Stage();
                stage.setTitle("Gefecht bearbeiten");
                stage.setScene(new Scene(dialog));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(EditButton.getScene().getWindow());
                stage.showAndWait();
              }
              catch (IOException ex) {
                sprechfenster.LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
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
    Sync.getInstance().addObserver(this);
  }

  public void SetTournament (iTournament tournament) {
    Tournament = tournament;
    FencerNames = new ArrayList<String>();
    if (Tournament != null) {
      try {
        for (iFencer fencer : Tournament.getAllParticipants()) {
          FencerNames.add(fencer.getFullName());
        }
      }
      catch (SQLException ex) {
        Logger.getLogger(QualificationFightTableController.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  public void SetGroupNumber (int groupNumber) {
    GroupNumber = groupNumber;
    LaneNumber = Integer.MIN_VALUE;
    MainPane.setText("Gruppe " + Integer.toString(groupNumber));
  }
  
  public void SetLaneNumber (int laneNumber)
  {
    LaneNumber = laneNumber;
    GroupNumber = Integer.MIN_VALUE;
    MainPane.setText("Bahn " + Integer.toString(LaneNumber));
  }

  public void SetFights (List<iPreliminary> qualificationFights) {
    FightsTableView.getItems().clear();
    if (qualificationFights != null) {
      int MaxRound = 0;
      for (iPreliminary qualificationFight : qualificationFights) {
        try {
          if (qualificationFight.getGroup() == GroupNumber ||qualificationFight.getLane() == LaneNumber) {
            MaxRound = Math.max(MaxRound, qualificationFight.getRound());
            FightsTableView.getItems().add(new FightPresenter(qualificationFight));
          }
        }
        catch (ObjectDeprecatedException ex) {
          Logger.getLogger(QualificationFightTableController.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
      StringToRoundNumber.setMinAndMaxValues(MaxRound, 1);
      StringToLaneNumber.setMinAndMaxValues(Tournament.getLanes(), 1);
    }
    UpdateStatus();
  }

  private void UpdateStatus () {
    if (Tournament != null) {
      FightsTableView.setEditable(!Tournament.isPreliminaryFinished());
    }
  }

  @Override
  public void update (Observable o, Object o1) {
    if (o1 instanceof Sync.change) {
      Sync.change changeType = (Sync.change) o1;
      if (changeType == Sync.change.finishedPreliminary) {
        UpdateStatus();
      }
    }
  }
}
