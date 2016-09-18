/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster;

import Model.Sync;
import Model.iSync;
import Model.iTournament;
import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Stefan
 */
public class NewTournamentDialogController implements Initializable {

    @FXML AnchorPane MainDialogPane;
    @FXML TextField NameTextField;
    @FXML DatePicker StartingDatePicker;
    @FXML ComboBox GroupsComboBox;
    @FXML ComboBox FinalRoundsComboBox;
    @FXML ImageView TournamentImageView;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        int maxRounds = 30;
        ArrayList<String> numbers = new ArrayList<>(maxRounds);
        for(Integer i = 1; i <= maxRounds; i++)
        {
            numbers.add(i.toString());
        }
        GroupsComboBox.getItems().addAll(numbers);
        FinalRoundsComboBox.getItems().addAll(numbers);
    }    
    
    @FXML
    private void handleCanceledButtonAction(ActionEvent event) {
        CloseDialog();
    }
    
    @FXML
    private void handleChangeImageButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(GetDialogStage());
        Image image = new Image(selectedFile.toURI().toString());
        TournamentImageView.setImage(image);
    }
    
    @FXML
    private void handleFinishedButtonAction(ActionEvent event) {
        boolean allFieldsFilled = true;
        if(NameTextField.getText().isEmpty())
        {
         allFieldsFilled = false;
        }
        if(StartingDatePicker.getValue() == null)
        {
         allFieldsFilled = false;
        }
        if(GroupsComboBox.getValue() == null)
        {
         allFieldsFilled = false;
        }
        if(FinalRoundsComboBox.getValue() == null)
        {
         allFieldsFilled = false;
        }
        if(allFieldsFilled)
        {
            iSync dataModel = Sync.getInstance();
            try { 
                iTournament newTournament = dataModel.createTournament(NameTextField.getText());
                newTournament.setDate(StartingDatePicker.getValue().format(DateTimeFormatter.ISO_DATE));
                newTournament.setGroups(Integer.parseInt(GroupsComboBox.getValue().toString()));
                newTournament.setFinalRounds(Integer.parseInt(FinalRoundsComboBox.getValue().toString()));
            } catch (SQLException ex) {
                Logger.getLogger(NewFencerDialogController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        CloseDialog();
    }
    
    private void CloseDialog()
    {
        GetDialogStage().close();
    }
    
    private Stage GetDialogStage()
    {
        return (Stage)MainDialogPane.getScene().getWindow();
    }
    
}