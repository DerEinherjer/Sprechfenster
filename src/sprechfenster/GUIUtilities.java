/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import model.iTournament;

/**
 *
 * @author Stefan
 */
public class GUIUtilities {
    public static boolean IsTournamentStarted(iTournament tournament) throws SQLException
    {
        return (tournament.isPreliminaryFinished() || tournament.getPreliminaryCount() > 0 && tournament.preliminaryWithoutTiming() == 0);
    }
    
    public static boolean ShowConfirmationDialog(String message) {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION,
                message,
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirmationDialog.showAndWait();
        return(result.isPresent() && result.get() == ButtonType.YES);
    }

    public static void FillNumberComboBox(ComboBox box, int startNumber, int maxNumber) {
        ArrayList<String> numbers = new ArrayList<>(maxNumber);
        for (Integer i = startNumber; i <= maxNumber; i++) {
            numbers.add(i.toString());
        }
        box.getItems().clear();
        box.getItems().addAll(numbers);
        if (box.getItems().size() > 0) {
            box.getSelectionModel().select(0);
        }
    }

    static String GetDateStringFromDatePicker(DatePicker picker) {
        return picker.getValue().format(DateTimeFormatter.ISO_DATE);
    }

    public static int GetIntegerFromStringComboBox(ComboBox box) {
        String value = (String) box.getSelectionModel().getSelectedItem();
        return Integer.parseInt(value);
    }

    public static int CompareWinsStrings(String winsString1, String winsString2) {
        try {
            int wins1 = Integer.parseInt(winsString1);
            int wins2 = Integer.parseInt(winsString2);
            return Integer.compare(wins1, wins2);
        } catch (NumberFormatException ex) {
            LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    public static int ComparePointsStrings(String pointsString1, String pointsString2) {
        try {
            String scoredHitsString1 = pointsString1.substring(0, pointsString1.indexOf("/"));
            int scoredHits1 = Integer.parseInt(scoredHitsString1);
            String scoredHitsString2 = pointsString2.substring(0, pointsString2.indexOf("/"));
            int scoredHits2 = Integer.parseInt(scoredHitsString2);
            int result = Integer.compare(scoredHits1, scoredHits2);
            if (result == 0) {
                String receivedHitsString1 = pointsString1.substring(pointsString1.indexOf("/") + 1);
                int receivedHits1 = Integer.parseInt(receivedHitsString1);
                String receivedHitsString2 = pointsString2.substring(pointsString2.indexOf("/") + 1);
                int receivedHits2 = Integer.parseInt(receivedHitsString2);
                result = Integer.compare(receivedHits2, receivedHits1);//inverse order since less received hits is better
            }
            return result;

        } catch (NumberFormatException ex) {
            LoggingUtilities.LOGGER.log(Level.SEVERE, null, ex);
            return 0;
        }
    }
}
