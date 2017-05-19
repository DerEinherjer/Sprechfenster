package Model.Rounds;

import Model.Fencer;
import Model.ObjectDeprecatedException;
import Model.ObjectExistException;
import static Model.Rounds.Preliminary.sync;
import Model.Sync;
import Model.Tournament;
import Model.iFencer;
import Model.iTournament;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author deus
 */
public abstract class Round 
{
    
    public static String getSQLString()
    {
        return "CREATE TABLE IF NOT EXISTS Vorrunden (ID int NOT NULL AUTO_INCREMENT UNIQUE,"
             + "TurnierID int,"
             + "Gruppe int DEFAULT -1,"
             + "Runde int DEFAULT -1,"
             + "Bahn int DEFAULT -1,"
             + "Teilnehmer1 int DEFAULT -1,"
             + "Teilnehmer2 int DEFAULT -1,"
             + "PunkteVon1 int DEFAULT 0,"
             + "PunkteVon2 int DEFAULT 0,"
             + "Beendet boolean DEFAULT FALSE,"
             + "GelbVon1 int DEFAULT 0,"
             + "RotVon1 int DEFAULT 0,"
             + "SchwarzVon1 int DEFAULT 0,"
             + "GelbVon2 int DEFAULT 0,"
             + "RotVon2 int DEFAULT 0,"
             + "SchwarzVon2 int DEFAULT 0,"
             + "FinalStrucktur int DEFAULT -1);";
    }
    
    
    public static Sync sync;
    
    
    boolean isValid =true;
	
    int ID;
    Tournament t;
	
    Integer group = null;
    Integer round = null;
    Integer lane = null;
    Fencer fencer1 = null;
    Fencer fencer2 = null;
    Integer pointsFor1 = null;
    Integer pointsFor2 = null;
    Boolean finished = null;
	
    Integer yellowFor1 = null;
    Integer redFor1 = null;
    Integer blackFor1 = null;
    Integer yellowFor2 = null;
    Integer redFor2 = null;
    Integer blackFor2 = null;
    
    Round(Map<String, Object> set) throws  ObjectExistException, SQLException
    {
	this.ID = (Integer) set.get("ID");
        
        //The DB return all colum names in caps
	this.t = Tournament.getTournament((Integer) set.get("TurnierID".toUpperCase()));
		
	this.group = (Integer) set.get("Gruppe".toUpperCase());
	this.round = (Integer) set.get("Runde".toUpperCase());
	this.lane = (Integer) set.get("Bahn".toUpperCase());
	this.fencer1 = Fencer.getFencer((Integer)set.get("Teilnehmer1".toUpperCase())); 
	this.fencer2 = Fencer.getFencer((Integer)set.get("Teilnehmer2".toUpperCase()));
	this.pointsFor1 = (Integer) set.get("PunkteVon1".toUpperCase());
	this.pointsFor2 = (Integer) set.get("PunkteVon2".toUpperCase());
	this.finished = (Boolean) set.get("Beendet".toUpperCase());
    }
        
