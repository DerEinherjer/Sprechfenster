/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

/**
 *
 * @author Stefan
 */
public class GUIUtilities {

  public static void FillNumberComboBox (ComboBox box, int startNumber, int maxNumber) {
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

  static String GetDateStringFromDatePicker (DatePicker picker) {
    return picker.getValue().format(DateTimeFormatter.ISO_DATE);
  }

  public static int GetIntegerFromStringComboBox (ComboBox box) {
    String value = (String) box.getSelectionModel().getSelectedItem();
    return Integer.parseInt(value);
  }
}
