/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.rounds;

import java.sql.SQLException;

/**
 *
 * @author Asgard
 */
public interface iQualificationMatch extends iMatch
{

  public int getQualificationGroup() throws SQLException;
}
