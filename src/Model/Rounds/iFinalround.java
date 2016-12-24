package Model.Rounds;

import Model.ObjectDeprecatedException;
import Model.Rounds.iPreliminary;
import java.sql.SQLException;
import java.util.List;

public interface iFinalround extends iRound
{
    public iFinalround getWinnerRound() throws ObjectDeprecatedException;
    public iFinalround getLoserRound() throws ObjectDeprecatedException;
    public List<iFinalround> getPrerounds() throws ObjectDeprecatedException;
}
