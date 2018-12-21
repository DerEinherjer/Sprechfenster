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
public interface iPreliminary extends iRound
{
    public int getPreliminaryGroup() throws SQLException;
}
