/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster;

import Model.iFencer;
import Model.iSync;
import Model.iTournament;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sprechfenster.Presenters.FencerPresenter;
import sprechfenster.Presenters.TournamentPresenter;

/**
 *
 * @author Stefan
 */
public class MainFXMLController implements Initializable, iFencerSelection {
    
    @FXML AnchorPane MainAnchorPane;
    @FXML AnchorPane MenuAnchorPane;
    @FXML AnchorPane ContentAnchorPane;
    @FXML SplitPane ContentSplitPane;
    @FXML AnchorPane LeftContentAnchorPane;
    @FXML AnchorPane RightContentAnchorPane;
    
    
    @FXML Button SummaryButton;
    @FXML Button NewTournamentButton;
    @FXML Button LoadTournamentButton;
    @FXML Button NewFencerButton;
    
    @FXML TableView TournamentTableView;
    @FXML TableColumn TournamentColumn;
    @FXML TableColumn DateColumn;
    @FXML TableColumn ParticipantColumn;
    @FXML TableColumn TournamentFightsColumn;
    
    @FXML TableView FencerTableView;
    @FXML TableColumn FencerColumn;
    @FXML TableColumn PortraitColumn;
    @FXML TableColumn FencingSchoolColumn;
    @FXML TableColumn FencerFightsColumn;
    @FXML TableColumn AgeColumn;
   
    private iSync DataModel;
    
    private TournamentPlanningController TournamentController;
    
    
    @Override
    public ObservableList<FencerPresenter> GetSelectedFencers()
    {
        return FencerTableView.getSelectionModel().getSelectedItems();
    }
    
    @FXML
    private void handleSummaryButtonAction(ActionEvent event) {
        LeftContentAnchorPane.getChildren().clear();
        LeftContentAnchorPane.getChildren().add(TournamentTableView);
        RightContentAnchorPane.getChildren().clear();
        RightContentAnchorPane.getChildren().add(FencerTableView);
        UpdateView();
    }    
    
    @FXML
    private void handleNewTournamentButtonAction(ActionEvent event) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("sprechfenster/resources/fxml/NewTournamentDialog.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Neues Turnier");
            stage.setScene(new Scene(root));
            stage.initOwner(NewTournamentButton.getScene().getWindow());
            stage.show();        
        } catch (IOException e) {
            e.printStackTrace();
        }
    } 
    
    @FXML
    private void handleLoadTournamentButtonAction(ActionEvent event) {
        TournamentPresenter tournamentPresenter = (TournamentPresenter)TournamentTableView.getSelectionModel().getSelectedItem();
        iTournament tournamentToDisplay = tournamentPresenter.getTournament();
        if(tournamentToDisplay == null)
        {
            ObservableList items = TournamentTableView.getItems();
            if(items.size() > 0)
            {
                tournamentToDisplay = (iTournament)items.get(0);
            }
        }
        if(tournamentToDisplay == null)
        {
            return;
        }
        else
        {
            LeftContentAnchorPane.getChildren().clear();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("sprechfenster/resources/fxml/TournamentPlanning.fxml"));
                Node tournamentPlanningView = loader.load();
                TournamentController = loader.<TournamentPlanningController>getController();
                TournamentController.setFencerSelectionInterface(this);
                TournamentController.setTournament(tournamentToDisplay);
                LeftContentAnchorPane.getChildren().add(tournamentPlanningView);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }    
    
    @FXML
    private void handleNewFencerButtonAction(ActionEvent event) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("sprechfenster/resources/fxml/NewFencerDialog.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Neuer Fechter");
            stage.setScene(new Scene(root));
            stage.initOwner(NewFencerButton.getScene().getWindow());
            stage.show();        
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DataModel = iSync.getInstance();
        
        FencerColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        FencingSchoolColumn.setCellValueFactory(new PropertyValueFactory<>("fencingSchool"));
        AgeColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        
        TournamentColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        DateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        UpdateView();
        
    }

    private void UpdateView()
    {
        try{
            List<TournamentPresenter> tournaments = new ArrayList<>();
            List<FencerPresenter> fencers = new ArrayList<>();
            for(iFencer fencer : DataModel.getAllFencer())
            {
                fencers.add(new FencerPresenter(fencer));
            }
            for(iTournament tournament : DataModel.getAllTournaments())
            {
                tournaments.add(new TournamentPresenter(tournament));
            }
            FencerTableView.getItems().setAll(fencers);
            TournamentTableView.getItems().setAll(tournaments);
        } catch (SQLException ex) {
            Logger.getLogger(NewFencerDialogController.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
}
