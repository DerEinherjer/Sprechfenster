package Model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tournament implements iTournament
{
	
	private int ID;
	private DBConnector con;
	
	private String name = null;
	private String date = null;
	private Integer groups = null;
	private Integer finalrounds = null;
	private Integer lanes = null;
	
	static String getSQLString()
	{
		return "CREATE TABLE Turniere (ID int NOT NULL AUTO_INCREMENT UNIQUE,"
				   + "Name varchar(255),"
				   + "Datum varchar(11),"
				   + "Gruppen int,"
				   + "Finalrunden int,"
				   + "Bahnen int);";
	}
	
	Tournament(int id, DBConnector con)
	{
		this.ID = id;
		this.con = con;
	}
	
	void initName(String name){if(this.name == null) this.name = name;}
	void initDate(String date){if(this.date == null) this.date = date;}
	void initGroups(int groups){if(this.groups == null) this.groups = groups;}
	void initFinalRounds(int rounds){if(this.finalrounds == null) this.finalrounds = rounds;}
	void initLanes(int lanes){if(this.lanes == null) this.lanes = lanes;}
	
	int getID(){return ID;}
	public String getName(){return name;}
	public String getDate(){return date;}
	public int getGroups(){return groups;}
	public int getFinalRounds(){return finalrounds;}
	public int getLanes(){return lanes;}

	public void setName(String name) throws SQLException
	{
		con.tournamentSetName(name, ID);
		this.name = name;
	}
	
	public void setDate(String date) throws SQLException
	{
		con.tournamentSetDate(date, ID);
		this.date = date;
	}
	
	public void setGroups(int groups) throws SQLException
	{
		con.tournamentSetGroups(groups, ID);
		this.groups = groups;
	}
	
	public void setFinalRounds(int rounds) throws SQLException
	{
		con.tournamentSetFinalRounds(rounds, ID);
		this.finalrounds = rounds;
	}
	
	public void setLanes(int lanes) throws SQLException
	{
		con.tournamentSetLanes(lanes, ID);
		this.lanes = lanes;
	}
	
	public String toString()
	{
		return ID+" | "+name;
	}
	
	public void addParticipant(iFencer f) throws SQLException
	{
		if(!(f instanceof Fencer)) return;
		int counts[] = con.getGroupsMemberCount(this); 
		int index = 0;
		for(int i = 1; i< counts.length; i++)
			if(counts[i]<counts[index])
				index = i;
		addParticipant(f, index+1);
	}
	
	public void addParticipant(iFencer f, int group) throws SQLException
	{
		if(!(f instanceof Fencer)) return;
		con.addParticipant(this, (Fencer)f, group);
	}

	private Map<Integer, Preliminary> preliminarys = new HashMap<>();
	public List<Preliminary> getAllPreliminary() throws SQLException 
	{
		List<Preliminary> ret = new ArrayList<>();
		for(Integer integer : con.getTournamentPreliminarys(this))
		{
			if(!preliminarys.containsKey(integer))
				preliminarys.put(integer, con.loadPreliminary(integer, this));
			ret.add(preliminarys.get(integer));
		}
		return ret;
	}
	
	public boolean isParticipant(iFencer f) throws SQLException
	{
		if(!(f instanceof Fencer)) return false;
		return con.isFencerParticipant(this, (Fencer)f);
	}
}

