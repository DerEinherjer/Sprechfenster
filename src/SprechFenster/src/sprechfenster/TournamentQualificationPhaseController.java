/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster;

import Model.ObjectDeprecatedException;
import Model.Sync;
import Model.iPreliminary;
import Model.iSync;
import Model.iTournament;
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
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
import sprechfenster.Presenters.QualificationFightPresenter;

/**
 * FXML Controller class
 *
 * @author Stefan
 */
public class TournamentQualificationPhaseController implements Initializable, Observer
{

    @FXML
    FlowPane GroupsPane;
    @FXML
    TableView<QualificationFightPresenter> FightsTableView;
    @FXML
    TableColumn<QualificationFightPresenter, Integer> RoundTableColumn;
    @FXML
    TableColumn<QualificationFightPresenter, Integer> LaneTableColumn;
    @FXML
    TableColumn<QualificationFightPresenter, String> FirstFencerTableColumn;
    @FXML
    TableColumn<QualificationFightPresenter, Integer> FirstFencerPointsTableColumn;
    @FXML
    TableColumn<QualificationFightPresenter, Integer> SecondFencerPointsTableColumn;
    @FXML
    TableColumn<QualificationFightPresenter, String> SecondFencerTableColumn;
    @FXML
    TableColumn<QualificationFightPresenter, Integer> GroupTableColumn;
    @FXML
    TableColumn EditTableColumn;
    @FXML
    TableColumn<QualificationFightPresenter, Boolean> FinishedTableColumn;
    @FXML
    Button CreateQualificationRoundsButton;

    private iTournament Tournament;
    private final ArrayList<GroupTableController> GroupControllers = new ArrayList<GroupTableController>();
    private final LimitedIntegerStringConverter StringToRoundNumber = new LimitedIntegerStringConverter(1,1);
    private final LimitedIntegerStringConverter StringToLaneNumber = new LimitedIntegerStringConverter(1,1);
    private final LimitedIntegerStringConverter StringToPointsConverter = new LimitedIntegerStringConverter(Integer.MAX_VALUE, 0);
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
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
        GroupTableColumn.setCellValueFactory(new PropertyValueFactory<>("Group"));
        FinishedTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(FinishedTableColumn));
        FinishedTableColumn.setCellValueFactory(new PropertyValueFactory<>("Finished"));
        EditTableColumn.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        Callback<TableColumn<QualificationFightPresenter, String>, TableCell<QualificationFightPresenter, String>> editCellFactory
                = //
                (final TableColumn<QualificationFightPresenter, String> param)
                -> 
                {
                    final TableCell<QualificationFightPresenter, String> cell = new TableCell<QualificationFightPresenter, String>()
                    {
                        final Button EditButton = new Button("Ändern");

                        @Override
                        public void updateItem(String item, boolean empty)
                        {
                            super.updateItem(item, empty);
                            if (empty)
                            {
                                setGraphic(null);
                                setText(null);
                            }
                            else
                            {
                                EditButton.setOnAction((ActionEvent event)
                                        -> 
                                        {
                                            try
                                            {
                                                QualificationFightPresenter fight = getTableView().getItems().get(getIndex());
                                                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(
                                                        "sprechfenster/resources/fxml/EditQualificationFightDialog.fxml"
                                                ));
                                                Parent dialog = loader.<Parent>load();
                                                EditQualificationFightDialogController controller = loader.getController();
                                                controller.SetData(fight.getFight(), Tournament);
                                                Stage stage = new Stage();
                                                stage.setTitle("Gefecht bearbeiten");
                                                stage.setScene(new Scene(dialog));
                                                stage.initModality(Modality.APPLICATION_MODAL);
                                                stage.initOwner(EditButton.getScene().getWindow());
                                                stage.showAndWait();
                                            }
                                            catch (IOException ex)
                                            {
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

    public void SetTournament(iTournament tournament)
    {
        Tournament = tournament;
        UpdateData();
    }

    @FXML
    private void HandleCreateQualificationRoundsButtonAction(ActionEvent event)
    {
        if (Tournament != null)
        {
            try
            {
                Tournament.createPreliminaryTiming();
                UpdateData();
            }
            catch (SQLException ex)
            {
                LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }

    private void UpdateData()
    {
        if (Tournament != null)
        {
            try
            {
                CreateQualificationRoundsButton.setDisable(Tournament.preliminaryWithoutTiming() == 0 || Tournament.isPreliminaryFinished());
                GroupControllers.clear();
                FightsTableView.getItems().clear();
                GroupsPane.getChildren().clear();
                if (Tournament.preliminaryWithoutTiming() < Tournament.getPreliminaryCount())
                {
                    List<iPreliminary> qualificationFights = Tournament.getAllPreliminary();
                    if (qualificationFights != null)
                    {
                        qualificationFights.sort((a, b)
                                -> 
                                {
                                    try
                                    {
                                        return Integer.compare(a.getGroup(), b.getGroup());
                                    }
                                    catch (ObjectDeprecatedException ex)
                                    {
                                        LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
                                        return -1;
                                    }
                        });
                        try
                        {
                            for (int groupNumber = 1; groupNumber <= Tournament.getGroups(); groupNumber++)
                            {

                                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("sprechfenster/resources/fxml/GroupTable.fxml"));
                                Node groupTable = loader.load();
                                GroupTableController controller = loader.getController();
                                controller.SetGroupName("Gruppe " + Integer.toString(groupNumber));
                                controller.SetTournament(Tournament);
                                controller.SetPhase(GroupTableController.TournamentPhase.QualificationPhase);
                                GroupControllers.add(controller);
                                GroupsPane.getChildren().add(groupTable);
                            }

                        }
                        catch (IOException ex)
                        {
                            LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
                        }
                        qualificationFights.sort((a, b)
                                -> 
                                {
                                    try
                                    {
                                        return Integer.compare(a.getRound(), b.getRound());
                                    }
                                    catch (ObjectDeprecatedException ex)
                                    {
                                        LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
                                        return -1;
                                    }
                        });
                        int MaxRound = 0;
                        for (iPreliminary qualificationFight : qualificationFights)
                        {
                            MaxRound = Math.max(MaxRound, qualificationFight.getRound());
                            int groupNumber;
                            try
                            {
                                groupNumber = qualificationFight.getGroup();
                            }
                            catch (ObjectDeprecatedException ex)
                            {
                                LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
                                groupNumber = Integer.MAX_VALUE;
                            }
                            if (groupNumber <= GroupControllers.size())
                            {
                                GroupTableController controller = GroupControllers.get(groupNumber - 1);
                                try
                                {
                                    controller.AddFencer(qualificationFight.getFencer());
                                }
                                catch (ObjectDeprecatedException ex)
                                {
                                    LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
                                }
                            }
                            FightsTableView.getItems().add(new QualificationFightPresenter(qualificationFight));
                        }
                        StringToRoundNumber.setMinAndMaxValues(MaxRound, 1);
                        StringToLaneNumber.setMinAndMaxValues(Tournament.getLanes(), 1);
                    }
                }
            }
            catch (SQLException ex)
            {
                LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
            }
            catch (ObjectDeprecatedException ex)
            {
                Logger.getLogger(TournamentQualificationPhaseController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void update(Observable o, Object o1)
    {
        if(o1 instanceof Sync.change)
        {
            Sync.change changeType = (Sync.change)o1;
            if(changeType == Sync.change.createdPreliminary)
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
