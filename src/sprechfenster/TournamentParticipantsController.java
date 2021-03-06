/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster;

import model.iFencer;
import model.iTournament;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import model.Fencer;
import sprechfenster.presenter.FencerPresenter;

/**
 * FXML Controller class
 *
 * @author Stefan
 */
public class TournamentParticipantsController implements Initializable, Observer
{

  @FXML
  TextField NameTextField;
  @FXML
  DatePicker StartDatePicker;

  @FXML
  TableView ParticipantsTableView;
  @FXML
  TableColumn ParticipantColumn;
  @FXML
  Button AddParticipantButton;
  @FXML
  Button RemoveParticipantButton;

  @FXML
  ComboBox FencingLanesComboBox;
  @FXML
  ComboBox QualificationGroupsComboBox;
  @FXML
  ComboBox FinalRoundsComboBox;

  private iFencerSelection FencerSelection;
  private iTournament Tournament;

  /**
   * Initializes the controller class.
   */
  @Override
  public void initialize(URL url, ResourceBundle rb)
  {
    ParticipantsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    ParticipantColumn.setCellValueFactory(new PropertyValueFactory<>("FullName"));
    GUIUtilities.FillNumberComboBox(FencingLanesComboBox, 1, 10);
    GUIUtilities.FillNumberComboBox(QualificationGroupsComboBox, 1, 20);
    GUIUtilities.FillNumberComboBox(FinalRoundsComboBox, 1, 20);
  }

  @Override
  public void update(Observable o, Object o1)
  {
    setTournament(Tournament);
  }

  public void setFencerSelectionInterface(iFencerSelection selectionInterface)
  {
    FencerSelection = selectionInterface;
  }

  public void setTournament(iTournament tournament)
  {
    Tournament = tournament;
    if (Tournament != null)
    {
      NameTextField.setText(Tournament.getName());
      StartDatePicker.setValue(LocalDate.parse(Tournament.getDate(), DateTimeFormatter.ISO_DATE));
      setComboBoxSelection(FencingLanesComboBox, 1, Tournament.getLanes());
      setComboBoxSelection(QualificationGroupsComboBox, 1, Tournament.getGroups());
      setComboBoxSelection(FinalRoundsComboBox, 1, Tournament.getFinalRounds());
      UpdateParticipantsList();
    }
  }

  private void UpdateParticipantsList()
  {
    if (Tournament != null)
    {
      try
      {
        List<FencerPresenter> presenters = new ArrayList<>();
        for (iFencer fencer : Tournament.getAllParticipants())
        {
          presenters.add(new FencerPresenter(fencer, Tournament));
        }
        ParticipantsTableView.getItems().setAll(presenters);
      } catch (SQLException ex)
      {
        LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
      }
    }
  }

  private void setComboBoxSelection(ComboBox box, int startNumber, int selectedNumber)
  {
    if (selectedNumber > box.getItems().size())
    {
      GUIUtilities.FillNumberComboBox(box, startNumber, selectedNumber);
    }
    box.getSelectionModel().select(selectedNumber - 1);
  }

