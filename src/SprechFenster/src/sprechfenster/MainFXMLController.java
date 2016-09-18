/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster;

import Model.Sync;
import Model.iSync;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 *
 * @author Stefan
 */
public class MainFXMLController implements Initializable {
    
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
   
    iSync DataModel;
    
    @FXML
    private void handleSummaryButtonAction(ActionEvent event) {
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
        System.out.println("load tournament button");
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
        DataModel = Sync.getInstance();
        
        FencerColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        FencingSchoolColumn.setCellValueFactory(new PropertyValueFactory<>("fencingSchool"));
        AgeColumn.setCellValueFactory(new PropertyValueFactory<>("birthday"));
        
        TournamentColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        DateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        UpdateView();
        
    }

    private void UpdateView()
    {
        try{
            FencerTableView.getItems().setAll(DataModel.getAllFencer());
            TournamentTableView.getItems().setAll(DataModel.getAllTournaments());
        } catch (SQLException ex) {
            Logger.getLogger(NewFencerDialogController.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
}
