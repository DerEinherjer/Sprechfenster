/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster;

import Model.iFencer;
import Model.iTournament;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import sprechfenster.Presenters.FencerPresenter;

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
    private AnchorPane MainAnchorPane;
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
    private Label GroupNameLabel;
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
        MainAnchorPane.minHeightProperty().bind(GroupTableView.prefHeightProperty().add(50));
        MainAnchorPane.maxHeightProperty().bind(GroupTableView.prefHeightProperty().add(50));
    }   
    
    public void SetGroupName(String groupName)
    {
        GroupNameLabel.setText(groupName);
    }
    
    public void SetTournament(iTournament tournament)
    {
        Tournament = tournament;
    }
    
    public void SetPhase(TournamentPhase phase)
    {
        Phase = phase;
        if(Phase == TournamentPhase.FinalPhase)
        {
            WinsTableColumn.setCellValueFactory(new PropertyValueFactory<>("FinalRoundWins"));
            PointsTableColumn.setCellValueFactory(new PropertyValueFactory<>("FinalRoundScore"));
        }
        else
        {
            WinsTableColumn.setCellValueFactory(new PropertyValueFactory<>("QualificationRoundWins"));
            PointsTableColumn.setCellValueFactory(new PropertyValueFactory<>("QualificationRoundPoints"));
        }
    }
    
    public void AddFencer(List<iFencer> fencers)
    {
        ObservableList items = GroupTableView.getItems();
        for(iFencer fencer : fencers)
        {
            FencerPresenter fencerPresenter = new FencerPresenter(fencer, Tournament);
            if(items.filtered( presenter -> ((FencerPresenter)presenter).getFencer() == fencer).isEmpty())
            {
                items.add(fencerPresenter);
            }
        }
    }
}
