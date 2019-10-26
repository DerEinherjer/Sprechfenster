/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.SQLException;
import model.DBConnection.DBBaseClass;
import model.DBConnection.DBTournament;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Asgard
 */
public class TestTournament
{

  @BeforeClass
  public static void setUpClass()
  {
  }

  @AfterClass
  public static void tearDownClass()
  {
  }

  @Before
  public void setUp() throws SQLException
  {
    DBBaseClass.initTestPhase();
  }

  @After
  public void tearDown()
  {
  }

  @Test
  public void testTournament()
  {
    try
    {
      Tournament t = new Tournament("TestTournament");
      t.setDate("2009-01-01");
      t.setGroups(4);
      t.setLanes(2);
      t.setFinalRounds(3);

      DBBaseClass.reset();

      assertEquals(Tournament.getAllTournaments().size(), 1);

      t = Tournament.getAllTournaments().get(0);

      assertEquals(t.getDate(), "2009-01-01");
      assertEquals(t.getGroups(), 4);
      assertEquals(t.getLanes(), 2);
      assertEquals(t.getFinalRounds(), 3);

      Fencer f = new Fencer("Peter", "Müller");

      t.addParticipant(f);

      assertTrue(t.isParticipant(f));
      assertEquals(t.getAllParticipants().size(), 1);

      DBBaseClass.reset();

      assertEquals(Tournament.getAllTournaments().size(), 1);
      assertEquals(Fencer.getAllFencer().size(), 1);

      t = Tournament.getAllTournaments().get(0);
      f = Fencer.getAllFencer().get(0);

      assertEquals(t.getAllParticipants().size(), 1);

      assertTrue(t.isParticipant(f));

      t.removeParticipant(f);

      assertFalse(t.isParticipant(f));

      DBBaseClass.reset();

      assertEquals(Tournament.getAllTournaments().size(), 1);
      assertEquals(Fencer.getAllFencer().size(), 1);

      t = Tournament.getAllTournaments().get(0);
      f = Fencer.getAllFencer().get(0);

      assertEquals(t.getAllParticipants().size(), 0);

      assertFalse(t.isParticipant(f));
    } catch (SQLException e)
    {
      e.printStackTrace();
      assert (false);
    }
  }
}
