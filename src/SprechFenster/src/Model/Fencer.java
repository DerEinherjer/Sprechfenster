package Model;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

class Fencer implements iFencer
{
	// -----
	private static DBConnector con = DBConnector.getInstants();
	private static Map<Integer, Fencer> fencers = new HashMap<>();
	
	static Fencer getFencer(int id) throws SQLException
	{
		if(!fencers.containsKey(id))
			con.loadFencer(id);
		return fencers.get(id);
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
		return "CREATE TABLE Fechter (ID int NOT NULL AUTO_INCREMENT UNIQUE,"
				   + "Vorname varchar(255),"
				   + "Nachname varchar(255),"
				   + "Geburtstag varchar(255),"
				   + "Fechtschule varchar(255),"
				   + "Nationalitaet varchar(255));";
	}
	
	Fencer(Map<String, Object> set, DBConnector con) throws ObjectExistExeption
	{
		this.ID = (Integer)set.get("ID");
		this.con = con;
		
		if(fencers.containsKey(this.ID))
			throw new ObjectExistExeption(fencers.get(this.ID));	
		fencers.put(this.ID, this);
		
		this.name = (String) set.get("Vorname");
		this.familyName = (String) set.get("Nachname");
		this.birthday = (String) set.get("Geburtstag");
		this.fencingSchool = (String) set.get("Fechtschule");
		this.nationality = (String) set.get("Nationalitaet");
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
		con.fencerSetName(name, ID);
		this.name = name;
	}
	
	public void setFamilyName(String name) throws SQLException
	{
		con.fencerSetFamilyName(name, ID);
		this.familyName = name;
	}
	
	public void setBirthday(String date) throws SQLException
	{
		con.fencerSetBirthday(date, ID);
		this.birthday = date;
	}
	
	public void setFencingSchool(String school) throws SQLException
	{
		con.fencerSetFencingSchool(school, ID);
		this.fencingSchool=school;
	}
	
	public void setNationality(String nation) throws SQLException
	{
		con.fencerSetNationality(nation, ID);
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
