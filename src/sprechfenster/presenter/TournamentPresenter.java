/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprechfenster.presenter;

import model.iTournament;

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
