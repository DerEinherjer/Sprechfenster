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
        
        //test that the tournament that has the given ID is returned
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

        //test that all tournaments that were created are returned
        assertEquals(allTournaments.size(), result.size());
        for (Tournament tournament : allTournaments)
        {
            assertTrue(result.contains(tournament));
        }
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
        int groups = 2;
        int lanes = 2;
        int numberOfFencers = 10;
        testGetAllPreliminaryWithParameters(groups, lanes, numberOfFencers);
        
        tearDown();
        setUp();
        
        groups = 5;
        lanes = 3;
        numberOfFencers = 23;
        testGetAllPreliminaryWithParameters(groups, lanes, numberOfFencers);
        
        tearDown();
        setUp();
        
        groups = 4;
        lanes = 1;
        numberOfFencers = 17;
        testGetAllPreliminaryWithParameters(groups, lanes, numberOfFencers);
        
        tearDown();
        setUp();
        
        groups = 1;
        lanes = 1;
        numberOfFencers = 6;
        testGetAllPreliminaryWithParameters(groups, lanes, numberOfFencers);
        
        tearDown();
        setUp();
        
        groups = 3;
        lanes = 2;
        numberOfFencers = 15;
        testGetAllPreliminaryWithParameters(groups, lanes, numberOfFencers);
    }
    

    
    private void testGetAllPreliminaryWithParameters(int groups, int lanes, int numberOfFencers) throws Exception
    {
        Tournament instance = TestUtilities.CreateTournament("TestTournament");
        TestUtilities.SetupTournamentQualificationRounds(instance, groups, lanes, numberOfFencers);
        
        List<iPreliminary> result = instance.getAllPreliminary();
        ArrayList<ArrayList<iPreliminary>> fightsPerGroup = new ArrayList<>();
        ArrayList<ArrayList<iFencer>> fencersPerGroup = new ArrayList<>();
        
        for(int i = 0; i < groups; i++)
        {
            fightsPerGroup.add(new ArrayList<>());
            fencersPerGroup.add(new ArrayList<>());
        }
        
        for(iPreliminary round : result)
        {
            int index = round.getGroup()-1;
            //the round must have a valid group number
            assertTrue(index < fightsPerGroup.size());
            assertTrue(index >= 0);
            
            fightsPerGroup.get(index).add(round);
            List<iFencer> fencersOfRound = round.getFencer();
            
            //every fight must be between two fencers
            assertEquals(2, fencersOfRound.size());
            
            iFencer firstFencer = fencersOfRound.get(0);
            iFencer secondFencer = fencersOfRound.get(1);
            
            //fencers must not fight against themselves
            assertFalse(firstFencer == secondFencer);
            
            //every fencer must be contained in exactly one group
            for(int i = 0; i < groups; i++)
            {
                if(i != index)
                {
                    assertFalse(fencersPerGroup.get(i).contains(firstFencer));
                    assertFalse(fencersPerGroup.get(i).contains(secondFencer));
                }
            }
            
            ArrayList<iFencer> fencerGroup = fencersPerGroup.get(index);
            if(!fencerGroup.contains(firstFencer))
            {
                fencerGroup.add(firstFencer);
            }
            if(!fencerGroup.contains(secondFencer))
            {
                fencerGroup.add(secondFencer);
            }
        }
        
        double numberOfFencersPerGroup = ((double)numberOfFencers)/((double) groups);
        
        for(int i = 1; i <= groups; i++)
        {
            long numberOfFencersInGroup = instance.getParticipantsOfGroup(i).size();
            //fencers must be divided into groups of equal size, plus/minus 1 fencer
            assertEquals((double)numberOfFencersPerGroup, numberOfFencersInGroup, 1.0);
            
            //every fencer in every group must fight exactly once against every other fencer in his group.
            //Per group, this gives us n*((n-1)/2) rounds.
            //Test that the correct number of fights is generated for each group:
            long fightsInGroup = Math.round(numberOfFencersInGroup*(numberOfFencersInGroup-1.0)/2.0);
            assertEquals(fightsInGroup, fightsPerGroup.get(i-1).size());
            
            //Test that every fencer in every group fights against all other fencers in his group.
            //combined with the number of rounds this ensures that every fencer fights every other fighter exactly once.
            ArrayList<iFencer> fencersOfGroup = fencersPerGroup.get(i-1);
            for(iFencer fencer : fencersOfGroup)
            {
                for(iFencer fencerOpponent : fencersOfGroup)
                {
                    if(fencerOpponent != fencer)
                    {
                        boolean foundFight = false;
                        for(iPreliminary round : fightsPerGroup.get(i-1))
                        {
                            List<iFencer> fencersOfRound = round.getFencer();
                            if(fencersOfRound.contains(fencer) && fencersOfRound.contains(fencerOpponent))
                            {
                                foundFight = true;
                                break;
                            }
                        }
                        assertTrue(foundFight);
                    }
                }
            }            
        }
    }
    
    /**
     * Test of finishPreliminary method, of class Tournament.
     */
    @Test
    public void testFinishPreliminary() throws Exception
    {   
        int groups = 2;
        int lanes = 2;
        int numberOfFencers = 10;
        int numberOfFinalRounds = 2;
        
        testFinishPreliminaryWithParameters(groups, lanes, numberOfFencers, numberOfFinalRounds);
        
        tearDown();
        setUp();
    }
    
     private void testFinishPreliminaryWithParameters(int groups, int lanes, int numberOfFencers, int numberOfFinalRounds) throws Exception
    {
        Tournament instance = TestUtilities.CreateTournament("TestTournament");
        instance.setFinalRounds(numberOfFinalRounds);
        TestUtilities.SetupTournamentQualificationRounds(instance, groups, lanes, numberOfFencers);   
        for(iPreliminary round : instance.getAllPreliminary())
        {
            round.setPoints(round.getFencer().get(0), 5);
            round.setPoints(round.getFencer().get(1), 1);
            round.setFinished(true);
        }
        
        Boolean result = instance.finishPreliminary();
        assertTrue(result);
        
        List<iFinalround> finalMatches = instance.getAllFinalrounds();
        //The finals use an elimination system:
        //1. The winners of the qualification rounds are paired.
        //2. Each pair fences a match. The looser is out, the winner continues to the next round.
        //3. This continues until only 2 fencers are left. The is the final round, determining first and second place.
        //4. The loosers of the round before the final round fence for the third place.
        //
        //Since the last final round always has one match, the second last always has two matches, the third last four and so on.
        //A single additional match is held for the third place.
        //Thus, if the number of final rounds is set to n, there are (sum[i=0..n-1](2^i)) + 1 matches.

        int correctNumberOfMatches = 0;
        for(int i = numberOfFinalRounds-1; i >= 0; i--)
        {
            //calculate number of matches
            correctNumberOfMatches += Math.pow(2, i);
        }
        correctNumberOfMatches++;//add the match for the third place
        
        assertEquals(correctNumberOfMatches ,finalMatches.size());
        
        for(int i = 1; i <= numberOfFinalRounds; i++)
        {
            List<iFinalround> matchesOfRound = new ArrayList<>();
            for(iFinalround match : finalMatches)
            {
                if(match.getFinalRound() == i)
                {
                    matchesOfRound.add(match);
                }
            }
            System.out.println("Runde "+i+": "+matchesOfRound.size()+"("+(int)Math.pow(2, numberOfFinalRounds-i)+")");
            //With the number of final rounds set to n,
            //the first round must have 2^(n-1) matches, second round 2^(n-2), ...
            //TODO: what about the match for third place? Which round number does it receive?
            assertEquals((int)Math.pow(2, numberOfFinalRounds-i), matchesOfRound.size());
            for(iFinalround match : matchesOfRound)
            {
                assertEquals(2, match.getFencer().size());
                assertTrue(match.getFencer().get(0) != match.getFencer().get(1)); 
                assertTrue(match.getLane() > 0 && match.getLane() <= lanes); 
                match.setPoints(match.getFencer().get(0), 7);
                match.setPoints(match.getFencer().get(1), 3);
                match.setFinished(true);
            }
            //TODO: test that the pairings for the first round are correct
            //TODO: test that the pairings for the n+1-th round are correct once the n-th round is finished
            //TODO: test that the match for the third place has the correct pairing
        }
    }

}
