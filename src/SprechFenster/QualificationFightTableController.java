/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster;

import Model.ObjectDeprecatedException;
import Model.iPreliminary;
import Model.iTournament;
import java.io.IOException;
import java.net.URL;
import java.util.List;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import sprechfenster.Presenters.QualificationFightPresenter;

/**
 * FXML Controller class
 *
 * @author Stefan
 */
public class QualificationFightTableController implements Initializable
{

    @FXML
    AnchorPane MainAnchorPane;
    @FXML
    Label GroupNameLabel;
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

    private iTournament Tournament;
    private int GroupNumber;
    private final LimitedIntegerStringConverter StringToRoundNumber = new LimitedIntegerStringConverter(1, 1);
    private final LimitedIntegerStringConverter StringToLaneNumber = new LimitedIntegerStringConverter(1, 1);
    private final LimitedIntegerStringConverter StringToPointsConverter = new LimitedIntegerStringConverter(Integer.MAX_VALUE, 0);

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        FightsTableView.setFixedCellSize(30);
        FightsTableView.prefHeightProperty().bind(FightsTableView.fixedCellSizeProperty().multiply(Bindings.size(FightsTableView.getItems()).add(1.01)));
        MainAnchorPane.minHeightProperty().bind(FightsTableView.prefHeightProperty().add(50));
        MainAnchorPane.maxHeightProperty().bind(FightsTableView.prefHeightProperty().add(50));
        
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
                                                sprechfenster.EditQualificationFightDialogController controller = loader.getController();
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
    }

    public void SetTournament(iTournament tournament)
    {
        Tournament = tournament;
    }

    public void SetGroupNumber(int groupNumber)
    {
        GroupNumber = groupNumber;
        GroupNameLabel.setText("Gruppe "+Integer.toString(groupNumber));
    }

    public void SetFights(List<iPreliminary> qualificationFights)
    {
        try
        {
            FightsTableView.getItems().clear();
            if (qualificationFights != null)
            {
                int MaxRound = 0;
                for (iPreliminary qualificationFight : qualificationFights)
                {
                    if (qualificationFight.getGroup() == GroupNumber)
                    {
                        MaxRound = Math.max(MaxRound, qualificationFight.getRound());
                        FightsTableView.getItems().add(new QualificationFightPresenter(qualificationFight));
                    }
                }
                StringToRoundNumber.setMinAndMaxValues(MaxRound, 1);
                StringToLaneNumber.setMinAndMaxValues(Tournament.getLanes(), 1);
            }
        }
        catch (ObjectDeprecatedException ex)
        {
            Logger.getLogger(sprechfenster.TournamentQualificationPhaseController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
