/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster.Presenters;

import Model.iTournament;

/**
 *
 * @author Stefan
 */
public class TournamentPresenter {
    
    private iTournament Tournament;
    
    public TournamentPresenter(iTournament tournamentToPresent)
    { 
        if(tournamentToPresent == null)
        {
            throw new IllegalArgumentException("tournamentToPresent must not be null");
        }
        this.Tournament = tournamentToPresent;
    }
    
    @Override
    public boolean equals(Object other)
    {
        if(other == this)
        {
            return true;
        }
        else
        {
            if(!(other instanceof TournamentPresenter))
            {
                return false;
            }
            else
            {
                return Tournament.equals(((TournamentPresenter)other).Tournament);
            }
        }
    }
    
    @Override 
    public int hashCode()
    {
        return Tournament.hashCode();
    }
    
    public iTournament getTournament()
    {
        return Tournament;
    }
    
    public String getName()
    {
        return Tournament.getName();
    }
    
    public String getDate()
    {
        return Tournament.getDate();
    }
}
