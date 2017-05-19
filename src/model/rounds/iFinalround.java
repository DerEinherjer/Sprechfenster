package model.rounds;

import model.ObjectDeprecatedException;
import model.rounds.iPreliminary;
import java.sql.SQLException;
import java.util.List;

public interface iFinalround extends iRound
{
    public iFinalround getWinnerRound() throws ObjectDeprecatedException;
    public iFinalround getLoserRound() throws ObjectDeprecatedException;
    public List<iFinalround> getPrerounds() throws ObjectDeprecatedException;
    
    /**
    * This function returns the final round the match is part of (quarterfinal; semifinal; final)
    * If it is the match for the 3. place it will return -1.
    * @return the number of the round
    */
    public int getFinalRound();
}
