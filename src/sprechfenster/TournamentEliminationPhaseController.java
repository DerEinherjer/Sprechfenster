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
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.ObjectDeprecatedException;
import model.iFencer;
import model.iTournament;
import model.rounds.iFinalsMatch;
import sprechfenster.presenter.FightPresenter;

/**
 * FXML Controller class
 *
 * @author Stefan
 */
public class TournamentEliminationPhaseController implements Initializable, Observer
{

  @FXML
  Button CreateEliminationRoundsButton;
  @FXML
  Button AbortEliminationRoundsButton;
  @FXML
  FlowPane FencersPane;
  @FXML
  Tab BracketTab;
  @FXML
  Tab TableTab;
  @FXML
  HBox BracketViewHBox;

  @FXML
  TableView<FightPresenter> FightsTableView;
  @FXML
  TableColumn<FightPresenter, Integer> RoundTableColumn;
  @FXML
  TableColumn<FightPresenter, Integer> LaneTableColumn;
  @FXML
  TableColumn<FightPresenter, String> FirstFencerTableColumn;
  @FXML
  TableColumn<FightPresenter, Integer> FirstFencerPointsTableColumn;
  @FXML
  TableColumn<FightPresenter, Integer> SecondFencerPointsTableColumn;
  @FXML
  TableColumn<FightPresenter, String> SecondFencerTableColumn;
  @FXML
  TableColumn EditTableColumn;
  @FXML
  TableColumn<FightPresenter, Boolean> FinishedTableColumn;

  private iTournament Tournament;
  private ArrayList<ArrayList<iFinalsMatch>> RoundToFights;
  private ArrayList<String> FencerNames;

  private final ArrayList<TournamentBracketController> BracketControllers = new ArrayList<TournamentBracketController>();
  private final LimitedIntegerStringConverter StringToRoundNumber = new LimitedIntegerStringConverter(1, 1);
  private final LimitedIntegerStringConverter StringToLaneNumber = new LimitedIntegerStringConverter(1, 1);
  private final LimitedIntegerStringConverter StringToPointsConverter = new LimitedIntegerStringConverter(Integer.MAX_VALUE, 0);

  /**
   * Initializes the controller class.
   */
  @Override
  public void initialize(URL url, ResourceBundle rb)
  {
    InitializeTableTab();
  }

  private void InitializeTableTab()
  {
    RoundTableColumn.setCellValueFactory(new PropertyValueFactory<>("Round"));
    RoundTableColumn.setCellFactory(TextFieldTableCell.forTableColumn(StringToRoundNumber));
    LaneTableColumn.setCellValueFactory(new PropertyValueFactory<>("Lane"));
    LaneTableColumn.setCellFactory(TextFieldTableCell.forTableColumn(StringToLaneNumber));
    FirstFencerTableColumn.setCellValueFactory(new PropertyValueFactory<>("FirstFencerName"));
    FirstFencerPointsTableColumn.setCellValueFactory(new PropertyValueFactory<>("FirstFencerPoints"));
    FirstFencerPointsTableColumn.setCellFactory(TextFieldTableCell.forTableColumn(StringToPointsConverter));
    SecondFencerTableColumn.setCellValueFactory(new PropertyValueFactory<>("SecondFencerName"));
    SecondFencerPointsTableColumn.setCellValueFactory(new PropertyValueFactory<>("SecondFencerPoints"));
    SecondFencerPointsTableColumn.setCellFactory(TextFieldTableCell.forTableColumn(StringToPointsConverter));
    FinishedTableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(FinishedTableColumn));
    FinishedTableColumn.setCellValueFactory(new PropertyValueFactory<>("Finished"));
    EditTableColumn.setCellValueFactory(new PropertyValueFactory<>("Finished"));

