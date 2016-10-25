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

    @FXML
    private AnchorPane MainAnchorPane;
    @FXML
    private TableView GroupTableView;
    @FXML
    private TableColumn FencerNameTableColumn;
    @FXML
    private TableColumn PointsTableColumn;
    @FXML
    private TableColumn WinsTableColumn;
    @FXML
    private TableColumn FencingSchoolTableColumn;
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
        FencerNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        WinsTableColumn.setCellValueFactory(new PropertyValueFactory<>("Wins"));
        PointsTableColumn.setCellValueFactory(new PropertyValueFactory<>("Points"));
        FencingSchoolTableColumn.setCellValueFactory(new PropertyValueFactory<>("FencingSchool"));
    }   
    
    public void SetGroupName(String groupName)
    {
        GroupNameLabel.setText(groupName);
    }
    
    public void SetTournament(iTournament tournament)
    {
        Tournament = tournament;
    }
    
    public void AddFencer(List<iFencer> fencers)
    {
        ObservableList items = GroupTableView.getItems();
        for(iFencer fencer : fencers)
        {
            FencerPresenter fencerPresenter = new FencerPresenter(fencer, Tournament);
            if(!items.contains(fencerPresenter))
            {
                items.add(fencerPresenter);
            }
        }
    }
}
