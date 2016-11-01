package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



//TODO: Alle Felder aller Tabellen müessen immer einen Wert haben um aus der Init Funktion keine Sicherheitslücke zu machen

class DBConnector 
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
		catch (SQLException e) {e.printStackTrace();}
		

		try 
		{
			con.prepareStatement(Tournament.getSQLString()).executeUpdate();
		} 
		catch (SQLException e) {e.printStackTrace();}
		
		try 
		{
			con.prepareStatement(Participation.getSQLString()).executeUpdate();
		} 
		catch (SQLException e) {e.printStackTrace();}
		
		try
		{
			con.prepareStatement(Preliminary.getSQLString()).executeUpdate();
			
		}
		catch (SQLException e) {System.out.println(e.getMessage());e.printStackTrace();}
		
		try
		{
			con.prepareStatement(Finalround.getSQLString()).executeUpdate();
		}
		catch (SQLException e) {e.printStackTrace();}
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
		
		if(rs.next())
			return rs.getInt(1);
		else
			throw new SQLException("Didn't return ID of the new fencer.");
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
		
		gfvStmt.setInt(1, id);
		ResultSet rs = gfvStmt.executeQuery();
		if(!rs.next())
			throw new SQLException("Didn't found the fencer.");
		
		Fencer ret;
		try 
		{
			ret = new Fencer(rowToHash(rs), this);
		} 
		catch (ObjectExistExeption e) 
		{
			ret = (Fencer) e.getObject();
		}
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
			String sql = "INSERT INTO Turniere (Name, Datum, Gruppen, Finalrunden, Bahnen, InFinalrunden) VALUES (?, '', 2, 2, 2, FALSE);";
			ctStmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		}
		
		ctStmt.setString(1, name);
		ctStmt.executeUpdate();
		
		ResultSet rs = ctStmt.getGeneratedKeys();
		if(!rs.next())
			throw new SQLException("Could not generate Tournament");
		int ret = rs.getInt(1);
		rs.close();
		return ret;
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
		
		gtvStmt.setInt(1, id);
		ResultSet rs = gtvStmt.executeQuery();
		if(!rs.next())
			throw new SQLException("Didn't found tournament.");
		
		Tournament ret;
		try 
		{
			ret = new Tournament(rowToHash(rs));
		} 
		catch (ObjectExistExeption e) 
		{
			ret = (Tournament) e.getObject();
		}
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
	
	
	private PreparedStatement apStmt = null;
	int addPreliminary(Tournament t) throws SQLException
	{
		if(apStmt == null)
		{
			String sql = "INSERT INTO Vorrunden (TurnierID, Gruppe, Teilnehmer1, Teilnehmer2) VALUES (?, 0, -1, -1);";
			apStmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		}
		
		apStmt.setInt(1, t.getID());
		apStmt.executeUpdate();
		ResultSet rs = spStmt.getGeneratedKeys();
		if(!rs.next())
			throw new SQLException("Could not create preliminary.");
		int ret = rs.getInt(1);
		rs.close();
		return ret;
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
			cp2Stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		}
		cp1Stmt.setInt(1, t.getID());
		cp1Stmt.setInt(2, group);
		cp1Stmt.setInt(3, f.getID());
		
		ResultSet rs = cp1Stmt.executeQuery();
		
		cp2Stmt.setInt(1, t.getID());
		cp2Stmt.setInt(2, group);
		cp2Stmt.setInt(3, f.getID());
		
		while(rs.next())
		{
			cp2Stmt.setInt(4, rs.getInt("FechterID"));
			cp2Stmt.executeUpdate();
			ResultSet rs2 = cp2Stmt.getGeneratedKeys();
			if(!rs2.next())
				throw new SQLException("Could not generate preliminary");
			int id = rs2.getInt(1);
			rs2.close();
			loadPreliminary(id);
		}
	}
	
	private PreparedStatement rp1Stmt = null;
	private PreparedStatement rp2Stmt = null;
	void removeParticipant(Fencer f) throws SQLException
	{
		if(rp1Stmt == null)
		{
			String sql = "DELETE FROM Teilnahme WHERE FechterID = ?;";
			rp1Stmt = con.prepareStatement(sql);
			
			sql = "DELETE FROM Vorrunden WHERE Teilnehmer1 = ? OR Teilnehmer2 = ?;";
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
	void loadPreliminary(int id) throws SQLException
	{
		if(id<0) return;
		if(lpStmt == null)
		{
			String sql = "SELECT * FROM Vorrunden WHERE ID = ?;";
			lpStmt = con.prepareStatement(sql);
		}
		
		lpStmt.setInt(1, id);
		ResultSet rs = lpStmt.executeQuery();
		if(!rs.next())//TODO
			throw new SQLException("Didn't found preliminary");
		
		try 
		{
			new Preliminary(rowToHash(rs));
		} 
		catch (ObjectExistExeption e) {}
		rs.close();
	}
	
	private PreparedStatement stfp1Stmt = null;
	private PreparedStatement stfp2Stmt = null;
	boolean setTimeForPreliminary(Preliminary p, int round, int lane) throws SQLException
	{
		if(stfp1Stmt == null)
		{
			String sql = "UPDATE Vorrunden SET Runde = ?, Bahn = ? WHERE ID = ?;";
			stfp1Stmt = con.prepareStatement(sql);
			
			sql = "SELECT Bahnen FROM Turniere WHERE ID = ?;";
			stfp2Stmt = con.prepareStatement(sql);
		}
		Tournament t = (Tournament)p.getTournament();
		stfp2Stmt.setInt(1, t.getID());
		ResultSet rs = stfp2Stmt.executeQuery();
		if(!rs.next())
			throw new SQLException("Could not found Tournament. (ID "+t.getID()+")");
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
	
	private PreparedStatement spfrStmt = null;
	void setPointsFR(int prelimID, int fencerID, int points) throws SQLException
	{
		if(spfrStmt == null)
		{
			String sql = "UPDATE Finalrunden SET PunkteVon1 = CASE WHEN Teilnehmer1 = ? THEN ? ELSE PunkteVon1 END, "
					                           +"PunkteVon2 = CASE WHEN Teilnehmer2 = ? THEN ? ELSE PunkteVon2 END WHERE ID = ?;";
			spfrStmt = con.prepareStatement(sql);
		}
		
		spfrStmt.setInt(1, fencerID);
		spfrStmt.setInt(2, points);
		spfrStmt.setInt(3, fencerID);
		spfrStmt.setInt(4, points);
		spfrStmt.setInt(5, prelimID);
		spfrStmt.executeUpdate();
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
		if(gecStmt == null)
		{
			String sql = "SELECT Ausruestungskontrolle FROM Teilnahme WHERE TurnierID = ? AND FechterID = ?;";
			gecStmt = con.prepareStatement(sql);
		}
		
		gecStmt.setInt(1, t.getID());
		gecStmt.setInt(2, f.getID());
		ResultSet rs = gecStmt.executeQuery();
		rs.next();//TODO
		return rs.getBoolean("Ausruestungskontrolle");
	}
	
	private PreparedStatement stff1Stmt = null;
	private PreparedStatement stff2Stmt = null;
	boolean setTimeForFinalround(Finalround p, int round, int lane) throws SQLException
	{
		if(stff1Stmt == null)
		{
			String sql = "UPDATE Vorrunden SET Runde = ?, Bahn = ? WHERE ID = ?;";
			stff1Stmt = con.prepareStatement(sql);
			
			sql = "SELECT Bahnen FROM Turniere AS t, Vorrunden AS v WHERE v.TurnierID = t.ID AND v.ID = ?;";
			stff2Stmt = con.prepareStatement(sql);
		}
		
		stff2Stmt.setInt(1, p.getID());
		ResultSet rs = stff2Stmt.executeQuery();
		rs.next(); //TODO
		if(lane<1||lane>rs.getInt("Bahnen")) return false;
		
		
		stff1Stmt.setInt(1, round);
		stff1Stmt.setInt(2, lane);
		stff1Stmt.setInt(3, p.getID());
		stff1Stmt.executeUpdate();
		return true;
	}
	
	
	
	private PreparedStatement lfStmt = null;
	void loadFinalround(int id) throws SQLException
	{
		if(id < 0)return;
		if(lfStmt == null)
		{
			String sql = "SELECT * FROM Finalrunden WHERE ID = ?;";
			lfStmt = con.prepareStatement(sql);
		}
		
		lfStmt.setInt(1, id);
		ResultSet rs = lfStmt.executeQuery();
		if(!rs.next())
			throw new SQLException("Didn't found the finalround. (ID: "+id+")");

		try 
		{
			new Finalround(rowToHash(rs));
		} 
		catch (ObjectExistExeption e) {}
		rs.close();
	}
	
	private PreparedStatement rpfpStmt = null;
	void removeParticipantFromPrelim(Preliminary p, Fencer f) throws SQLException
	{
		if(rpfpStmt == null)
		{
			String sql = "UPDATE Vorrunden SET Teilnehmer1 = CASE WHEN Teilnehmer1 = ? THEN -1 ELSE Teilnehmer1 END, "
                                             +"Teilnehmer2 = CASE WHEN Teilnehmer2 = ? THEN -1 ELSE Teilnehmer2 END WHERE ID = ?;";
			rpfpStmt = con.prepareStatement(sql);
		}
		
		rpfpStmt.setInt(1, f.getID());
		rpfpStmt.setInt(2, f.getID());
		rpfpStmt.setInt(3, p.getID());
		rpfpStmt.executeUpdate();
	}
	
	private PreparedStatement rpffStmt = null;
	void removeParticipantFromFinal(Finalround p, Fencer f) throws SQLException
	{
		if(rpffStmt == null)
		{
			String sql = "UPDATE Finalrunden SET Teilnehmer1 = CASE WHEN Teilnehmer1 = ? THEN -1 ELSE Teilnehmer1 END, "
                                               +"Teilnehmer2 = CASE WHEN Teilnehmer2 = ? THEN -1 ELSE Teilnehmer2 END WHERE ID = ?;";
			rpffStmt = con.prepareStatement(sql);
		}
		
		rpffStmt.setInt(1, f.getID());
		rpffStmt.setInt(2, f.getID());
		rpffStmt.setInt(3, p.getID());
		rpffStmt.executeUpdate();
	}
	
	private PreparedStatement aptpStmt = null;
	void addParticipantToPrelim(Preliminary p, Fencer f) throws SQLException
	{
		if(aptpStmt == null)
		{
			String sql = "UPDATE Vorrunden SET Teilnehmer1 = CASE WHEN Teilnehmer1 = -1 THEN ? ELSE Teilnehmer1 END, "
                                             +"Teilnehmer2 = CASE WHEN Teilnehmer2 = -1 AND Teilnehmer1 != -1 THEN ? ELSE Teilnehmer2 END WHERE ID = ?;";
			aptpStmt = con.prepareStatement(sql);
		}
		aptpStmt.setInt(1, f.getID());
		aptpStmt.setInt(2, f.getID());
		aptpStmt.setInt(3, p.getID());
		aptpStmt.executeUpdate();
	}
	
	private PreparedStatement aptfStmt = null;
	void addParticipantToFinal(Finalround p, Fencer f) throws SQLException
	{
		if(aptfStmt == null)
		{
			String sql = "UPDATE Finalrunden SET Teilnehmer1 = CASE WHEN Teilnehmer1 = -1 THEN ? ELSE Teilnehmer1 END, "
                                             +"Teilnehmer2 = CASE WHEN Teilnehmer2 = -1 AND Teilnehmer1 != -1 THEN ? ELSE Teilnehmer2 END WHERE ID = ?;";
			aptfStmt = con.prepareStatement(sql);
		}
		aptfStmt.setInt(1, f.getID());
		aptfStmt.setInt(2, f.getID());
		aptfStmt.setInt(3, p.getID());
		aptfStmt.executeUpdate();
	}
	
	private PreparedStatement spipStmt = null; 
	void switchParticipantsInPrelim(Preliminary p, Fencer out, Fencer in) throws SQLException
	{
		if(spipStmt == null)
		{
			String sql = "UPDATE Vorrunden SET Teilnehmer1 = CASE WHEN Teilnehmer1 = ? AND Teilnehmer2 != ? THEN ? ELSE Teilnehmer1 END, "
                                             +"Teilnehmer2 = CASE WHEN Teilnehmer2 = ? AND Teilnehmer1 != ? THEN ? ELSE Teilnehmer2 END WHERE ID = ?;";
			spipStmt = con.prepareStatement(sql);
		}
		
		spipStmt.setInt(1, out.getID());
		spipStmt.setInt(2, in.getID());
		spipStmt.setInt(3, in.getID());
		spipStmt.setInt(4, out.getID());
		spipStmt.setInt(5, in.getID());
		spipStmt.setInt(6, in.getID());
		spipStmt.setInt(7, p.getID());
		spipStmt.executeUpdate();
	}
	
	private PreparedStatement spifStmt = null; 
	void switchParticipantsInFinal(Finalround p, Fencer out, Fencer in) throws SQLException
	{
		if(spifStmt == null)
		{
			String sql = "UPDATE Finalrunden SET Teilnehmer1 = CASE WHEN Teilnehmer1 = ? AND Teilnehmer2 != ? THEN ? ELSE Teilnehmer1 END, "
                                             +"Teilnehmer2 = CASE WHEN Teilnehmer2 = ? AND Teilnehmer1 != ? THEN ? ELSE Teilnehmer2 END WHERE ID = ?;";
			spifStmt = con.prepareStatement(sql);
		}
		
		spifStmt.setInt(1, out.getID());
		spifStmt.setInt(2, in.getID());
		spifStmt.setInt(3, in.getID());
		spifStmt.setInt(4, out.getID());
		spifStmt.setInt(5, in.getID());
		spifStmt.setInt(6, in.getID());
		spifStmt.setInt(7, p.getID());
		spifStmt.executeUpdate();
	}
	
	
	private PreparedStatement cfrStmt = null;
	void createFinalRounds(Tournament t) throws SQLException
	{
		removeFinalrounds(t);
		createFinalRounds(t.getFinalRounds()-1, t, -1, -1);
	}
	private void createFinalRounds(int n,Tournament t, int winner, int loser) throws SQLException
	{
		if(cfrStmt == null)
		{
			String sql = "INSERT INTO Finalrunden (TurnierID, Gewinner, Verlierer) VALUES (?,?,?);";
			cfrStmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		}

		cfrStmt.setInt(1, t.getID());
		cfrStmt.setInt(2, winner);
		cfrStmt.setInt(3, loser);
		
		cfrStmt.executeUpdate();
		ResultSet rs = cfrStmt.getGeneratedKeys();
		if(!rs.next())
			throw new SQLException("Could not generate finalround.");
		int tmp = rs.getInt(1);
		rs.close();
		int newloser = -1;
		
		if(winner==-1)
		{
			cfrStmt.executeUpdate();
			rs = cfrStmt.getGeneratedKeys();
			if(!rs.next())
				throw new SQLException("Could not generate finalround");
			newloser = rs.getInt(1);
		}
		
		
		if(n!=0)
		{
			createFinalRounds(n-1, t,tmp, newloser);
			createFinalRounds(n-1, t,tmp, newloser);
		}
		else
		{
			loadFinalround(tmp);
		}
	}
	
	private PreparedStatement frcStmt = null;
	int finalroundsCount(Tournament t) throws SQLException
	{
		if(frcStmt == null)
		{
			String sql = "SELECT SUM(ID) AS Count FROM Finalrunden WHERE TurnierID = ?;";
			frcStmt = con.prepareStatement(sql);
		}
		
		frcStmt.setInt(1, t.getID());
		ResultSet rs = frcStmt.executeQuery();
		
		if(!rs.next())
			throw new SQLException("Could not count finalrounds.");
		
		return rs.getInt("Count");
	}
	
	private PreparedStatement rfrStmt = null;
	private void removeFinalrounds(Tournament t) throws SQLException
	{
		if(rfrStmt == null)
		{
			String sql = "DELETE FROM Finalrunden WHERE TurnierID = ?;";
			rfrStmt = con.prepareStatement(sql);
		}
		
		rfrStmt.setInt(1, t.getID());
		rfrStmt.executeUpdate();
	}
	
	private PreparedStatement lapStmt = null;//gapStmt allread in use -> LoadAllPreliminary -> lapStmt
	List<Integer> getAllPreliminarys() throws SQLException
	{
		if(lapStmt == null)
		{
			String sql = "SELECT ID FROM Vorrunden";
			lapStmt= con.prepareStatement(sql);
		}
		
		ResultSet rs = lapStmt.executeQuery();
		
		List<Integer> ret  = new ArrayList<>();
		while(rs.next())
			ret.add(rs.getInt(1));
		
		return ret;
	}
	
	private PreparedStatement gafrStmt = null;
	List<Integer> getAllFinalrounds() throws SQLException
	{
		if(gafrStmt == null)
		{
			String sql = "SELECT ID FROM Finalrunden";
			gafrStmt = con.prepareStatement(sql);
		}
		
		ResultSet rs = gafrStmt.executeQuery();
		
		List<Integer> ret = new ArrayList<>();
		
		while(rs.next())
			ret.add(rs.getInt(1));
		
		return ret;
	}
	
	private PreparedStatement apfStmt = null;
	@Deprecated
	boolean allPreliminaryFinished(Tournament t) throws SQLException
	{
		if(apfStmt == null)
		{
			String sql = "SELCT COUNT(ID) as Unfertig FROM Vorrunden WHERE Beendet = false AND TurnierID = ?;";
			apfStmt = con.prepareStatement(sql);
		}
		
		apfStmt.setInt(1, t.getID());
		ResultSet rs = apfStmt.executeQuery();
		
		if(!rs.next())
			throw new SQLException("Cloud not count unfinished preliminarys");
		
		int unfinished = rs.getInt("Unfertig");
		
		rs.close();
		
		return unfinished==0;
	}
	

	private Map<String, Object> rowToHash(ResultSet rs) throws SQLException
	{
		Map<String, Object> ret = new HashMap<>();
		ResultSetMetaData md = rs.getMetaData();
		int columns = md.getColumnCount();
		for(int i=1; i<=columns; ++i){
			ret.put(md.getColumnName(i),rs.getObject(i));
		}
		return ret;
	}

	private PreparedStatement sfpStmt = null;
	void setFinishedPreliminary(Tournament t) throws SQLException
	{
		if(sfpStmt == null)
		{
			String sql = "UPDATE Turniere SET InFinalrunden = TRUE WHERE ID = ?;";
			sfpStmt = con.prepareStatement(sql);
		}
		
		sfpStmt.setInt(1, t.getID());
		sfpStmt.executeUpdate();
	}
	
	private PreparedStatement sypStmt = null;
	void setYellowPrelim(Preliminary p, Fencer f, int count) throws SQLException
	{
		if(sypStmt == null)
		{
			String sql = "UPDATE Vorrunden SET GelbVon1 = CASE WHEN Teilnehmer1 = ? THEN ? ELSE GelbVon1 END,"
						                     +"GelbVon2 = CASE WHEN Teilnehmer2 = ? THEN ? ELSE GelbVon2 END "
						                     +"WHERE ID = ?";
			sypStmt = con .prepareStatement(sql);
		}
		
		sypStmt.setInt(1, f.getID());
		sypStmt.setInt(2, count);
		sypStmt.setInt(3, f.getID());
		sypStmt.setInt(4, count);
		sypStmt.setInt(5, p.getID());
		
		sypStmt.executeUpdate();
	}
	
	private PreparedStatement syfStmt = null;
	void setYellowFinal(Finalround p, Fencer f, int count) throws SQLException
	{
		if(syfStmt == null)
		{
			String sql = "UPDATE Finalrunden SET GelbVon1 = CASE WHEN Teilnehmer1 = ? THEN ? ELSE GelbVon1 END,"
						                      +"GelbVon2 = CASE WHEN Teilnehmer2 = ? THEN ? ELSE GelbVon2 END "
						                      +"WHERE ID = ?";
			syfStmt = con .prepareStatement(sql);
		}
		
		syfStmt.setInt(1, f.getID());
		syfStmt.setInt(2, count);
		syfStmt.setInt(3, f.getID());
		syfStmt.setInt(4, count);
		syfStmt.setInt(5, p.getID());
		
		syfStmt.executeUpdate();
	}
	
	private PreparedStatement srpStmt = null;
	void setRedPrelim(Preliminary p, Fencer f, int count) throws SQLException
	{
		if(srpStmt == null)
		{
			String sql = "UPDATE Vorrunden SET RotVon1 = CASE WHEN Teilnehmer1 = ? THEN ? ELSE RotVon1 END,"
						                     +"RotVon2 = CASE WHEN Teilnehmer2 = ? THEN ? ELSE RotVon2 END "
						                     +"WHERE ID = ?";
			srpStmt = con .prepareStatement(sql);
		}
		
		srpStmt.setInt(1, f.getID());
		srpStmt.setInt(2, count);
		srpStmt.setInt(3, f.getID());
		srpStmt.setInt(4, count);
		srpStmt.setInt(5, p.getID());
		
		srpStmt.executeUpdate();
	}
	
	private PreparedStatement srfStmt = null;
	void setRedFinal(Finalround p, Fencer f, int count) throws SQLException
	{
		if(srfStmt == null)
		{
			String sql = "UPDATE Finalrunden SET RotVon1 = CASE WHEN Teilnehmer1 = ? THEN ? ELSE RotVon1 END,"
						                      +"RotVon2 = CASE WHEN Teilnehmer2 = ? THEN ? ELSE RotVon2 END "
						                      +"WHERE ID = ?";
			srfStmt = con .prepareStatement(sql);
		}
		
		srfStmt.setInt(1, f.getID());
		srfStmt.setInt(2, count);
		srfStmt.setInt(3, f.getID());
		srfStmt.setInt(4, count);
		srfStmt.setInt(5, p.getID());
		
		srfStmt.executeUpdate();
	}
	
	private PreparedStatement sbpStmt = null;
	void setBlackPrelim(Preliminary p, Fencer f, int count) throws SQLException
	{
		if(sbpStmt == null)
		{
			String sql = "UPDATE Vorrunden SET SchwarzVon1 = CASE WHEN Teilnehmer1 = ? THEN ? ELSE SchwarzVon1 END,"
						                     +"SchwarzVon2 = CASE WHEN Teilnehmer2 = ? THEN ? ELSE SchwarzVon2 END "
						                     +"WHERE ID = ?";
			sbpStmt = con .prepareStatement(sql);
		}
		
		sbpStmt.setInt(1, f.getID());
		sbpStmt.setInt(2, count);
		sbpStmt.setInt(3, f.getID());
		sbpStmt.setInt(4, count);
		sbpStmt.setInt(5, p.getID());
		
		sbpStmt.executeUpdate();
	}
	
	private PreparedStatement sbfStmt = null;
	void setBlackFinal(Finalround p, Fencer f, int count) throws SQLException
	{
		if(sbfStmt == null)
		{
			String sql = "UPDATE Finalrunden SET SchwarzVon1 = CASE WHEN Teilnehmer1 = ? THEN ? ELSE SchwarzVon1 END,"
						                      +"SchwarzVon2 = CASE WHEN Teilnehmer2 = ? THEN ? ELSE SchwarzVon2 END "
						                      +"WHERE ID = ?";
			sbfStmt = con .prepareStatement(sql);
		}
		
		sbfStmt.setInt(1, f.getID());
		sbfStmt.setInt(2, count);
		sbfStmt.setInt(3, f.getID());
		sbfStmt.setInt(4, count);
		sbfStmt.setInt(5, p.getID());
		
		sbfStmt.executeUpdate();
	}
}