    Callback<TableColumn<FightPresenter, Boolean>, TableCell<FightPresenter, Boolean>> editCellFactory
            = //
            (final TableColumn<FightPresenter, Boolean> param)
            ->
    {
      final TableCell<FightPresenter, Boolean> cell = new TableCell<FightPresenter, Boolean>()
      {
        final Button EditButton = new Button("Ändern");

        @Override
        public void updateItem(Boolean item, boolean empty)
        {
          super.updateItem(item, empty);
          if (empty)
          {
            setGraphic(null);
            setText(null);
          } else
          {
            EditButton.setOnAction((ActionEvent event)
                    ->
            {
              try
              {
                FightPresenter fight = getTableView().getItems().get(getIndex());
                Parent dialog = GUIUtilities.LoadAsParent(this.getClass(), "EditFightDialog.fxml");
                EditFightDialogController controller = GUIUtilities.GetLoader().<EditFightDialogController>getController();
                controller.SetData(fight.getFight(), Tournament, FencerNames);
                Stage stage = new Stage();
                stage.setTitle("Gefecht bearbeiten");
                stage.setScene(new Scene(dialog));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(EditButton.getScene().getWindow());
                stage.showAndWait();
              } catch (IOException ex)
              {
                LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
              }

            });
            setGraphic(EditButton);
            setText(null);
          }
        }
      };
      return cell;
    };
    EditTableColumn.setCellFactory(editCellFactory);
  }

  public void SetTournament(iTournament tournament)
  {
    Tournament = tournament;
    FencerNames = new ArrayList<String>();
    if (Tournament != null)
    {
      try
      {
        for (iFencer fencer : Tournament.getAllParticipants())
        {
          FencerNames.add(fencer.getFullName());
        }
      } catch (SQLException ex)
      {
        Logger.getLogger(TournamentEliminationPhaseController.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    updateAll();

  }

  private void updateRounds()
  {
    RoundToFights = new ArrayList<>();
    if (Tournament != null)
    {
      for (int i = 0; i < Tournament.getFinalRounds(); i++)
      {
        ArrayList<iFinalsMatch> fightsForRound = new ArrayList<>();
        RoundToFights.add(fightsForRound);
      }
      for (iFinalsMatch round : Tournament.getAllFinalsMatches())
      {
        int roundNumber = round.getFinalRound();
        if (roundNumber == -1)
        {
          roundNumber = Tournament.getFinalRounds();
        }
        RoundToFights.get(roundNumber - 1).add(round);
      }
    }
  }

  @FXML
  private void handleCreateEliminationRoundsButtonAction(ActionEvent event)
  {
    if (Tournament != null)
    {
      try
      {
        boolean confirmed = GUIUtilities.ShowConfirmationDialog("Die Vorrunden können nicht mehr verändert werden, wenn das Finale begonnen wird. Fortfahren?");
        if (confirmed)
        {
          Tournament.startFinalsPhase();
          updateAll();
        }
      } catch (SQLException | ObjectDeprecatedException ex)
      {
        LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
        System.out.println("exc2");
      }
    }
  }

  @FXML
  private void handleAbortEliminationRoundsButtonAction(ActionEvent event)
  {
    if (Tournament != null)
    {
      try
      {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION,
                "Alle bisherigen Finalrunden-Ergebnisse werden gelöscht. Fortfahren?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES)
        {
          Tournament.abortFinalsPhase();
          updateAll();
        }
      } catch (SQLException ex)
      {
        LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
      }
    }
  }

  private void updateAll()
  {
    updateRounds();
    updateTableTabData();
    createBracketTabData();
  }

  private void updateTableTabData()
  {
    FightsTableView.getItems().clear();
    FencersPane.getChildren().clear();
    if (Tournament != null)
    {
      try
      {
        Node finalRoundScoreGroupTable = GUIUtilities.LoadAsNode(this.getClass(), "GroupTable.fxml");
        GroupTableController finalRoundsScoreController = GUIUtilities.GetLoader().<GroupTableController>getController();
        finalRoundsScoreController.SetGroupName("Finalrundenergebnisse");
        finalRoundsScoreController.SetTournament(Tournament);
        finalRoundsScoreController.SetPhase(GroupTableController.TournamentPhase.FinalPhase);
        FencersPane.getChildren().add(finalRoundScoreGroupTable);
        finalRoundsScoreController.AddFencers(Tournament.getAllParticipants());
        Node preliminaryRoundScoreGroupTable = GUIUtilities.LoadAsNode(this.getClass(), "GroupTable.fxml");
        GroupTableController preliminaryRoundsScoreController = GUIUtilities.GetLoader().<GroupTableController>getController();
        preliminaryRoundsScoreController.SetGroupName("Vorrundenergebnisse");
        preliminaryRoundsScoreController.SetTournament(Tournament);
        preliminaryRoundsScoreController.SetPhase(GroupTableController.TournamentPhase.QualificationPhase);
        preliminaryRoundsScoreController.AddFencers(Tournament.getAllParticipants());
        FencersPane.getChildren().add(preliminaryRoundScoreGroupTable);

        StringToLaneNumber.setMinAndMaxValues(Tournament.getLanes(), 1);
        CreateEliminationRoundsButton.setDisable(!Tournament.areAllQualificationMatchesFinished());
        AbortEliminationRoundsButton.setDisable(Tournament.isFinalsPhase());
        int maxRound = 0;
        for (iFinalsMatch finalRound : Tournament.getAllFinalsMatches())
        {
          maxRound = Math.max(maxRound, finalRound.getRound());
          if (finalRound.getFencer().size() == 2)
          {
            FightsTableView.getItems().add(new FightPresenter(finalRound, Tournament));
          }
        }
        StringToRoundNumber.setMinAndMaxValues(maxRound, 1);
      } catch (IOException | SQLException | ObjectDeprecatedException ex)
      {
        LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
      }
    }
  }

  private void createBracketTabData()
  {
    for (TournamentBracketController controller : BracketControllers)
    {
      controller.deleteObserver(this);
    }
    BracketControllers.clear();
    BracketViewHBox.getChildren().clear();
    if (Tournament != null)
    {
      final int finalRounds = Tournament.getFinalRounds();
      int round = 0;
      boolean isLastRound;
      boolean firstPlaceMatch = false;
      int spacingBetweenBrackets = 1;
      int spacingForFirstBracket = 0;
      for (int i = finalRounds - 1; i >= 0; i--)
      {
        round++;
        isLastRound = round == finalRounds;
        VBox roundMatchesBox = new VBox(0);
        roundMatchesBox.fillWidthProperty().setValue(false);
        Label roundTitle = new Label("Runde " + round);
        roundMatchesBox.getChildren().add(roundTitle);
        int numberOfMatchesInRound = (int) Math.pow(2, i);
        if (isLastRound)
        {
          //add one match for the third place
          numberOfMatchesInRound++;
        }
        for (int j = 0; j < numberOfMatchesInRound; j++)
        {
          try
          {
            if (j == 0)
            {
              //initial spacing from the top, for the first match only
              int numberOfDummys = spacingForFirstBracket;
              for (int x = 0; x < numberOfDummys; x++)
              {
                addVerticalSpacer(roundMatchesBox);
              }
              if (isLastRound)
              {
                Label matchTitle = new Label("Finale");
                roundMatchesBox.getChildren().add(matchTitle);
                firstPlaceMatch = true;
              }
            } else
            {
              if (isLastRound)
              {
                //Small fixed spacing between final match and match for third place
                addVerticalSpacer(roundMatchesBox);
                Label matchTitle = new Label("3. Platz");
                roundMatchesBox.getChildren().add(matchTitle);
                firstPlaceMatch = false;
              } else
              {
                //apply spacing between two matches
                for (int x = 0; x < spacingBetweenBrackets; x++)
                {
                  addVerticalSpacer(roundMatchesBox);
                }
              }
            }
            iFinalsMatch fight = null;
            int roundIndex = round - 1;
            if (roundIndex < RoundToFights.size() && j < RoundToFights.get(roundIndex).size())
            {
              if (isLastRound)
              {
                //special handling for last round: make sure the final match appears under the correct label,
                //and the third place match appears below it (also under its own label)
                for (iFinalsMatch match : RoundToFights.get(roundIndex))
                {
                  if (firstPlaceMatch && match.getFinalRound() != -1)
                  {
                    //first place match has a final round != -1
                    fight = match;
                  } else
                  {
                    if (!firstPlaceMatch && match.getFinalRound() == -1)
                    {
                      //third place match has final round == -1
                      fight = match;
                    }
                  }
                }
              } else
              {
                fight = RoundToFights.get(roundIndex).get(j);
              }
            }
            TournamentBracketController bracketController = addBracketForFight(fight, roundMatchesBox);
            BracketControllers.add(bracketController);
            bracketController.addObserver(this);
          } catch (IOException e)
          {
            LoggingUtilities.LOGGER.log(Level.SEVERE, null, e);
          }
        }
        BracketViewHBox.getChildren().add(roundMatchesBox);
        Separator spacer = new Separator();
        spacer.setOrientation(Orientation.VERTICAL);
        BracketViewHBox.getChildren().add(spacer);
        spacingForFirstBracket = spacingForFirstBracket * 2 + 1;
        spacingBetweenBrackets = spacingBetweenBrackets * 2 + 1;
      }
    }
  }

  private TournamentBracketController addBracketForFight(iFinalsMatch fight, VBox matchesBox) throws IOException
  {
    //load the control for the next match and display it
    Node bracket = GUIUtilities.LoadAsNode(this.getClass(), "TournamentBracket.fxml");
    TournamentBracketController bracketController = GUIUtilities.GetLoader().<TournamentBracketController>getController();
    bracketController.SetData(fight, Tournament, FencerNames);
    matchesBox.getChildren().add(bracket);
    return bracketController;
  }

  private void addVerticalSpacer(VBox box) throws IOException
  {
    Separator spacer = new Separator();
    spacer.setOrientation(Orientation.VERTICAL);
    spacer.setMinHeight(116);
    spacer.setMaxHeight(116);
    spacer.setVisible(false);
    box.getChildren().add(spacer);
  }

  @Override
  public void update(Observable o, Object o1)
  {
    updateTableTabData();
    createBracketTabData();
  }

}
