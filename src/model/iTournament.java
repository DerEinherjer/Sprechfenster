package model;

import model.rounds.iFinalround;
import java.sql.SQLException;
import java.util.List;
import model.rounds.iPreliminary;

public interface iTournament {

    
  /**
   * This Function returns the Database Id of the Tournament object.
   * @return ID
   */
  public int getID ();

  /**
   * This function returns the name given to this tournament.
   * @return Name of the tournament
   */
  public String getName ();

  /**
   * This fnction returns the date the tournament is set for.
   * @return Date of the tournament
   */
  public String getDate ();

  /**
   * This function returns the number of groups of the preliminaries.
   * @return Number of groups
   */
  public int getGroups ();

  /**
   * This function returns the number of round of the final.
   * 1  Only Finale     2 fencer
   * 2  Semi-Finals     4 fencer
   * 3  Quater-Finals   8 fencer
   * ...
   * @return Number of Rounds in the Final
   */
  public int getFinalRounds ();

  /**
   * This function returns the number of lanes which are availible for the Tournament.
   * @return Number of lanes
   */
  public int getLanes ();

  /**
   * This function sets the name of the tournament.
   * @param name    New name for the tournament
   * @throws SQLException 
   */
  public void setName (String name) throws SQLException;

  /**
   * This function sets the date of the tournament.
   * @param date    New date the tournament is set for
   * @throws SQLException 
   */
  public void setDate (String date) throws SQLException;

  /**
   * This function sets the number of groups in which the preliminary will be fought.
   * WARNING:   This can only be set befor the preliminary is started
   * @param groups  New number of groups
   * @throws SQLException 
   */
  public void setGroups (int groups) throws SQLException;

  /**
   * This function sets the number of rounds of the final.
   * 1  Only Finale     2 fencer
   * 2  Semi-Finals     4 fencer
   * 3  Quater-Finals   8 fencer
   * WARNING:   This can only be set befor the preliminary is started
   * @param rounds      New number of rounds
   * @throws SQLException 
   */
  public void setFinalRounds (int rounds) throws SQLException;

  /**
   * This function sets the number of lanes which are availible for the tournament.
   * WARNING:   This can only be set befor the preliminary is started
   * @param lanes   New number of lanes
   * @throws SQLException 
   */
  public void setLanes (int lanes) throws SQLException;

  /**
   * This function adds a fencer to the tournament. The fencer is automatical put
   * in the preliminary group with the fewest fencer.
   * WARNING:   This can only be set befor the preliminary is started
   * @param f   The fencer which will be added to the tournament
   * @throws SQLException 
   */
  public void addParticipant (iFencer f) throws SQLException;

  /**
   * This function adds a fencer to the tournament. The fencer will be put in the
   * given preliminary group.
   * WARNING:   This can only be set befor the preliminary is started
   * @param f       The fencer which will be added to the tournament
   * @param group   The group the fencer will be put in
   * @throws SQLException 
   */
  public void addParticipant (iFencer f, int group) throws SQLException;

  /**
   * This functions checks if a fencer is already participating in the tournament.
   * @param f   The fencer the function checks for
   * @return    TRUE if the fencer is participating in this tournament
   * @throws SQLException 
   */
  public boolean isParticipant (iFencer f) throws SQLException;

  /**
   * This function returns all fencer which are participating in this tournament.
   * @return    All particepating fencer
   * @throws SQLException 
   */
  public List<iFencer> getAllParticipants () throws SQLException;

  /**
   * This function returns all fencer of a given preliminary group
   * @param group   The group of which the fencers will be returned
   * @return    All fencers of the given group
   * @throws SQLException 
   */
  public List<iFencer> getParticipantsOfGroup (int group) throws SQLException;

  /**
   * This function retourns the preliminary group of a given fencer.
   * @param f   The fencer
   * @return    The preliminary group he is in 
   * @throws SQLException 
   */
  public int getParticipantGroup (iFencer f) throws SQLException;

  /**
   * This function revoces the participation of a given fencer
   * WARNING:   This can only be set befor the preliminary is started
   * @param f   The fencer which will be removed from the tournament
   * @throws SQLException 
   */
  public void removeParticipant (iFencer f) throws SQLException;

  /**
   * This functions "removes" a fencer from an already started tournament. This
   * can happen if a fencer get disqualified of hurt badly. He will loose all open
   * fights 0:5.
   * @param f   The fencer which drops out
   * @throws SQLException 
   */
  public void dropOut (iFencer f) throws SQLException;

  /**
   * This functions marks a fencer as "has payed" or "has not payed".
   * @param f       Fencer which will be marked
   * @param paid    If TRUE fencer will be marked as "has payed"
   * @throws SQLException 
   */
  public void setEntryFee (iFencer f, boolean paid) throws SQLException;

  /**
   * This functions marks a fencer as "equipment got checked" or "equipment got not checked".
   * @param f       Fencer which will be marked
   * @param checked If TRUE fencer will be marked as "equipment got checked"
   * @throws SQLException 
   */
  public void setEquipmentCheck (iFencer f, boolean checked) throws SQLException;

  /**
   * This function checks if a fencer is marked as "has payed"
   * @param f   Fencer which will be checked
   * @return    TRUE if the fencer is marked as "has payed"
   * @throws SQLException 
   */
  public boolean getEntryFee (iFencer f) throws SQLException;

