package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


//TODO: Alle Felder aller Tabellen müessen immer einen Wert haben um aus der Init Funktion keine Sicherheitslücke zu machen

public class DBConnector 
{
	private static String url = "jdbc:h2:~/SprechfensterData";
	private static DBConnector dbConnector;
	
	private Connection con;
	
	private DBConnector()
	{
		try 
		{
			Class.forName("org.h2.Driver");
			
			con = DriverManager.getConnection(url, "", "");
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		createTabels();
	}
	
	private void createTabels()
	{
		try 
		{
			con.prepareStatement(Fencer.getSQLString()).executeUpdate();
		} 
		catch (SQLException e) {}
		

		try 
		{
			con.prepareStatement(Tournament.getSQLString()).executeUpdate();
		} 
		catch (SQLException e) {}
		
		try 
		{
			con.prepareStatement(Participation.getSQLString()).executeUpdate();
		} 
		catch (SQLException e) {}
		
		try
		{
			con.prepareStatement(Preliminary.getSQLString()).executeUpdate();
		}
		catch (SQLException e) {}
	}
	
	static DBConnector getInstants()
	{
		if(dbConnector == null)
			dbConnector = new DBConnector();
		return dbConnector;
	}
	
	private PreparedStatement cfStmt = null;
	int createFencer(String firstname, String familyname) throws SQLException
	{
		if(firstname == null || familyname == null)
			return -1;
		
		if(cfStmt == null)
		{
			String sql = "INSERT INTO Fechter (Vorname, Nachname, Geburtstag, Fechtschule, Nationalitaet) VALUES (?, ?, '', '', '');";
			cfStmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		}
		
		cfStmt.setString(1, firstname);
		cfStmt.setString(2, familyname);
		cfStmt.executeUpdate();
		ResultSet rs = cfStmt.getGeneratedKeys();
		rs.next();//TODO
		return rs.getInt(1);
	}
	
	private PreparedStatement gfvStmt = null;
	Fencer loadFencer(int id) throws SQLException
	{
		if(id<0) return null;
		if(gfvStmt == null)
		{
			String sql = "SELECT * FROM Fechter WHERE ID = ?;";
			gfvStmt = con.prepareStatement(sql);
		}
		
		Fencer ret = new Fencer(id, this);
		
		gfvStmt.setInt(1, id);
		ResultSet rs = gfvStmt.executeQuery();
		rs.next();//TODO
		ret.initName(rs.getString("Vorname"));
		ret.initFamilyName(rs.getString("Nachname"));
		ret.initBirthday(rs.getString("Geburtstag"));
		ret.initFencingSchool(rs.getString("Fechtschule"));
		ret.initNationality(rs.getString("Nationalitaet"));
		rs.close();
		
		return ret;
	}
	
	private PreparedStatement gafStmt = null;
	List<Integer> getAllFencer() throws SQLException
	{
		if(gafStmt == null)
		{
			String sql = "SELECT ID FROM Fechter";
			gafStmt = con.prepareStatement(sql);
		}
		
		List<Integer> ret = new ArrayList<>();
		
		ResultSet rs = gafStmt.executeQuery();
		while(rs.next())
		{
			ret.add(rs.getInt("ID"));
		}
		rs.close();
		return ret;
	}
	
	private PreparedStatement fsnStmt = null;
	void fencerSetName(String name, int id) throws SQLException
	{
		if(fsnStmt == null)
		{
			String sql = "UPDATE Fechter SET Vorname = ? WHERE ID = ?;";
			fsnStmt = con.prepareStatement(sql);
		}
		fsnStmt.setString(1, name);
		fsnStmt.setInt(2, id);
		fsnStmt.executeUpdate();
	}
	
	private PreparedStatement fsfnStmt = null;
	void fencerSetFamilyName(String name, int id) throws SQLException
	{
		if(fsfnStmt == null)
		{
			String sql = "UPDATE Fechter SET Nachname = ? WHERE ID = ?;";
			fsfnStmt = con.prepareStatement(sql);
		}
		fsfnStmt.setString(1, name);
		fsfnStmt.setInt(2, id);
		fsfnStmt.executeUpdate();
	}
	
	private PreparedStatement fsbStmt = null;
	void fencerSetBirthday(String date, int id) throws SQLException
	{
		if(fsbStmt == null)
		{
			String sql = "UPDATE Fechter SET Geburtstag = ? WHERE ID = ?;";
			fsbStmt = con.prepareStatement(sql);
		}
		fsbStmt.setString(1, date);
		fsbStmt.setInt(2, id);
		fsbStmt.executeUpdate();
	}
	
	private PreparedStatement fsfsStmt = null;
	void fencerSetFencingSchool(String school, int id) throws SQLException
	{
		if(fsfsStmt == null)
		{
			String sql = "UPDATE Fechter SET Fechtschule = ? WHERE ID = ?;";
			fsfsStmt = con.prepareStatement(sql);
		}
		fsfsStmt.setString(1, school);
		fsfsStmt.setInt(2, id);
		fsfsStmt.executeUpdate();
	}
	
	private PreparedStatement fsnatStmt = null;
	void fencerSetNationality(String nation, int id) throws SQLException
	{
		if(fsnatStmt == null)
		{
			String sql = "UPDATE Fechter SET Nationalitaet = ? WHERE ID = ?;";
			fsnatStmt = con.prepareStatement(sql);
		}
		fsnatStmt.setString(1, nation);
		fsnatStmt.setInt(2, id);
		fsnatStmt.executeUpdate();
	}
	
	private PreparedStatement ctStmt = null;
	int createTournament(String name) throws SQLException
	{
		if(name == null)
			return -1;
		if(ctStmt == null)
		{
			String sql = "INSERT INTO Turniere (Name, Datum, Gruppen, Finalrunden, Bahnen) VALUES (?, '', 2, 2, 2);";
			ctStmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		}
		
		ctStmt.setString(1, name);
		return ctStmt.executeUpdate();
	}
	
	private PreparedStatement gtvStmt = null;
	Tournament loadTournament(int id) throws SQLException
	{
		if(id<0) return null;
		if(gtvStmt == null)
		{
			String sql = "SELECT * FROM Turniere WHERE ID = ?;";
			gtvStmt = con.prepareStatement(sql);
		}
		
		Tournament ret = new Tournament(id, this);
		
		gtvStmt.setInt(1, id);
		ResultSet rs = gtvStmt.executeQuery();
		rs.next();//TODO
		ret.initName(rs.getString("Name"));
		ret.initDate(rs.getString("Datum"));
		ret.initGroups(rs.getInt("Gruppen"));
		ret.initFinalRounds(rs.getInt("Finalrunden"));
		ret.initLanes(rs.getInt("Bahnen"));
		rs.close();
		
		return ret;
	}
	
	private PreparedStatement gatStmt = null;
	List<Integer> getAllTournaments() throws SQLException 
	{
		if(gatStmt == null)
		{
			String sql = "SELECT ID FROM Turniere;";
			gatStmt = con.prepareStatement(sql);
		}
		
		List<Integer> ret = new ArrayList<>();
		ResultSet rs = gatStmt.executeQuery();
		
		while(rs.next())
		{
			ret.add(rs.getInt("ID"));
		}
		return ret;
	}
	
	
	private PreparedStatement tsnStmt = null;
	void tournamentSetName(String name, int id) throws SQLException
	{
		if(tsnStmt == null)
		{
			String sql = "UPDATE Turniere SET Name = ? WHERE ID = ?;";
			tsnStmt = con.prepareStatement(sql);
		}
		tsnStmt.setString(1, name);
		tsnStmt.setInt(2, id);
		tsnStmt.executeUpdate();
	}
	
	private PreparedStatement tsdStmt = null;
	void tournamentSetDate(String date, int id) throws SQLException
	{
		if(tsdStmt == null)
		{
			String sql = "UPDATE Turniere SET Datum = ? WHERE ID = ?;";
			tsdStmt = con.prepareStatement(sql);
		}
		tsdStmt.setString(1, date);
		tsdStmt.setInt(2, id);
		tsdStmt.executeUpdate();
	}
	
	private PreparedStatement tsgStmt = null;
	void tournamentSetGroups(int groups, int id) throws SQLException
	{
		if(tsgStmt == null)
		{
			String sql = "UPDATE Turniere SET Gruppen = ? WHERE ID = ?;";
			tsgStmt = con.prepareStatement(sql);
		}
		tsgStmt.setInt(1, groups);
		tsgStmt.setInt(2, id);
		tsgStmt.executeUpdate();
	}
	
	private PreparedStatement tsfrStmt = null;
	void tournamentSetFinalRounds(int rounds, int id) throws SQLException
	{
		if(tsfrStmt == null)
		{
			String sql = "UPDATE Turniere SET Finalrunden = ? WHERE ID = ?;";
			tsfrStmt = con.prepareStatement(sql);
		}
		tsfrStmt.setInt(1, rounds);
		tsfrStmt.setInt(2, id);
		tsfrStmt.executeUpdate();
	}
	
	private PreparedStatement tslStmt = null;
	void tournamentSetLanes(int lanes, int id) throws SQLException
	{
		if(tslStmt == null)
		{
			String sql = "UPDATE Turniere SET Bahnen = ? WHERE ID = ?;";
			tslStmt = con.prepareStatement(sql);
		}
		tslStmt.setInt(1, lanes);
		tslStmt.setInt(2, id);
		tslStmt.executeUpdate();
	}
	
	private PreparedStatement ifpStmt = null;
	boolean isFencerParticipant(Tournament t, Fencer f) throws SQLException
	{
		if(ifpStmt == null)
		{
			String sql = "SELECT COUNT(FechterID) AS Anzahl FROM Teilnahme WHERE TurnierID = ? AND FechterID = ?;";
			ifpStmt = con.prepareStatement(sql);
		}
		ifpStmt.setInt(1, t.getID());
		ifpStmt.setInt(2, f.getID());
		ResultSet rs = ifpStmt.executeQuery();
		rs.next(); //TODO
		if(rs.getInt("Anzahl")>0)
			return true;
		return false;
	}
	
	private PreparedStatement ap1Stmt = null;
	private PreparedStatement ap2Stmt = null;
	void addParticipant(Tournament t, Fencer f, int group) throws SQLException
	{
		if(ap1Stmt == null)
		{
			String sql = "SELECT Gruppen FROM Turniere WHERE ID = ?;";
			ap1Stmt = con.prepareStatement(sql);
			
			
			sql = "INSERT INTO Teilnahme (TurnierID, FechterID, Gruppe) VALUES (?, ?, ?);";
			ap2Stmt = con.prepareStatement(sql);
		}
		if(!isFencerParticipant(t, f))
		{
			ap1Stmt.setInt(1, t.getID());
			ResultSet rs = ap1Stmt.executeQuery();
			rs.next();
			if(group<1||group>rs.getInt("Gruppen"))
				return; //TODO
			ap2Stmt.setInt(1, t.getID());
			ap2Stmt.setInt(2, f.getID());
			ap2Stmt.setInt(3, group);
			ap2Stmt.executeUpdate();
			
			createPreliminary(t, f, group);
		}
	}
	
	private PreparedStatement cp1Stmt = null;
	private PreparedStatement cp2Stmt = null;
	private void createPreliminary(Tournament t,Fencer f, int group) throws SQLException
	{
		if(cp1Stmt == null)
		{
			String sql = "SELECT FechterID FROM Teilnahme WHERE TurnierID = ? AND Gruppe = ? AND FechterID != ?;";
			cp1Stmt = con.prepareStatement(sql);
			
			sql = "INSERT INTO Vorrunden (TurnierID, Gruppe, Teilnehmer1, Teilnehmer2) VALUES (?, ?, ?, ?);";
			cp2Stmt = con.prepareStatement(sql);
		}
		
		cp1Stmt.setInt(1, t.getID());
		cp1Stmt.setInt(2, group);
		cp1Stmt.setInt(3, f.getID());
		
		ResultSet rs = cp1Stmt.executeQuery();
		
		cp2Stmt.setInt(1, t.getID());
		cp2Stmt.setInt(2, group);
		cp2Stmt.setInt(3, f.getID());
		
		int count = 0;
		while(rs.next())
		{
			cp2Stmt.setInt(4, rs.getInt("FechterID"));
			cp2Stmt.executeUpdate();
		}
	}
	
	private PreparedStatement rp1Stmt = null;
	private PreparedStatement rp2Stmt = null;
	void removeParticipant(Fencer f) throws SQLException
	{
		if(rp1Stmt == null)
		{
			String sql = "DELET FROM Teilnehmer WHERE FechterID = ?;";
			rp1Stmt = con.prepareStatement(sql);
			
			sql = "DELET FROM Vorrunden WHERE Teilnehmer1 = ? OR Teilnehmer2 = ?;";
			rp2Stmt = con.prepareStatement(sql);
		}
		
		rp1Stmt.setInt(1, f.getID());
		rp1Stmt.executeUpdate();
		
		rp2Stmt.setInt(1, f.getID());
		rp2Stmt.setInt(2, f.getID());
		rp2Stmt.executeUpdate();
	}
	
	private PreparedStatement ggmc1Stmt = null;
	private PreparedStatement ggmc2Stmt = null;
	int[] getGroupsMemberCount(Tournament t) throws SQLException
	{
		if(ggmc1Stmt == null)
		{
			String sql = "SELECT Gruppen FROM Turniere WHERE ID = ?";
			ggmc1Stmt = con.prepareStatement(sql);
			
			sql = "SELECT Gruppe FROM Teilnahme WHERE TurnierID = ?;";
			ggmc2Stmt = con.prepareStatement(sql);
		}
		
		ggmc1Stmt.setInt(1, t.getID());
		ResultSet rs = ggmc1Stmt.executeQuery();
		
		rs.next();//TODO
		int ret[] = new int [rs.getInt("Gruppen")];
		rs.close();
		
		ggmc2Stmt.setInt(1, t.getID());
		rs = ggmc2Stmt.executeQuery();
		
		while(rs.next())
		{
			ret[rs.getInt("Gruppe")-1]++;
		}
		
		return ret;
	}
	
	private PreparedStatement gtpStmt = null;
	List<Integer> getTournamentPreliminarys(Tournament t) throws SQLException
	{
		if(gtpStmt == null)
		{
			String sql = "SELECT ID FROM Vorrunden WHERE TurnierID = ?;";
			gtpStmt = con.prepareStatement(sql);
		}
		
		List<Integer> ret = new ArrayList<>();
		
		gtpStmt.setInt(1, t.getID());
		ResultSet rs = gtpStmt.executeQuery();
		
		while(rs.next())
			ret.add(rs.getInt("ID"));
		
		return ret;
	}
	
	private PreparedStatement lpStmt = null;
	Preliminary loadPreliminary(int id) throws SQLException
	{
		if(lpStmt == null)
		{
			String sql = "SELECT * FROM Vorrunden WHERE ID = ?;";
			lpStmt = con.prepareStatement(sql);
		}
		
		lpStmt.setInt(1, id);
		ResultSet rs = lpStmt.executeQuery();
		rs.next();//TODO
		
		Preliminary ret = new Preliminary(id, this);
		ret.initTurnamentID(rs.getInt("TurnierID"));
		ret.initGroup(rs.getInt("Gruppe"));
		ret.initRound(rs.getInt("Runde"));
		ret.initLane(rs.getInt("Bahn"));
		Fencer f1 = Sync.getInstance().getFencer(rs.getInt("Teilnehmer1"));
		Fencer f2 = Sync.getInstance().getFencer(rs.getInt("Teilnehmer2"));
		ret.initFencer1(f1);
		ret.initFencer2(f2);
		ret.initPointsFor(f1, rs.getInt("PunkteVon1"));
		ret.initPointsFor(f2, rs.getInt("PunkteVon2"));
		
		
		return ret;
	}
	
	private PreparedStatement stfp1Stmt = null;
	private PreparedStatement stfp2Stmt = null;
	public boolean setTimeForPreliminary(Preliminary p, int round, int lane) throws SQLException
	{
		if(stfp1Stmt == null)
		{
			String sql = "UPDATE Vorrunden SET Runde = ?, Bahn = ? WHERE ID = ?;";
			stfp1Stmt = con.prepareStatement(sql);
			
			sql = "SELECT Bahnen FROM Turniere AS t, Vorrunden AS v WHERE v.TurnierID = t.ID AND v.ID = ?;";
			stfp2Stmt = con.prepareStatement(sql);
		}
		
		stfp2Stmt.setInt(1, p.getID());
		ResultSet rs = stfp2Stmt.executeQuery();
		rs.next(); //TODO
		if(lane<1||lane>rs.getInt("Bahnen")) return false;
		
		
		stfp1Stmt.setInt(1, round);
		stfp1Stmt.setInt(2, lane);
		stfp1Stmt.setInt(3, p.getID());
		stfp1Stmt.executeUpdate();
		return true;
	}
	
	private PreparedStatement gapStmt = null;
	List<Integer> getAllParticipants(Tournament t) throws SQLException
	{
		if(gapStmt == null)
		{
			String sql = "SELECT FechterID FROM Teilnahme WHERE TurnierID = ?;";
			gapStmt = con.prepareStatement(sql);
		}
		
		List<Integer> ret = new ArrayList<>();
		
		gapStmt.setInt(1, t.getID());
		ResultSet rs = gapStmt.executeQuery();
		
		while(rs.next())
			ret.add(rs.getInt("FechterID"));
		
		return ret;
	}
	
	private PreparedStatement gpgStmt = null;
	int getParticipantGroup(Tournament t, Fencer f) throws SQLException
	{
		if(gpgStmt == null)
		{
			String sql = "SELECT Gruppe FROM Teilnahme WHERE TurnierID = ? AND FechterID = ?;";
			gpgStmt = con.prepareStatement(sql); 
		}
		
		gpgStmt.setInt(1, t.getID());
		gpgStmt.setInt(2, f.getID());
		ResultSet rs = gpgStmt.executeQuery();
		rs.next();//TODO
		return rs.getInt("Gruppe");
	}
	
	private PreparedStatement sefStmt = null;
	void setEntryFee(Tournament t,Fencer f, boolean paided) throws SQLException
	{
		if(sefStmt == null)
		{
			String sql = "UPDATE Teilnahme SET Startgeld = ? WHERE TurnierID = ? AND FechterID = ?;";
			sefStmt = con.prepareStatement(sql);
		}
		
		sefStmt.setBoolean(1, paided);
		sefStmt.setInt(2, t.getID());
		sefStmt.setInt(3, f.getID());
		sefStmt.executeUpdate();
	}
	
	private PreparedStatement secStmt = null;
	void setEquipmentCheck(Tournament t, Fencer f, boolean checked) throws SQLException
	{
		if(secStmt == null)
		{
			String sql = "UPDATE Teilnahme SET Ausruestungskontrolle = ? WHERE  TurnierID = ? AND FechterID = ?;";
			secStmt = con.prepareStatement(sql);
		}

		sefStmt.setBoolean(1, checked);
		sefStmt.setInt(2, t.getID());
		sefStmt.setInt(3, f.getID());
		sefStmt.executeUpdate();
		
	}
	
	private PreparedStatement spStmt = null;
	void setPoints(int prelimID, int fencerID, int points) throws SQLException
	{
		if(spStmt == null)
		{
			String sql = "UPDATE Vorrunden SET PunkteVon1 = CASE WHEN Teilnehmer1 = ? THEN ? ELSE PunkteVon1 END, "
					                         +"PunkteVon2 = CASE WHEN Teilnehmer2 = ? THEN ? ELSE PunkteVon2 END WHERE ID = ?;";
			spStmt = con.prepareStatement(sql);
		}
		
		spStmt.setInt(1, fencerID);
		spStmt.setInt(2, points);
		spStmt.setInt(3, fencerID);
		spStmt.setInt(4, points);
		spStmt.setInt(5, prelimID);
		spStmt.executeUpdate();
	}
	
	private PreparedStatement gpStmt = null;
	int getPoints(int prelimID, int fencerID) throws SQLException
	{
		if(gpStmt == null)
		{
			String sql = "SELECT CASE WHEN Teilnehmer1 = ? THEN PunkteVon1 ELSE PunkteVon2 END AS Punkte FROM Vorrunden WHERE ID = ?;";
			gpStmt = con.prepareStatement(sql);
		}
		
		gpStmt.setInt(1, fencerID);
		gpStmt.setInt(2, prelimID);
		ResultSet rs = gpStmt.executeQuery();
		rs.next();//TODO
		return rs.getInt("Punkte");
	}
	
	private PreparedStatement gefStmt = null;
	boolean getEntryFee(Tournament t, Fencer f) throws SQLException
	{
		if(gefStmt == null)
		{
			String sql = "SELECT Startgeld FROM Teilnahme WHERE TurnierID = ? AND FechterID = ?;";
			gefStmt = con.prepareStatement(sql);
		}
		
		gefStmt.setInt(1, t.getID());
		gefStmt.setInt(2, f.getID());
		ResultSet rs = gefStmt.executeQuery();
		rs.next();//TODO
		return rs.getBoolean("Startgeld");
	}
	
	private PreparedStatement gecStmt = null;
	boolean getEquipmentCheck(Tournament t, Fencer f) throws SQLException
	{
		if(gefStmt == null)
		{
			String sql = "SELECT Ausruestungskontrolle FROM Teilnahme WHERE TurnierID = ? AND FechterID = ?;";
			gefStmt = con.prepareStatement(sql);
		}
		
		gefStmt.setInt(1, t.getID());
		gefStmt.setInt(2, f.getID());
		ResultSet rs = gefStmt.executeQuery();
		rs.next();//TODO
		return rs.getBoolean("Ausruestungskontrolle");
	}
}
