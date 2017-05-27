/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.fail;

/**
 *
 * @author Stefan
 */
public class TestUtilities {

  public static Tournament CreateTournament () {
    return CreateTournament("TournamentName");
  }

  public static Tournament CreateTournament (String name) {
    try {
      iTournament tournament = Sync.getInstance().createTournament(name);
      tournament.setDate("2017-05-25");
      tournament.setGroups(4);
      tournament.setFinalRounds(2);
      tournament.setLanes(1);
      return (Tournament) tournament;
    }
    catch (Exception ex) {
      Logger.getLogger(TournamentTest.class.getName()).log(Level.SEVERE, null, ex);
      fail("failed to create Tournament");
      return null;
    }
  }

  public static Fencer CreateFencer (String forename, String surname) {
    try {
      iFencer fencer = Sync.getInstance().createFencer(forename, surname, "1986-03-14", "deutsch", "Sieben Schwerter");
      fencer.setBirthday("1986-03-14");
      fencer.setFencingSchool("Sieben Schwerter");
      fencer.setNationality("Deutschland");
      return (Fencer) fencer;
    }
    catch (Exception ex) {
      Logger.getLogger(TournamentTest.class.getName()).log(Level.SEVERE, null, ex);
      fail("failed to create Fencer");
      return null;
    }
  }

  public static Fencer CreateFencer () {
    return CreateFencer("Vorname", "Nachname");
  }

  public static void SetupTournamentQualificationRounds (Tournament instance, int groups, int lanes, int numberOfFencers) throws Exception {
    for (int i = 0; i < numberOfFencers; i++) {
      iFencer fencer = TestUtilities.CreateFencer("Fencer_" + i, "Surename");
      instance.addParticipant(fencer);
    }

    instance.setLanes(lanes);
    instance.setGroups(groups);

    instance.createPreliminaryTiming();
  }
}
