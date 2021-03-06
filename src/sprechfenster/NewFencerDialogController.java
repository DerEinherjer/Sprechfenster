/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster;

import model.iFencer;
import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
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
import model.Fencer;

/**
 * FXML Controller class
 *
 * @author Stefan
 */
public class NewFencerDialogController implements Initializable
{

  @FXML
  AnchorPane MainDialogPane;

  @FXML
  ImageView FencerPortraitImageView;

  @FXML
  Button FinishedButton;

  @FXML
  Button CancelButton;

  @FXML
  Button ChangeImageButton;

  @FXML
  TextField FirstNameTextField;

  @FXML
  TextField LastNameTextField;

  @FXML
  ComboBox FencingSchoolComboBox;

  @FXML
  DatePicker BirthdayDatePicker;

  @FXML
  ComboBox NationalityComboBox;

  /**
   * Initializes the controller class.
   */
  @Override
  public void initialize(URL url, ResourceBundle rb)
  {
    FencingSchoolComboBox.getItems().addAll("7 Schwerter", "Krîfon", "Asteria", "Schwert und Bogen", "Der Fechtboden", "Institut für Stabfechten", "TG Münster");
    FencingSchoolComboBox.getSelectionModel().selectFirst();
    NationalityComboBox.getItems().addAll("Deutschland", "Österreich", "Schweiz");
    NationalityComboBox.getSelectionModel().selectFirst();
    BirthdayDatePicker.setValue(LocalDate.of(1986, Month.MARCH, 14));

  }

  @FXML
  private void handleFinishedButtonAction(ActionEvent event)
  {
    boolean allFieldsFilled = true;
    if (FirstNameTextField.getText().isEmpty())
    {
      allFieldsFilled = false;
    }
    if (LastNameTextField.getText().isEmpty())
    {
      allFieldsFilled = false;
    }
    if (FencingSchoolComboBox.getValue() == null)
    {
      allFieldsFilled = false;
    }
    if (BirthdayDatePicker.getValue() == null)
    {
      allFieldsFilled = false;
    }
    if (NationalityComboBox.getValue() == null)
    {
      allFieldsFilled = false;
    }
    if (allFieldsFilled)
    {
      try
      {
        String firstName = FirstNameTextField.getText();
        String familyName = LastNameTextField.getText();
        String birthDay = BirthdayDatePicker.getValue().format(DateTimeFormatter.ISO_DATE);
        String nationality = NationalityComboBox.getValue().toString();
        String fencingSchool = FencingSchoolComboBox.getValue().toString();
        //adds the fencer to the db as a side-effect
        iFencer newFencer = new Fencer(firstName, familyName, birthDay, nationality, fencingSchool);
      
      } catch (SQLException ex)
      {
        LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
      }
    }
    CloseDialog();
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
    return (Stage) MainDialogPane.getScene().getWindow();
  }
}
