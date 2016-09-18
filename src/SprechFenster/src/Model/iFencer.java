package Model;

import java.sql.SQLException;

public interface iFencer 
{
	public String getName();
	public String getFamilyName();
        public String getFullName();
	public String getBirthday();
	public String getFencingSchool();
	public String getNationality();
	
	public void setName(String name) throws SQLException;
	public void setFamilyName(String name) throws SQLException;
	public void setBirthday(String birthday) throws SQLException;
	public void setFencingSchool(String school) throws SQLException;
	public void setNationality(String nation) throws SQLException;
}
