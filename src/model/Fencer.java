package model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import model.DBConnection.DBEntetyRepresenter;
import model.DBConnection.DBFencerRepresenter;


public class Fencer extends Observable implements DBEntetyRepresenter, iFencer
{
    @Override
    public void init() throws SQLException
    {
        DBFencerRepresenter.createTable();
        DBFencerRepresenter.loadFencer();
    }

    @Override
    public void onStartUp() {}
    
    @Override
    public void onExit()
    {
        Map<Integer, Fencer> tmp = fencers;
        fencers = new HashMap<>();
        
        for (Map.Entry<Integer, Fencer> entry : tmp.entrySet())
        {
            entry.getValue().invalidate();
        }
    }
    
    //#########################################################################
    
    static Sync sync;
    private static Map<Integer, Fencer> fencers = new HashMap<>();
    
    static List<Fencer> getAllFencer()
    {
        List<Fencer> ret = new ArrayList<>();
        for(Map.Entry<Integer, Fencer> entry : fencers.entrySet())
        {
            ret.add(entry.getValue());
        }
        return ret;
    }
    
    public static Fencer getFencer(int id)
    {
        return fencers.get(id);
    }
    
    private void invalidate()
    {
        ID = -1;
        isValid = false;
    }
    
    //#########################################################################
    
    
    private int ID;

    private boolean isValid = true;

    private String name = null;
    private String familyName = null;
    private String birthday = null;
    private String fencingSchool = null;
    private String nationality = null;
    
    /**
     * DON'T USE THIS!
     * IT IS FOR THE USE OF THE INTERFACE ONLY AND WILL CRASH THE PROGRAMM IF 
     * USED OTHERWISE.
     */
    public Fencer(){}
    
    public Fencer(Map<String, Object> set) throws ObjectExistException 
    {
        this.ID = (Integer) set.get("ID");

        if (fencers.containsKey(this.ID)) 
            throw new ObjectExistException(fencers.get(this.ID));
        
        fencers.put(this.ID, this);

        this.name = (String) set.get("Vorname".toUpperCase());
        this.familyName = (String) set.get("Nachname".toUpperCase());
        this.birthday = (String) set.get("Geburtstag".toUpperCase());
        this.fencingSchool = (String) set.get("Fechtschule".toUpperCase());
        this.nationality = (String) set.get("Nationalitaet".toUpperCase());
    }
    
    public Fencer (String firstname, String familyName) throws SQLException
    {
        this.ID = DBFencerRepresenter.createFencer(firstname, familyName);
        fencers.put(ID, this);
        
        this.name = firstname;
        this.familyName = familyName;
        this.birthday = "1970-01-01";
        this.fencingSchool = "Nicht Angegeben";
        this.nationality = "Nicht Angegeben";
        
        //sync.observeThis(this);

        setChanged();
        notifyObservers(new EventPayload(this, EventPayload.Type.fencerCreated));
    }
    
    @Override
    public int getID()
    {
        if(!isValid) throw new ObjectDeprecatedException();
        return ID;
    }

    @Override
    public String getName () 
    {
        if(!isValid) throw new ObjectDeprecatedException();
        return name;
    }

    @Override
    public String getFamilyName()
    {
        if(!isValid) throw new ObjectDeprecatedException();
        return familyName;
    }

    @Override
    public String getFullName()
    {
        if(!isValid) throw new ObjectDeprecatedException();
        return name + " " + familyName;
    }

    @Override
    public String getBirthday()
    {
        if(!isValid) throw new ObjectDeprecatedException();
        return birthday;
    }

    @Override
    public String getFencingSchool()
    {
        if(!isValid) throw new ObjectDeprecatedException();
        return fencingSchool;
    }

    @Override
    public String getNationality()
    {
        if(!isValid) throw new ObjectDeprecatedException();
        return nationality;
    }

    @Override
    public void setName (String name) throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        DBFencerRepresenter.fencerSetName(name, ID);
        this.name = name;
        
        setChanged();
        notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
    }

    @Override
    public void setFamilyName (String name) throws SQLException 
    {
        if(!isValid) throw new ObjectDeprecatedException();
        DBFencerRepresenter.fencerSetFamilyName(name, ID);
        this.familyName = name;
        
        setChanged();
        notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
    }

    @Override
    public void setBirthday (String date) throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        DBFencerRepresenter.fencerSetBirthday(date, ID);
        this.birthday = date;
        
        setChanged();
        notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
    }

    @Override
    public void setFencingSchool (String school) throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        DBFencerRepresenter.fencerSetFencingSchool(school, ID);
        this.fencingSchool = school;
        
        setChanged();
        notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
  }

    @Override
    public void setNationality (String nation) throws SQLException
    {
        if(!isValid) throw new ObjectDeprecatedException();
        DBFencerRepresenter.fencerSetNationality(nation, ID);
        this.nationality = nation;
        
        setChanged();
        notifyObservers(new EventPayload(this, EventPayload.Type.valueChanged));
    }

    @Override
    public void delete () throws SQLException
    {
        if (!isValid) {
          return;
        }
        
        for (Tournament t : Tournament.getAllTournaments())
        {
            if (t.isParticipant(this))
            {
                return;
            }
        }

        setChanged();
        notifyObservers(new EventPayload(this, EventPayload.Type.fencerDeleted));
    
        fencers.remove(ID);
        DBFencerRepresenter.removeFencer(ID);
        ID = -1;
        isValid = false;
    }
    
    
    
    @Override
    public boolean equals (Object other)
    {
        if(!isValid) return false;
        
        if(other == null)
        {
            return false;
        }
        if(other == this)
        {
            return true;
        }
        if(!(other instanceof Fencer)) 
        {
            return false;
        }
        if(((Fencer) other).getID() == ID)
        {
            return true;
        }
        return false;
  }
}
