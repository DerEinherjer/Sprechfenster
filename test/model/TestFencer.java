/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class TestFencer {
    
    public TestFencer() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws SQLException
    {
        DBSuperClass.initTestPhase();
    }
    
    @After
    public void tearDown() throws SQLException
    {
    }
    
    @Test
    public void testFencer()
    {
        
        try 
        {
            Fencer f = new Fencer("Peter", "Müller");
            
            assertEquals("Peter", f.getName());
            assertEquals("Müller", f.getFamilyName());
            
            f.setName("Michael");
            f.setFamilyName("Schmidt");
            f.setBirthday("1970-01-01");
            f.setFencingSchool("Fechten mit Stil");
            f.setNationality("Deutsch");
            
            DBSuperClass.reset();
            
            assertEquals(1, Fencer.getAllFencer().size());
            f = Fencer.getAllFencer().get(0);
            
            assertEquals("Michael", f.getName());
            assertEquals("Schmidt", f.getFamilyName());
            assertEquals("1970-01-01", f.getBirthday());
            assertEquals("Fechten mit Stil", f.getFencingSchool());
            assertEquals("Deutsch", f.getNationality());
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            assert(false);
        }
        
    }
}
