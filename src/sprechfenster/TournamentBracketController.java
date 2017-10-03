/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import model.ObjectDeprecatedException;
import model.iFencer;
import model.iTournament;
import model.rounds.iRound;

/**
 * FXML Controller class
 *
 * @author Stefan
 */
public class TournamentBracketController implements Initializable {

    @FXML
    private AnchorPane BracketAnchorPane;
    @FXML
    private ComboBox<String> FirstFencerComboBox;
    @FXML
    private ComboBox<String> SecondFencerComboBox;
    @FXML
    private ComboBox<String> FirstFencerPointsComboBox;
    @FXML
    private ComboBox<String> SecondFencerPointsComboBox;
    @FXML
    private CheckBox FightFinishedCheckBox;
    @FXML
    private ComboBox<String> LaneComboBox;
    @FXML
    private Label LaneLabel;

    private iRound Fight;
    private iTournament Tournament;
    private ArrayList<String> FencerNames;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        GUIUtilities.FillNumberComboBox(LaneComboBox, 1, 20);
        GUIUtilities.FillNumberComboBox(FirstFencerPointsComboBox, 0, 30);
        GUIUtilities.FillNumberComboBox(SecondFencerPointsComboBox, 0, 30);
    }

    public void SetData(iRound fight, iTournament tournament, ArrayList<String> fencerNames) {
        Fight = fight;
        Tournament = tournament;
        FencerNames = fencerNames;
        updateData();
    }

    private void updateData() {
        if (Fight != null && Tournament != null) {
            try {
                iFencer firstFencer = Fight.getFencer().get(0);
                iFencer secondFencer = Fight.getFencer().get(1);
                FirstFencerComboBox.getItems().setAll(FencerNames);
                SecondFencerComboBox.getItems().setAll(FencerNames);
                for (String name : FencerNames) {
                    if (name.equals(firstFencer.getFullName())) {
                        FirstFencerComboBox.getSelectionModel().select(name);
                    }
                    if (name.equals(secondFencer.getFullName())) {
                        SecondFencerComboBox.getSelectionModel().select(name);
                    }
                }
                GUIUtilities.FillNumberComboBox(LaneComboBox, 1, Tournament.getLanes());
                LaneComboBox.getSelectionModel().select(Fight.getLane() - 1);
                FirstFencerPointsComboBox.getSelectionModel().select(Fight.getPoints(Fight.getFencer().get(0)));
                SecondFencerPointsComboBox.getSelectionModel().select(Fight.getPoints(Fight.getFencer().get(1)));
                FightFinishedCheckBox.setSelected(Fight.isFinished());
            } catch (ObjectDeprecatedException ex) {
                LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }

}
