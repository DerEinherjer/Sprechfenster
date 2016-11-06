/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster;

import Model.iSync;
import Model.iTournament;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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
import javafx.scene.control.cell.PropertyValueFactory;
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
public class TournamentEliminationPhaseController implements Initializable, Observer
{

    @FXML
    Button CreateEliminationRoundsButton;
    @FXML
    FlowPane FencersPane;
    @FXML
    TableView<QualificationFightPresenter> FightsTableView;
    @FXML
    TableColumn RoundTableColumn;
    @FXML
    TableColumn LaneTableColumn;
    @FXML
    TableColumn FirstFencerTableColumn;
    @FXML
    TableColumn FirstFencerPointsTableColumn;
    @FXML
    TableColumn SecondFencerPointsTableColumn;
    @FXML
    TableColumn SecondFencerTableColumn;
    @FXML
    TableColumn EditTableColumn;
    @FXML
    TableColumn StatusTableColumn;

    iTournament Tournament;
    GroupTableController FencerListController;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        iSync.getInstance().addObserver(this);
        RoundTableColumn.setCellValueFactory(new PropertyValueFactory<>("Round"));
        LaneTableColumn.setCellValueFactory(new PropertyValueFactory<>("Lane"));
        FirstFencerTableColumn.setCellValueFactory(new PropertyValueFactory<>("FirstFencerName"));
        FirstFencerPointsTableColumn.setCellValueFactory(new PropertyValueFactory<>("FirstFencerPoints"));
        SecondFencerTableColumn.setCellValueFactory(new PropertyValueFactory<>("SecondFencerName"));
        SecondFencerPointsTableColumn.setCellValueFactory(new PropertyValueFactory<>("SecondFencerPoints"));
        StatusTableColumn.setCellValueFactory(new PropertyValueFactory<>("Status"));
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
                                                UpdateData();
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
    private void handleCreateEliminiationRoundsButtonAction(ActionEvent event)
    {
        if (Tournament != null)
        {
            try
            {
                Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION,
                        "Die Vorrunden können nicht mehr verändert werden, wenn das Finale begonnen wird. Fortfahren?",
                        ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> result = confirmationDialog.showAndWait();
                if(result.isPresent() && result.get() == ButtonType.YES)
                {
                    Tournament.finishPreliminary();
                    UpdateData();
                }
            }
            catch (SQLException ex)
            {
                LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }

    private void UpdateData()
    {
        if(Tournament != null)
        {
            try
            {
                FightsTableView.getItems().clear();
                FencersPane.getChildren().clear();
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("sprechfenster/resources/fxml/GroupTable.fxml"));
                Node groupTable = loader.load();
                GroupTableController controller = loader.getController();
                controller.SetGroupName("Fechter");
                controller.SetTournament(Tournament);

                FencerListController = controller;
                FencersPane.getChildren().add(groupTable);
                controller.AddFencer(Tournament.getAllParticipants());
                CreateEliminationRoundsButton.setDisable(Tournament.isPreliminaryFinished() || Tournament.preliminaryWithoutTiming() > 0);
            }
            catch (IOException | SQLException ex)
            {
                LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void update(Observable o, Object o1)
    {
        UpdateData();
    }

}
