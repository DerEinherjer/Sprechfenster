package model.rounds;

import model.ObjectDeprecatedException;
import model.iFencer;
import model.iTournament;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author deus
 */
public interface iRound 
{
     /**
         * This function returns the index of the group in witch the fencers are.
         * group number  = index + 1.
         * @return  Index of the group.
         * @throws ObjectDeprecatedException The preliminare got deleted.
         */
	public int getGroup() throws ObjectDeprecatedException;
        
        /**
         * This function returns a list of all feners who are assigned to this fight.
         * @return  List of fencers.
         * @throws ObjectDeprecatedException The preliminare got deleted.
         */
	public List<iFencer> getFencer() throws ObjectDeprecatedException;
        
        /**
         * This function returns the round in wich the fight is fought.
         * @return Index of the assigned round.
         * @throws ObjectDeprecatedException The preliminare got deleted.
         */
	public int getRound() throws ObjectDeprecatedException;
        
        /**
         * This function returns the lane this fight is assigned to.
         * @return Index of the lane;
         * @throws ObjectDeprecatedException The preliminare got deleted.
         */
	public int getLane() throws ObjectDeprecatedException;
        
        /**
         * This function sets the round in wich and the lane on wich this fight will be fought.
         * @param round Index of the round.
         * @param lane  Index of the lane.
         * @return Returns if the slot was free and the time is set arcording to the parameters.
         * @throws SQLException Cloud not be updated in the database.
         * @throws ObjectDeprecatedException The preliminare got deleted.
         */
	public boolean setTime(int round, int lane) throws SQLException, ObjectDeprecatedException;
	
        /**
         * This function marks the fight as fought. Most of the setter will stop working.
         * @param finisch   true = finished; false = unfinished
         * @throws SQLException Cloud not be updated in the database.
         * @throws ObjectDeprecatedException The preliminare got deleted.
         */
	public void setFinished(boolean finisch) throws SQLException, ObjectDeprecatedException;
	
        /**
         * This functions sets the point for a given fencer in this fight.
         * @param f         The fencer that got the points.
         * @param points    Number of points the fencer got.
         * @throws SQLException Cloud not be updated in the database.
         * @throws ObjectDeprecatedException The preliminare got deleted.
         */
	public void setPoints(iFencer f, int points) throws SQLException, ObjectDeprecatedException;
        
        /**
         * This function returns the points that a given fencer earned in this fight.
         * @param f The fencer.
         * @return  The points the fencer earned.
         * @throws ObjectDeprecatedException The preliminare got deleted.
         */
	public int getPoints(iFencer f) throws ObjectDeprecatedException;
        /**
         * This function returns the points wich the OTHER fencer earned.
         * @param f Fencer who got hit.
         * @return  Points wich was earned.
         * @throws ObjectDeprecatedException The preliminare got deleted.
         */
	public int getOpponentPoints(iFencer f) throws ObjectDeprecatedException;
	
        /**
         * This function returns the status of the fight.
         * @return  true = finished; false = unfinished
         * @throws ObjectDeprecatedException The preliminare got deleted.
         */
	public boolean isFinished() throws ObjectDeprecatedException;
        
        /**
         * This funktion returns the winner of the fight. if the fight is not finished it will return null;
         * @return  Winner of the fight.
         * @throws ObjectDeprecatedException The preliminare got deleted.
         */
	public iFencer getWinner() throws ObjectDeprecatedException;
	
         /**
         * This funktion returns the loser of the fight. if the fight is not finished it will return null;
         * @return  Loser of the fight.
         * @throws ObjectDeprecatedException The preliminare got deleted.
         */
        public iFencer getLoser() throws ObjectDeprecatedException;
        
        /**
         * This function returns the turnament wich the fight belongs to.
         * @return  The tournament.
         * @throws ObjectDeprecatedException The preliminare got deleted.
         */
	public iTournament getTournament() throws ObjectDeprecatedException;
	
