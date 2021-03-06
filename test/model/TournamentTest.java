/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.rounds.iFinalsMatch;
import model.rounds.iQualificationMatch;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
  }

  @AfterClass
  public static void tearDownClass()
  {
  }

  @Before
  public void setUp()
  {
    //Setup database restore point

  }

  @After
  public void tearDown()
  {

    //reset database to old state
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
    //TODO: fix test
    /*iFencer f = Sync.getInstance().createFencer("Asdf", "Fdsa", "1986-03-14", "deutsch", "Sieben Schwerter");
    Tournament instance = (Tournament) Sync.getInstance().createTournament("TestTournament");
    instance.addParticipant(f);
    assertTrue(instance.getAllParticipants().contains(f));*/
  }

  /**
   * Test of addParticipant method, of class Tournament.
   */
  @Test
  public void testAddParticipant_iFencer_int() throws Exception
  {
    System.out.println("addParticipant");
    //TODO: fix test
    /*
    iFencer f = Sync.getInstance().createFencer("Asdf", "Fdsa", "1986-03-14", "deutsch", "Sieben Schwerter");
    Tournament instance = (Tournament) Sync.getInstance().createTournament("TestTournament");
    int group = 2;
    instance.setGroups(group);
    instance.addParticipant(f, group);
    assertTrue(instance.getAllParticipants().contains(f));
    assertEquals(instance.getParticipantGroup(f), group);
    assertTrue(instance.getParticipantsOfGroup(group).contains(f));*/
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

    List<iQualificationMatch> result = instance.getAllQualificationMatches();
    ArrayList<ArrayList<iQualificationMatch>> fightsPerGroup = new ArrayList<>();
    ArrayList<ArrayList<iFencer>> fencersPerGroup = new ArrayList<>();

    for (int i = 0; i < groups; i++)
    {
      fightsPerGroup.add(new ArrayList<>());
      fencersPerGroup.add(new ArrayList<>());
    }

    for (iQualificationMatch round : result)
    {
      int index = round.getQualificationGroup() - 1;
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
      for (int i = 0; i < groups; i++)
      {
        if (i != index)
        {
          assertFalse(fencersPerGroup.get(i).contains(firstFencer));
          assertFalse(fencersPerGroup.get(i).contains(secondFencer));
        }
      }

      ArrayList<iFencer> fencerGroup = fencersPerGroup.get(index);
      if (!fencerGroup.contains(firstFencer))
      {
        fencerGroup.add(firstFencer);
      }
      if (!fencerGroup.contains(secondFencer))
      {
        fencerGroup.add(secondFencer);
      }
    }

    double numberOfFencersPerGroup = ((double) numberOfFencers) / ((double) groups);

    for (int i = 1; i <= groups; i++)
    {
      long numberOfFencersInGroup = instance.getParticipantsOfGroup(i).size();
      //fencers must be divided into groups of equal size, plus/minus 1 fencer
      assertEquals((double) numberOfFencersPerGroup, numberOfFencersInGroup, 1.0);

      //every fencer in every group must fight exactly once against every other fencer in his group.
      //Per group, this gives us n*((n-1)/2) rounds.
      //Test that the correct number of fights is generated for each group:
      long fightsInGroup = Math.round(numberOfFencersInGroup * (numberOfFencersInGroup - 1.0) / 2.0);
      assertEquals(fightsInGroup, fightsPerGroup.get(i - 1).size());

      //Test that every fencer in every group fights against all other fencers in his group.
      //combined with the number of rounds this ensures that every fencer fights every other fighter exactly once.
      ArrayList<iFencer> fencersOfGroup = fencersPerGroup.get(i - 1);
      for (iFencer fencer : fencersOfGroup)
      {
        for (iFencer fencerOpponent : fencersOfGroup)
        {
          if (fencerOpponent != fencer)
          {
            boolean foundFight = false;
            for (iQualificationMatch round : fightsPerGroup.get(i - 1))
            {
              List<iFencer> fencersOfRound = round.getFencer();
              if (fencersOfRound.contains(fencer) && fencersOfRound.contains(fencerOpponent))
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
    for (iQualificationMatch round : instance.getAllQualificationMatches())
    {
      round.setPoints(round.getFencer().get(0), 5);
      round.setPoints(round.getFencer().get(1), 1);
      round.setFinished(true);
    }

    instance.startFinalsPhase();

    List<iFinalsMatch> finalMatches = instance.getAllFinalsMatches();
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
    for (int i = numberOfFinalRounds - 1; i >= 0; i--)
    {
      //calculate number of matches
      correctNumberOfMatches += Math.pow(2, i);
    }
    correctNumberOfMatches++;//add the match for the third place

    assertEquals(correctNumberOfMatches, finalMatches.size());

    for (int i = 1; i <= numberOfFinalRounds; i++)
    {
      List<iFinalsMatch> matchesOfRound = new ArrayList<>();
      for (iFinalsMatch match : finalMatches)
      {
        if (match.getFinalRound() == i)
        {
          matchesOfRound.add(match);
        }
      }

      if (i == 1)
      {
        List<iScore> scores = new ArrayList<>();
        for (iFinalsMatch match : matchesOfRound)
        {
          scores.add(instance.getFencersScoreFromQualificationPhase(match.getFencer().get(0)));
          scores.add(instance.getFencersScoreFromQualificationPhase(match.getFencer().get(1)));
        }

        Collections.sort(scores);//Sort the list so that equal scores always behind each other
        //so that they will end up in that same category

        //This will put als same scores in one category
        List<List<iScore>> categories = new ArrayList<>();
        for (iScore score : scores)
        {
          if (categories.size() > 0 && categories.get(categories.size() - 1).get(0).equals(score))
          {
            categories.get(categories.size() - 1).add(score);
          } else
          {
            categories.add(new ArrayList<>());
            categories.get(categories.size() - 1).add(score);
          }
        }

        for (iFinalsMatch match : matchesOfRound)
        {
          iScore scoreF0 = instance.getFencersScoreFromQualificationPhase(match.getFencer().get(0));
          iScore scoreF1 = instance.getFencersScoreFromQualificationPhase(match.getFencer().get(1));
          int indexF0 = scores.indexOf(scoreF0);
          int indexF1 = scores.indexOf(scoreF1);
          int startF0 = -1;
          int endF0 = -1;
          int startF1 = -1;
          int endF1 = -1;

          //The following part calculates witch possition the fencer could have (1 for unic scores more if more tahn 1 fencer has the same score)
          //startFX is the first possible index -1
          //endFX is the last possible index
          for (int c = 0; c < categories.size(); c++)
          {
            if (endF0 == -1)
            {
              if (categories.get(c).get(0).equals(scoreF0))
              {
                endF0 = startF0 + categories.get(c).size();
              } else
              {
                startF0 += categories.get(c).size();
              }
            }

            if (endF1 == -1)
            {
              if (categories.get(c).get(0).equals(scoreF1))
              {
                endF1 = startF1 + categories.get(c).size();
              } else
              {
                startF1 += categories.get(c).size();
              }
            }
          }

          //The following loop checks if ther is a possible mutation of the score list in witch the the two fencer are paired
          //The rule for pairing is: first vs last; second vs prelast; ...
          boolean found = false;
          for (int index = 0; index < scores.size(); index++)
          {
            if (index > startF0 && index <= endF0)
            {
              if (scores.size() - index > startF1 && scores.size() - index <= endF1)
              {
                found = true;
                break;
              }
            }
          }
          assert (found);

        }
      }

      //With the number of final rounds set to n,
      //the first round must have 2^(n-1) matches, second round 2^(n-2), ...
      assertEquals((int) Math.pow(2, numberOfFinalRounds - i), matchesOfRound.size());
      for (iFinalsMatch match : matchesOfRound)
      {
        CheckAndFinishFinalround(match, lanes);
      }
      if (i == numberOfFinalRounds)
      {
        //we are in the finals. The match for third place should have fencers now!
        iFinalsMatch thirdPlaceMatch = null;
        for (iFinalsMatch match : finalMatches)
        {
          if (match.getFinalRound() == -1)
          {
            thirdPlaceMatch = match;
            break;
          }
        }
        assertTrue(thirdPlaceMatch != null);
        CheckAndFinishThirdPlace(thirdPlaceMatch, lanes);
      }
      //TODO: test that the pairings for the first round are correct
    }
  }

  private void CheckAndFinishThirdPlace(iFinalsMatch match, int lanes) throws Exception
  {
    assertEquals(2, match.getFencer().size());
    assertTrue(match.getFencer().get(0) != match.getFencer().get(1));
    assertTrue(match.getLane() > 0 && match.getLane() <= lanes);
    //TODO: fix test
/*
    if (match.getPreviousMatches().size() == 2)
    {
      List<iFencer> fencers = match.getFencer();
      iFencer fencer1 = null;
      iFencer fencer2 = null;
      iFinalsMatch preround1 = match.getPreviousMatches().get(0);
      iFinalsMatch preround2 = match.getPreviousMatches().get(1);
      if (preround1.isFencerInMatch(fencers.get(0)) && preround2.isFencerInMatch(fencers.get(1)))
      {
        fencer1 = fencers.get(0);
        fencer2 = fencers.get(1);
      } else
      {
        if (preround1.isFencerInMatch(fencers.get(1)) && preround2.isFencerInMatch(fencers.get(0)))
        {
          fencer1 = fencers.get(1);
          fencer2 = fencers.get(0);
        } else
        {
          assertTrue(false);//Die fechter stammen nicht jeweils aus einem der vorherigen Matches
        }
      }
      assertTrue(!preround1.getWinner().equals(fencer1));
      assertTrue(!preround2.getWinner().equals(fencer2));
      assertTrue(preround1.getPoints(fencer1) < preround1.getOpponentPoints(fencer1));
      assertTrue(preround2.getPoints(fencer2) < preround2.getOpponentPoints(fencer2));
    } else
    {
      assertTrue(false); //Gefecht um den 3. Platz muss vorhergehende Runden haben
    }*/
    match.setPoints(match.getFencer().get(0), 7);
    match.setPoints(match.getFencer().get(1), 3);
    match.setFinished(true);
  }

  private void CheckAndFinishFinalround(iFinalsMatch match, int lanes) throws Exception
  {
    assertEquals(2, match.getFencer().size());
    assertTrue(match.getFencer().get(0) != match.getFencer().get(1));
    assertTrue(match.getLane() > 0 && match.getLane() <= lanes);
    /*
    if (match.getPreviousMatches().size() == 2)
    {
      List<iFencer> fencers = match.getFencer();
      iFencer fencer1 = null;
      iFencer fencer2 = null;
      iFinalsMatch preround1 = match.getPreviousMatches().get(0);
      iFinalsMatch preround2 = match.getPreviousMatches().get(1);
      if (preround1 == null)
      {
        System.out.println("Preround1 ist null");
      }
      if (preround2 == null)
      {
        System.out.println("Preround2 ist null");
      }
      if (fencers.get(0) == null)
      {
        System.out.println("get(0) ist null");
      }
      if (fencers.get(1) == null)
      {
        System.out.println("get(1) ist null");
      }
      if (preround1.isFencerInMatch(fencers.get(0)) && preround2.isFencerInMatch(fencers.get(1)))
      {
        fencer1 = fencers.get(0);
        fencer2 = fencers.get(1);
      } else
      {
        if (preround1.isFencerInMatch(fencers.get(1)) && preround2.isFencerInMatch(fencers.get(0)))
        {
          fencer1 = fencers.get(1);
          fencer2 = fencers.get(0);
        } else
        {
          assertTrue(false);//Die fechter stammen nicht jeweils aus einem der vorherigen Matches
        }
      }
      assertTrue(preround1.getWinner().equals(fencer1));
      assertTrue(preround2.getWinner().equals(fencer2));
      assertTrue(preround1.getPoints(fencer1) > preround1.getOpponentPoints(fencer1));
      assertTrue(preround2.getPoints(fencer2) > preround2.getOpponentPoints(fencer2));
    }
     */
//TODO: fix test
    match.setPoints(match.getFencer().get(0), 7);
    match.setPoints(match.getFencer().get(1), 3);
    match.setFinished(true);
  }

}
