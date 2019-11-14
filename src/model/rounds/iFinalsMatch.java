package model.rounds;

import java.util.List;
import model.ObjectDeprecatedException;

public interface iFinalsMatch extends iMatch
{

  public iFinalsMatch getWinnerMatch() throws ObjectDeprecatedException;

  public iFinalsMatch getLoserMatch() throws ObjectDeprecatedException;

  public List<iFinalsMatch> getPreviousMatches() throws ObjectDeprecatedException;

  /**
   * This function returns the final round the match is part of (quarterfinal; semifinal; final) If it is the match for the 3. place it will return -1.
   *
   * @return the number of the round
   */
  public int getFinalRound();
}
