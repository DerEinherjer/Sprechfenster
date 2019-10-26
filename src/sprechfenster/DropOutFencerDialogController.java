/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.iFencer;
import model.iTournament;
import sprechfenster.presenter.FencerPresenter;

public class DropOutFencerDialogController implements Initializable
{

  @FXML
  Pane MainDialogPane;
  @FXML
  TableView ParticipantsTableView;
  @FXML
  TableColumn ParticipantColumn;
  @FXML
  Button CancelButton;
  @FXML
  Button DropOutFencerButton;

  iTournament Tournament;

  @Override
  public void initialize(URL url, ResourceBundle rb)
  {
    ParticipantsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    ParticipantColumn.setCellValueFactory(new PropertyValueFactory<>("FullName"));
  }

  public void setTournament(iTournament tournament)
  {
    Tournament = tournament;
    updateParticipantsList();
  }

  @FXML
  private void handleDropOutFencerButtonAction(ActionEvent event)
  {

    if (Tournament != null)
    {
      try
      {
        List<iFencer> participants = Tournament.getAllParticipants();
        ObservableList<FencerPresenter> selectedPresenters = ParticipantsTableView.getSelectionModel().getSelectedItems();
        if (selectedPresenters.size() > 0)
        {
          if (GUIUtilities.IsTournamentStarted(Tournament))
          {
            boolean confirmed = GUIUtilities.ShowConfirmationDialog("Ausgewählte Fechter werden aus dem laufenden Turnier ausgeschieden. Forfahren?");
            if (confirmed)
            {
              for (FencerPresenter fencerPresenter : selectedPresenters)
              {
                boolean fencerRemoved = participants.remove(fencerPresenter.getFencer());
                if (fencerRemoved)
                {
                  Tournament.dropOut(fencerPresenter.getFencer());
                }
              }
            }
          } else
          {
            for (FencerPresenter fencerPresenter : selectedPresenters)
            {
              boolean fencerRemoved = participants.remove(fencerPresenter.getFencer());
              if (fencerRemoved)
              {
                Tournament.removeParticipant(fencerPresenter.getFencer());
              }
            }
          }
        }

      } catch (SQLException ex)
      {
        LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
      }
    }
    closeDialog();
  }

  @FXML
  private void handleCancelButtonAction(ActionEvent event)
  {
    closeDialog();
  }

  private void updateParticipantsList()
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

  private void closeDialog()
  {
    getDialogStage().close();
  }

  private Stage getDialogStage()
  {
    return (Stage) MainDialogPane.getScene().getWindow();
  }
}
