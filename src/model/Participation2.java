package model;

import model.DBConnection.DBParticipationRepresenter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.DBConnection.DBEntetyRepresenter;

public class Participation2 implements DBEntetyRepresenter
{
    @Override
    public void init() throws SQLException
    {
        DBParticipationRepresenter.createTable();
        DBParticipationRepresenter.loadParticipations();
    }

    @Override
    public void onStartUp() throws SQLException
    {
        for (Map.Entry<Integer, Participation2> entry : participations.entrySet())
        {
            entry.getValue().updateRelations();
        }
    }

    @Override
    public void onExit()
    {
        Map<Integer, Participation2> tmp = participations;
        participations = new HashMap<>();
        
        for (Map.Entry<Integer, Participation2> entry : tmp.entrySet())
        {
            entry.getValue().invalidate();
        }
    }
    //#########################################################################
    
    private static Map<Integer, Participation2> participations = new HashMap<>();
    
    public static Map<Integer, Participation2> getAllParticipantsForTournament(Tournament t)
    {
        Map<Integer, Participation2> ret = new HashMap<>();
        for (Map.Entry<Integer, Participation2> entry : participations.entrySet())
        {
            if(entry.getValue().tournamentID == t.getID())
                ret.put(entry.getValue().fencerID ,entry.getValue());
        }
        return ret;
    }
    
    public static List<Participation2> getParticipantsFromGroup(Tournament t, int group)
    {
        List<Participation2> ret = new ArrayList<>();
        for (Map.Entry<Integer, Participation2> entry : participations.entrySet())
        {
            if(entry.getValue().group == group)
                ret.add(entry.getValue());
        }
        return ret;
    }
    
    //#########################################################################
    
    private Integer ID = null;
    
    private boolean isValid = true;
    
    private Tournament t = null;
    private Integer tournamentID = null;
    private Fencer f = null;
    private Integer fencerID = null;
    private Integer group = null;
    private Boolean entryFee = null;
    private Boolean equipmentChecked = null;
    private Boolean dropedOut = null;
    private String comment = null;
    
    /**
     * DON'T USE THIS!
     * IT IS FOR THE USE OF THE INTERFACE ONLY AND WILL CRASH THE PROGRAMM IF 
     * USED OTHERWISE.
     */
    public Participation2(){}
    
    public Participation2(Tournament t, Fencer f, int group) throws SQLException
    {
        this.ID = DBParticipationRepresenter.createParticipation(t.getID(), f.getID(), group);
        participations.put(ID, this);
        
        this.t = t;
        this.tournamentID = t.getID();
        this.f = f;
        this.fencerID = f.getID();
        this.group = group;
        
        entryFee = false;
        equipmentChecked = false;
        dropedOut = false;
        comment = "";
        
        //No h2 supportet default value for clob found so we have to set it manuly
        setComment("");
    }
    
    public Participation2(Map<String, Object> set) throws ObjectExistException
    {
        this.ID = (Integer) set.get("ID");

        if (participations.containsKey(this.ID))
        {
            throw new ObjectExistException(participations.get(this.ID));
        }
        participations.put(this.ID, this);

        this.tournamentID = (Integer) set.get("TurnierID".toUpperCase());
        this.fencerID = (Integer) set.get("FechterID".toUpperCase());
        this.group = (Integer) set.get("Gruppe".toUpperCase());
        this.entryFee = (Boolean) set.get("Startgeld".toUpperCase());
        this.equipmentChecked = (Boolean) set.get("Ausruestungskontrolle".toUpperCase());
        this.dropedOut = (Boolean) set.get("Ausgeschieden".toUpperCase());
        this.comment = (String) set.get("Kommentar".toUpperCase());
    }
    
    private void updateRelations()
    {
        t = Tournament.getTournament(this.tournamentID);
        f = Fencer.getFencer(this.fencerID);
    }
    
    public Fencer getFencer()
    {
        if(!isValid) throw new ObjectDeprecatedException();
        return f;
    }
    
    public int getGroup()
    {
        if(!isValid) throw new ObjectDeprecatedException();
        return group;
    }
    
    public int getID()
    {
        if(!isValid) throw new ObjectDeprecatedException();
        return ID;
    }
    
    public void delete() throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        DBParticipationRepresenter.deleteParticipation(ID);
        participations.remove(ID);
        
        ID=-1;
        isValid = false;
    }
    
    public void setEntryFee(boolean paid) throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        DBParticipationRepresenter.updateEntryFee(ID, paid);
        this.entryFee = paid;
    }
    
    public void setEquipmentCheck(boolean checked) throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        DBParticipationRepresenter.updateEquepmentCheck(ID, checked);
        this.equipmentChecked = checked;
    }
    
    public boolean getEntryFee()
    {
        if(!isValid) throw new ObjectDeprecatedException();
        return this.entryFee;
    }
    
    public boolean getEquipmentCheck()
    {
        if(!isValid) throw new ObjectDeprecatedException();
        return this.equipmentChecked;
    } 
    
    public String getComment()
    {
        if(!isValid) throw new ObjectDeprecatedException();
        return comment;
    }
    
    public void setComment(String comment) throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        DBParticipationRepresenter.updateComment(ID, comment);
        this.comment = comment;
    }
    
    private void invalidate()
    {
        isValid = false;
        ID = -1;
    }
}
