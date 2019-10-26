/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster;

import javafx.util.StringConverter;

/**
 *
 * @author Stefan
 */
public class LimitedIntegerStringConverter extends StringConverter<Integer>
{

  int MaxValue;
  int MinValue;

  public LimitedIntegerStringConverter(int maxValue, int minValue)
  {
    MaxValue = maxValue;
    MinValue = minValue;

  }

  public void setMinAndMaxValues(int maxValue, int minValue)
  {
    MaxValue = maxValue;
    MinValue = minValue;
  }

  @Override
  public String toString(Integer object)
  {
    return Integer.toString(object);
  }

  @Override
  public Integer fromString(String string)
  {
    try
    {
      int parsed = Integer.parseInt(string);
      if (parsed >= MinValue && parsed <= MaxValue)
      {
        return parsed;
      } else
      {
        if (parsed < MinValue)
        {
          return 1;
        } else
        {
          return MaxValue;
        }
      }
    } catch (NumberFormatException e)
    {
      return 1;
    }
  }
}