  @FXML
  private void ParticipantsTableViewDragOver(DragEvent event)
  {
    if (event.getGestureSource() != ParticipantsTableView
            && event.getDragboard().hasString()
            && Tournament != null)
    {
      String dragContent = event.getDragboard().getString();
      if (dragContent != null)
      {
        if (dragContent.startsWith("FencerIDs;"))
        {
          event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
      }
    }
    event.consume();
  }

  @FXML
  private void ParticipantsTableViewDragDropped(DragEvent event)
  {
    Boolean successfulDrop = false;
    if (event.getDragboard().hasString())
    {
      String dragContent = event.getDragboard().getString();
      if (dragContent != null)
      {
        if (dragContent.startsWith("FencerIDs;"))
        {
          StringTokenizer tokenizer = new StringTokenizer(dragContent, ";");
          while (tokenizer.hasMoreTokens())
          {
            String token = tokenizer.nextToken();
            try
            {
              int fencerId = Integer.parseInt(token);
              iFencer fencer = Fencer.getFencer(fencerId);
              if (fencer != null && Tournament != null)
              {
                if (!Tournament.isParticipant(fencer))
                {
                  Tournament.addParticipant(fencer);
                }
              }
            } catch (SQLException | NumberFormatException ex)
            {
              //do nothing
            }
          }
          successfulDrop = true;
          UpdateParticipantsList();
        }
      }
    }
    event.setDropCompleted(successfulDrop);
    event.consume();
  }

  @FXML
  private void handleNameChange(ActionEvent event)
  {
    if (Tournament != null)
    {
      try
      {
        Tournament.setName(NameTextField.getText());
      } catch (SQLException ex)
      {
        LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
      }
    }
  }

  @FXML
  private void handleStartDateChange(ActionEvent event)
  {
    if (Tournament != null)
    {
      try
      {
        Tournament.setDate(GUIUtilities.GetDateStringFromDatePicker(StartDatePicker));
      } catch (SQLException ex)
      {
        LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
      }
    }
  }

  @FXML
  private void handleFencingLanesChange(ActionEvent event)
  {
    if (Tournament != null)
    {
      try
      {
        Tournament.setLanes(GUIUtilities.GetIntegerFromStringComboBox(FencingLanesComboBox));
      } catch (SQLException ex)
      {
        LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
      }
    }
  }

  @FXML
  private void handleQualificationsGroupsChange(ActionEvent event)
  {
    if (Tournament != null)
    {
      try
      {
        Tournament.setGroups(GUIUtilities.GetIntegerFromStringComboBox(QualificationGroupsComboBox));
      } catch (SQLException ex)
      {
        LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
      }
    }
  }

  @FXML
  private void handleFinalRoundsChange(ActionEvent event)
  {
    if (Tournament != null)
    {
      try
      {
        Tournament.setFinalRounds(GUIUtilities.GetIntegerFromStringComboBox(FinalRoundsComboBox));
      } catch (SQLException ex)
      {
        LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
      }
    }
  }

  @FXML
  private void handleAddParticipantButton(ActionEvent event)
  {
    if (Tournament != null)
    {
      try
      {
        List<iFencer> participants = Tournament.getAllParticipants();

        if (FencerSelection != null)
        {
          List<FencerPresenter> selectedFencers = new ArrayList<FencerPresenter>();
          selectedFencers.addAll(FencerSelection.GetSelectedFencers());
          for (FencerPresenter fencerPresenter : selectedFencers)
          {
            if (fencerPresenter != null)
            {
              if (!participants.contains(fencerPresenter.getFencer()))
              {
                Tournament.addParticipant(fencerPresenter.getFencer());
              }
            }
          }
          UpdateParticipantsList();
        }
      } catch (SQLException ex)
      {
        LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
      }
    }
  }

  @FXML
  private void handleRemoveParticipantButton(ActionEvent event)
  {
    if (Tournament != null)
    {
      try
      {
        boolean confirmed = true;
        boolean tournamentStarted = GUIUtilities.IsTournamentStarted(Tournament);
        List<iFencer> participants = Tournament.getAllParticipants();
        ObservableList<FencerPresenter> selectedPresenters = ParticipantsTableView.getSelectionModel().getSelectedItems();
        if (selectedPresenters.size() > 0)
        {
          if (tournamentStarted)
          {
            confirmed = GUIUtilities.ShowConfirmationDialog("Ausgewählte Fechter werden aus dem laufenden Turnier ausgeschieden. Forfahren?");
          }
          if (confirmed)
          {
            for (FencerPresenter fencerPresenter : selectedPresenters)
            {
              boolean fencerRemoved = participants.remove(fencerPresenter.getFencer());
              if (fencerRemoved)
              {
                if (tournamentStarted)
                {
                  Tournament.dropOut(fencerPresenter.getFencer());
                } else
                {
                  Tournament.removeParticipant(fencerPresenter.getFencer());
                }
              }
            }
          }
        }
      } catch (SQLException ex)
      {
        LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
      }
      UpdateParticipantsList();
    }
  }
}
