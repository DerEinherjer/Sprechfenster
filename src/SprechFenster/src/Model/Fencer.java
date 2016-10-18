package Model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Fencer implements iFencer
{
	// -----
	//private static DBConnector con = DBConnector.getInstants();
	private static Sync sync = (Sync)iSync.getInstance();
	private static Map<Integer, Fencer> fencers = new HashMap<>();
	
	static Fencer getFencer(int id) throws SQLException
	{
		if(!fencers.containsKey(id))
			sync.loadFencer(id);
		return fencers.get(id);
	}
	
	static List<Fencer> getFencer(Tournament t) throws SQLException
	{
		List<Fencer> ret = new ArrayList<>();
		for(Map.Entry<Integer, Fencer> entry : fencers.entrySet())
			if(sync.isFencerParticipant(t, entry.getValue()))
				ret.add(entry.getValue());
		return ret;
	}
	
	static List<Fencer> getAllFencer()
	{
		List<Fencer> ret = new ArrayList<>();
		for(Map.Entry<Integer, Fencer> entry : fencers.entrySet())
			ret.add(entry.getValue());
		return ret;
	}
	// -----
	private int ID;
	
	private String name = null;
	private String familyName = null;
	private String birthday = null;
	private String fencingSchool = null;
	private String nationality = null;
	
	static String getSQLString()
	{
		return "CREATE TABLE IF NOT EXISTS Fechter (ID int NOT NULL AUTO_INCREMENT UNIQUE,"
				   + "Vorname varchar(255) DEFAULT 'Max',"
				   + "Nachname varchar(255) DEFAULT 'Musterman',"
				   + "Geburtstag varchar(255) DEFAULT '1970-01-01',"
				   + "Fechtschule varchar(255) DEFAULT 'Keine Fechtschule',"
				   + "Nationalitaet varchar(255) DEFAULT 'Nicht Angegeben');";
	}
	
	Fencer(Map<String, Object> set, DBConnector con) throws ObjectExistExeption
	{
		this.ID = (Integer)set.get("ID");
		
		if(fencers.containsKey(this.ID))
			throw new ObjectExistExeption(fencers.get(this.ID));	
		fencers.put(this.ID, this);
		
		this.name = (String) set.get("Vorname".toUpperCase());
		this.familyName = (String) set.get("Nachname".toUpperCase());
		this.birthday = (String) set.get("Geburtstag".toUpperCase());
		this.fencingSchool = (String) set.get("Fechtschule".toUpperCase());
		this.nationality = (String) set.get("Nationalitaet".toUpperCase());
	}
	
	int getID(){return ID;}
	public String getName(){return name;}
	public String getFamilyName(){return familyName;}
    public String getFullName() {return name+" "+familyName;}
	public String getBirthday(){return birthday;}
	public String getFencingSchool(){return fencingSchool;}
	public String getNationality(){return nationality;}
	
	public void setName(String name) throws SQLException
	{
		sync.fencerSetName(name, ID);
		this.name = name;
	}
	
	public void setFamilyName(String name) throws SQLException
	{
		sync.fencerSetFamilyName(name, ID);
		this.familyName = name;
	}
	
	public void setBirthday(String date) throws SQLException
	{
		sync.fencerSetBirthday(date, ID);
		this.birthday = date;
	}
	
	public void setFencingSchool(String school) throws SQLException
	{
		sync.fencerSetFencingSchool(school, ID);
		this.fencingSchool=school;
	}
	
	public void setNationality(String nation) throws SQLException
	{
		sync.fencerSetNationality(nation, ID);
		this.nationality=nation;
	}
	
	
	public String toString()
	{
		return ID+" | "+name+" | "+familyName; 
	}
	
	@Override
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof Fencer))return false;
	    if(((Fencer)other).getID()==ID)
	    	return true;
	    return false;
	}
	
}