  /**
   * This function checks if a fencer is marked as "equipment got checked"
   * @param f   Fencer which will be checked
   * @return    TRUE if the fencer is marked as "equipment got checked"
   * @throws SQLException 
   */
  public boolean getEquipmentCheck (iFencer f) throws SQLException;

  /**
   * This function retourns all preliminary fights of this tournament
   * @return    All preliminary fights
   * @throws SQLException 
   */
  public List<iPreliminary> getAllPreliminary () throws SQLException;

  /**
   * This function returns the number of preliminary fights.
   * @return    number of fights
   * @throws SQLException 
   */
  public int getPreliminaryCount () throws SQLException;

  /**
   * This function returns all preliminary fights a an array, which represents
   * an schedule. The first parameter is the round, the second parameter the
   * lane in which will be fought.
   * WARNING:   The schedule will MOST LIKELY contain NULL-pointer.
   * @return    Schedule as array
   * @throws SQLException 
   */
  public iPreliminary[][] getPreliminarySchedule () throws SQLException;

  /**
   * Gets the score of a fencer for the preliminarys.
   * @param f   Fencer
   * @return    Score
   */
  public iScore getScoreFromPrelim (iFencer f);

  /**
   * Gets the score of a fencer for the finals.
   * @param f   Fencer
   * @return    Score
   */
  public iScore getScoreFromFinal (iFencer f);

  /**
   * This function returns the scores of the preliminary for all fencer.
   * @return    The scores
   * @throws SQLException 
   */
  public List<iScore> getScoresPrelim () throws SQLException;

  /**
   * This function returns the scores of the finals for all fencer.
   * @return    The scores
   * @throws SQLException 
   */
  public List<iScore> getScoresFinal () throws SQLException;

  /**
   * This function returns the preliminary scores for all fencers sorted in the
   * respectiv groups they fought in.
   * @return    Array of scores representing the groups
   * @throws SQLException 
   */
  public List<iScore>[] getScoresInGroups () throws SQLException;

  /**
   * This function retourns all fights of the final.
   * @return    Fights of the final
   */
  public List<iFinalround> getAllFinalrounds ();

  /**
   * Returns the number of yellow cards a fencer was given this tournament.
   * @param f   Fencer which got the cards
   * @return    number of cards
   * @throws ObjectDeprecatedException 
   */
  public int getYellowFor (iFencer f) throws ObjectDeprecatedException;

  /**
   * Returns the number of red cards a fencer was given this tournament.
   * @param f   Fencer which got the cards
   * @return    number of cards
   * @throws ObjectDeprecatedException 
   */
  public int getRedFor (iFencer f) throws ObjectDeprecatedException;

  /**
   * Returns the number of black cards a fencer was given this tournament.
   * @param f   Fencer which got the cards
   * @return    number of cards
   * @throws ObjectDeprecatedException 
   */
  public int getBlackFor (iFencer f) throws ObjectDeprecatedException;

  /**
   * This function returns the comment which is made for this fencer.
   * @param f   The fencer
   * @return    The comment
   * @throws SQLException 
   */
  public String getComment (iFencer f) throws SQLException;

  /**
   * This functions saves a comment for the given fencer.
   * @param f       The fencer which is assosiated
   * @param comment The comment 
   * @throws SQLException 
   */
  public void setComment (iFencer f, String comment) throws SQLException;

  /**
   * This function deletes the tournamen. All will be gone.
   * WARNING:   Be carefull.
   * @throws SQLException 
   */
  public void delete () throws SQLException;
  
  
  
  /**
   * This function changes the status of the Tournament from preparing phase to
   * preliminary.
   * @throws SQLException 
   */
  public void startPreliminary() throws SQLException;
  
  /**
   * This function will change the status of the tournament back from preliminary
   * to preparing phase.
   * WARNING:   All data enterd in the preliminary phase will be lost.
   * @throws SQLException 
   */
  public void abortPreliminary() throws SQLException;
  
  /**
   * This function will change the status of the tournament from preliminary to
   * final.
   * @throws SQLException
   * @throws ObjectDeprecatedException 
   */
  public void startFinalrounds() throws SQLException, ObjectDeprecatedException;
  
  /**
   * This function will change the status of the tournament back from final
   * to preliminary.
   * WARNING:   All data enterd in the final phase will be lost.
   * @throws SQLException 
   */
  public void abortFinalrounds() throws SQLException;
  
  /**
   * This function will change the satus of the tournament from final to finished.
   * @throws SQLException 
   */
  public void finishTournament() throws SQLException;
  
  /**
   * This function will change the status of the tournament back from finished
   * to final.
   * @throws SQLException 
   */
  public void reopenFinalrounds() throws SQLException;
  
  /**
   * This function returns true if the tournament is in the preparing phase.
   * @return    A boolean value
   */
  public boolean isPreparingPhase();
  
  /**
   * This function returns true if the tournament is in the preliminary phase.
   * @return    A boolean value
   */
  public boolean isPreliminaryPhase();
    
  /**
   * This function returns true if the tournament is in the final phase.
   * @return    A boolean value
   */
  public boolean isFinalPhase();
  
  /**
   * This function returns true if the tournament is finished.
   * @return    A boolean value
   */
  public boolean isFinished();
}
