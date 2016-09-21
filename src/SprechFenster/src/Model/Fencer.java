package Model;

import java.sql.SQLException;

class Fencer implements iFencer
{
	private int ID;
	private DBConnector con;
	
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
	
	Fencer(int id, DBConnector con)
	{
		this.ID = id;
		this.con = con;
	}
	
	void initName(String name){if(this.name==null)this.name=name;}
	void initFamilyName(String name){if(this.familyName==null)this.familyName=name;}
	void initBirthday(String date){if(this.birthday==null)this.birthday=date;}
	void initFencingSchool(String school){if(this.fencingSchool==null)this.fencingSchool=school;}
	void initNationality(String nation){if(this.nationality==null)this.nationality=nation;}
	
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
	
}
