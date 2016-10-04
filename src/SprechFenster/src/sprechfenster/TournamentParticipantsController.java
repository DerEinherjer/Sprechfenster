/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster;

import Model.iFencer;
import Model.iTournament;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import sprechfenster.Presenters.FencerPresenter;

/**
 * FXML Controller class
 *
 * @author Stefan
 */
public class TournamentParticipantsController implements Initializable {

    
    @FXML TextField NameTextField;
    @FXML DatePicker StartDatePicker;
    
    @FXML TableView ParticipantsTableView; 
    @FXML TableColumn ParticipantColumn;
    @FXML Button AddParticipantButton;
    @FXML Button RemoveParticipantButton;
    
    @FXML ComboBox FencingLanesComboBox;
    @FXML ComboBox QualificationGroupsComboBox;
    @FXML ComboBox FinalRoundsComboBox;
    
    private iFencerSelection FencerSelection;
    private iTournament Tournament;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ParticipantColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        GUIUtilities.FillNumberComboBox(FencingLanesComboBox, 10);
        GUIUtilities.FillNumberComboBox(QualificationGroupsComboBox, 20);
        GUIUtilities.FillNumberComboBox(FinalRoundsComboBox, 20);
    }    
    
    public void setFencerSelectionInterface(iFencerSelection selectionInterface)
    {
        FencerSelection = selectionInterface;
    }
    
    public void setTournament(iTournament tournament)
    {
        Tournament = tournament;
        if(Tournament != null)
        {
            NameTextField.setText(Tournament.getName());
            StartDatePicker.setValue(LocalDate.parse(Tournament.getDate(), DateTimeFormatter.ISO_DATE));
            setComboBoxSelection(FencingLanesComboBox, Tournament.getLanes());
            setComboBoxSelection(QualificationGroupsComboBox, Tournament.getGroups());
            setComboBoxSelection(FinalRoundsComboBox, Tournament.getFinalRounds());
            UpdateParticipantsList();
        }
    }
    
    private void UpdateParticipantsList()
    {
        if(Tournament != null)
        {
            try 
            {
                List<FencerPresenter> presenters = new ArrayList<>();
                for(iFencer fencer : Tournament.getAllParticipants())
                {
                    presenters.add(new FencerPresenter(fencer));
                }
                ParticipantsTableView.getItems().setAll(presenters);
            } catch (SQLException ex) {
                Logger.getLogger(TournamentParticipantsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void setComboBoxSelection(ComboBox box, int selectedNumber)
    {
        if(selectedNumber > box.getItems().size())
        {
            GUIUtilities.FillNumberComboBox(box, selectedNumber);
        }
        box.getSelectionModel().select(selectedNumber);
    }
    
    @FXML
    private void handleNameChange(ActionEvent event)
    {
        if(Tournament != null)
        {
            try 
            {
                Tournament.setName(NameTextField.getText());
            } catch (SQLException ex) {
                Logger.getLogger(TournamentParticipantsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @FXML
    private void handleStartDateChange(ActionEvent event)
    {
        if(Tournament != null)
        {
            try 
            {
                Tournament.setDate(GUIUtilities.GetDateStringFromDatePicker(StartDatePicker));
            } catch (SQLException ex) {
                Logger.getLogger(TournamentParticipantsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @FXML
    private void handleFencingLanesChange(ActionEvent event)
    {
        if(Tournament != null)
        {
            try {
                Tournament.setLanes(FencingLanesComboBox.getSelectionModel().getSelectedIndex());
            } catch (SQLException ex) {
                Logger.getLogger(TournamentParticipantsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @FXML
    private void handleQualificationsGroupsChange(ActionEvent event)
    {
        if(Tournament != null)
        {
            try 
            {
                Tournament.setGroups(QualificationGroupsComboBox.getSelectionModel().getSelectedIndex());
            } catch (SQLException ex) {
                Logger.getLogger(TournamentParticipantsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
     @FXML
    private void handleFinalRoundsChange(ActionEvent event)
    {
        if(Tournament != null)
        {
            try 
            {
                Tournament.setFinalRounds(FinalRoundsComboBox.getSelectionModel().getSelectedIndex());
            } catch (SQLException ex) {
                Logger.getLogger(TournamentParticipantsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @FXML
    private void handleAddParticipantButton(ActionEvent event)
    {
        if(Tournament != null)
        {
            try 
            {
                List<iFencer> participants = Tournament.getAllParticipants();

                if(FencerSelection != null)
                {
                    for(FencerPresenter fencerPresenter : FencerSelection.GetSelectedFencers())
                    {
                        if(!participants.contains(fencerPresenter.getFencer()))
                        {
                            Tournament.addParticipant(fencerPresenter.getFencer());
                        }
                    }
                    UpdateParticipantsList();
                }
            } catch (SQLException ex) {
                Logger.getLogger(TournamentParticipantsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @FXML
    private void handleRemoveParticipantButton(ActionEvent event)
    {
        if(Tournament != null)
        {
            try 
            {
                List<iFencer> participants = Tournament.getAllParticipants();
                ObservableList<FencerPresenter> selectedPresenters = ParticipantsTableView.getSelectionModel().getSelectedItems();
                for(FencerPresenter fencerPresenter : selectedPresenters)
                {
                   boolean fencerRemoved = participants.remove(fencerPresenter.getFencer());
                   if(fencerRemoved)
                   {
                        Tournament.removeParticipant(fencerPresenter.getFencer());
                   }
                }
                UpdateParticipantsList();
            } catch (SQLException ex) {
                Logger.getLogger(TournamentParticipantsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
