/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Fencer;
import model.Tournament;
import model.iFencer;
import model.iTournament;
import sprechfenster.presenter.FencerPresenter;
import sprechfenster.presenter.TournamentPresenter;

/**
 *
 * @author Stefan
 */
public class MainFXMLController implements Initializable, iFencerSelection, Observer
{

  @FXML
  AnchorPane MainAnchorPane;
  @FXML
  AnchorPane MenuAnchorPane;
  @FXML
  AnchorPane ContentAnchorPane;
  @FXML
  SplitPane ContentSplitPane;
  @FXML
  AnchorPane LeftContentAnchorPane;
  @FXML
  AnchorPane RightContentAnchorPane;
  @FXML
  ToolBar MainToolBar;

  @FXML
  Button OverviewButton;
  @FXML
  Button NewTournamentButton;
  @FXML
  Button LoadTournamentButton;
  @FXML
  Button NewFencerButton;
  @FXML
  Button ShowParticipantsButton;
  @FXML
  Button ShowGroupsButton;
  @FXML
  Button ShowFinalRoundsButton;
  @FXML
  Button DeleteFencerButton;
  @FXML
  Button DropOutFencerButton;
  @FXML
  Button DeleteTournamentButton;

  @FXML
  TableView<TournamentPresenter> TournamentTableView;
  @FXML
  TableColumn TournamentColumn;
  @FXML
  TableColumn DateColumn;
  @FXML
  TableColumn ParticipantColumn;
  @FXML
  TableColumn TournamentFightsColumn;

  @FXML
  TableView<FencerPresenter> FencerTableView;
  @FXML
  TableColumn FencerColumn;
  @FXML
  TableColumn PortraitColumn;
  @FXML
  TableColumn FencingSchoolColumn;
  @FXML
  TableColumn FencerFightsColumn;
  @FXML
  TableColumn AgeColumn;

  private TournamentParticipantsController ParticipantsController;
  private TournamentQualificationPhaseController QualificationPhaseController;
  private TournamentEliminationPhaseController EliminationPhaseController;
  private iTournament ActiveTournament;

  @Override
  public ObservableList<FencerPresenter> GetSelectedFencers()
  {
    return FencerTableView.getSelectionModel().getSelectedItems();
  }

  @FXML
  private void handleSummaryButtonAction(ActionEvent event)
  {
    SetupOverview();
    UpdateOverview();
  }

  @FXML
  private void handleNewTournamentButtonAction(ActionEvent event)
  {
    Parent root;
    try
    {
      root = FXMLLoader.load(getClass().getClassLoader().getResource("sprechfenster/resources/fxml/NewTournamentDialog.fxml"));
      Stage stage = new Stage();
      stage.setTitle("Neues Turnier");
      stage.setScene(new Scene(root));
      stage.initOwner(NewTournamentButton.getScene().getWindow());
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.showAndWait();
      UpdateOverview();
    } catch (IOException e)
    {
      LoggingUtilities.LOGGER.log(Level.SEVERE, null, e);
    }
  }

  @FXML
  private void handleLoadTournamentButtonAction(ActionEvent event)
  {
    TournamentPresenter tournamentPresenter = (TournamentPresenter) TournamentTableView.getSelectionModel().getSelectedItem();
    iTournament tournament = null;
    if (tournamentPresenter == null)
    {
      ObservableList items = TournamentTableView.getItems();
      if (items.size() > 0)
      {
        tournamentPresenter = (TournamentPresenter) items.get(0);
      }
    }
    if (tournamentPresenter != null)
    {
      tournament = tournamentPresenter.getTournament();
      SetupTournamentParticipants(tournament);
    }
  }

  @FXML
  private void FencerTableViewOnDragDetected(MouseEvent event)
  {
    Dragboard db = FencerTableView.startDragAndDrop(TransferMode.ANY);
    StringBuilder fencerIdsBuilder = new StringBuilder();
    fencerIdsBuilder.append("FencerIDs;");
    for (FencerPresenter presenter : GetSelectedFencers())
    {
      fencerIdsBuilder.append(presenter.getFencer().getID());
      fencerIdsBuilder.append(';');
    }
    ClipboardContent draggedContent = new ClipboardContent();
    draggedContent.putString(fencerIdsBuilder.toString());
    db.setContent(draggedContent);
    event.consume();
  }

