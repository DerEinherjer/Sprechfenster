package Model;

import java.util.ArrayList;
import java.util.List;

public class Preliminary implements iPreliminary
{
	private int ID;
	private DBConnector con;
	
	private Integer tournamentID = null;
	private Integer group = null;
	private Fencer fencer1 = null;
	private Fencer fencer2 = null;
	
	static String getSQLString()
	{
		return "CREATE TABLE Vorrunden( ID int NOT NULL AUTO_INCREMENT UNIQUE,"
				+ "TurnierID int,"
				+ "Gruppe int,"
				+ "TerminID int,"
				+ "Teilnehmer1 int,"
				+ "Teilnehmer2 int,)";
	}
	
	public Preliminary(int id, DBConnector con) 
	{
		this.ID = id;
		this.con = con;
	}

	public void initTurnamentID(int id) {if(this.tournamentID == null) this.tournamentID = id;}
	public void initGroup(int group) {if(this.group == null) this.group = group;}
	public void initFencer1(Fencer f) {if(this.fencer1 == null) this.fencer1 = f;}
	public void initFencer2(Fencer f) {if(this.fencer2 == null) this.fencer2 = f;}
	
	public int getGroup(){return group;}
	public List<Fencer> getFencer(){List<Fencer> ret = new ArrayList<>();ret.add(fencer1);ret.add(fencer2);return ret;}

}
