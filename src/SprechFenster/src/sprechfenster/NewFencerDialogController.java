/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster;

import Model.iFencer;
import Model.iSync;
import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Stefan
 */
public class NewFencerDialogController implements Initializable {

    @FXML AnchorPane MainDialogPane;
    
    @FXML ImageView FencerPortraitImageView;
    
    @FXML Button FinishedButton;
    
    @FXML Button CancelButton;
    
    @FXML Button ChangeImageButton;
    
    @FXML TextField FirstNameTextField;
    
    @FXML TextField LastNameTextField;
    
    @FXML ComboBox FencingSchoolComboBox;
    
    @FXML DatePicker BirthdayDatePicker;
    
    @FXML ComboBox NationalityComboBox;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        FencingSchoolComboBox.getItems().addAll("7 Schwerter", "Krîfon", "Asteria", "Schwert und Bogen");
        NationalityComboBox.getItems().addAll("Deutschland", "Österreich", "Schweiz");
    }   
    
    @FXML
    private void handleFinishedButtonAction(ActionEvent event) {
        boolean allFieldsFilled = true;
        if(FirstNameTextField.getText().isEmpty())
        {
            allFieldsFilled = false;
        }
        if(LastNameTextField.getText().isEmpty())
        {
            allFieldsFilled = false;
        }
        if(FencingSchoolComboBox.getValue() == null)
        {
            allFieldsFilled = false;
        }
        if(BirthdayDatePicker.getValue() == null)
        {
            allFieldsFilled = false;
        }
        if(NationalityComboBox.getValue() == null)
        {
            allFieldsFilled = false;
        }
        if(allFieldsFilled)
        {
            iSync dataModel = iSync.getInstance();
            try { 
                iFencer newFencer = dataModel.createFencer(FirstNameTextField.getText(), LastNameTextField.getText());
                newFencer.setBirthday(BirthdayDatePicker.getValue().format(DateTimeFormatter.ISO_DATE));
                newFencer.setNationality(NationalityComboBox.getValue().toString());
                newFencer.setFencingSchool(FencingSchoolComboBox.getValue().toString());
            } catch (SQLException ex) {
                Logger.getLogger(NewFencerDialogController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        CloseDialog();
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
        new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(GetDialogStage());
        Image image = new Image(selectedFile.toURI().toString());
        FencerPortraitImageView.setImage(image);
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