  private void SetupOverview()
  {
    ContentAnchorPane.getChildren().clear();
    ContentAnchorPane.getChildren().add(ContentSplitPane);
    LeftContentAnchorPane.getChildren().clear();
    LeftContentAnchorPane.getChildren().add(TournamentTableView);
    RightContentAnchorPane.getChildren().clear();
    RightContentAnchorPane.getChildren().add(FencerTableView);
    ObservableList<Node> items = MainToolBar.getItems();
    items.clear();
    items.add(OverviewButton);
    items.add(NewTournamentButton);
    items.add(LoadTournamentButton);
    items.add(NewFencerButton);
    items.add(DeleteFencerButton);
    items.add(DeleteTournamentButton);
    ActiveTournament = null;
    if (EliminationPhaseController != null)
    {
      EliminationPhaseController.SetTournament(null);
    }
    if (QualificationPhaseController != null)
    {
      QualificationPhaseController.SetTournament(null);
    }
  }

  private void SetupTournamentParticipants(iTournament tournament)
  {
    if (tournament != null)
    {
      try
      {
        ContentAnchorPane.getChildren().clear();
        ContentAnchorPane.getChildren().add(ContentSplitPane);
        LeftContentAnchorPane.getChildren().clear();

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("sprechfenster/resources/fxml/TournamentParticipants.fxml"));
        Node tournamentPlanningView = loader.load();
        ParticipantsController = loader.<TournamentParticipantsController>getController();
        ParticipantsController.setFencerSelectionInterface(this);
        ParticipantsController.setTournament(tournament);
        LeftContentAnchorPane.getChildren().add(tournamentPlanningView);
        ActiveTournament = tournament;
        SetupToolbarForActiveTournament();
      } catch (IOException ex)
      {
        LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
      }

    }
  }

  private void SetupTournamentQualificationPhase(iTournament tournament)
  {
    if (tournament != null)
    {
      try
      {
        ContentAnchorPane.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("sprechfenster/resources/fxml/QualificationPhase.fxml"));
        Node tournamentQualificationPhaseView = loader.load();
        ContentAnchorPane.getChildren().add(tournamentQualificationPhaseView);
        QualificationPhaseController = loader.<TournamentQualificationPhaseController>getController();
        QualificationPhaseController.SetTournament(tournament);
        SetupToolbarForActiveTournament();
      } catch (IOException ex)
      {
        LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
      }
    }
  }

  private void SetupTournamentFinalEliminationPhase(iTournament tournament)
  {
    if (tournament != null)
    {
      try
      {
        ContentAnchorPane.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("sprechfenster/resources/fxml/EliminiationPhase.fxml"));
        Node tournamentFinalEliminationPhaseView = loader.load();
        ContentAnchorPane.getChildren().add(tournamentFinalEliminationPhaseView);
        EliminationPhaseController = loader.<TournamentEliminationPhaseController>getController();
        EliminationPhaseController.SetTournament(tournament);
        SetupToolbarForActiveTournament();
      } catch (IOException ex)
      {
        LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
      }
    }
  }

  private void SetupToolbarForActiveTournament()
  {
    ObservableList<Node> items = MainToolBar.getItems();
    items.clear();
    items.add(OverviewButton);
    items.add(NewFencerButton);
    items.add(ShowParticipantsButton);
    items.add(ShowGroupsButton);
    items.add(ShowFinalRoundsButton);
    items.add(DropOutFencerButton);
  }

  @FXML
  private void handleNewFencerButtonAction(ActionEvent event)
  {
    Parent root;
    try
    {
      root = FXMLLoader.load(getClass().getClassLoader().getResource("sprechfenster/resources/fxml/NewFencerDialog.fxml"));
      Stage stage = new Stage();
      stage.setTitle("Neuer Fechter");
      stage.setScene(new Scene(root));
      stage.initOwner(NewFencerButton.getScene().getWindow());
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.showAndWait();
      UpdateOverview();
    } catch (IOException e)
    {
      LoggingUtilities.LOGGER.log(Level.SEVERE, null, e);
    }
  }

  @FXML
  private void handleDropOutFencerButtonAction(ActionEvent event)
  {
    Parent root;
    try
    {
      FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("sprechfenster/resources/fxml/DropOutFencerDialog.fxml"));
      root = loader.load();
      DropOutFencerDialogController controller = loader.<DropOutFencerDialogController>getController();
      controller.setTournament(ActiveTournament);
      Stage stage = new Stage();
      stage.setTitle("Fechter ausscheiden");
      stage.setScene(new Scene(root));
      stage.initOwner(DropOutFencerButton.getScene().getWindow());
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.showAndWait();
      UpdateOverview();
    } catch (IOException e)
    {
      LoggingUtilities.LOGGER.log(Level.SEVERE, null, e);
    }
  }

  @FXML
  private void handleDeleteTournamentButtonAction(ActionEvent event)
  {
    boolean confirmed = GUIUtilities.ShowConfirmationDialog("Sollen die ausgewählten Turniere wirklich gelöscht werden?");
    if (confirmed)
    {
      for (TournamentPresenter presenter : TournamentTableView.getSelectionModel().getSelectedItems())
      {
        try
        {
          presenter.getTournament().delete();
        } catch (SQLException e)
        {
          LoggingUtilities.LOGGER.log(Level.SEVERE, null, e);
        }
      }
      TournamentTableView.getItems().removeAll(TournamentTableView.getSelectionModel().getSelectedItems());
    }
  }

  @FXML
  private void handleDeleteFencerButtonAction(ActionEvent event)
  {
    boolean confirmed = GUIUtilities.ShowConfirmationDialog("Sollen die ausgewählten Fechter wirklich gelöscht werden?");
    if (confirmed)
    {
      for (FencerPresenter presenter : FencerTableView.getSelectionModel().getSelectedItems())
      {
        try
        {
          presenter.getFencer().delete();
        } catch (SQLException e)
        {
          LoggingUtilities.LOGGER.log(Level.SEVERE, null, e);
        }
      }
      FencerTableView.getItems().removeAll(FencerTableView.getSelectionModel().getSelectedItems());
    }
  }

  @FXML
  private void handleShowGroupsButtonAction(ActionEvent event)
  {
    SetupTournamentQualificationPhase(ActiveTournament);
  }

  @FXML
  private void handleShowFinalRoundsButtonAction(ActionEvent event)
  {
    SetupTournamentFinalEliminationPhase(ActiveTournament);
  }

  @FXML
  private void handleShowParticipantsButtonAction(ActionEvent event)
  {
    SetupTournamentParticipants(ActiveTournament);
  }

  @FXML
  private void handleUpdateAllAction(ActionEvent event)
  {
    UpdateOverview();
    if (ParticipantsController != null)
    {
      ParticipantsController.update(null, this);
    }
    if (QualificationPhaseController != null)
    {
      QualificationPhaseController.update(null, this);
    }
    if (EliminationPhaseController != null)
    {
      EliminationPhaseController.update(null, this);
    }
  }

  @Override
  public void initialize(URL url, ResourceBundle rb)
  {
    FencerColumn.setCellValueFactory(new PropertyValueFactory<>("FullName"));
    FencingSchoolColumn.setCellValueFactory(new PropertyValueFactory<>("FencingSchool"));
    AgeColumn.setCellValueFactory(new PropertyValueFactory<>("Age"));

    TournamentColumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
    DateColumn.setCellValueFactory(new PropertyValueFactory<>("Date"));

    FencerTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    DeleteTournamentButton.setVisible(false);//Deleting tournaments from the database causes a crash the next time the program is started. Hide the button for now.
    SetupOverview();
    UpdateOverview();

  }

  @Override
  public void update(Observable o, Object o1)
  {
    UpdateOverview();
  }

  private void UpdateOverview()
  {
    List<TournamentPresenter> tournaments = new ArrayList<>();
    List<FencerPresenter> fencers = new ArrayList<>();
    for (iFencer fencer : Fencer.getAllFencer())
    {
      fencers.add(new FencerPresenter(fencer, null));
    }
    for (iTournament tournament : Tournament.getAllTournaments())
    {
      tournaments.add(new TournamentPresenter(tournament));
    }
    FencerTableView.getItems().setAll(fencers);
    TournamentTableView.getItems().setAll(tournaments);
  }
}
