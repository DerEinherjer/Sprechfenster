/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Model.Rounds.Finalround;
import Model.Rounds.Preliminary;
import Model.Rounds.iFinalround;
import Model.Rounds.iPreliminary;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Stefan
 */
public class TournamentTest
{
    private static String oldDatabaseURL;

    public TournamentTest()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
        //Sync object must be created already and we need to setup database restore point
        Sync sync = (Sync) Sync.getInstance();
        oldDatabaseURL = sync.getDatabaseURL();
        sync.setDatabaseURL("jdbc:h2:~/SprechFensterTestDatabase");
    }

    @AfterClass
    public static void tearDownClass()
    {
        Sync sync = (Sync) Sync.getInstance();
        sync.setDatabaseURL(oldDatabaseURL);
    }

    @Before
    public void setUp()
    {
        //Setup database restore point
        Sync sync = (Sync) Sync.getInstance();
        try
        {
            sync.setDatabaseSavePoint();
        }
        catch (SQLException ex)
        {
            Logger.getLogger(TournamentTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Failed to setup database savepoint");
        }
    }

    @After
    public void tearDown()
    {

        //clear caches
        Tournament.ClearDatabaseCache();
        Fencer.ClearDatabaseCache();
        Preliminary.ClearDatabaseCache();
        Finalround.ClearDatabaseCache();
        
        //reset database to old state
        Sync sync = (Sync) Sync.getInstance();
        try
        {
            sync.restoreDatabaseSavePoint();
        }
        catch (SQLException ex)
        {
            Logger.getLogger(TournamentTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Failed to restore database savepoint");
        }
    }

    /**
     * Test of getTournament method, of class Tournament.
     */
    @Test
    public void testGetTournament() throws Exception
    {
        System.out.println("getTournament");
        Tournament expectedResult = TestUtilities.CreateTournament();
        Tournament result = Tournament.getTournament(expectedResult.getID());
        assertEquals(expectedResult, result);
    }

    /**
     * Test of getAllTournaments method, of class Tournament.
     */
    @Test
    public void testGetAllTournaments()
    {
        System.out.println("getAllTournaments");
        ArrayList<Tournament> allTournaments = new ArrayList<>();
        allTournaments.add(TestUtilities.CreateTournament());
        allTournaments.add(TestUtilities.CreateTournament());
        allTournaments.add(TestUtilities.CreateTournament());
        List<Tournament> result = Tournament.getAllTournaments();

        assertEquals(allTournaments.size(), result.size());
        for (Tournament tournament : allTournaments)
        {
            assertTrue(result.contains(tournament));
        }
    }

    /**
     * Test of getSQLString method, of class Tournament.
     */
    @Test
    public void testGetSQLString()
    {
        System.out.println("getSQLString");
        String expResult = "";
        String result = Tournament.getSQLString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getName method, of class Tournament.
     */
    @Test
    public void testGetName()
    {
        System.out.println("getName");
        String expResult = "asdf";
        Tournament instance = TestUtilities.CreateTournament(expResult);
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDate method, of class Tournament.
     */
    @Test
    public void testGetDate()
    {
        System.out.println("getDate");
        Tournament instance = null;
        String expResult = "";
        String result = instance.getDate();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getGroups method, of class Tournament.
     */
    @Test
    public void testGetGroups()
    {
        System.out.println("getGroups");
        Tournament instance = null;
        int expResult = 0;
        int result = instance.getGroups();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFinalRounds method, of class Tournament.
     */
    @Test
    public void testGetFinalRounds()
    {
        System.out.println("getFinalRounds");
        Tournament instance = null;
        int expResult = 0;
        int result = instance.getFinalRounds();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLanes method, of class Tournament.
     */
    @Test
    public void testGetLanes()
    {
        System.out.println("getLanes");
        Tournament instance = null;
        int expResult = 0;
        int result = instance.getLanes();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setName method, of class Tournament.
     */
    @Test
    public void testSetName() throws Exception
    {
        System.out.println("setName");
        String name = "";
        Tournament instance = null;
        instance.setName(name);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDate method, of class Tournament.
     */
    @Test
    public void testSetDate() throws Exception
    {
        System.out.println("setDate");
        String date = "";
        Tournament instance = null;
        instance.setDate(date);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setGroups method, of class Tournament.
     */
    @Test
    public void testSetGroups() throws Exception
    {
        System.out.println("setGroups");
        int groups = 0;
        Tournament instance = null;
        instance.setGroups(groups);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setFinalRounds method, of class Tournament.
     */
    @Test
    public void testSetFinalRounds() throws Exception
    {
        System.out.println("setFinalRounds");
        int rounds = 0;
        Tournament instance = null;
        instance.setFinalRounds(rounds);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setLanes method, of class Tournament.
     */
    @Test
    public void testSetLanes() throws Exception
    {
        System.out.println("setLanes");
        int lanes = 0;
        Tournament instance = null;
        instance.setLanes(lanes);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class Tournament.
     */
    @Test
    public void testToString()
    {
        System.out.println("toString");
        Tournament instance = null;
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addParticipant method, of class Tournament.
     */
    @Test
    public void testAddParticipant_iFencer() throws Exception
    {
        System.out.println("addParticipant");
        iFencer f = Sync.getInstance().createFencer("Asdf", "Fdsa");
        Tournament instance = (Tournament) Sync.getInstance().createTournament("TestTournament");
        instance.addParticipant(f);
        assertTrue(instance.getAllParticipants().contains(f));
    }

    /**
     * Test of addParticipant method, of class Tournament.
     */
    @Test
    public void testAddParticipant_iFencer_int() throws Exception
    {
        System.out.println("addParticipant");
        iFencer f = Sync.getInstance().createFencer("Asdf", "Fdsa");
        Tournament instance = (Tournament) Sync.getInstance().createTournament("TestTournament");
        int group = 2;
        instance.setGroups(group);
        instance.addParticipant(f, group);
        assertTrue(instance.getAllParticipants().contains(f));
        assertEquals(instance.getParticipantGroup(f), group);
        assertTrue(instance.getParticipantsOfGroup(group).contains(f));
    }

    /**
     * Test of getAllPreliminary method, of class Tournament.
     */
    @Test
    public void testGetAllPreliminary() throws Exception
    {
        System.out.println("getAllPreliminary");
        int groups = 2;
        int lanes = 2;
        int numberOfFencers = 10;
        double fencersPerGroup = ((double)numberOfFencers)/((double) groups);
        ArrayList<iFencer> fencers = new ArrayList<>();
        
        for(int i = 0; i < numberOfFencers; i++)
        {
            fencers.add(TestUtilities.CreateFencer("Fencer_"+i, "Surename"));
        }
        
        Tournament instance = TestUtilities.CreateTournament("TestTournament");
        instance.setLanes(lanes);
        instance.setGroups(groups);

        instance.createPreliminaryTiming();
        List<iPreliminary> result = instance.getAllPreliminary();
        for(int i = 0; i < groups; i++)
        {
            assertEquals((double)instance.getParticipantsOfGroup(i).size(), fencersPerGroup, 1.0);
        }
        
        long fightsPerGroup = Math.round(fencersPerGroup*(fencersPerGroup-1.0)/2.0);
        assertEquals(result.size(), fightsPerGroup*groups);
        
        //TODO: test that every fencer in every group fights against all other fencers in his group, exactly once
        //TODO: test that every fencer is put into exactly one group
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isParticipant method, of class Tournament.
     */
    @Test
    public void testIsParticipant() throws Exception
    {
        System.out.println("isParticipant");
        iFencer f = null;
        Tournament instance = null;
        boolean expResult = false;
        boolean result = instance.isParticipant(f);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createPreliminaryTiming method, of class Tournament.
     */
    @Test
    public void testCreatePreliminaryTiming() throws Exception
    {
        System.out.println("createPreliminaryTiming");
        Tournament instance = null;
        instance.createPreliminaryTiming();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPreliminarySchedule method, of class Tournament.
     */
    @Test
    public void testGetPreliminarySchedule() throws Exception
    {
        System.out.println("getPreliminarySchedule");
        Tournament instance = null;
        iPreliminary[][] expResult = null;
        iPreliminary[][] result = instance.getPreliminarySchedule();
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllParticipants method, of class Tournament.
     */
    @Test
    public void testGetAllParticipants() throws Exception
    {
        System.out.println("getAllParticipants");
        Tournament instance = null;
        List<iFencer> expResult = null;
        List<iFencer> result = instance.getAllParticipants();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getParticipantGroup method, of class Tournament.
     */
    @Test
    public void testGetParticipantGroup() throws Exception
    {
        System.out.println("getParticipantGroup");
        iFencer f = null;
        Tournament instance = null;
        int expResult = 0;
        int result = instance.getParticipantGroup(f);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeParticipant method, of class Tournament.
     */
    @Test
    public void testRemoveParticipant() throws Exception
    {
        System.out.println("removeParticipant");
        iFencer f = null;
        Tournament instance = null;
        instance.removeParticipant(f);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setEntryFee method, of class Tournament.
     */
    @Test
    public void testSetEntryFee() throws Exception
    {
        System.out.println("setEntryFee");
        iFencer f = null;
        boolean paid = false;
        Tournament instance = null;
        instance.setEntryFee(f, paid);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setEquipmentCheck method, of class Tournament.
     */
    @Test
    public void testSetEquipmentCheck() throws Exception
    {
        System.out.println("setEquipmentCheck");
        iFencer f = null;
        boolean checked = false;
        Tournament instance = null;
        instance.setEquipmentCheck(f, checked);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEntryFee method, of class Tournament.
     */
    @Test
    public void testGetEntryFee() throws Exception
    {
        System.out.println("getEntryFee");
        iFencer f = null;
        Tournament instance = null;
        boolean expResult = false;
        boolean result = instance.getEntryFee(f);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEquipmentCheck method, of class Tournament.
     */
    @Test
    public void testGetEquipmentCheck() throws Exception
    {
        System.out.println("getEquipmentCheck");
        iFencer f = null;
        Tournament instance = null;
        boolean expResult = false;
        boolean result = instance.getEquipmentCheck(f);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addWinPrelim method, of class Tournament.
     */
    @Test
    public void testAddWinPrelim() throws Exception
    {
        System.out.println("addWinPrelim");
        Fencer f = null;
        Tournament instance = null;
        instance.addWinPrelim(f);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addWinFinal method, of class Tournament.
     */
    @Test
    public void testAddWinFinal() throws Exception
    {
        System.out.println("addWinFinal");
        Fencer f = null;
        Tournament instance = null;
        instance.addWinFinal(f);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of subWinPrelim method, of class Tournament.
     */
    @Test
    public void testSubWinPrelim() throws Exception
    {
        System.out.println("subWinPrelim");
        Fencer f = null;
        Tournament instance = null;
        instance.subWinPrelim(f);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of subWinFinal method, of class Tournament.
     */
    @Test
    public void testSubWinFinal() throws Exception
    {
        System.out.println("subWinFinal");
        Fencer f = null;
        Tournament instance = null;
        instance.subWinFinal(f);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addHitsPrelim method, of class Tournament.
     */
    @Test
    public void testAddHitsPrelim() throws Exception
    {
        System.out.println("addHitsPrelim");
        Fencer f = null;
        int points = 0;
        Tournament instance = null;
        instance.addHitsPrelim(f, points);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addHitsFinal method, of class Tournament.
     */
    @Test
    public void testAddHitsFinal() throws Exception
    {
        System.out.println("addHitsFinal");
        Fencer f = null;
        int points = 0;
        Tournament instance = null;
        instance.addHitsFinal(f, points);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addGotHitPrelim method, of class Tournament.
     */
    @Test
    public void testAddGotHitPrelim() throws Exception
    {
        System.out.println("addGotHitPrelim");
        Fencer f = null;
        int points = 0;
        Tournament instance = null;
        instance.addGotHitPrelim(f, points);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addGotHitFinal method, of class Tournament.
     */
    @Test
    public void testAddGotHitFinal() throws Exception
    {
        System.out.println("addGotHitFinal");
        Fencer f = null;
        int points = 0;
        Tournament instance = null;
        instance.addGotHitFinal(f, points);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getScoreFromPrelim method, of class Tournament.
     */
    @Test
    public void testGetScoreFromPrelim()
    {
        System.out.println("getScoreFromPrelim");
        iFencer f = null;
        Tournament instance = null;
        Score expResult = null;
        Score result = instance.getScoreFromPrelim(f);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getScoreFromFinal method, of class Tournament.
     */
    @Test
    public void testGetScoreFromFinal()
    {
        System.out.println("getScoreFromFinal");
        iFencer f = null;
        Tournament instance = null;
        Score expResult = null;
        Score result = instance.getScoreFromFinal(f);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getScoresPrelim method, of class Tournament.
     */
    @Test
    public void testGetScoresPrelim() throws Exception
    {
        System.out.println("getScoresPrelim");
        Tournament instance = null;
        List<iScore> expResult = null;
        List<iScore> result = instance.getScoresPrelim();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getScoresFinal method, of class Tournament.
     */
    @Test
    public void testGetScoresFinal() throws Exception
    {
        System.out.println("getScoresFinal");
        Tournament instance = null;
        List<iScore> expResult = null;
        List<iScore> result = instance.getScoresFinal();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getScoresInGroups method, of class Tournament.
     */
    @Test
    public void testGetScoresInGroups() throws Exception
    {
        System.out.println("getScoresInGroups");
        Tournament instance = null;
        List[] expResult = null;
        List[] result = instance.getScoresInGroups();
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPreliminaryCount method, of class Tournament.
     */
    @Test
    public void testGetPreliminaryCount() throws Exception
    {
        System.out.println("getPreliminaryCount");
        Tournament instance = null;
        int expResult = 0;
        int result = instance.getPreliminaryCount();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getParticipantsOfGroup method, of class Tournament.
     */
    @Test
    public void testGetParticipantsOfGroup() throws Exception
    {
        System.out.println("getParticipantsOfGroup");
        int group = 0;
        Tournament instance = null;
        List<iFencer> expResult = null;
        List<iFencer> result = instance.getParticipantsOfGroup(group);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of preliminaryWithoutTiming method, of class Tournament.
     */
    @Test
    public void testPreliminaryWithoutTiming() throws Exception
    {
        System.out.println("preliminaryWithoutTiming");
        Tournament instance = null;
        int expResult = 0;
        int result = instance.preliminaryWithoutTiming();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addPreliminary method, of class Tournament.
     */
    @Test
    public void testAddPreliminary() throws Exception
    {
        System.out.println("addPreliminary");
        Tournament instance = null;
        instance.addPreliminary();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isPreliminaryFinished method, of class Tournament.
     */
    @Test
    public void testIsPreliminaryFinished()
    {
        System.out.println("isPreliminaryFinished");
        Tournament instance = null;
        boolean expResult = false;
        boolean result = instance.isPreliminaryFinished();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of finishPreliminary method, of class Tournament.
     */
    @Test
    public void testFinishPreliminary() throws Exception
    {
        System.out.println("finishPreliminary");
        Tournament instance = null;
        boolean expResult = false;
        boolean result = instance.finishPreliminary();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of allPreliminaryFinished method, of class Tournament.
     */
    @Test
    public void testAllPreliminaryFinished()
    {
        System.out.println("allPreliminaryFinished");
        Tournament instance = null;
        boolean expResult = false;
        boolean result = instance.allPreliminaryFinished();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllFinalrounds method, of class Tournament.
     */
    @Test
    public void testGetAllFinalrounds()
    {
        System.out.println("getAllFinalrounds");
        Tournament instance = null;
        List<iFinalround> expResult = null;
        List<iFinalround> result = instance.getAllFinalrounds();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of dropOut method, of class Tournament.
     */
    @Test
    public void testDropOut() throws Exception
    {
        System.out.println("dropOut");
        iFencer f = null;
        Tournament instance = null;
        instance.dropOut(f);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getYellowFor method, of class Tournament.
     */
    @Test
    public void testGetYellowFor() throws Exception
    {
        System.out.println("getYellowFor");
        iFencer f = null;
        Tournament instance = null;
        int expResult = 0;
        int result = instance.getYellowFor(f);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getRedFor method, of class Tournament.
     */
    @Test
    public void testGetRedFor() throws Exception
    {
        System.out.println("getRedFor");
        iFencer f = null;
        Tournament instance = null;
        int expResult = 0;
        int result = instance.getRedFor(f);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBlackFor method, of class Tournament.
     */
    @Test
    public void testGetBlackFor() throws Exception
    {
        System.out.println("getBlackFor");
        iFencer f = null;
        Tournament instance = null;
        int expResult = 0;
        int result = instance.getBlackFor(f);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getComment method, of class Tournament.
     */
    @Test
    public void testGetComment() throws Exception
    {
        System.out.println("getComment");
        iFencer f = null;
        Tournament instance = null;
        String expResult = "";
        String result = instance.getComment(f);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setComment method, of class Tournament.
     */
    @Test
    public void testSetComment() throws Exception
    {
        System.out.println("setComment");
        iFencer f = null;
        String comment = "";
        Tournament instance = null;
        instance.setComment(f, comment);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class Tournament.
     */
    @Test
    public void testEquals()
    {
        System.out.println("equals");
        Object other = null;
        Tournament instance = null;
        boolean expResult = false;
        boolean result = instance.equals(other);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
