/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.SQLException;
import model.DBConnection.DBSuperClass;
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
public class TestParticipation {
    
    public TestParticipation() {
    }
    
    @BeforeClass
    public static void setUpClass() throws SQLException
    {
        DBSuperClass.initTestPhase();
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    
    @Test
    public void hello()
    {
        try
        {
            Tournament t = new Tournament("TestTurnier");
            Fencer f = new Fencer("", "");
            
            t.addParticipant(f);
            
            assertEquals(t.getEntryFee(f), false);
            assertEquals(t.getEquipmentCheck(f), false);
            assertEquals(t.getComment(f), "");
            
            DBSuperClass.reset();
            
            t = Tournament.getAllTournaments().get(0);
            f = Fencer.getAllFencer().get(0);
            
            assertEquals(t.getEntryFee(f), false);
            assertEquals(t.getEquipmentCheck(f), false);
            assertEquals(t.getComment(f), "");
            
            t.setEntryFee(f, true);
            t.setEquipmentCheck(f, true);
            t.setComment(f, "Kommentar");
            
            assertEquals(t.getEntryFee(f), true);
            assertEquals(t.getEquipmentCheck(f), true);
            assertEquals(t.getComment(f), "Kommentar");
            
            DBSuperClass.reset();
            
            t = Tournament.getAllTournaments().get(0);
            f = Fencer.getAllFencer().get(0);
            
            assertEquals(t.getEntryFee(f), true);
            assertEquals(t.getEquipmentCheck(f), true);
            assertEquals(t.getComment(f), "Kommentar");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