        /**
         * This Function removes a given fencer from the fight.
         * @param f Fencer wich will be removed.
         * @return Returens true if the fencer got removed.
         * @throws SQLException Cloud not be updated in the database.
         * @throws ObjectDeprecatedException The preliminare got deleted.
         */
	public boolean removeParticipant(iFencer f) throws SQLException, ObjectDeprecatedException;
        
        /**
         * This function will add a fencer to the fight if there a less than two and hes is not allready part of it.
         * @param f Fencer who will be added.
         * @return Returns true if the fencer got added.
         * @throws SQLException Cloud not be updated in the database.
         * @throws ObjectDeprecatedException The preliminare got deleted.
         */
	public boolean addParticipant(iFencer f) throws SQLException, ObjectDeprecatedException;
        
        /**
         * This function switches one fenecr out for an other one. This only works if the first one participates and the second one do not.
         * @param out   Fenecr who will be switched out.
         * @param in    Fencer who will be switched in.
         * @return  Returns true if the fencers got switched.
         * @throws SQLException Cloud not be updated in the database.
         * @throws ObjectDeprecatedException The preliminare got deleted.
         */
	public boolean switchParticipantOut(iFencer out, iFencer in) throws SQLException, ObjectDeprecatedException;
        
        /**
         * This function returns true if the fencer participates in this fight.
         * @param f Fencer for who clarity is needed.
         * @return  Returns true if fencer participates.
         * @throws ObjectDeprecatedException The preliminare got deleted.
         */
	public boolean isFencer(iFencer f) throws ObjectDeprecatedException;
	
        /**
         * This function sets the number of yellow cards for a fencer in this fight.
         * @param f     Fenecr who got the cards.
         * @param count Number of yellow cards the fencer got;
         * @throws SQLException Cloud not be updated in the database.
         * @throws ObjectDeprecatedException The preliminare got deleted.
         */
	public void setYellow(iFencer f, int count) throws SQLException, ObjectDeprecatedException;
	
        /**
         * This function sets the number of red cards for a fencer in this fight.
         * @param f     Fenecr who got the cards.
         * @param count Number of red cards the fencer got;
         * @throws SQLException Cloud not be updated in the database.
         * @throws ObjectDeprecatedException The preliminare got deleted.
         */
        public void setRed(iFencer f, int count) throws SQLException, ObjectDeprecatedException;
	
        /**
         * This function sets the number of black cards for a fencer in this fight.
         * @param f     Fenecr who got the cards.
         * @param count Number of black cards the fencer got;
         * @throws SQLException Cloud not be updated in the database.
         * @throws ObjectDeprecatedException The preliminare got deleted.
         */
        public void setBlack(iFencer f, int count) throws SQLException, ObjectDeprecatedException;
	
        /**
         * This function returns the nummber of yellow cards the fencer got in this fight.
         * @param f Fenecr who got the cards.
         * @return  Number of yellow cards the fencer got.
         * @throws ObjectDeprecatedException The preliminare got deleted.
         */
        public int getYellow(iFencer f) throws ObjectDeprecatedException;
        
        /**
         * This function returns the nummber of red cards the fencer got in this fight.
         * @param f Fenecr who got the cards.
         * @return  Number of red cards the fencer got.
         * @throws ObjectDeprecatedException The preliminare got deleted.
         */
	public int getRed(iFencer f) throws ObjectDeprecatedException;
        
        /**
         * This function returns the nummber of black cards the fencer got in this fight.
         * @param f Fenecr who got the cards.
         * @return  Number of black cards the fencer got.
         * @throws ObjectDeprecatedException The preliminare got deleted.
         */
	public int getBlack(iFencer f) throws ObjectDeprecatedException;
	
        /**
         * This function deletes the preliminary this object is representing and marks the object as depricated.
         * @throws SQLException Number of black cards the fencer got.
         * @throws ObjectDeprecatedException Allready delete, genius.
         */
	public void delete() throws SQLException, ObjectDeprecatedException;
}
