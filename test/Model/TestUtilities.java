/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Model.Rounds.Preliminary;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.fail;

/**
 *
 * @author Stefan
 */
public class TestUtilities
{

    private static int ID = 0;
    private static int Groups = 4;
    private static int FinalRounds = 2;
    private static int Lanes = 1;
    private static boolean InFinals = false;
    private static String Name = "TournamentName";
    private static String Date = "2017-05-25";

    public static Tournament CreateTournament(int id)
    {
        try
        {
            Map<String, Object> set = new HashMap<String, Object>();
            set.put("ID", id);
            set.put("NAME", Name);
            set.put("DATUM", Date);
            set.put("GRUPPEN", Groups);
            set.put("FINALRUNDEN", FinalRounds);
            set.put("BAHNEN", Lanes);
            set.put("INFINALRUNDEN", InFinals);
            return new Tournament(set);
        }
        catch (Exception ex)
        {
            Logger.getLogger(TournamentTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("failed to create Tournament");
            return null;
        }
    }

    public static Fencer CreateFencer(int id)
    {
        try
        {
            Map<String, Object> set = new HashMap<String, Object>();
            set.put("ID", id);
            set.put("VORNAME", "FechterVorname");
            set.put("NACHNAME", "FechterNachname");
            set.put("GEBURTSTAG", "1986-03-14");
            set.put("FECHTSCHULE", "Fechtschule");
            set.put("NATIONALITÄT", "Deutsch");
            return new Fencer(set);
        }
        catch (Exception ex)
        {
            Logger.getLogger(TournamentTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("failed to create Fencer");
            return null;
        }
    }
}
