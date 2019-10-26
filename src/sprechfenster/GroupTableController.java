/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster;

import model.iFencer;
import model.iTournament;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import sprechfenster.presenter.FencerPresenter;

/**
 * FXML Controller class
 *
 * @author Stefan
 */
public class GroupTableController implements Initializable
{

  public enum TournamentPhase
  {
    Invalid,
    FinalPhase,
    QualificationPhase
  }

  private TournamentPhase Phase = TournamentPhase.QualificationPhase;
  @FXML
  private TitledPane MainPane;
  @FXML
  private TableView<FencerPresenter> GroupTableView;
  @FXML
  private TableColumn<FencerPresenter, String> FencerNameTableColumn;
  @FXML
  private TableColumn<FencerPresenter, String> PointsTableColumn;
  @FXML
  private TableColumn<FencerPresenter, String> WinsTableColumn;
  @FXML
  private TableColumn<FencerPresenter, String> FencingSchoolTableColumn;
  @FXML
  private iTournament Tournament;

  /**
   * Initializes the controller class.
   */
  @Override
  public void initialize(URL url, ResourceBundle rb)
  {
    FencerNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("FullName"));
    WinsTableColumn.setCellValueFactory(new PropertyValueFactory<>("QualificationRoundWins"));
    PointsTableColumn.setCellValueFactory(new PropertyValueFactory<>("QualificationRoundPoints"));
    FencingSchoolTableColumn.setCellValueFactory(new PropertyValueFactory<>("FencingSchool"));
    GroupTableView.setFixedCellSize(25);
    GroupTableView.prefHeightProperty().bind(GroupTableView.fixedCellSizeProperty().multiply(Bindings.size(GroupTableView.getItems()).add(1.01)));
    WinsTableColumn.setComparator(GUIUtilities::CompareWinsStrings);
    PointsTableColumn.setComparator(GUIUtilities::ComparePointsStrings);
  }

  public void SetGroupName(String groupName)
  {
    MainPane.setText(groupName);
  }

  public void SetTournament(iTournament tournament)
  {
    Tournament = tournament;
  }

  public void SetPhase(TournamentPhase phase)
  {
    Phase = phase;
    if (Phase == TournamentPhase.FinalPhase)
    {
      WinsTableColumn.setCellValueFactory(new PropertyValueFactory<>("FinalRoundWins"));
      PointsTableColumn.setCellValueFactory(new PropertyValueFactory<>("FinalRoundScore"));
    } else
    {
      WinsTableColumn.setCellValueFactory(new PropertyValueFactory<>("QualificationRoundWins"));
      PointsTableColumn.setCellValueFactory(new PropertyValueFactory<>("QualificationRoundPoints"));
    }
  }

  public void AddFencers(List<iFencer> fencers)
  {
    ObservableList items = GroupTableView.getItems();
    for (iFencer fencer : fencers)
    {
      FencerPresenter fencerPresenter = new FencerPresenter(fencer, Tournament);
      if (items.filtered(presenter -> ((FencerPresenter) presenter).getFencer() == fencer).isEmpty())
      {
        items.add(fencerPresenter);
      }
    }
    GroupTableView.getSortOrder().clear();
    WinsTableColumn.setSortType(TableColumn.SortType.DESCENDING);
    PointsTableColumn.setSortType(TableColumn.SortType.DESCENDING);
    GroupTableView.getSortOrder().add(WinsTableColumn);
    GroupTableView.getSortOrder().add(PointsTableColumn);
  }
}