    public int getID() throws ObjectDeprecatedException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        return ID;
    }
    public int getGroup() throws ObjectDeprecatedException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        return group;
    }
    public int getRound() throws ObjectDeprecatedException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        return round;
    }
    public int getLane() throws ObjectDeprecatedException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        return lane;
    }
    public List<iFencer> getFencer() throws ObjectDeprecatedException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        List<iFencer> ret = new ArrayList<>();
        if(fencer1 != null)
            ret.add(fencer1);
        if(fencer2 != null)
            ret.add(fencer2);
        return ret;
    }
        
    public iTournament getTournament() throws ObjectDeprecatedException
    {
        if(!isValid) throw new ObjectDeprecatedException();
                
        return t;
    }
        
    public boolean setTime(int round, int lane) throws SQLException, ObjectDeprecatedException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        if(finished || t.isPreliminaryFinished()) return false;
                
        if(sync.setTime(this, round, lane))
        {
            this.round = round;
            this.lane = lane;
            return true;
        }
        return false;
    }
        
    public void setPoints(iFencer f, int points) throws SQLException, ObjectDeprecatedException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        if(finished) return;
	if(fencer1.equals(f))
        {
            pointsFor1 = points;
        }
	if(fencer2.equals(f))
        {
            pointsFor2 = points;
        }	
        //set points in sync AFTER setting points in this object, since 
        //the sync also triggers the update notification for the observers!
	sync.setPoints(ID, ((Fencer)f).getID(), points);
	
    }
	
    public int getPoints(iFencer f) throws ObjectDeprecatedException
    {
        if(!isValid) throw new ObjectDeprecatedException();
                
        if(fencer1.equals(f))
            return pointsFor1;
        if(fencer2.equals(f))
            return pointsFor2;
        return -1;
    }
        
    public int getOpponentPoints(iFencer f) throws ObjectDeprecatedException
    {
        if(!isValid) throw new ObjectDeprecatedException();
                
        if(fencer1.equals(f))
            return pointsFor2;
        if(fencer2.equals(f))
            return pointsFor1;
        return -1;
    }
	
    public iFencer getWinner() throws ObjectDeprecatedException
    {
        if(!isValid) throw new ObjectDeprecatedException();
                
        if(finished)
        {
            if(pointsFor1>pointsFor2)
                return fencer1;
            if(pointsFor1<pointsFor2)
                return fencer2;
        }
        return null;
    }
        
    public boolean isFinished() throws ObjectDeprecatedException
    {
        if(!isValid) throw new ObjectDeprecatedException();
                
        return finished;
    }
        
    public boolean removeParticipant(iFencer f) throws SQLException, ObjectDeprecatedException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        if(finished || t.isPreliminaryFinished()) return false;
		
        if(fencer1!=null && fencer1.equals(f))
        {
            sync.removeParticipantFromPrelim(this, (Fencer) f);
            fencer1 = null;
            pointsFor1 = 0;
            return true;
        }
        else if(fencer2!=null && fencer2.equals(f))
        {
            sync.removeParticipantFromPrelim(this, (Fencer) f);
            fencer2 = null;
            pointsFor2 = 0;
            return true;
        }
        return false;
    }
	
    public boolean addParticipant(iFencer f) throws SQLException, ObjectDeprecatedException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        if(finished) return false;
		
        if(fencer1 == null)
        {
            sync.addParticipantToPrelim(this, (Fencer) f);
            fencer1 = (Fencer)f;
            return true;
        }
        else if(fencer2 == null)
        {
            sync.addParticipantToPrelim(this, (Fencer) f);
            fencer2 = (Fencer)f;
            return true;
        }
        return false;
    }
	
    public boolean switchParticipantOut(iFencer out, iFencer in) throws SQLException, ObjectDeprecatedException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        if(finished || t.isPreliminaryFinished()) return false;
		
	if(fencer1.equals(out)&&!fencer2.equals(in))
	{
            sync.switchParticipantsInPrelim(this, (Fencer) out, (Fencer) in);
            fencer1 = (Fencer)in;
            pointsFor1 = 0;
            return true;
        }
        else if(fencer2.equals(out)&&!fencer1.equals(in))
        {
            sync.switchParticipantsInPrelim(this, (Fencer) out, (Fencer) in);
            fencer2 = (Fencer)in;
            pointsFor2 = 0;
            return true;
        }
        return false;
    }
    
    public boolean isFencer(iFencer f) throws ObjectDeprecatedException
    {
        if(!isValid) throw new ObjectDeprecatedException();
                
        if(fencer1!=null && fencer1.equals(f))
			return true;
        if(fencer1!=null && fencer2.equals(f))
            return true;
        return false;
    }
    
    public void setYellow(iFencer f, int count) throws SQLException, ObjectDeprecatedException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        if(finished || t.isPreliminaryFinished()) return;
                
        if(f.equals(fencer1))
        {
            sync.setYellowPrelim(this, (Fencer)f, count);
            yellowFor1 = count;
        }
        else if(f.equals(fencer2))
        {
            sync.setYellowPrelim(this, (Fencer)f, count);
            yellowFor2 = count;
        }
    }
	
    public void setRed(iFencer f, int count) throws SQLException, ObjectDeprecatedException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        if(finished || t.isPreliminaryFinished()) return ;
                
        if(f.equals(fencer1))
        {
            sync.setRedPrelim(this, (Fencer)f, count);
            redFor1 = count;
        }
        else if(f.equals(fencer2))
        {
            sync.setRedPrelim(this, (Fencer)f, count);
            redFor2 = count;
        }
    }
	
    public void setBlack(iFencer f, int count) throws SQLException, ObjectDeprecatedException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        if(finished || t.isPreliminaryFinished()) return;
                
        if(f.equals(fencer1))
        {
            sync.setBlackPrelim(this, (Fencer)f, count);
            blackFor1 = count;
        }
        else if(f.equals(fencer2))
        {
            sync.setBlackPrelim(this, (Fencer)f, count);
            blackFor2 = count;
        }
    }
	
    public int getYellow(iFencer f) throws ObjectDeprecatedException
    {
        if(!isValid) throw new ObjectDeprecatedException();
                
        if(f.equals(fencer1))
            return yellowFor1;
        if(f.equals(fencer2))
            return yellowFor2;
        return -1;
    }
	
    public int getRed(iFencer f) throws ObjectDeprecatedException
    {
        if(!isValid) throw new ObjectDeprecatedException();
                
        if(f.equals(fencer1))
            return redFor1;
        if(f.equals(fencer2))
            return redFor2;
        return -1;
    }
	
    public int getBlack(iFencer f) throws ObjectDeprecatedException
    {
        if(!isValid) throw new ObjectDeprecatedException();
                
        if(f.equals(fencer1))
            return blackFor1;
        if(f.equals(fencer2))
            return blackFor2;
        return -1;
    }
    
    @Override
    public boolean equals(Object other)
    {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Preliminary))return false;
        try 
        {
            if(((Preliminary)other).getID()==ID)
                return true;
        } 
        catch (ObjectDeprecatedException e) {}
        return false;
    }
    
    Fencer getLoser()
    {
	if(finished)
	{
            if(pointsFor1>pointsFor2)
		return fencer2;
            if(pointsFor1<pointsFor2)
		return fencer1;
	}
	return null;
    }
}
