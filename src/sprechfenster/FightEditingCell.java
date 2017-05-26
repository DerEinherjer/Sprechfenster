/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import sprechfenster.presenter.FightPresenter;

/**
 *
 * @author Stefan
 */
public class FightEditingCell extends TableCell<FightPresenter, Integer> {

  private TextField TextField;
  private StringConverter<Integer> StringConverter;

  public FightEditingCell (StringConverter<Integer> converter) {
    StringConverter = converter;
  }

  @Override
  public void startEdit () {
    if (!isEmpty()) {
      super.startEdit();
      createTextField();
      setText(null);
      setGraphic(TextField);
      TextField.selectAll();
    }
  }

  @Override
  public void cancelEdit () {
    super.cancelEdit();

    setText(StringConverter.toString(getItem()));
    setGraphic(null);
  }

  @Override
  public void updateItem (Integer item, boolean empty) {
    super.updateItem(item, empty);

    if (empty) {
      setText(null);
      setGraphic(null);
    }
    else {
      if (isEditing()) {
        if (TextField != null) {
          TextField.setText(getString());
        }
        setText(null);
        setGraphic(TextField);
      }
      else {
        setText(getString());
        setGraphic(null);
      }
    }
  }

  private void createTextField () {
    TextField = new TextField(getString());
    TextField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
    TextField.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0,
            Boolean arg1, Boolean arg2) -> {
      if (!arg2) {
        commitEdit(StringConverter.fromString(TextField.getText()));
      }
    });
  }

  private String getString () {
    return getItem() == null ? "" : getItem().toString();
  }
}
