package Model;

import Model.Rounds.Preliminary;
import Model.Rounds.Finalround;
import Model.Rounds.Round;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;




//TODO: Alle Felder aller Tabellen müessen immer einen Wert haben um aus der Init Funktion keine Sicherheitslücke zu machen

class DBConnector 
{
	private static String databaseURL = "jdbc:h2:~/SprechfensterData";
	private static DBConnector dbConnector;
	
	private Connection con;
	
        public String GetDatabaseURL()
        {
            return databaseURL;
        }
        
        public void SetDatabaseURL(String url)
        {
           databaseURL = url;
        }

        public void SetDatabaseSavepoint() throws SQLException
        {
            con.setSavepoint();
        }
        
        public void RestoreDatabase() throws SQLException
        {
            con.rollback();
        }
        
	private DBConnector()
	{
		try 
		{
			Class.forName("org.h2.Driver");
			
			con = DriverManager.getConnection(databaseURL, "", "");
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
			con.prepareStatement(Round.getSQLString()).executeUpdate();
			
		}
		catch (SQLException e) {e.printStackTrace();}
		
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
			ret = new Fencer(rowToHash(rs));
		} 
		catch (ObjectExistException e) 
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
		catch (ObjectExistException e) 
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
			String sql = "SELECT * FROM Vorrunden WHERE ID = ? AND FinalStrucktur = -1;";
			lpStmt = con.prepareStatement(sql);
		}
		
		lpStmt.setInt(1, id);
		ResultSet rs = lpStmt.executeQuery();
		if(!rs.next())
			throw new SQLException("Didn't found preliminary. (ID: "+id+")");
		
		try 
		{
			new Preliminary(rowToHash(rs));
		} 
		catch (ObjectExistException e) {}
		rs.close();
	}
	
	private PreparedStatement stfp1Stmt = null;
	private PreparedStatement stfp2Stmt = null;
	boolean setTime(Round p, int round, int lane) throws SQLException
	{
		if(stfp1Stmt == null)
		{
			String sql = "UPDATE Vorrunden SET Runde = ?, Bahn = ? WHERE ID = ?;";
			stfp1Stmt = con.prepareStatement(sql);
			
			sql = "SELECT Bahnen FROM Turniere WHERE ID = ?;";
			stfp2Stmt = con.prepareStatement(sql);
		}
		try
		{
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
		catch(ObjectDeprecatedException e)
		{
			return false;
		}
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
	
	private PreparedStatement lnf1Stmt = null;
	private PreparedStatement lnf2Stmt = null;
        void loadFinalround(int id) throws SQLException
        {
            if(lnf1Stmt == null)
            {
                String sql = "SELECT * FROM Vorrunden WHERE ID = ? AND FinalStrucktur != -1;";
                lnf1Stmt = con.prepareStatement(sql);
                
                sql = "SELECT * FROM Finalrunden WHERE ID = ?;";
                lnf2Stmt = con.prepareStatement(sql);
            }
            
            lnf1Stmt.setInt(1, id);
            ResultSet rs = lnf1Stmt.executeQuery();
            
            if(!rs.next())
                throw new SQLException("Didn't found the finalround. (ID: "+id+")");
            
            int fsindex = rs.getInt("FinalStrucktur");
            if(fsindex == -1)
                throw new SQLException("The ID is not a Finalround. (ID: "+id+")");
            
            Map<String, Object> set = rowToHash(rs);
            set.remove("FinalStrucktur");
            rs.close();
            
            lnf2Stmt.setInt(1, fsindex);
            rs = lnf2Stmt.executeQuery();
            
            if(!rs.next())
                throw new SQLException("Didn't found the finalroundstruckture. (ID: "+id+")");
            
            Map<String, Object> tmp = rowToHash(rs);
            rs.close();
            tmp.remove("ID");
            set.putAll(tmp);
            try
            {
                new Finalround(set);
            } 
            catch (ObjectExistException ex){}//Can be ignored safely because it does not change the state of the programm after this line
        }
	
	private PreparedStatement rpfpStmt = null;
	void removeParticipantFromPrelim(Round p, Fencer f) throws SQLException
	{
		if(rpfpStmt == null)
		{
			String sql = "UPDATE Vorrunden SET Teilnehmer1 = CASE WHEN Teilnehmer1 = ? THEN -1 ELSE Teilnehmer1 END, "
                                             +"Teilnehmer2 = CASE WHEN Teilnehmer2 = ? THEN -1 ELSE Teilnehmer2 END WHERE ID = ?;";
			rpfpStmt = con.prepareStatement(sql);
		}
		try
		{
			rpfpStmt.setInt(1, f.getID());
			rpfpStmt.setInt(2, f.getID());
			rpfpStmt.setInt(3, p.getID());
			rpfpStmt.executeUpdate();
		}
		catch (ObjectDeprecatedException e) {}
	}
	
	private PreparedStatement aptpStmt = null;
	void addParticipantToPrelim(Round p, Fencer f) throws SQLException
	{
		if(aptpStmt == null)
		{
			String sql = "UPDATE Vorrunden SET Teilnehmer1 = CASE WHEN Teilnehmer1 = -1 THEN ? ELSE Teilnehmer1 END, "
                                             +"Teilnehmer2 = CASE WHEN Teilnehmer2 = -1 AND Teilnehmer1 != -1 THEN ? ELSE Teilnehmer2 END WHERE ID = ?;";
			aptpStmt = con.prepareStatement(sql);
		}
		try
		{
			aptpStmt.setInt(1, f.getID());
			aptpStmt.setInt(2, f.getID());
			aptpStmt.setInt(3, p.getID());
			aptpStmt.executeUpdate();
		}
		catch(ObjectDeprecatedException e){}
	}
	
	private PreparedStatement spipStmt = null; 
	void switchParticipantsInPrelim(Round p, Fencer out, Fencer in) throws SQLException
	{
		if(spipStmt == null)
		{
			String sql = "UPDATE Vorrunden SET Teilnehmer1 = CASE WHEN Teilnehmer1 = ? AND Teilnehmer2 != ? THEN ? ELSE Teilnehmer1 END, "
                                             +"Teilnehmer2 = CASE WHEN Teilnehmer2 = ? AND Teilnehmer1 != ? THEN ? ELSE Teilnehmer2 END WHERE ID = ?;";
			spipStmt = con.prepareStatement(sql);
		}
		try
		{
			spipStmt.setInt(1, out.getID());
			spipStmt.setInt(2, in.getID());
			spipStmt.setInt(3, in.getID());
			spipStmt.setInt(4, out.getID());
			spipStmt.setInt(5, in.getID());
			spipStmt.setInt(6, in.getID());
			spipStmt.setInt(7, p.getID());
			spipStmt.executeUpdate();
		}
		catch(ObjectDeprecatedException e){}
	}
	
	private PreparedStatement frcStmt = null;
	int finalroundsCount(Tournament t) throws SQLException
	{
		if(frcStmt == null)
		{
			String sql = "SELECT SUM(ID) AS Count FROM Vorrunden WHERE TurnierID = ? AND FinalStrucktur != -1;";
			frcStmt = con.prepareStatement(sql);
		}
		
		frcStmt.setInt(1, t.getID());
		ResultSet rs = frcStmt.executeQuery();
		
		if(!rs.next())
			throw new SQLException("Could not count finalrounds.");
		
		return rs.getInt("Count");
	}
	
	private PreparedStatement rfrStmt1 = null;
	private PreparedStatement rfrStmt2 = null;
	private PreparedStatement rfrStmt3 = null;
	private void removeFinalrounds(int tournamentID) throws SQLException
	{
            if(rfrStmt1 == null)
            {
                String sql = "SELECT FinalStrucktur FROM Vorrunden WHERE TurnierID = ? AND FinalStrucktur != -1;";
                rfrStmt1 = con.prepareStatement(sql);

                sql = "DELETE FROM Vorrunden WHERE TurnierID = ? AND FinalStrucktur != -1;";
                rfrStmt1 = con.prepareStatement(sql);

		sql = "DELETE FROM Finalrunden WHERE ID = ?;";
		rfrStmt3 = con.prepareStatement(sql);
            }
		
            rfrStmt1.setInt(1, tournamentID);
            ResultSet rs = rfrStmt1.executeQuery();
            List<Integer> ids = new ArrayList<>();
            while(rs.next())
                ids.add(rs.getInt("FinalStrucktur"));
            rs.close();
                
            rfrStmt2.setInt(1, tournamentID);
            rfrStmt2.executeUpdate();
                
            for(Integer id : ids)
            {
                rfrStmt3.setInt(1, id);
                rfrStmt3.executeUpdate();
            }
	}
	
	private PreparedStatement lapStmt = null;//gapStmt allread in use -> LoadAllPreliminary -> lapStmt
	List<Integer> getAllPreliminarys() throws SQLException
	{
            if(lapStmt == null)
            {
                String sql = "SELECT ID FROM Vorrunden WHERE FinalStrucktur = -1";
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
			String sql = "SELECT ID FROM Vorrunden WHERE FinalStrucktur != -1;";
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
	void setYellowPrelim(Round p, Fencer f, int count) throws SQLException
	{
		if(sypStmt == null)
		{
			String sql = "UPDATE Vorrunden SET GelbVon1 = CASE WHEN Teilnehmer1 = ? THEN ? ELSE GelbVon1 END,"
						                     +"GelbVon2 = CASE WHEN Teilnehmer2 = ? THEN ? ELSE GelbVon2 END "
						                     +"WHERE ID = ?";
			sypStmt = con .prepareStatement(sql);
		}
		try
		{
			sypStmt.setInt(1, f.getID());
			sypStmt.setInt(2, count);
			sypStmt.setInt(3, f.getID());
			sypStmt.setInt(4, count);
			sypStmt.setInt(5, p.getID());
			
			sypStmt.executeUpdate();
		}
		catch(ObjectDeprecatedException e){}
	}
	
	private PreparedStatement srpStmt = null;
	void setRedPrelim(Round p, Fencer f, int count) throws SQLException
	{
		if(srpStmt == null)
		{
			String sql = "UPDATE Vorrunden SET RotVon1 = CASE WHEN Teilnehmer1 = ? THEN ? ELSE RotVon1 END,"
						                     +"RotVon2 = CASE WHEN Teilnehmer2 = ? THEN ? ELSE RotVon2 END "
						                     +"WHERE ID = ?";
			srpStmt = con .prepareStatement(sql);
		}
		try
		{
			srpStmt.setInt(1, f.getID());
			srpStmt.setInt(2, count);
			srpStmt.setInt(3, f.getID());
			srpStmt.setInt(4, count);
			srpStmt.setInt(5, p.getID());
			
			srpStmt.executeUpdate();
		}
		catch(ObjectDeprecatedException e){}
	}
	
	private PreparedStatement sbpStmt = null;
	void setBlackPrelim(Round p, Fencer f, int count) throws SQLException
	{
		if(sbpStmt == null)
		{
			String sql = "UPDATE Vorrunden SET SchwarzVon1 = CASE WHEN Teilnehmer1 = ? THEN ? ELSE SchwarzVon1 END,"
						                     +"SchwarzVon2 = CASE WHEN Teilnehmer2 = ? THEN ? ELSE SchwarzVon2 END "
						                     +"WHERE ID = ?";
			sbpStmt = con .prepareStatement(sql);
		}
		try
		{
			sbpStmt.setInt(1, f.getID());
			sbpStmt.setInt(2, count);
			sbpStmt.setInt(3, f.getID());
			sbpStmt.setInt(4, count);
			sbpStmt.setInt(5, p.getID());
			
			sbpStmt.executeUpdate();
		}
		catch(ObjectDeprecatedException e){}
	}
	
	private PreparedStatement gdoStmt = null;
	boolean getDropedOut(Tournament t, Fencer f) throws SQLException
	{
		if(gdoStmt == null)
		{
			String sql = "SELECT Ausgeschieden FROM Teilnahme WHERE TurnierID = ? AND FechterID = ?;";
			gdoStmt = con.prepareStatement(sql);			
		}
		
		gdoStmt.setInt(1, t.getID());
		gdoStmt.setInt(2, f.getID());
		ResultSet rs = gdoStmt.executeQuery();
		if(!rs.next())
			throw new SQLException("Could not find participant entry. (TournamentId: "+t.getID()+", FencerID: "+f.getID()+")");
		
		boolean ret = rs.getBoolean(1);
		rs.close();
		return ret;
	}
	
	private PreparedStatement gcStmt = null;
	String getComment(Tournament t, Fencer f) throws SQLException
	{
		if(gcStmt == null)
		{
			String sql = "SELECT Kommentar FROM Teilnahme WHERE TurnierID = ? AND FechterID = ?;";
			gcStmt = con.prepareStatement(sql);
		}
		
		gcStmt.setInt(1, t.getID());
		gcStmt.setInt(2, f.getID());
		ResultSet rs = gcStmt.executeQuery();
		
		if(!rs.next())
			throw new SQLException("Could not get comment.");
		
		String ret = rs.getString("Kommentar");
		rs.close();
		
		return ret;
	}
	
	private PreparedStatement scStmt = null;
	void setComment(Tournament t, Fencer f, String comment) throws SQLException
	{
		if(scStmt == null)
		{
			String sql = "UPDATE Teilnahme SET Kommentar = ? WHERE TurnierID = ? AND FechterID = ?;";
			scStmt = con.prepareStatement(sql);
		}
		
		scStmt.setString(1, comment);
		scStmt.setInt(2, t.getID());
		scStmt.setInt(3, f.getID());
		
		scStmt.executeUpdate();
	}

    private PreparedStatement spfStmt = null;
    void setPrelimFinished(Round p, Boolean finished) throws SQLException, ObjectDeprecatedException 
    {
	if(spfStmt == null)
	{
            String sql = "UPDATE Vorrunden SET Beendet = ? WHERE ID = ?;";
            spfStmt = con.prepareStatement(sql);
	}
		
	spfStmt.setBoolean(1, finished);
	spfStmt.setInt(2, p.getID());
	spfStmt.executeUpdate();
    }

    private PreparedStatement rpStmt = null;
    void removePreliminary(int id) throws SQLException 
    {
        if(rpStmt == null)
        {
            String sql = "DELETE FROM Vorrunden WHERE ID = ?;";
            rpStmt = con.prepareStatement(sql);
        }
        
        rpStmt.setInt(1, id);
        rpStmt.executeUpdate();
    } 
    
    void createFinalRounds(Tournament t) throws SQLException
    {
        createFinalRounds(t.getID(), -1, -1, t.getFinalRounds());
    }
    
    void createFinalRounds(int tournamentid, int winnerround, int loserround, int deepth) throws SQLException
    {
        if(deepth<=0)
        {
            try
            {
                loadFinalround(winnerround);
            }
            catch(SQLException e){} //Can be ignored safely
            try
            {
                loadFinalround(loserround);
            }
            catch(SQLException e){} //Can be ignored safely
            return;
        }
        
        int newwinnerround = createFinalRound(tournamentid , winnerround, loserround, deepth);
        int newloserround = -1;
        
        if(winnerround == -1)
            newloserround = createFinalRound(tournamentid , -1, -1, -1);
        
        createFinalRounds(tournamentid, newwinnerround, newloserround, deepth-1);
        createFinalRounds(tournamentid, newwinnerround, newloserround, deepth-1);
    }
    
    private PreparedStatement cfrStmt1 = null;
    private PreparedStatement cfrStmt2 = null;
    private int createFinalRound(int tournamentid, int winnerround, int loserround, int round) throws SQLException
    {
        if(cfrStmt1 == null)
        {
            String sql = "INSERT INTO Finalrunden (GewinnerRunde, VerliererRunde, FinalRunde) "+
                         "VALUES (? ,? ,?);";
            cfrStmt1 = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            sql = "INSERT INTO Vorrunden (TurnierID, FinalStrucktur) VALUES (? ,?);";
            cfrStmt2 = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        }
        int ret;
        
        cfrStmt1.setInt(1, winnerround);
        cfrStmt1.setInt(2, loserround);
        cfrStmt1.setInt(3, round);
        cfrStmt1.executeUpdate();
        
        ResultSet rs = cfrStmt1.getGeneratedKeys();
        if(!rs.next())
            throw new SQLException("Cloud not create FinalroundStructur.");
        
        int finalroundstructur = rs.getInt(1);
        rs.close();
        
        cfrStmt2.setInt(1, tournamentid);
        cfrStmt2.setInt(2, finalroundstructur);
        cfrStmt2.executeUpdate();
        
        rs = cfrStmt2.getGeneratedKeys();
        if(!rs.next())
            throw new SQLException("Cloud not create Round for finalround.");
        
        ret = rs.getInt(1);
        
        return ret;
    }
}