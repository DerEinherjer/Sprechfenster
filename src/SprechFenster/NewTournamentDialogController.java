/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster;

import Model.iSync;
import Model.iTournament;
import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
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
public class NewTournamentDialogController implements Initializable
{

    @FXML
    AnchorPane MainDialogPane;
    @FXML
    TextField NameTextField;
    @FXML
    DatePicker StartingDatePicker;
    @FXML
    ComboBox GroupsComboBox;
    @FXML
    ComboBox FinalRoundsComboBox;
    @FXML
    ComboBox LanesComboBox;
    @FXML
    ImageView TournamentImageView;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        GUIUtilities.FillNumberComboBox(GroupsComboBox, 1, 20);
        GUIUtilities.FillNumberComboBox(FinalRoundsComboBox, 1, 20);
        GUIUtilities.FillNumberComboBox(LanesComboBox, 1, 10);
        StartingDatePicker.setValue(LocalDate.now());
    }

    @FXML
    private void handleCanceledButtonAction(ActionEvent event)
    {
        CloseDialog();
    }

    @FXML
    private void handleChangeImageButtonAction(ActionEvent event)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(GetDialogStage());
        Image image = new Image(selectedFile.toURI().toString());
        TournamentImageView.setImage(image);
    }

    @FXML
    private void handleFinishedButtonAction(ActionEvent event)
    {
        boolean allFieldsFilled = true;
        if (NameTextField.getText().isEmpty())
        {
            allFieldsFilled = false;
        }
        if (StartingDatePicker.getValue() == null)
        {
            allFieldsFilled = false;
        }
        if (GroupsComboBox.getValue() == null)
        {
            allFieldsFilled = false;
        }
        if (FinalRoundsComboBox.getValue() == null)
        {
            allFieldsFilled = false;
        }
        if(LanesComboBox.getValue() == null)
        {
            allFieldsFilled = false;
        }
        if (allFieldsFilled)
        {
            iSync dataModel = iSync.getInstance();
            try
            {
                iTournament newTournament = dataModel.createTournament(NameTextField.getText());
                newTournament.setDate(GUIUtilities.GetDateStringFromDatePicker(StartingDatePicker));
                newTournament.setGroups(GUIUtilities.GetIntegerFromStringComboBox(GroupsComboBox));
                newTournament.setFinalRounds(GUIUtilities.GetIntegerFromStringComboBox(FinalRoundsComboBox));
                newTournament.setLanes(GUIUtilities.GetIntegerFromStringComboBox(LanesComboBox));
            }
            catch (Exception ex)
            {
                LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
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
        return (Stage) MainDialogPane.getScene().getWindow();
    }

}
