/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.SQLException;
import model.DBConnection.DBBaseClass;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import model.rounds.iQualificationMatch;

/**
 *
 * @author Asgard
 */
public class CreatePreliminarySchedule
{

  public CreatePreliminarySchedule()
  {
  }

  @BeforeClass
  public static void setUpClass() throws SQLException
  {
    DBBaseClass.initTestPhase();
  }

  @AfterClass
  public static void tearDownClass()
  {
  }

  @Before
  public void setUp()
  {
  }

  @After
  public void tearDown()
  {
  }

  // TODO add test methods here.
  // The methods must be annotated with annotation @Test. For example:
  //
  // @Test
  // public void hello() {}
  @Test
  public void testSchedule() throws SQLException
  {
    Tournament t = new Tournament("TestTournament");

    Fencer a = new Fencer("A", "A");
    Fencer b = new Fencer("B", "B");
    Fencer c = new Fencer("C", "C");
    Fencer d = new Fencer("D", "D");

    Fencer e = new Fencer("E", "E");
    Fencer f = new Fencer("F", "F");
    Fencer g = new Fencer("G", "G");
    Fencer h = new Fencer("H", "H");

    t.addParticipant(a, 0);
    t.addParticipant(b, 0);
    t.addParticipant(c, 0);
    t.addParticipant(d, 0);

    t.addParticipant(e, 1);
    t.addParticipant(f, 1);
    t.addParticipant(g, 1);
    t.addParticipant(h, 1);

    t.startQualificationPhase();

    iQualificationMatch[][] schedule = t.getQualificationMatchSchedule();

    for (int i = 0; i < schedule.length; i++)
    {
      for (int j = 0; j < schedule[i].length; j++)
      {
        System.out.print(schedule[i][j].getFencer().get(0).getName() + "" + schedule[i][j].getFencer().get(1).getName() + "\t");
      }

      System.out.println("");
    }
    
    iQualificationMatch p = t.getAllQualificationMatches().get(0);

    iFencer f1 = p.getFencer().get(0);
    iFencer f2 = p.getFencer().get(1);

    p.setPoints(f1, 5);
    p.setPoints(f2, 3);

    p.setFinished(true);

    iScore s1 = t.getFencersScoreFromQualificationPhase(f1);
    iScore s2 = t.getFencersScoreFromQualificationPhase(f2);

    System.out.println("F1: " + s1.getWins() + " | " + s1.getHits() + " | " + s1.getGotHit());
    System.out.println("F2: " + s2.getWins() + " | " + s2.getHits() + " | " + s2.getGotHit());
  }
}
